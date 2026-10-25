package com.nhom14.filesync.model;

import java.io.Serializable;

/**
 * Mục tác vụ cần thực hiện trong kế hoạch đồng bộ
 */
public class SyncItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String relativePath;
    private SyncAction action;
    private FileMetadata clientMeta;
    private FileMetadata serverMeta;
    private String conflictRenamedPath; // Tên mới nếu giải quyết xung đột (giữ cả hai bản)
    private String detail;

    public SyncItem() {
    }

    public SyncItem(String relativePath, SyncAction action, FileMetadata clientMeta, FileMetadata serverMeta, String detail) {
        this.relativePath = relativePath;
        this.action = action;
        this.clientMeta = clientMeta;
        this.serverMeta = serverMeta;
        this.detail = detail;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public SyncAction getAction() {
        return action;
    }

    public void setAction(SyncAction action) {
        this.action = action;
    }

    public FileMetadata getClientMeta() {
        return clientMeta;
    }

    public void setClientMeta(FileMetadata clientMeta) {
        this.clientMeta = clientMeta;
    }

    public FileMetadata getServerMeta() {
        return serverMeta;
    }

    public void setServerMeta(FileMetadata serverMeta) {
        this.serverMeta = serverMeta;
    }

    public String getConflictRenamedPath() {
        return conflictRenamedPath;
    }

    public void setConflictRenamedPath(String conflictRenamedPath) {
        this.conflictRenamedPath = conflictRenamedPath;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "[" + action + "] " + relativePath + " (" + detail + ")";
    }
}