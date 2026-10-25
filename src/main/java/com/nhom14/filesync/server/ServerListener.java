package com.nhom14.filesync.server;

import com.nhom14.filesync.model.FileMetadata;
import java.util.List;

/**
 * Interface lắng nghe sự kiện từ Server để cập nhật giao diện GUI và Web
 */
public interface ServerListener {
    void onServerStarted(int port, boolean ssl, String rootDir);
    void onServerStopped();
    void onClientConnected(String clientIp, int port);
    void onClientDisconnected(String clientIp, int port);
    void onLog(String message);
    void onFileListUpdated(List<FileMetadata> files);
}