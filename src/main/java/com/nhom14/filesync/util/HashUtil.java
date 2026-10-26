package com.nhom14.filesync.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Tiện ích băm SHA-256 kiểm tra tính toàn vẹn dữ liệu
 *
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class HashUtil {

    private static final String ALGORITHM = "SHA-256";

    /**
     * Tính toán SHA-256 của tệp tin theo luồng đọc (không tốn RAM cho file lớn)
     */
    public static String calculateSha256(File file) {
        if (!file.exists() || !file.isFile()) {
            return "";
        }
        try (InputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] buffer = new byte[65536]; // 64KB buffer
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return bytesToHex(digest.digest());
        } catch (Exception e) {
            System.err.println("Lỗi tính mã băm SHA-256 cho file " + file.getName() + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * Tính toán SHA-256 cho một mảng byte nhị phân (dùng cho Chunk Packet)
     */
    public static String calculateSha256(byte[] data, int offset, int length) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(data, offset, length);
            return bytesToHex(digest.digest());
        } catch (Exception e) {
            return "";
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}