package com.nhom14.filesync.model;

/**
 * Thông tin tiến độ đồng bộ dùng cho giao diện GUI
 */
public class SyncProgress {
    private String currentFile;
    private int currentFileIndex;
    private int totalFiles;
    private int currentChunk;
    private int totalChunks;
    private long currentFileBytesTransferred;
    private long currentFileTotalBytes;
    private int overallPercentage;
    private String statusMessage;

    public SyncProgress(String currentFile, int currentFileIndex, int totalFiles,
                        int currentChunk, int totalChunks,
                        long currentFileBytesTransferred, long currentFileTotalBytes,
                        int overallPercentage, String statusMessage) {
        this.currentFile = currentFile;
        this.currentFileIndex = currentFileIndex;
        this.totalFiles = totalFiles;
        this.currentChunk = currentChunk;
        this.totalChunks = totalChunks;
        this.currentFileBytesTransferred = currentFileBytesTransferred;
        this.currentFileTotalBytes = currentFileTotalBytes;
        this.overallPercentage = overallPercentage;
        this.statusMessage = statusMessage;
    }

    public String getCurrentFile() { return currentFile; }
    public int getCurrentFileIndex() { return currentFileIndex; }
    public int getTotalFiles() { return totalFiles; }
    public int getCurrentChunk() { return currentChunk; }
    public int getTotalChunks() { return totalChunks; }
    public long getCurrentFileBytesTransferred() { return currentFileBytesTransferred; }
    public long getCurrentFileTotalBytes() { return currentFileTotalBytes; }
    public int getOverallPercentage() { return overallPercentage; }
    public String getStatusMessage() { return statusMessage; }

    public int getFilePercentage() {
        if (currentFileTotalBytes <= 0) return 100;
        return (int) Math.min(100, (currentFileBytesTransferred * 100) / currentFileTotalBytes);
    }
}