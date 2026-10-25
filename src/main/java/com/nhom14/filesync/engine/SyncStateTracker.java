package com.nhom14.filesync.engine;

import com.nhom14.filesync.model.FileMetadata;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trình theo dõi trạng thái lịch sử đồng bộ (Sync State Snapshot)
 * Cho phép giải quyết bài toán đồng bộ xóa tệp 2 chiều (Bidirectional Deletion Sync)
 *
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class SyncStateTracker {

    private static final String STATE_FILE_NAME = ".sync_state.dat";

    /**
     * Nạp ảnh chụp trạng thái (Snapshot) lần đồng bộ trước từ thư mục
     */
    public static Map<String, FileMetadata> loadSnapshot(File rootDir) {
        Map<String, FileMetadata> snapshot = new HashMap<>();
        File stateFile = new File(rootDir, STATE_FILE_NAME);
        if (!stateFile.exists() || !stateFile.isFile()) {
            return snapshot;
        }

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(stateFile)))) {
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                String path = in.readUTF();
                String name = in.readUTF();
                long size = in.readLong();
                long lastModified = in.readLong();
                String sha256 = in.readUTF();
                snapshot.put(path, new FileMetadata(path, name, size, lastModified, sha256));
            }
        } catch (Exception e) {
            System.err.println("Cảnh báo: Không thể đọc snapshot cũ: " + e.getMessage());
        }
        return snapshot;
    }

    /**
     * Lưu ảnh chụp trạng thái sau khi hoàn tất phiên đồng bộ thành công
     */
    public static void saveSnapshot(File rootDir, List<FileMetadata> currentFiles) {
        File stateFile = new File(rootDir, STATE_FILE_NAME);
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(stateFile)))) {
            out.writeInt(currentFiles.size());
            for (FileMetadata f : currentFiles) {
                out.writeUTF(f.getRelativePath());
                out.writeUTF(f.getFileName());
                out.writeLong(f.getFileSize());
                out.writeLong(f.getLastModified());
                out.writeUTF(f.getSha256());
            }
            out.flush();
        } catch (Exception e) {
            System.err.println("Lỗi ghi snapshot trạng thái: " + e.getMessage());
        }
    }
}