package com.nhom14.filesync.model;

/**
 * Định nghĩa các hành động đồng bộ tệp tin
 */
public enum SyncAction {
    UP_TO_DATE("Đã đồng bộ", "Tệp tin hai bên hoàn toàn khớp mã băm SHA-256"),
    UPLOAD("Tải lên Server", "Tệp tin mới hoặc được sửa đổi từ Client"),
    DOWNLOAD("Tải về Client", "Tệp tin mới hoặc có phiên bản mới hơn trên Server"),
    DELETE_REMOTE("Xóa trên Server", "Tệp tin đã bị xóa ở Client và cần đồng bộ xóa trên Server"),
    DELETE_LOCAL("Xóa ở Client", "Tệp tin đã bị xóa trên Server và cần đồng bộ xóa ở Client"),
    CONFLICT("XUNG ĐỘT (Conflict)", "Cả Client và Server đều sửa đổi cùng một tệp khi mất kết nối");

    private final String displayName;
    private final String description;

    SyncAction(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}