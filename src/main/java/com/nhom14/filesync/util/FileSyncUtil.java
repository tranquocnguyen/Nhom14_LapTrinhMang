package com.nhom14.filesync.util;

import com.nhom14.filesync.model.FileMetadata;
import com.nhom14.filesync.security.PathSecurity;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Tiện ích quét thư mục, quản lý tệp tin và tạo tên tệp xung đột
 *
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Lý Quốc Thái (MSSV: 2380602014)
 */
public class FileSyncUtil {

    /**
     * Quét đệ quy toàn bộ thư mục và tạo danh sách Metadata tệp tin
     */
    public static List<FileMetadata> scanDirectory(File rootDir) throws IOException {
        List<FileMetadata> metadataList = new ArrayList<>();
        if (rootDir == null || !rootDir.exists() || !rootDir.isDirectory()) {
            return metadataList;
        }

        Path rootPath = rootDir.toPath();
        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // Bỏ qua các file tạm .part và file hệ thống
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(".part") || fileName.startsWith(".sync_state") || fileName.startsWith(".DS_Store") || fileName.equalsIgnoreCase("Thumbs.db")) {
                    return FileVisitResult.CONTINUE;
                }

                Path relPath = rootPath.relativize(file);
                String normalizedRelPath = relPath.toString().replace('\\', '/');

                long size = attrs.size();
                long lastModified = attrs.lastModifiedTime().toMillis();
                String sha256 = HashUtil.calculateSha256(file.toFile());

                FileMetadata meta = new FileMetadata(normalizedRelPath, fileName, size, lastModified, sha256);
                metadataList.add(meta);
                return FileVisitResult.CONTINUE;
            }
        });

        Collections.sort(metadataList);
        return metadataList;
    }

    /**
     * Quy tắc xử lý xung đột tệp (Bắt buộc theo yêu cầu đề tài):
     * Đổi tên tệp xung đột thành: [Tên]_conflict_Client_[yyyyMMdd_HHmmss].[Đuôi]
     */
    public static String generateConflictPath(String originalRelativePath) {
        String clean = originalRelativePath.replace('\\', '/');
        int lastSlash = clean.lastIndexOf('/');
        String dir = lastSlash >= 0 ? clean.substring(0, lastSlash + 1) : "";
        String fullFileName = lastSlash >= 0 ? clean.substring(lastSlash + 1) : clean;

        String baseName;
        String extension = "";
        int dotIndex = fullFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = fullFileName.substring(0, dotIndex);
            extension = fullFileName.substring(dotIndex);
        } else {
            baseName = fullFileName;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());

        return dir + baseName + "_conflict_Client_" + timestamp + extension;
    }

    public static File getPartFile(File targetFile) {
        return new File(targetFile.getParentFile(), targetFile.getName() + ".part");
    }

    public static void ensureParentDirectories(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}