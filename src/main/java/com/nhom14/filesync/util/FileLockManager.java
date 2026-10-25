package com.nhom14.filesync.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Cơ chế khóa tệp tin (File Locking) chống xung đột truy cập đa luồng
 * khi nhiều Client đồng bộ cùng thời điểm
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class FileLockManager {

    private static final ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();

    private static ReentrantReadWriteLock getLock(String relativePath) {
        String key = relativePath.replace('\\', '/').toLowerCase();
        return locks.computeIfAbsent(key, k -> new ReentrantReadWriteLock(true));
    }

    public static void lockForRead(String relativePath) {
        getLock(relativePath).readLock().lock();
    }

    public static void unlockForRead(String relativePath) {
        getLock(relativePath).readLock().unlock();
    }

    public static void lockForWrite(String relativePath) {
        getLock(relativePath).writeLock().lock();
    }

    public static void unlockForWrite(String relativePath) {
        getLock(relativePath).writeLock().unlock();
    }
}