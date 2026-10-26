package com.nhom14.filesync;

import com.nhom14.filesync.gui.ClientForm;
import com.nhom14.filesync.gui.MainLauncherForm;
import com.nhom14.filesync.gui.ServerForm;

import javax.swing.UIManager;

/**
 * Điểm khởi chạy chính của Ứng dụng Đồng bộ Tệp tin
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Lý Quốc Thái (MSSV: 2380602014)
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 */
public class Main {

    public static void main(String[] args) {
        // Thiết lập Look and Feel hiện đại
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName()) || "Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> {
            if (args != null && args.length > 0) {
                if ("--server".equalsIgnoreCase(args[0])) {
                    new ServerForm().setVisible(true);
                    return;
                } else if ("--client".equalsIgnoreCase(args[0])) {
                    new ClientForm().setVisible(true);
                    return;
                }
            }
            // Mặc định mở Màn hình điều khiển tổng thể
            new MainLauncherForm().setVisible(true);
        });
    }
}
