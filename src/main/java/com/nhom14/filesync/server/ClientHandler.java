package com.nhom14.filesync.server;

import com.nhom14.filesync.engine.ChunkEngine;
import com.nhom14.filesync.engine.SyncDiffEngine;
import com.nhom14.filesync.engine.SyncStateTracker;
import com.nhom14.filesync.model.*;
import com.nhom14.filesync.protocol.PacketStream;
import com.nhom14.filesync.protocol.ProtocolConstants;
import com.nhom14.filesync.security.PathSecurity;
import com.nhom14.filesync.util.FileLockManager;
import com.nhom14.filesync.util.FileSyncUtil;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Luồng xử lý giao thức đồng bộ cho từng Client kết nối (Đa luồng Socket)
 * Hỗ trợ Stop-and-Wait ACK, Batch ACK, Resume Transfer, và Xử lý Xung đột
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Lý Quốc Thái (MSSV: 2380602014)
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final SyncServer server;
    private final File rootDir;
    private PacketStream stream;
    private final String clientInfo;

    public ClientHandler(Socket socket, SyncServer server, File rootDir) {
        this.socket = socket;
        this.server = server;
        this.rootDir = rootDir;
        this.clientInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void run() {
        try {
            stream = new PacketStream(socket.getInputStream(), socket.getOutputStream());
            server.log("Client kết nối từ: " + clientInfo);

            while (!socket.isClosed()) {
                String cmd;
                try {
                    cmd = stream.readCommand();
                } catch (Exception e) {
                    break; // Client đã ngắt kết nối
                }

                if (ProtocolConstants.CMD_HELLO.equals(cmd)) {
                    handleHello();
                } else if (ProtocolConstants.CMD_SEND_METADATA.equals(cmd)) {
                    handleMetadataAndPlan();
                } else if (ProtocolConstants.CMD_UPLOAD_START.equals(cmd)) {
                    handleUpload();
                } else if (ProtocolConstants.CMD_DOWNLOAD_START.equals(cmd)) {
                    handleDownload();
                } else if (ProtocolConstants.CMD_DELETE_FILE.equals(cmd)) {
                    handleDelete();
                } else if (ProtocolConstants.CMD_BYE.equals(cmd)) {
                    server.log("Client " + clientInfo + " gửi lệnh kết thúc (BYE)");
                    break;
                }
            }
        } catch (Exception e) {
            server.log("Lỗi phiên làm việc với Client " + clientInfo + ": " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void handleHello() throws IOException {
        String clientVersion = stream.readUTF();
        server.log("Bắt tay phiên bản: " + clientVersion + " từ " + clientInfo);
        stream.writeCommand(ProtocolConstants.CMD_HELLO_OK);
        stream.writeUTF(ProtocolConstants.PROTOCOL_VERSION);
    }

    private void handleMetadataAndPlan() throws Exception {
        int count = stream.readInt();
        List<FileMetadata> clientMetadata = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            clientMetadata.add(stream.readMetadata());
        }
        server.log("Nhận " + count + " tệp metadata từ " + clientInfo);

        // Quét thư mục Server
        List<FileMetadata> serverMetadata = FileSyncUtil.scanDirectory(rootDir);
        server.updateFileList(serverMetadata);

        // Nạp snapshot lịch sử trước đó của Server để phát hiện xóa tệp 2 chiều
        Map<String, FileMetadata> snapshot = SyncStateTracker.loadSnapshot(rootDir);

        // So sánh tính toán SyncPlan 3 chiều (Client, Server, Base Snapshot)
        SyncPlan plan = SyncDiffEngine.computeSyncPlan(clientMetadata, serverMetadata, snapshot);
        server.log("Đã lập kế hoạch đồng bộ: " + plan.getItems().size() + " mục cho " + clientInfo);

        // Gửi kế hoạch về cho Client
        stream.writeCommand(ProtocolConstants.CMD_SYNC_PLAN_ITEM);
        stream.writeInt(plan.getItems().size());
        for (SyncItem item : plan.getItems()) {
            stream.writeUTF(item.getRelativePath());
            stream.writeUTF(item.getAction().name());
            stream.writeUTF(item.getConflictRenamedPath() != null ? item.getConflictRenamedPath() : "");
            stream.writeUTF(item.getDetail());

            if (item.getServerMeta() != null) {
                stream.writeBoolean(true);
                stream.writeMetadata(item.getServerMeta());
            } else {
                stream.writeBoolean(false);
            }
        }
    }

    private void handleUpload() throws Exception {
        String relativePath = stream.readUTF();
        long fileSize = stream.readLong();
        int totalChunks = stream.readInt();
        String expectedHash = stream.readUTF();
        int chunkSize = stream.readInt();
        long lastModified = stream.readLong();
        String transMode = stream.readUTF();

        File targetFile = PathSecurity.resolveAndValidate(rootDir, relativePath);
        FileSyncUtil.ensureParentDirectories(targetFile);
        File partFile = FileSyncUtil.getPartFile(targetFile);
        FileSyncUtil.ensureParentDirectories(partFile);

        FileLockManager.lockForWrite(relativePath);
        try {
            long resumeOffset = ChunkEngine.getResumeOffset(partFile, chunkSize);
            int startChunk = (int) (resumeOffset / chunkSize);
            server.log("Upload file: " + relativePath + " (" + (ProtocolConstants.MODE_BATCH_WINDOW.equals(transMode) ? "Batch Window" : "Stop-and-Wait") + ", Resume từ chunk: " + startChunk + "/" + totalChunks + ")");

            stream.writeCommand(ProtocolConstants.CMD_UPLOAD_RESUME);
            stream.writeLong(resumeOffset);
            stream.writeInt(startChunk);

            int currentChunkIdx = startChunk;
            while (currentChunkIdx < totalChunks) {
                String cmd = stream.readCommand();
                if (ProtocolConstants.CMD_UPLOAD_CHUNK.equals(cmd)) {
                    ChunkPacket packet = stream.readChunk();
                    boolean verified = ChunkEngine.writeChunkAndVerify(partFile, packet);
                    if (verified) {
                        stream.writeCommand(ProtocolConstants.CMD_UPLOAD_ACK);
                        stream.writeInt(packet.getChunkIndex());
                        currentChunkIdx++;
                    } else {
                        stream.writeCommand(ProtocolConstants.CMD_ERROR);
                        stream.writeUTF("Chunk " + packet.getChunkIndex() + " bị lỗi mã băm SHA-256");
                        return;
                    }
                } else if (ProtocolConstants.CMD_UPLOAD_BATCH_CHUNKS.equals(cmd)) {
                    int batchSize = stream.readInt();
                    boolean allOk = true;
                    int lastAcked = currentChunkIdx;
                    for (int b = 0; b < batchSize; b++) {
                        ChunkPacket packet = stream.readChunk();
                        if (allOk) {
                            boolean verified = ChunkEngine.writeChunkAndVerify(partFile, packet);
                            if (verified) {
                                lastAcked = packet.getChunkIndex();
                            } else {
                                allOk = false;
                            }
                        }
                    }

                    if (allOk) {
                        stream.writeCommand(ProtocolConstants.CMD_UPLOAD_BATCH_ACK);
                        stream.writeInt(lastAcked);
                        currentChunkIdx = lastAcked + 1;
                    } else {
                        stream.writeCommand(ProtocolConstants.CMD_ERROR);
                        stream.writeUTF("Lỗi xác thực khối batch tại chunk " + lastAcked);
                        return;
                    }
                } else {
                    throw new IOException("Lệnh không mong đợi: " + cmd);
                }
            }

            String finishCmd = stream.readCommand();
            if (ProtocolConstants.CMD_UPLOAD_END.equals(finishCmd)) {
                boolean success = ChunkEngine.finalizeFile(partFile, targetFile, expectedHash, lastModified);
                if (success) {
                    stream.writeCommand(ProtocolConstants.CMD_UPLOAD_SUCCESS);
                    server.log("Đã nhận và ghép hoàn chỉnh tệp: " + relativePath);
                    List<FileMetadata> updatedFiles = FileSyncUtil.scanDirectory(rootDir);
                    server.updateFileList(updatedFiles);
                    SyncStateTracker.saveSnapshot(rootDir, updatedFiles);
                } else {
                    stream.writeCommand(ProtocolConstants.CMD_ERROR);
                    stream.writeUTF("Kiểm tra toàn vẹn SHA-256 sau khi ghép file thất bại!");
                }
            }
        } finally {
            FileLockManager.unlockForWrite(relativePath);
        }
    }

    private void handleDownload() throws Exception {
        String relativePath = stream.readUTF();
        int chunkSize = stream.readInt();
        long clientResumeOffset = stream.readLong();
        String transMode = stream.readUTF();

        File file = PathSecurity.resolveAndValidate(rootDir, relativePath);
        if (!file.exists() || !file.isFile()) {
            stream.writeCommand(ProtocolConstants.CMD_ERROR);
            stream.writeUTF("Tệp tin không tồn tại trên Server: " + relativePath);
            return;
        }

        FileLockManager.lockForRead(relativePath);
        try {
            long fileSize = file.length();
            long lastModified = file.lastModified();
            String fullHash = com.nhom14.filesync.util.HashUtil.calculateSha256(file);
            int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);
            int startChunk = (int) (clientResumeOffset / chunkSize);

            stream.writeCommand(ProtocolConstants.CMD_DOWNLOAD_RESUME);
            stream.writeLong(fileSize);
            stream.writeInt(totalChunks);
            stream.writeInt(startChunk);
            stream.writeLong(lastModified);
            stream.writeUTF(fullHash);

            server.log("Gửi file " + relativePath + " cho " + clientInfo + " (Mode: " + transMode + ", từ chunk " + startChunk + "/" + totalChunks + ")");

            int currentChunkIdx = startChunk;
            while (currentChunkIdx < totalChunks) {
                if (ProtocolConstants.MODE_BATCH_WINDOW.equals(transMode)) {
                    int batchCount = Math.min(4, totalChunks - currentChunkIdx);
                    stream.writeCommand(ProtocolConstants.CMD_DOWNLOAD_BATCH_CHUNKS);
                    stream.writeInt(batchCount);
                    for (int b = 0; b < batchCount; b++) {
                        int idx = currentChunkIdx + b;
                        long offset = (long) idx * chunkSize;
                        ChunkPacket packet = ChunkEngine.readChunk(file, relativePath, idx, totalChunks, offset, chunkSize);
                        stream.writeChunk(packet);
                    }
                    String ackCmd = stream.readCommand();
                    if (!ProtocolConstants.CMD_DOWNLOAD_BATCH_ACK.equals(ackCmd)) {
                        throw new IOException("Lỗi nhận Batch ACK từ Client tại chunk " + currentChunkIdx);
                    }
                    int ackIndex = stream.readInt();
                    currentChunkIdx = ackIndex + 1;
                } else {
                    long currentOffset = (long) currentChunkIdx * chunkSize;
                    ChunkPacket packet = ChunkEngine.readChunk(file, relativePath, currentChunkIdx, totalChunks, currentOffset, chunkSize);
                    stream.writeCommand(ProtocolConstants.CMD_DOWNLOAD_CHUNK);
                    stream.writeChunk(packet);

                    String ackCmd = stream.readCommand();
                    if (!ProtocolConstants.CMD_DOWNLOAD_ACK.equals(ackCmd)) {
                        throw new IOException("Client phản hồi lỗi ở chunk " + currentChunkIdx);
                    }
                    int ackIndex = stream.readInt();
                    currentChunkIdx++;
                }
            }

            stream.writeCommand(ProtocolConstants.CMD_DOWNLOAD_END);
            String clientResult = stream.readCommand();
            server.log("Download tệp " + relativePath + " hoàn tất cho " + clientInfo);
        } finally {
            FileLockManager.unlockForRead(relativePath);
        }
    }

    private void handleDelete() throws Exception {
        String relativePath = stream.readUTF();
        File targetFile = PathSecurity.resolveAndValidate(rootDir, relativePath);
        FileLockManager.lockForWrite(relativePath);
        try {
            if (targetFile.exists()) {
                targetFile.delete();
                server.log("Đã đồng bộ xóa tệp: " + relativePath);
                List<FileMetadata> updatedFiles = FileSyncUtil.scanDirectory(rootDir);
                server.updateFileList(updatedFiles);
                SyncStateTracker.saveSnapshot(rootDir, updatedFiles);
            }
            stream.writeCommand(ProtocolConstants.CMD_DELETE_SUCCESS);
        } finally {
            FileLockManager.unlockForWrite(relativePath);
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {}
        server.removeClient(this);
        server.log("Client đã ngắt kết nối: " + clientInfo);
    }

    public String getClientInfo() {
        return clientInfo;
    }
}
