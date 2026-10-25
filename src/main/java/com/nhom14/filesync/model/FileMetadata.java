package com.nhom14.filesync.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Lớp đại diện cho thông tin mô tả tệp tin (Metadata)
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Lý Quốc Thái (MSSV: 2380602014)
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 */
public class FileMetadata implements Serializable, Comparable<FileMetadata> {
    private static final long serialVersionUID = 1L;

    private String relativePath;  // Đường dẫn tương đối chuẩn hóa (vd: docs/report.docx)
    private String fileName;      // Tên tệp tin (vd: report.docx)
    private long fileSize;        // Kích thước (bytes)
    private long lastModified;    // Thời gian chỉnh sửa cuối (epoch millis)
    private String sha256;        // Mã băm SHA-256 để kiểm tra toàn vẹn
    private boolean isDeleted;    // Đánh dấu tệp tin đã bị xóa (Tombstone)

    public FileMetadata() {
        this.isDeleted = false;
    }

    public FileMetadata(String relativePath, String fileName, long fileSize, long lastModified, String sha256) {
        this(relativePath, fileName, fileSize, lastModified, sha256, false);
    }

    public FileMetadata(String relativePath, String fileName, long fileSize, long lastModified, String sha256, boolean isDeleted) {
        this.relativePath = relativePath.replace('\\', '/');
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.sha256 = sha256 != null ? sha256.toLowerCase() : "";
        this.isDeleted = isDeleted;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath != null ? relativePath.replace('\\', '/') : null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256 != null ? sha256.toLowerCase() : "";
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getFormattedSize() {
        if (fileSize < 1024) return fileSize + " B";
        int exp = (int) (Math.log(fileSize) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %cB", fileSize / Math.pow(1024, exp), unit);
    }

    public String getFormattedLastModified() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(lastModified));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetadata that = (FileMetadata) o;
        return Objects.equals(relativePath, that.relativePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relativePath);
    }

    @Override
    public int compareTo(FileMetadata o) {
        return this.relativePath.compareToIgnoreCase(o.relativePath);
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "path='" + relativePath + '\'' +
                ", size=" + fileSize +
                ", sha256='" + sha256 + '\'' +
                ", deleted=" + isDeleted +
                '}';
    }
}