package com.nhom14.filesync.server;

import com.nhom14.filesync.model.FileMetadata;
import com.nhom14.filesync.security.SslContextFactory;
import com.nhom14.filesync.util.FileSyncUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Máy chủ đồng bộ hóa tệp tin đa luồng hỗ trợ cả TCP thường và SSL/TLS
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class SyncServer {

    private final int port;
    private final boolean useSsl;
    private final File rootDir;
    private final ServerListener listener;

    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private ExecutorService threadPool;
    private final List<ClientHandler> activeClients = new CopyOnWriteArrayList<>();
    private List<FileMetadata> currentFiles = new ArrayList<>();
    private final List<String> recentLogs = new CopyOnWriteArrayList<>();
    private WebDashboardServer webDashboard;

    public SyncServer(int port, boolean useSsl, File rootDir, ServerListener listener) {
        this.port = port;
        this.useSsl = useSsl;
        this.rootDir = rootDir;
        this.listener = listener;
    }

    public void start() throws Exception {
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        if (useSsl) {
            SSLContext sslContext = SslContextFactory.createServerSslContext();
            SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) ssf.createServerSocket(port);
            sslServerSocket.setEnabledProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
            this.serverSocket = sslServerSocket;
        } else {
            this.serverSocket = new ServerSocket(port);
        }

        running = true;
        threadPool = Executors.newCachedThreadPool();

        log("Server khởi động thành công trên cổng " + port + (useSsl ? " [BẢO MẬT SSL/TLS]" : " [TCP THƯỜNG]"));
        log("Thư mục lưu trữ đồng bộ: " + rootDir.getAbsolutePath());

        // Khởi động Web Dashboard tiện lợi
        try {
            webDashboard = new WebDashboardServer(this, 8088);
            webDashboard.start();
            log("Web Dashboard giám sát sẵn sàng tại: http://localhost:8088");
        } catch (Exception e) {
            log("Không thể bật Web Dashboard cổng 8088: " + e.getMessage());
        }

        // Quét ban đầu
        updateFileList(FileSyncUtil.scanDirectory(rootDir));

        if (listener != null) {
            listener.onServerStarted(port, useSsl, rootDir.getAbsolutePath());
        }

        // Luồng lắng nghe kết nối
        threadPool.execute(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket, this, rootDir);
                    activeClients.add(handler);
                    if (listener != null) {
                        listener.onClientConnected(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                    }
                    threadPool.execute(handler);
                } catch (Exception e) {
                    if (!running) break;
                    log("Lỗi tiếp nhận kết nối: " + e.getMessage());
                }
            }
        });
    }

    public void stop() {
        running = false;
        try {
            for (ClientHandler handler : activeClients) {
                handler.closeConnection();
            }
            activeClients.clear();
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (webDashboard != null) {
                webDashboard.stop();
            }
            if (threadPool != null) {
                threadPool.shutdownNow();
            }
        } catch (Exception ignored) {}

        log("Server đã dừng hoạt động.");
        if (listener != null) {
            listener.onServerStopped();
        }
    }

    public void removeClient(ClientHandler handler) {
        activeClients.remove(handler);
        if (listener != null) {
            listener.onClientDisconnected(handler.getClientInfo(), 0);
        }
    }

    public synchronized void updateFileList(List<FileMetadata> files) {
        this.currentFiles = files;
        if (listener != null) {
            listener.onFileListUpdated(files);
        }
    }

    public void log(String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String logEntry = "[" + time + "] " + message;
        recentLogs.add(logEntry);
        if (recentLogs.size() > 500) {
            recentLogs.remove(0);
        }
        System.out.println(logEntry);
        if (listener != null) {
            listener.onLog(logEntry);
        }
    }

    public boolean isRunning() { return running; }
    public int getPort() { return port; }
    public boolean isUseSsl() { return useSsl; }
    public File getRootDir() { return rootDir; }
    public List<ClientHandler> getActiveClients() { return activeClients; }
    public List<FileMetadata> getCurrentFiles() { return currentFiles; }
    public List<String> getRecentLogs() { return recentLogs; }
}