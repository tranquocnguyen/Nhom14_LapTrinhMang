package com.nhom14.filesync.engine;

import com.nhom14.filesync.model.FileMetadata;
import com.nhom14.filesync.model.SyncAction;
import com.nhom14.filesync.model.SyncItem;
import com.nhom14.filesync.model.SyncPlan;
import com.nhom14.filesync.util.FileSyncUtil;

import java.util.*;

/**
 * Động cơ đối chiếu Metadata và Sinh kế hoạch đồng bộ (3-Way Sync Diff Engine)
 * Tích hợp cơ chế phát hiện và giải quyết xung đột (Conflict Resolution)
 * và đồng bộ trạng thái xóa file 2 chiều (Bidirectional Deletion Sync)
 *
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class SyncDiffEngine {

    /**
     * So sánh danh sách metadata Client, Server và Snapshot lịch sử để tạo ra SyncPlan tối ưu
     */
    public static SyncPlan computeSyncPlan(List<FileMetadata> clientList, List<FileMetadata> serverList, Map<String, FileMetadata> snapshot) {
        SyncPlan plan = new SyncPlan();

        Map<String, FileMetadata> serverMap = new HashMap<>();
        for (FileMetadata meta : serverList) {
            serverMap.put(meta.getRelativePath(), meta);
        }

        Map<String, FileMetadata> clientMap = new HashMap<>();
        for (FileMetadata meta : clientList) {
            clientMap.put(meta.getRelativePath(), meta);
        }

        if (snapshot == null) {
            snapshot = Collections.emptyMap();
        }

        Set<String> allPaths = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        allPaths.addAll(clientMap.keySet());
        allPaths.addAll(serverMap.keySet());
        allPaths.addAll(snapshot.keySet());

        for (String path : allPaths) {
            FileMetadata cMeta = clientMap.get(path);
            FileMetadata sMeta = serverMap.get(path);
            FileMetadata baseMeta = snapshot.get(path);

            if (cMeta != null && sMeta != null) {
                // Tệp tồn tại ở cả Client và Server
                if (cMeta.getSha256().equalsIgnoreCase(sMeta.getSha256())) {
                    // Nội dung hoàn toàn trùng khớp
                    plan.addItem(new SyncItem(path, SyncAction.UP_TO_DATE, cMeta, sMeta, "Nội dung khớp mã SHA-256"));
                } else {
                    // Nội dung khác nhau! Kiểm tra với Snapshot trước đó
                    boolean clientChanged = (baseMeta == null) || !cMeta.getSha256().equalsIgnoreCase(baseMeta.getSha256());
                    boolean serverChanged = (baseMeta == null) || !sMeta.getSha256().equalsIgnoreCase(baseMeta.getSha256());

                    if (clientChanged && serverChanged) {
                        // QUY TẮC XỬ LÝ XUNG ĐỘT (Bắt buộc theo yêu cầu đề tài):
                        // Cả hai bên đều bị chỉnh sửa độc lập -> Kích hoạt cơ chế bảo vệ dữ liệu!
                        SyncItem conflictItem = new SyncItem(path, SyncAction.CONFLICT, cMeta, sMeta,
                                "Xung đột: Cả Client và Server đều có sửa đổi mới. Kích hoạt quy tắc bảo vệ dữ liệu!");
                        String conflictPath = FileSyncUtil.generateConflictPath(path);
                        conflictItem.setConflictRenamedPath(conflictPath);
                        plan.addItem(conflictItem);
                    } else if (clientChanged) {
                        plan.addItem(new SyncItem(path, SyncAction.UPLOAD, cMeta, sMeta, "Client có bản cập nhật mới hơn"));
                    } else {
                        plan.addItem(new SyncItem(path, SyncAction.DOWNLOAD, cMeta, sMeta, "Server có bản cập nhật mới hơn"));
                    }
                }
            } else if (cMeta != null && sMeta == null) {
                // Tệp có ở Client nhưng không có ở Server
                if (baseMeta != null && cMeta.getSha256().equalsIgnoreCase(baseMeta.getSha256())) {
                    // Tệp từng tồn tại trong Snapshot, Client không đổi nhưng Server đã xóa
                    plan.addItem(new SyncItem(path, SyncAction.DELETE_LOCAL, cMeta, null, "Server đã xóa tệp này"));
                } else {
                    // Tệp mới được thêm ở Client
                    plan.addItem(new SyncItem(path, SyncAction.UPLOAD, cMeta, null, "Tệp mới ở Client"));
                }
            } else if (cMeta == null && sMeta != null) {
                // Tệp có ở Server nhưng không có ở Client
                if (baseMeta != null && sMeta.getSha256().equalsIgnoreCase(baseMeta.getSha256())) {
                    // Tệp từng tồn tại trong Snapshot, Server không đổi nhưng Client đã xóa
                    plan.addItem(new SyncItem(path, SyncAction.DELETE_REMOTE, null, sMeta, "Client đã xóa tệp này"));
                } else {
                    // Tệp mới được thêm ở Server
                    plan.addItem(new SyncItem(path, SyncAction.DOWNLOAD, null, sMeta, "Tệp mới ở Server"));
                }
            }
        }

        return plan;
    }

    public static SyncPlan computeSyncPlan(List<FileMetadata> clientList, List<FileMetadata> serverList) {
        return computeSyncPlan(clientList, serverList, Collections.emptyMap());
    }
}