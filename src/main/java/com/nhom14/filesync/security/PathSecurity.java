package com.nhom14.filesync.security;

import java.io.File;
import java.io.IOException;

/**
 * Lớp bảo vệ chống tấn công Path Traversal qua đường dẫn tương đối của metadata
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 */
public class PathSecurity {

    /**
     * Kiểm tra tính hợp lệ và an toàn của đường dẫn tương đối trong thư mục gốc
     */
    public static File resolveAndValidate(File rootDir, String relativePath) throws SecurityException, IOException {
        if (rootDir == null || relativePath == null) {
            throw new IllegalArgumentException("Thư mục gốc hoặc đường dẫn tương đối không được rỗng");
        }

        // Chuẩn hóa dấu phân cách
        String cleanRel = relativePath.replace('\\', '/').trim();

        // Chống chuỗi nguy hiểm cố tình đi lùi thư mục
        if (cleanRel.contains("..") || cleanRel.startsWith("/") || cleanRel.contains(":") || cleanRel.contains("\0")) {
            throw new SecurityException("CẢNH BÁO BẢO MẬT: Phát hiện nỗ lực Path Traversal với đường dẫn: " + relativePath);
        }

        File targetFile = new File(rootDir, cleanRel);
        String canonicalRootDir = rootDir.getCanonicalPath();
        String canonicalTarget = targetFile.getCanonicalPath();

        // Đảm bảo tuyệt đối tệp tin đích phải nằm bên trong thư mục gốc
        if (!canonicalTarget.equals(canonicalRootDir) && !canonicalTarget.startsWith(canonicalRootDir + File.separator)) {
            throw new SecurityException("CẢNH BÁO BẢO MẬT: Đường dẫn vượt ra ngoài thư mục gốc: " + relativePath);
        }

        return targetFile;
    }
}