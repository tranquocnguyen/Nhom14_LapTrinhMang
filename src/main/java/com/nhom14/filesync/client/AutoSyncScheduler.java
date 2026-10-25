package com.nhom14.filesync.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Trình lập lịch đồng bộ tự động theo chu kỳ thời gian (Periodic Auto-Sync)
 *
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 */
public class AutoSyncScheduler {

    private final SyncClient client;
    private ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    public AutoSyncScheduler(SyncClient client) {
        this.client = client;
    }

    public synchronized void start(int intervalSeconds) {
        if (running) {
            stop();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        running = true;
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (running) {
                    client.sync();
                }
            } catch (Exception e) {
                System.err.println("Lỗi đồng bộ tự động: " + e.getMessage());
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    public synchronized void stop() {
        running = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    public boolean isRunning() {
        return running;
    }
}