package com.nhom14.filesync.engine;

import com.nhom14.filesync.model.ChunkPacket;
import com.nhom14.filesync.util.FileSyncUtil;
import com.nhom14.filesync.util.HashUtil;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Động cơ chia nhỏ khối (Chunking Engine) và Tiếp tục truyền tải (Resume Transfer)
 * Hỗ trợ truyền tải dữ liệu lớn thời gian thực
 *
 * @author Lý Quốc Thái (MSSV: 2380602014)
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class ChunkEngine {

    /**
     * Đọc một Chunk dữ liệu từ vị trí offset trong file
     */
    public static ChunkPacket readChunk(File file, String relativePath, int chunkIndex, int totalChunks, long offset, int chunkSize) throws Exception {
        byte[] buffer = new byte[chunkSize];
        int bytesRead;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(offset);
            bytesRead = raf.read(buffer);
        }

        if (bytesRead <= 0) {
            return new ChunkPacket(relativePath, chunkIndex, totalChunks, offset, 0, new byte[0], "");
        }

        String chunkHash = HashUtil.calculateSha256(buffer, 0, bytesRead);
        return new ChunkPacket(relativePath, chunkIndex, totalChunks, offset, bytesRead, buffer, chunkHash);
    }

    /**
     * Ghi một Chunk dữ liệu vào file tạm (.part) và kiểm tra mã băm khối
     */
    public static boolean writeChunkAndVerify(File partFile, ChunkPacket packet) throws Exception {
        // Đảm bảo thư mục cha tồn tại
        FileSyncUtil.ensureParentDirectories(partFile);

        // Kiểm tra tính toàn vẹn của chunk nhận được
        String computedHash = HashUtil.calculateSha256(packet.getData(), 0, packet.getLength());
        if (!computedHash.equalsIgnoreCase(packet.getChunkHash())) {
            System.err.println("Mã băm chunk " + packet.getChunkIndex() + " không khớp!");
            return false;
        }

        // Ghi dữ liệu vào vị trí offset của file tạm
        try (RandomAccessFile raf = new RandomAccessFile(partFile, "rw")) {
            raf.seek(packet.getOffset());
            raf.write(packet.getData(), 0, packet.getLength());
        }
        return true;
    }

    /**
     * Cơ chế Tiếp tục truyền tải (Resume transfer):
     * Kiểm tra kích thước file tạm .part đã nhận được để tính số chunk đã có
     */
    public static long getResumeOffset(File partFile, int chunkSize) {
        if (!partFile.exists() || !partFile.isFile()) {
            return 0;
        }
        long length = partFile.length();
        // Căn chỉnh theo kích thước chunk nguyên vẹn
        long validChunks = length / chunkSize;
        return validChunks * chunkSize;
    }

    /**
     * Hoàn tất ghép file: Đổi tên file tạm .part thành file chính thức,
     * kiểm tra SHA-256 toàn bộ và khôi phục mốc thời gian lastModified gốc
     */
    public static boolean finalizeFile(File partFile, File targetFile, String expectedFullSha256, long lastModified) {
        if (!partFile.exists()) {
            return false;
        }

        // Kiểm tra mã băm SHA-256 của toàn bộ file hoàn chỉnh
        String fullHash = HashUtil.calculateSha256(partFile);
        if (!fullHash.equalsIgnoreCase(expectedFullSha256)) {
            System.err.println("Ghép file thất bại: SHA-256 tổng thể không khớp! Mong đợi: " + expectedFullSha256 + ", Thực tế: " + fullHash);
            partFile.delete();
            return false;
        }

        // Đảm bảo thư mục đích tồn tại
        FileSyncUtil.ensureParentDirectories(targetFile);

        // Xóa file đích cũ nếu đã tồn tại trước khi đổi tên
        if (targetFile.exists()) {
            targetFile.delete();
        }

        boolean renamed = partFile.renameTo(targetFile);
        if (!renamed) {
            try {
                java.nio.file.Files.move(partFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                renamed = true;
            } catch (Exception e) {
                System.err.println("Lỗi đổi tên file tạm: " + e.getMessage());
                return false;
            }
        }

        if (renamed && lastModified > 0) {
            targetFile.setLastModified(lastModified);
        }
        return renamed;
    }

    public static boolean finalizeFile(File partFile, File targetFile, String expectedFullSha256) {
        return finalizeFile(partFile, targetFile, expectedFullSha256, 0);
    }
}