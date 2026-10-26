package com.nhom14.filesync.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**

* Trình lập lịch đồng bộ tự động theo chu kỳ thời gian.
*
* @author Nguyễn Trung Trực - MSSV: 2380602385 - Lớp: 23DTHB1
  */
  public class AutoSyncScheduler {

  private final SyncClient client;
  private ScheduledExecutorService scheduler;
  private volatile boolean running = false;

  public AutoSyncScheduler(SyncClient client) {
  this.client = client;
  }

  public synchronized void start(int intervalSeconds) {
  if (intervalSeconds <= 0) {
  throw new IllegalArgumentException(
  "Khoảng thời gian đồng bộ phải lớn hơn 0 giây.");
  }

   stop();

   scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
       Thread thread = new Thread(runnable, "AutoSyncScheduler-Thread");
       thread.setDaemon(true);
       return thread;
   });

   running = true;

   scheduler.scheduleAtFixedRate(() -> {
       if (!running) {
           return;
       }

       try {
           client.sync();
       } catch (Exception e) {
           System.err.println(
                   "Lỗi đồng bộ tự động: " + e.getMessage());
       }
   }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);

  }

  public synchronized void stop() {
  running = false;

   if (scheduler != null) {
       scheduler.shutdownNow();
       scheduler = null;
   }
  }

  public boolean isRunning() {
  return running;
  }
  }
