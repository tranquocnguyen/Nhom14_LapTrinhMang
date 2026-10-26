package com.nhom14.filesync.client;

import com.nhom14.filesync.model.SyncPlan;
import com.nhom14.filesync.model.SyncProgress;

/**
 * Interface nhận thông báo tiến trình và sự kiện từ SyncClient
 */
public interface ClientListener {
    void onConnected(String host, int port, boolean ssl);
    void onDisconnected();
    void onLog(String message);
    void onProgress(SyncProgress progress);
    void onSyncPlanReady(SyncPlan plan);
    void onSyncFinished(boolean success, String summary);
}