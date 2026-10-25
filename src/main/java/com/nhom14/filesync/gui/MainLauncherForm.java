package com.nhom14.filesync.gui;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

/**
 * Màn hình chọn khởi chạy ứng dụng (Server hoặc Client hoặc cả hai)
 * Thiết kế hiện đại theo xu hướng trẻ trung (Modern Studio UI)
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Lý Quốc Thái (MSSV: 2380602014)
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 */
public class MainLauncherForm extends javax.swing.JFrame {

    public MainLauncherForm() {
        initComponents();
        this.setSize(720, 560);
        this.setLocationRelativeTo(null);
        applyModernTheme();
    }

    private void applyModernTheme() {
        getContentPane().setBackground(ModernTheme.SLATE_50);
        pnlHeader.setBackground(ModernTheme.SLATE_900);
        pnlHeader.setBorder(new EmptyBorder(22, 25, 22, 25));

        pnlButtons.setBackground(ModernTheme.SLATE_50);
        pnlButtons.setBorder(new EmptyBorder(30, 60, 30, 60));

        ModernTheme.styleButton(btnOpenServer, "primary");
        ModernTheme.styleButton(btnOpenClient, "primary");
        ModernTheme.styleButton(btnOpenBoth, "success");
        ModernTheme.styleButton(btnOpenWeb, "warning");
        ModernTheme.styleButton(btnExit, "neutral");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblHeader1 = new javax.swing.JLabel();
        lblHeader2 = new javax.swing.JLabel();
        lblHeader3 = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        btnOpenServer = new javax.swing.JButton();
        btnOpenClient = new javax.swing.JButton();
        btnOpenBoth = new javax.swing.JButton();
        btnOpenWeb = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SyncPulse 14 • Hệ Thống Đồng Bộ Tệp Tin - Nhóm 14 (HUTECH)");
        setResizable(false);

        pnlHeader.setBackground(new java.awt.Color(15, 23, 42));
        pnlHeader.setLayout(new java.awt.GridLayout(3, 1, 0, 4));

        lblHeader1.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        lblHeader1.setForeground(new java.awt.Color(255, 255, 255));
        lblHeader1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader1.setText("⚡ SYNCPULSE 14 • FILE SYNCHRONIZATION STUDIO");
        pnlHeader.add(lblHeader1);

        lblHeader2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblHeader2.setForeground(new java.awt.Color(226, 232, 240));
        lblHeader2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader2.setText("Học phần: Lập trình mạng máy tính • Lớp: 23DTHB1 • Khoa CNTT HUTECH");
        pnlHeader.add(lblHeader2);

        lblHeader3.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblHeader3.setForeground(new java.awt.Color(165, 180, 252));
        lblHeader3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader3.setText("👑 Trần Quốc Nguyễn (NT)  |  🌸 Bùi Nhật Quỳnh Hương  |  ⚡ Lý Quốc Thái  |  🎯 Nguyễn Trung Trực");
        pnlHeader.add(lblHeader3);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlButtons.setBackground(new java.awt.Color(248, 250, 252));
        pnlButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 55, 30, 55));
        pnlButtons.setLayout(new java.awt.GridLayout(5, 1, 0, 14));

        btnOpenServer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenServer.setText("🖥️ 1. Khởi Chạy SERVER (Máy Chủ TCP / SSL / Web Dashboard)");
        btnOpenServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenServerActionPerformed(evt);
            }
        });
        pnlButtons.add(btnOpenServer);

        btnOpenClient.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenClient.setText("💻 2. Khởi Chạy CLIENT (Máy Khách Đồng Bộ & Chống Xung Đột)");
        btnOpenClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenClientActionPerformed(evt);
            }
        });
        pnlButtons.add(btnOpenClient);

        btnOpenBoth.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenBoth.setText("🚀 3. Mở Cả Server và Client (Chế Độ Kiểm Thử Trực Quan 2 Cửa Sổ)");
        btnOpenBoth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenBothActionPerformed(evt);
            }
        });
        pnlButtons.add(btnOpenBoth);

        btnOpenWeb.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenWeb.setText("🌐 4. Mở Web Dashboard Giám Sát (Browser: http://localhost:8088)");
        btnOpenWeb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenWebActionPerformed(evt);
            }
        });
        pnlButtons.add(btnOpenWeb);

        btnExit.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnExit.setText("❌ 5. Thoát Ứng Dụng");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        pnlButtons.add(btnExit);

        getContentPane().add(pnlButtons, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenServerActionPerformed
        ServerForm serverForm = new ServerForm();
        serverForm.setVisible(true);
    }//GEN-LAST:event_btnOpenServerActionPerformed

    private void btnOpenClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenClientActionPerformed
        ClientForm clientForm = new ClientForm();
        clientForm.setVisible(true);
    }//GEN-LAST:event_btnOpenClientActionPerformed

    private void btnOpenBothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenBothActionPerformed
        ServerForm serverForm = new ServerForm();
        serverForm.setLocation(40, 60);
        serverForm.setVisible(true);

        ClientForm clientForm = new ClientForm();
        clientForm.setLocation(820, 60);
        clientForm.setVisible(true);
    }//GEN-LAST:event_btnOpenBothActionPerformed

    private void btnOpenWebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenWebActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:8088/"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể mở trình duyệt: " + e.getMessage() + "\nVui lòng truy cập thủ công: http://localhost:8088", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnOpenWebActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName()) || "Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> {
            new MainLauncherForm().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnOpenBoth;
    private javax.swing.JButton btnOpenClient;
    private javax.swing.JButton btnOpenServer;
    private javax.swing.JButton btnOpenWeb;
    private javax.swing.JLabel lblHeader1;
    private javax.swing.JLabel lblHeader2;
    private javax.swing.JLabel lblHeader3;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlHeader;
    // End of variables declaration//GEN-END:variables
}
