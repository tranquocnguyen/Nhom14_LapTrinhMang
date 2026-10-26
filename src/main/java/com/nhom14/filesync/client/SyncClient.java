package com.nhom14.filesync.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import com.nhom14.filesync.engine.ChunkEngine;
import com.nhom14.filesync.engine.SyncStateTracker;
import com.nhom14.filesync.model.*;
import com.nhom14.filesync.protocol.PacketStream;
import com.nhom14.filesync.protocol.ProtocolConstants;
import com.nhom14.filesync.security.PathSecurity;
import com.nhom14.filesync.security.SslContextFactory;
import com.nhom14.filesync.util.FileSyncUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Máy khách đồng bộ tệp tin (Sync Client)
 * Hỗ trợ Stop-and-Wait ACK, Batch ACK, Resume Transfer, và Xử lý Xung đột
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Lý Quốc Thái (MSSV: 2380602014)
 */
public class SyncClient {

    private final String host;
    private final int port;
    private final boolean useSsl;
    private final File rootDir;
    private int chunkSize = ProtocolConstants.DEFAULT_CHUNK_SIZE;
    private String transmissionMode = ProtocolConstants.MODE_BATCH_WINDOW;
    private final ClientListener listener;

    private Socket socket;
    private PacketStream stream;
    private volatile boolean syncing = false;

    public SyncClient(String host, int port, boolean useSsl, File rootDir, ClientListener listener) {
        this.host = host;
        this.port = port;
        this.useSsl = useSsl;
        this.rootDir = rootDir;
        this.listener = listener;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void setTransmissionMode(String mode) {
        this.transmissionMode = mode;
    }

    public String getTransmissionMode() {
        return transmissionMode;
    }

    public void connect() throws Exception {
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        log("Đang kết nối tới Server " + host + ":" + port + (useSsl ? " [SSL/TLS]" : " [TCP]") + "...");
        if (useSsl) {
            SSLContext sslContext = SslContextFactory.createClientSslContext();
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) ssf.createSocket(host, port);
            sslSocket.setEnabledProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
            sslSocket.startHandshake();
            this.socket = sslSocket;
        } else {
            this.socket = new Socket(host, port);
        }

        stream = new PacketStream(socket.getInputStream(), socket.getOutputStream());

        // Bắt tay giao thức HELLO
        stream.writeCommand(ProtocolConstants.CMD_HELLO);
        stream.writeUTF(ProtocolConstants.PROTOCOL_VERSION);

        String response = stream.readCommand();
        if (ProtocolConstants.CMD_HELLO_OK.equals(response)) {
            String serverVersion = stream.readUTF();
            log("Kết nối thành công! Phiên bản Server: " + serverVersion);
            if (listener != null) {
                listener.onConnected(host, port, useSsl);
            }
        } else {
            throw new IOException("Server từ chối bắt tay giao thức: " + response);
        }
    }

    public void disconnect() {
        try {
            if (stream != null) {
                stream.writeCommand(ProtocolConstants.CMD_BYE);
            }
        } catch (Exception ignored) {}
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {}
        log("Đã ngắt kết nối với Server.");
        if (listener != null) {
            listener.onDisconnected();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public synchronized CompletableFuture<Boolean> sync() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (syncing) {
            log("Tiến trình đồng bộ đang chạy, vui lòng đợi...");
            future.complete(false);
            return future;
        }
        syncing = true;

        new Thread(() -> {
            try {
                if (!isConnected()) {
                    connect();
                }

                log("--- BẮT ĐẦU ĐỒNG BỘ DỮ LIỆU (Mode: " + transmissionMode + ") ---");
                // 1. Quét tệp tin Client và tính mã băm SHA-256
                log("Đang quét thư mục Client: " + rootDir.getAbsolutePath() + "...");
                List<FileMetadata> clientMetadata = FileSyncUtil.scanDirectory(rootDir);
                log("Đã quét được " + clientMetadata.size() + " tệp tin ở Client.");

                // 2. Gửi Metadata lên Server
                stream.writeCommand(ProtocolConstants.CMD_SEND_METADATA);
                stream.writeInt(clientMetadata.size());
                for (FileMetadata meta : clientMetadata) {
                    stream.writeMetadata(meta);
                }

                // 3. Nhận Kế hoạch đồng bộ (SyncPlan) từ Server
                String planCmd = stream.readCommand();
                if (!ProtocolConstants.CMD_SYNC_PLAN_ITEM.equals(planCmd)) {
                    throw new IOException("Không nhận được kế hoạch đồng bộ từ Server");
                }

                int planItemCount = stream.readInt();
                SyncPlan plan = new SyncPlan();
                for (int i = 0; i < planItemCount; i++) {
                    String relPath = stream.readUTF();
                    String actionName = stream.readUTF();
                    String conflictRenamed = stream.readUTF();
                    String detail = stream.readUTF();
                    boolean hasServerMeta = stream.readBoolean();
                    FileMetadata serverMeta = hasServerMeta ? stream.readMetadata() : null;

                    SyncItem item = new SyncItem(relPath, SyncAction.valueOf(actionName), null, serverMeta, detail);
                    if (!conflictRenamed.isEmpty()) {
                        item.setConflictRenamedPath(conflictRenamed);
                    }
                    plan.addItem(item);
                }

                if (listener != null) {
                    listener.onSyncPlanReady(plan);
                }

                log("Kế hoạch đồng bộ: " + plan.getItems().size() + " mục (Tải lên: "
                        + plan.getActionCount(SyncAction.UPLOAD) + ", Tải về: "
                        + plan.getActionCount(SyncAction.DOWNLOAD) + ", Xung đột: "
                        + plan.getActionCount(SyncAction.CONFLICT) + ", Xóa: "
                        + (plan.getActionCount(SyncAction.DELETE_LOCAL) + plan.getActionCount(SyncAction.DELETE_REMOTE)) + ")");

                // 4. Thực thi từng mục tác vụ trong SyncPlan
                int actionableTotal = 0;
                for (SyncItem item : plan.getItems()) {
                    if (item.getAction() != SyncAction.UP_TO_DATE) actionableTotal++;
                }

                int processedActionable = 0;

                for (SyncItem item : plan.getItems()) {
                    if (item.getAction() == SyncAction.UP_TO_DATE) {
                        continue;
                    }

                    processedActionable++;
                    int overallPercent = (int) ((processedActionable * 100.0) / Math.max(1, actionableTotal));

                    switch (item.getAction()) {
                        case UPLOAD -> executeUpload(item, processedActionable, actionableTotal, overallPercent);
                        case DOWNLOAD -> executeDownload(item, processedActionable, actionableTotal, overallPercent);
                        case CONFLICT -> executeConflict(item, processedActionable, actionableTotal, overallPercent);
                        case DELETE_REMOTE -> executeDeleteRemote(item);
                        case DELETE_LOCAL -> executeDeleteLocal(item);
                        default -> {}
                    }
                }

                // Cập nhật Snapshot trạng thái sau khi hoàn tất
                List<FileMetadata> postSyncFiles = FileSyncUtil.scanDirectory(rootDir);
                SyncStateTracker.saveSnapshot(rootDir, postSyncFiles);

                log("ĐỒNG BỘ HOÀN TẤT THÀNH CÔNG 100%!");
                if (listener != null) {
                    listener.onSyncFinished(true, "Đồng bộ thành công! Mọi tệp tin đều đã đồng nhất.");
                }
                future.complete(true);

            } catch (Exception e) {
                log("Lỗi trong quá trình đồng bộ: " + e.getMessage());
                if (listener != null) {
                    listener.onSyncFinished(false, "Lỗi: " + e.getMessage());
                }
                future.completeExceptionally(e);
            } finally {
                syncing = false;
            }
        }).start();
        return future;
    }

    public boolean syncBlocking(long timeoutSeconds) throws Exception {
        return sync().get(timeoutSeconds, TimeUnit.SECONDS);
    }

    private void executeUpload(SyncItem item, int fileIdx, int totalFiles, int overallPct) throws Exception {
        File file = PathSecurity.resolveAndValidate(rootDir, item.getRelativePath());
        long fileSize = file.length();
        long lastModified = file.lastModified();
        int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);
        String fileHash = item.getClientMeta() != null ? item.getClientMeta().getSha256() : com.nhom14.filesync.util.HashUtil.calculateSha256(file);

        log("[UPLOAD] Đang tải lên: " + item.getRelativePath() + " (" + formatBytes(fileSize) + ", " + totalChunks + " chunks, " + transmissionMode + ")...");

        stream.writeCommand(ProtocolConstants.CMD_UPLOAD_START);
        stream.writeUTF(item.getRelativePath());
        stream.writeLong(fileSize);
        stream.writeInt(totalChunks);
        stream.writeUTF(fileHash);
        stream.writeInt(chunkSize);
        stream.writeLong(lastModified);
        stream.writeUTF(transmissionMode);

        // Nhận thông tin Resume từ Server
        String resCmd = stream.readCommand();
        if (!ProtocolConstants.CMD_UPLOAD_RESUME.equals(resCmd)) {
            throw new IOException("Server không phản hồi thông tin Resume hợp lệ");
        }
        long resumeOffset = stream.readLong();
        int startChunk = stream.readInt();

        if (startChunk > 0) {
            log(">>> [RESUME TRANSFER] Tiếp tục gửi từ chunk " + startChunk + "/" + totalChunks);
        }

        int currentChunkIdx = startChunk;
        while (currentChunkIdx < totalChunks) {
            if (ProtocolConstants.MODE_BATCH_WINDOW.equals(transmissionMode)) {
                int batchCount = Math.min(4, totalChunks - currentChunkIdx);
                stream.writeCommand(ProtocolConstants.CMD_UPLOAD_BATCH_CHUNKS);
                stream.writeInt(batchCount);

                for (int b = 0; b < batchCount; b++) {
                    int idx = currentChunkIdx + b;
                    long offset = (long) idx * chunkSize;
                    ChunkPacket packet = ChunkEngine.readChunk(file, item.getRelativePath(), idx, totalChunks, offset, chunkSize);
                    stream.writeChunk(packet);
                }

                String ackCmd = stream.readCommand();
                if (ProtocolConstants.CMD_UPLOAD_BATCH_ACK.equals(ackCmd)) {
                    int lastAcked = stream.readInt();
                    currentChunkIdx = lastAcked + 1;
                } else {
                    throw new IOException("Lỗi nhận Batch ACK từ Server tại chunk " + currentChunkIdx);
                }
            } else {
                long offset = (long) currentChunkIdx * chunkSize;
                ChunkPacket packet = ChunkEngine.readChunk(file, item.getRelativePath(), currentChunkIdx, totalChunks, offset, chunkSize);

                stream.writeCommand(ProtocolConstants.CMD_UPLOAD_CHUNK);
                stream.writeChunk(packet);

                String ackCmd = stream.readCommand();
                if (ProtocolConstants.CMD_UPLOAD_ACK.equals(ackCmd)) {
                    int ackIndex = stream.readInt();
                    currentChunkIdx++;
                } else {
                    throw new IOException("Server báo lỗi tại chunk " + currentChunkIdx);
                }
            }

            long transferred = Math.min(fileSize, (long) currentChunkIdx * chunkSize);
            if (listener != null) {
                listener.onProgress(new SyncProgress(
                        item.getRelativePath(), fileIdx, totalFiles,
                        currentChunkIdx, totalChunks, transferred, fileSize,
                        overallPct, "Đang tải lên " + currentChunkIdx + "/" + totalChunks + " khối (" + transmissionMode + ")..."
                ));
            }
        }

        stream.writeCommand(ProtocolConstants.CMD_UPLOAD_END);
        String uploadResult = stream.readCommand();
        if (ProtocolConstants.CMD_UPLOAD_SUCCESS.equals(uploadResult)) {
            log("[UPLOAD THÀNH CÔNG] Đã tải lên và xác thực mã SHA-256 tệp: " + item.getRelativePath());
        } else {
            throw new IOException("Server kiểm tra toàn vẹn thất bại khi upload: " + item.getRelativePath());
        }
    }

    private void executeDownload(SyncItem item, int fileIdx, int totalFiles, int overallPct) throws Exception {
        File targetFile = PathSecurity.resolveAndValidate(rootDir, item.getRelativePath());
        FileSyncUtil.ensureParentDirectories(targetFile);
        File partFile = FileSyncUtil.getPartFile(targetFile);
        FileSyncUtil.ensureParentDirectories(partFile);

        long resumeOffset = ChunkEngine.getResumeOffset(partFile, chunkSize);
        log("[DOWNLOAD] Đang yêu cầu tải về: " + item.getRelativePath() + " (Resume offset: " + resumeOffset + ")...");

        stream.writeCommand(ProtocolConstants.CMD_DOWNLOAD_START);
        stream.writeUTF(item.getRelativePath());
        stream.writeInt(chunkSize);
        stream.writeLong(resumeOffset);
        stream.writeUTF(transmissionMode);

        String resumeCmd = stream.readCommand();
        if (ProtocolConstants.CMD_ERROR.equals(resumeCmd)) {
            throw new IOException("Server báo lỗi khi yêu cầu download: " + stream.readUTF());
        }

        long fileSize = stream.readLong();
        int totalChunks = stream.readInt();
        int startChunk = stream.readInt();
        long lastModified = stream.readLong();
        String expectedHash = stream.readUTF();

        int currentChunkIdx = startChunk;
        while (currentChunkIdx < totalChunks) {
            String chunkCmd = stream.readCommand();
            if (ProtocolConstants.CMD_DOWNLOAD_BATCH_CHUNKS.equals(chunkCmd)) {
                int batchCount = stream.readInt();
                int lastAcked = currentChunkIdx;
                for (int b = 0; b < batchCount; b++) {
                    ChunkPacket packet = stream.readChunk();
                    boolean verified = ChunkEngine.writeChunkAndVerify(partFile, packet);
                    if (verified) {
                        lastAcked = packet.getChunkIndex();
                    } else {
                        throw new IOException("Chunk download batch " + packet.getChunkIndex() + " bị lỗi mã băm");
                    }
                }
                stream.writeCommand(ProtocolConstants.CMD_DOWNLOAD_BATCH_ACK);
                stream.writeInt(lastAcked);
                currentChunkIdx = lastAcked + 1;
            } else if (ProtocolConstants.CMD_DOWNLOAD_CHUNK.equals(chunkCmd)) {
                ChunkPacket packet = stream.readChunk();
                boolean verified = ChunkEngine.writeChunkAndVerify(partFile, packet);
                if (verified) {
                    stream.writeCommand(ProtocolConstants.CMD_DOWNLOAD_ACK);
                    stream.writeInt(packet.getChunkIndex());
                    currentChunkIdx++;
                } else {
                    throw new IOException("Chunk download " + packet.getChunkIndex() + " bị lỗi mã băm");
                }
            } else {
                throw new IOException("Lệnh tải về không xác định: " + chunkCmd);
            }

            long transferred = Math.min(fileSize, (long) currentChunkIdx * chunkSize);
            if (listener != null) {
                listener.onProgress(new SyncProgress(
                        item.getRelativePath(), fileIdx, totalFiles,
                        currentChunkIdx, totalChunks, transferred, fileSize,
                        overallPct, "Đang tải về " + currentChunkIdx + "/" + totalChunks + " khối..."
                ));
            }
        }

        String finishCmd = stream.readCommand();
        if (ProtocolConstants.CMD_DOWNLOAD_END.equals(finishCmd)) {
            boolean finalized = ChunkEngine.finalizeFile(partFile, targetFile, expectedHash, lastModified);
            if (finalized) {
                stream.writeCommand(ProtocolConstants.CMD_DOWNLOAD_SUCCESS);
                log("[DOWNLOAD THÀNH CÔNG] Đã tải về và ghép hoàn chỉnh tệp: " + item.getRelativePath());
            } else {
                throw new IOException("Kiểm tra mã SHA-256 sau khi tải về thất bại cho tệp: " + item.getRelativePath());
            }
        }
    }

    private void executeConflict(SyncItem item, int fileIdx, int totalFiles, int overallPct) throws Exception {
        log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        log(">>> [PHÁT HIỆN XUNG ĐỘT (CONFLICT)]: Tệp " + item.getRelativePath());
        log(">>> ÁP DỤNG QUY TẮC BẢO TOÀN DỮ LIỆU: Không tự ý ghi đè tệp.");

        File localFile = PathSecurity.resolveAndValidate(rootDir, item.getRelativePath());
        String conflictRenamedRel = item.getConflictRenamedPath() != null ?
                item.getConflictRenamedPath() : FileSyncUtil.generateConflictPath(item.getRelativePath());

        File conflictFile = PathSecurity.resolveAndValidate(rootDir, conflictRenamedRel);
        FileSyncUtil.ensureParentDirectories(conflictFile);

        if (localFile.exists()) {
            boolean renamed = localFile.renameTo(conflictFile);
            if (!renamed) {
                java.nio.file.Files.move(localFile.toPath(), conflictFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            log(">>> Đã sao lưu bản sửa đổi của Client thành: " + conflictFile.getName());
        }

        log(">>> Đang tải bản từ Server về để người dùng tự đối chiếu...");
        executeDownload(item, fileIdx, totalFiles, overallPct);
        log(">>> [XỬ LÝ XUNG ĐỘT HOÀN TẤT] Bạn có thể mở cả 2 tệp để kiểm tra đối chiếu!");
        log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    private void executeDeleteRemote(SyncItem item) throws Exception {
        log("[DELETE REMOTE] Yêu cầu xóa tệp trên Server do Client đã xóa: " + item.getRelativePath());
        stream.writeCommand(ProtocolConstants.CMD_DELETE_FILE);
        stream.writeUTF(item.getRelativePath());
        String result = stream.readCommand();
        if (ProtocolConstants.CMD_DELETE_SUCCESS.equals(result)) {
            log("[DELETE THÀNH CÔNG] Đã xóa tệp trên Server: " + item.getRelativePath());
        }
    }

    private void executeDeleteLocal(SyncItem item) throws Exception {
        log("[DELETE LOCAL] Xóa tệp cục bộ do Server đã xóa: " + item.getRelativePath());
        File localFile = PathSecurity.resolveAndValidate(rootDir, item.getRelativePath());
        if (localFile.exists()) {
            localFile.delete();
            log("[DELETE LOCAL THÀNH CÔNG] Đã xóa tệp cục bộ: " + item.getRelativePath());
        }
    }

    private void log(String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String logEntry = "[" + time + "] " + message;
        System.out.println(logEntry);
        if (listener != null) {
            listener.onLog(logEntry);
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %cB", bytes / Math.pow(1024, exp), unit);
    }
}
