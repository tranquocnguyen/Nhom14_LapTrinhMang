package com.nhom14.filesync.model;

import java.io.Serializable;

/**
 * Gói tin khối dữ liệu (Chunk Packet) dùng để truyền tải file lớn
 */
public class ChunkPacket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String relativePath;
    private int chunkIndex;     // Số thứ tự chunk (0, 1, 2, ...)
    private int totalChunks;    // Tổng số chunk của file
    private long offset;        // Vị trí offset byte trong file
    private int length;         // Chiều dài thực tế của khối dữ liệu byte[]
    private byte[] data;        // Dữ liệu nhị phân
    private String chunkHash;   // SHA-256 của riêng khối dữ liệu này

    public ChunkPacket() {
    }

    public ChunkPacket(String relativePath, int chunkIndex, int totalChunks, long offset, int length, byte[] data, String chunkHash) {
        this.relativePath = relativePath;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.offset = offset;
        this.length = length;
        this.data = data;
        this.chunkHash = chunkHash;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public long getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public String getChunkHash() {
        return chunkHash;
    }
}