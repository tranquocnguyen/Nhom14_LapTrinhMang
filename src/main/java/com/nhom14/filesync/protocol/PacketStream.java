package com.nhom14.filesync.protocol;

import com.nhom14.filesync.model.ChunkPacket;
import com.nhom14.filesync.model.FileMetadata;

import java.io.*;

/**
 * Trình đóng gói và đọc/ghi dòng dữ liệu giao thức nhị phân và lệnh điều khiển
 *
 * @author Lý Quốc Thái (MSSV: 2380602014)
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class PacketStream {

    private final DataInputStream in;
    private final DataOutputStream out;

    public PacketStream(InputStream in, OutputStream out) {
        this.in = new DataInputStream(new BufferedInputStream(in));
        this.out = new DataOutputStream(new BufferedOutputStream(out));
    }

    public synchronized void writeCommand(String command) throws IOException {
        out.writeUTF(command);
        out.flush();
    }

    public synchronized String readCommand() throws IOException {
        return in.readUTF();
    }

    public synchronized void writeMetadata(FileMetadata meta) throws IOException {
        out.writeUTF(meta.getRelativePath());
        out.writeUTF(meta.getFileName());
        out.writeLong(meta.getFileSize());
        out.writeLong(meta.getLastModified());
        out.writeUTF(meta.getSha256());
        out.writeBoolean(meta.isDeleted());
        out.flush();
    }

    public synchronized FileMetadata readMetadata() throws IOException {
        String relativePath = in.readUTF();
        String fileName = in.readUTF();
        long fileSize = in.readLong();
        long lastModified = in.readLong();
        String sha256 = in.readUTF();
        boolean isDeleted = in.readBoolean();
        return new FileMetadata(relativePath, fileName, fileSize, lastModified, sha256, isDeleted);
    }

    public synchronized void writeChunk(ChunkPacket packet) throws IOException {
        out.writeUTF(packet.getRelativePath());
        out.writeInt(packet.getChunkIndex());
        out.writeInt(packet.getTotalChunks());
        out.writeLong(packet.getOffset());
        out.writeInt(packet.getLength());
        out.writeUTF(packet.getChunkHash());
        out.write(packet.getData(), 0, packet.getLength());
        out.flush();
    }

    public synchronized ChunkPacket readChunk() throws IOException {
        String relativePath = in.readUTF();
        int chunkIndex = in.readInt();
        int totalChunks = in.readInt();
        long offset = in.readLong();
        int length = in.readInt();
        String chunkHash = in.readUTF();
        byte[] data = new byte[length];
        in.readFully(data);
        return new ChunkPacket(relativePath, chunkIndex, totalChunks, offset, length, data, chunkHash);
    }

    public synchronized void writeInt(int v) throws IOException {
        out.writeInt(v);
        out.flush();
    }

    public synchronized int readInt() throws IOException {
        return in.readInt();
    }

    public synchronized void writeLong(long v) throws IOException {
        out.writeLong(v);
        out.flush();
    }

    public synchronized long readLong() throws IOException {
        return in.readLong();
    }

    public synchronized void writeUTF(String s) throws IOException {
        out.writeUTF(s);
        out.flush();
    }

    public synchronized String readUTF() throws IOException {
        return in.readUTF();
    }

    public synchronized void writeBoolean(boolean v) throws IOException {
        out.writeBoolean(v);
        out.flush();
    }

    public synchronized boolean readBoolean() throws IOException {
        return in.readBoolean();
    }
}