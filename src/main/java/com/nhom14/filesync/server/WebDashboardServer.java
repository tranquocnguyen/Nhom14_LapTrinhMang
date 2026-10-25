package com.nhom14.filesync.server;

import com.nhom14.filesync.model.FileMetadata;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Máy chủ Web Monitoring Dashboard thế hệ mới (NextGen Web Dashboard)
 * Giao diện hiện đại, xu hướng trẻ trung, chuẩn công nghệ 2026
 * Hỗ trợ lọc tìm kiếm tệp tin tức thì, bảng thống kê thời gian thực và nhật ký Cyber Terminal
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 */
public class WebDashboardServer {

    private final SyncServer server;
    private final int webPort;
    private HttpServer httpServer;

    public WebDashboardServer(SyncServer server, int webPort) {
        this.server = server;
        this.webPort = webPort;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(webPort), 0);
        httpServer.createContext("/", new DashboardHandler());
        httpServer.setExecutor(null);
        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    private class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<FileMetadata> files = server.getCurrentFiles();
            List<String> logs = server.getRecentLogs();
            int clientCount = server.getActiveClients().size();

            StringBuilder fileRows = new StringBuilder();
            long totalBytes = 0;
            for (FileMetadata f : files) {
                totalBytes += f.getFileSize();
                String ext = getExtension(f.getFileName()).toUpperCase();
                String badgeColor = switch (ext) {
                    case "BIN", "DAT" -> "bg-purple-500/20 text-purple-300 border-purple-500/30";
                    case "TXT", "MD" -> "bg-blue-500/20 text-blue-300 border-blue-500/30";
                    case "PDF", "DOCX" -> "bg-rose-500/20 text-rose-300 border-rose-500/30";
                    case "PNG", "JPG" -> "bg-amber-500/20 text-amber-300 border-amber-500/30";
                    default -> "bg-slate-500/20 text-slate-300 border-slate-500/30";
                };

                fileRows.append("<tr class='border-b border-slate-800 hover:bg-slate-800/40 transition-colors file-row'>")
                        .append("<td class='p-3.5 flex items-center space-x-3'>")
                        .append("<span class='px-2 py-0.5 rounded text-[10px] font-bold border ").append(badgeColor).append("'>").append(ext.isEmpty() ? "FILE" : ext).append("</span>")
                        .append("<span class='font-medium text-slate-200 file-name'>").append(f.getFileName()).append("</span>")
                        .append("</td>")
                        .append("<td class='p-3.5 text-slate-400 font-mono text-xs'>").append(f.getRelativePath()).append("</td>")
                        .append("<td class='p-3.5 text-emerald-400 font-semibold text-sm'>").append(f.getFormattedSize()).append("</td>")
                        .append("<td class='p-3.5 text-slate-400 text-xs'>").append(f.getFormattedLastModified()).append("</td>")
                        .append("<td class='p-3.5 font-mono text-xs text-indigo-400'>")
                        .append("<div class='flex items-center space-x-2'>")
                        .append("<span class='truncate max-w-[140px]' title='").append(f.getSha256()).append("'>").append(f.getSha256()).append("</span>")
                        .append("<button onclick=\"copyHash('").append(f.getSha256()).append("')\" class='text-[10px] px-2 py-0.5 rounded bg-slate-800 hover:bg-indigo-600 text-slate-300 hover:text-white transition-all'>Sao chép</button>")
                        .append("</div>")
                        .append("</td></tr>");
            }

            StringBuilder logEntries = new StringBuilder();
            int start = Math.max(0, logs.size() - 30);
            for (int i = logs.size() - 1; i >= start; i--) {
                String log = logs.get(i);
                String color = "text-slate-300";
                if (log.contains("THÀNH CÔNG") || log.contains("100%")) color = "text-emerald-400 font-semibold";
                else if (log.contains("XUNG ĐỘT") || log.contains("CONFLICT")) color = "text-rose-400 font-bold";
                else if (log.contains("UPLOAD") || log.contains("DOWNLOAD")) color = "text-sky-300";
                else if (log.contains("RESUME")) color = "text-amber-300";

                logEntries.append("<div class='py-0.5 leading-relaxed font-mono text-xs ").append(color).append("'>")
                        .append("<span class='text-slate-600 mr-2'>$</span>").append(escapeHtml(log)).append("</div>");
            }

            String formattedStorage = formatBytes(totalBytes);
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="vi" class="dark">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>SyncPulse 14 • Cloud File Synchronization Dashboard</title>
                    <script src="https://cdn.tailwindcss.com"></script>
                    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">
                    <meta http-equiv="refresh" content="6">
                    <style>
                        body { font-family: 'Plus Jakarta Sans', sans-serif; }
                        code, pre { font-family: 'JetBrains Mono', monospace; }
                        .glass-panel { background: rgba(15, 23, 42, 0.75); backdrop-filter: blur(12px); border: 1px solid rgba(51, 65, 85, 0.6); }
                        .glass-glow { box-shadow: 0 0 30px -10px rgba(99, 102, 241, 0.3); }
                    </style>
                </head>
                <body class="bg-[#0B0F19] text-slate-100 min-h-screen selection:bg-indigo-500 selection:text-white pb-12">

                    <!-- Top Announcement Bar -->
                    <div class="bg-gradient-to-r from-indigo-900/60 via-purple-900/60 to-slate-900 px-4 py-2 text-center text-xs text-indigo-200 border-b border-indigo-500/20 flex justify-center items-center space-x-2">
                        <span class="inline-block px-2 py-0.5 rounded bg-indigo-500/20 text-indigo-300 font-bold text-[10px]">HUTECH 23DTHB1</span>
                        <span>Đồ Án Cuối Kỳ: <strong>Ứng Dụng Đồng Bộ Tệp Tin (Client-Server File Synchronization)</strong></span>
                        <span class="text-indigo-400">• Nhóm 14</span>
                    </div>

                    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-6 space-y-6">

                        <!-- Header Bar -->
                        <header class="glass-panel p-6 rounded-2xl flex flex-col md:flex-row justify-between items-start md:items-center gap-4 glass-glow">
                            <div class="flex items-center space-x-4">
                                <div class="w-12 h-12 rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 flex items-center justify-center text-2xl shadow-lg shadow-indigo-500/30">
                                    ⚡
                                </div>
                                <div>
                                    <div class="flex items-center space-x-2">
                                        <h1 class="text-2xl font-extrabold tracking-tight bg-gradient-to-r from-white via-slate-100 to-slate-300 bg-clip-text text-transparent">
                                            SyncPulse 14 Studio
                                        </h1>
                                        <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">v1.0 Pro</span>
                                    </div>
                                    <p class="text-xs text-slate-400 mt-1">Multi-threaded TCP &bull; TLSv1.3 &bull; SHA-256 Checksum &bull; Batch Window ACK</p>
                                </div>
                            </div>

                            <div class="flex flex-wrap items-center gap-3">
                                <div class="flex items-center space-x-2 px-3.5 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-semibold">
                                    <span class="relative flex h-2 w-2">
                                        <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                                        <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
                                    </span>
                                    <span>CLUSTER ONLINE (CỔNG %d)</span>
                                </div>
                                <span class="px-3.5 py-1.5 rounded-xl bg-purple-500/10 border border-purple-500/30 text-purple-300 text-xs font-semibold">
                                    🔒 %s
                                </span>
                            </div>
                        </header>

                        <!-- Key Metrics Grid -->
                        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                            <!-- Card 1 -->
                            <div class="glass-panel p-5 rounded-2xl border border-slate-800 hover:border-indigo-500/50 transition-all">
                                <div class="flex justify-between items-center text-slate-400">
                                    <span class="text-xs font-bold uppercase tracking-wider">Tổng Tệp Server</span>
                                    <span class="text-lg">📁</span>
                                </div>
                                <div class="text-3xl font-extrabold text-white mt-3">%d <span class="text-xs font-normal text-slate-400">tệp tin</span></div>
                                <div class="text-[11px] text-emerald-400 font-medium mt-2 flex items-center">
                                    <span>✔ Quét tự động NIO.2</span>
                                </div>
                            </div>

                            <!-- Card 2 -->
                            <div class="glass-panel p-5 rounded-2xl border border-slate-800 hover:border-indigo-500/50 transition-all">
                                <div class="flex justify-between items-center text-slate-400">
                                    <span class="text-xs font-bold uppercase tracking-wider">Dung Lượng Lưu Trữ</span>
                                    <span class="text-lg">💾</span>
                                </div>
                                <div class="text-3xl font-extrabold text-indigo-400 mt-3">%s</div>
                                <div class="text-[11px] text-slate-400 font-medium mt-2">
                                    <span>Chunk size: 64KB - 256KB</span>
                                </div>
                            </div>

                            <!-- Card 3 -->
                            <div class="glass-panel p-5 rounded-2xl border border-slate-800 hover:border-indigo-500/50 transition-all">
                                <div class="flex justify-between items-center text-slate-400">
                                    <span class="text-xs font-bold uppercase tracking-wider">Clients Đang Nối</span>
                                    <span class="text-lg">👥</span>
                                </div>
                                <div class="text-3xl font-extrabold text-cyan-400 mt-3">%d <span class="text-xs font-normal text-slate-400">phiên</span></div>
                                <div class="text-[11px] text-cyan-300 font-medium mt-2">
                                    <span>FileLockManager đa luồng</span>
                                </div>
                            </div>

                            <!-- Card 4 -->
                            <div class="glass-panel p-5 rounded-2xl border border-slate-800 hover:border-indigo-500/50 transition-all">
                                <div class="flex justify-between items-center text-slate-400">
                                    <span class="text-xs font-bold uppercase tracking-wider">Cơ Chế Bảo Vệ</span>
                                    <span class="text-lg">🛡️</span>
                                </div>
                                <div class="text-xl font-extrabold text-purple-400 mt-3">CONFLICT-SAFE</div>
                                <div class="text-[11px] text-purple-300 font-medium mt-2">
                                    <span>Không ghi đè &bull; Chống Path Traversal</span>
                                </div>
                            </div>
                        </div>

                        <!-- Main Content Layout -->
                        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

                            <!-- Left: File Listing Table (2 cols) -->
                            <div class="lg:col-span-2 glass-panel rounded-2xl p-6 border border-slate-800 space-y-4">
                                <div class="flex flex-col sm:flex-row justify-between sm:items-center gap-3">
                                    <div>
                                        <h2 class="text-lg font-bold text-white flex items-center space-x-2">
                                            <span>📂 Danh Sách Tệp Tin Server</span>
                                            <span class="text-xs px-2 py-0.5 rounded-full bg-slate-800 text-slate-300">%d mục</span>
                                        </h2>
                                        <p class="text-xs text-slate-400 mt-0.5">Tất cả tệp đang được quản trị và đồng bộ trên máy chủ</p>
                                    </div>
                                    <div>
                                        <input type="text" id="searchInput" onkeyup="filterFiles()" placeholder="🔍 Tìm kiếm file..."
                                               class="w-full sm:w-48 px-3 py-1.5 rounded-xl bg-slate-900 border border-slate-700 text-xs text-slate-200 placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition-colors">
                                    </div>
                                </div>

                                <div class="overflow-x-auto rounded-xl border border-slate-800">
                                    <table class="w-full text-left text-sm" id="filesTable">
                                        <thead class="bg-slate-900/90 text-slate-400 text-xs font-semibold uppercase tracking-wider border-b border-slate-800">
                                            <tr>
                                                <th class="p-3">Tên Tệp</th>
                                                <th class="p-3">Đường Dẫn</th>
                                                <th class="p-3">Kích Thước</th>
                                                <th class="p-3">Sửa Đổi Lần Cuối</th>
                                                <th class="p-3">Mã Băm SHA-256</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            %s
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <!-- Right: Terminal Live Logs (1 col) -->
                            <div class="glass-panel rounded-2xl p-6 border border-slate-800 flex flex-col justify-between space-y-4">
                                <div>
                                    <div class="flex justify-between items-center">
                                        <h2 class="text-lg font-bold text-white flex items-center space-x-2">
                                            <span>⚡ Nhật Ký Hoạt Động</span>
                                        </h2>
                                        <span class="text-[10px] text-slate-400 bg-slate-800 px-2 py-0.5 rounded">Real-time</span>
                                    </div>
                                    <p class="text-xs text-slate-400 mt-0.5">Lịch sử sự kiện, kết nối và xác thực từng chunk</p>
                                </div>

                                <div class="bg-[#050811] p-4 rounded-xl border border-slate-800/80 font-mono text-xs overflow-y-auto max-h-[460px] space-y-1 shadow-inner" id="logBox">
                                    %s
                                </div>

                                <div class="text-right">
                                    <button onclick="window.location.reload()" class="px-3 py-1.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold transition-all">
                                        🔄 Làm mới dữ liệu
                                    </button>
                                </div>
                            </div>

                        </div>

                        <!-- Team Members Footer Section -->
                        <div class="glass-panel p-6 rounded-2xl border border-slate-800">
                            <div class="text-center max-w-xl mx-auto mb-6">
                                <h3 class="text-base font-bold text-white">Thành Viên Nhóm Thực Hiện Đề Tài (Nhóm 14)</h3>
                                <p class="text-xs text-slate-400 mt-1">Lớp: 23DTHB1 &bull; Học phần: Lập Trình Mạng Máy Tính &bull; Khoa Công Nghệ Thông Tin - HUTECH</p>
                            </div>

                            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                                <!-- Member 1 -->
                                <div class="p-4 rounded-xl bg-slate-900/60 border border-slate-800 text-center hover:border-indigo-500/40 transition-colors">
                                    <div class="w-12 h-12 rounded-full bg-gradient-to-tr from-amber-500 to-rose-500 text-white font-bold flex items-center justify-center mx-auto text-sm shadow">
                                        QN
                                    </div>
                                    <div class="font-bold text-sm text-slate-200 mt-2.5">Trần Quốc Nguyễn</div>
                                    <div class="text-xs font-mono text-indigo-400">MSSV: 2380601506</div>
                                    <div class="mt-2 text-[11px] text-amber-300 font-semibold bg-amber-500/10 py-1 px-2 rounded-lg">Core Net</div>
                                </div>

                                <!-- Member 2 -->
                                <div class="p-4 rounded-xl bg-slate-900/60 border border-slate-800 text-center hover:border-indigo-500/40 transition-colors">
                                    <div class="w-12 h-12 rounded-full bg-gradient-to-tr from-pink-500 to-purple-500 text-white font-bold flex items-center justify-center mx-auto text-sm shadow">
                                        QH
                                    </div>
                                    <div class="font-bold text-sm text-slate-200 mt-2.5">Bùi Nhật Quỳnh Hương</div>
                                    <div class="text-xs font-mono text-indigo-400">MSSV: 2380600941</div>
                                    <div class="mt-2 text-[11px] text-pink-300 font-semibold bg-pink-500/10 py-1 px-2 rounded-lg">👑 Nhóm trưởng · Metadata & Conflict Diff</div>
                                </div>

                                <!-- Member 3 -->
                                <div class="p-4 rounded-xl bg-slate-900/60 border border-slate-800 text-center hover:border-indigo-500/40 transition-colors">
                                    <div class="w-12 h-12 rounded-full bg-gradient-to-tr from-cyan-500 to-blue-500 text-white font-bold flex items-center justify-center mx-auto text-sm shadow">
                                        QT
                                    </div>
                                    <div class="font-bold text-sm text-slate-200 mt-2.5">Lý Quốc Thái</div>
                                    <div class="text-xs font-mono text-indigo-400">MSSV: 2380602014</div>
                                    <div class="mt-2 text-[11px] text-cyan-300 font-semibold bg-cyan-500/10 py-1 px-2 rounded-lg">⚡ Chunking & Resume Transfer</div>
                                </div>

                                <!-- Member 4 -->
                                <div class="p-4 rounded-xl bg-slate-900/60 border border-slate-800 text-center hover:border-indigo-500/40 transition-colors">
                                    <div class="w-12 h-12 rounded-full bg-gradient-to-tr from-emerald-500 to-teal-500 text-white font-bold flex items-center justify-center mx-auto text-sm shadow">
                                        TT
                                    </div>
                                    <div class="font-bold text-sm text-slate-200 mt-2.5">Nguyễn Trung Trực</div>
                                    <div class="text-xs font-mono text-indigo-400">MSSV: 2380602385</div>
                                    <div class="mt-2 text-[11px] text-emerald-300 font-semibold bg-emerald-500/10 py-1 px-2 rounded-lg">🎯 Modern UI/UX & Scheduler</div>
                                </div>
                            </div>
                        </div>

                    </div>

                    <script>
                        function copyHash(hash) {
                            navigator.clipboard.writeText(hash);
                            alert('Đã sao chép mã SHA-256:\n' + hash);
                        }

                        function filterFiles() {
                            let input = document.getElementById('searchInput');
                            let filter = input.value.toLowerCase();
                            let rows = document.getElementsByClassName('file-row');
                            for (let i = 0; i < rows.length; i++) {
                                let nameEl = rows[i].getElementsByClassName('file-name')[0];
                                if (nameEl) {
                                    let txt = nameEl.textContent || nameEl.innerText;
                                    rows[i].style.display = txt.toLowerCase().indexOf(filter) > -1 ? '' : 'none';
                                }
                            }
                        }
                    </script>
                </body>
                </html>
                """,
                server.getPort(),
                server.isUseSsl() ? "SSL/TLS (TLSv1.3)" : "TCP Plaintext",
                files.size(),
                formattedStorage,
                clientCount,
                files.size(),
                fileRows.toString(),
                logEntries.toString()
            );

            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private static String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(dot + 1) : "";
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}
