package com.nhom14.filesync.model;

import java.io.Serializable;

/**
 * Gói tin xác nhận (ACK / NACK) phản hồi cho từng Chunk
 */
public class ChunkAck implements Serializable {
    private static final long serialVersionUID = 1L;

    private int chunkIndex;
    private boolean success;
    private long receivedBytes;
    private String message;

    public ChunkAck() {
    }

    public ChunkAck(int chunkIndex, boolean success, long receivedBytes, String message) {
        this.chunkIndex = chunkIndex;
        this.success = success;
        this.receivedBytes = receivedBytes;
        this.message = message;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChunkAck{index=" + chunkIndex + ", success=" + success + ", bytes=" + receivedBytes + "}";
    }
}