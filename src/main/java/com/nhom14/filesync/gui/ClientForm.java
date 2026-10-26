package com.nhom14.filesync.gui;

import com.nhom14.filesync.client.AutoSyncScheduler;
import com.nhom14.filesync.client.ClientListener;
import com.nhom14.filesync.client.SyncClient;
import com.nhom14.filesync.model.SyncItem;
import com.nhom14.filesync.model.SyncPlan;
import com.nhom14.filesync.model.SyncProgress;
import com.nhom14.filesync.protocol.ProtocolConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Giao diện điều khiển Máy khách (Client GUI)
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 * @author Lý Quốc Thái (MSSV: 2380602014)
 */
public class ClientForm extends javax.swing.JFrame implements ClientListener {

    private SyncClient client;
    private AutoSyncScheduler scheduler;
    private DefaultTableModel planTableModel;

    public ClientForm() {
        initComponents();
        this.setSize(980, 720);
        this.setLocationRelativeTo(null);
        planTableModel = (DefaultTableModel) tblPlan.getModel();
        applyModernTheme();
    }

    private void applyModernTheme() {
        getContentPane().setBackground(ModernTheme.SLATE_50);

        ModernTheme.styleButton(btnConnect, "primary");
        ModernTheme.styleButton(btnDisconnect, "danger");
        ModernTheme.styleButton(btnSync, "success");
        ModernTheme.styleButton(btnBrowse, "neutral");
        ModernTheme.styleButton(btnOpenDir, "primary");
        ModernTheme.styleButton(btnDemoData, "warning");
        ModernTheme.styleButton(btnTestConflict, "danger");
        ModernTheme.styleButton(btnClearLog, "neutral");

        ModernTheme.styleTable(tblPlan);
        ModernTheme.styleProgressBar(prgFile, ModernTheme.PRIMARY);
        ModernTheme.styleProgressBar(prgOverall, ModernTheme.SUCCESS);
        ModernTheme.styleTerminalLog(txtLogs);

        pnlNorth.setBackground(Color.WHITE);
        pnlConnect.setBackground(Color.WHITE);
        pnlSyncControls.setBackground(Color.WHITE);
        pnlCenter.setBackground(ModernTheme.SLATE_50);
        pnlSouth.setBackground(Color.WHITE);
        pnlProgress.setBackground(Color.WHITE);
        pnlLogBox.setBackground(Color.WHITE);
        pnlLogActions.setBackground(Color.WHITE);

        // Custom Cell Renderer cho tblPlan voi Badge Pills ruc ro
        tblPlan.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50);
                    c.setForeground(ModernTheme.SLATE_800);
                }

                if (column == 0 && value != null) {
                    setText("📄 " + value.toString());
                } else if (column == 1 && value != null) {
                    String act = value.toString().toUpperCase();
                    setFont(ModernTheme.FONT_BOLD);
                    if (act.contains("UP_TO_DATE") || act.contains("ĐỒNG BỘ") || act.contains("KHỚP")) {
                        setText("🟢 ĐỒNG NHẤT");
                        if (!isSelected) {
                            c.setBackground(ModernTheme.SUCCESS_LIGHT);
                            c.setForeground(ModernTheme.SUCCESS_HOVER);
                        }
                    } else if (act.contains("UPLOAD") || act.contains("TẢI LÊN")) {
                        setText("⬆ TẢI LÊN");
                        if (!isSelected) {
                            c.setBackground(ModernTheme.PRIMARY_LIGHT);
                            c.setForeground(ModernTheme.PRIMARY_HOVER);
                        }
                    } else if (act.contains("DOWNLOAD") || act.contains("TẢI VỀ")) {
                        setText("⬇ TẢI VỀ");
                        if (!isSelected) {
                            c.setBackground(new Color(240, 249, 255));
                            c.setForeground(new Color(3, 105, 161));
                        }
                    } else if (act.contains("CONFLICT") || act.contains("XUNG ĐỘT")) {
                        setText("⚠️ XUNG ĐỘT (ĐỔI TÊN)");
                        if (!isSelected) {
                            c.setBackground(ModernTheme.DANGER_LIGHT);
                            c.setForeground(ModernTheme.DANGER_HOVER);
                        }
                    } else if (act.contains("DELETE") || act.contains("XÓA")) {
                        setText("✖ XÓA TỆP");
                        if (!isSelected) {
                            c.setBackground(new Color(254, 226, 226));
                            c.setForeground(new Color(185, 28, 28));
                        }
                    }
                }
                return c;
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlNorth = new javax.swing.JPanel();
        pnlConnect = new javax.swing.JPanel();
        lblHost = new javax.swing.JLabel();
        txtHost = new javax.swing.JTextField();
        lblPort = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        chkSsl = new javax.swing.JCheckBox();
        btnConnect = new javax.swing.JButton();
        btnDisconnect = new javax.swing.JButton();
        cboTransMode = new javax.swing.JComboBox<>();
        pnlSyncControls = new javax.swing.JPanel();
        lblRootDir = new javax.swing.JLabel();
        txtRootDir = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        btnOpenDir = new javax.swing.JButton();
        btnSync = new javax.swing.JButton();
        chkAutoSync = new javax.swing.JCheckBox();
        spnInterval = new javax.swing.JSpinner();
        lblSeconds = new javax.swing.JLabel();
        cboChunkSize = new javax.swing.JComboBox<>();
        btnDemoData = new javax.swing.JButton();
        btnTestConflict = new javax.swing.JButton();
        pnlCenter = new javax.swing.JPanel();
        scrollTable = new javax.swing.JScrollPane();
        tblPlan = new javax.swing.JTable();
        pnlSouth = new javax.swing.JPanel();
        pnlProgress = new javax.swing.JPanel();
        lblFileProgress = new javax.swing.JLabel();
        prgFile = new javax.swing.JProgressBar();
        lblOverallProgress = new javax.swing.JLabel();
        prgOverall = new javax.swing.JProgressBar();
        pnlLogBox = new javax.swing.JPanel();
        scrollLog = new javax.swing.JScrollPane();
        txtLogs = new javax.swing.JTextArea();
        pnlLogActions = new javax.swing.JPanel();
        btnClearLog = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Máy Khách Đồng Bộ Tệp Tin (Sync Client) - Nhóm 14");

        pnlNorth.setLayout(new java.awt.GridLayout(2, 1));

        pnlConnect.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kết Nối Máy Chủ (Server Socket)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(0, 0, 153)));
        pnlConnect.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblHost.setText("IP Server:");
        pnlConnect.add(lblHost);

        txtHost.setColumns(10);
        txtHost.setText("localhost");
        pnlConnect.add(txtHost);

        lblPort.setText("Port:");
        pnlConnect.add(lblPort);

        txtPort.setColumns(5);
        txtPort.setText("8888");
        pnlConnect.add(txtPort);

        chkSsl.setSelected(true);
        chkSsl.setText("SSL/TLS");
        pnlConnect.add(chkSsl);

        btnConnect.setText("Kết Nối");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });
        pnlConnect.add(btnConnect);

        btnDisconnect.setText("Ngắt Nối");
        btnDisconnect.setEnabled(false);
        btnDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDisconnectActionPerformed(evt);
            }
        });
        pnlConnect.add(btnDisconnect);

        cboTransMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Batch Window (Cửa sổ trượt - Nhanh)", "Stop-and-Wait (Từng khối ACK)" }));
        cboTransMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTransModeActionPerformed(evt);
            }
        });
        pnlConnect.add(cboTransMode);

        pnlNorth.add(pnlConnect);

        pnlSyncControls.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cấu Hình Đồng Bộ & Chuyển Tải", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(0, 102, 0)));
        pnlSyncControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblRootDir.setText("Thư mục:");
        pnlSyncControls.add(lblRootDir);

        txtRootDir.setColumns(20);
        txtRootDir.setText("./client_data");
        pnlSyncControls.add(txtRootDir);

        btnBrowse.setText("Chọn...");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        pnlSyncControls.add(btnBrowse);

        btnOpenDir.setText("Mở Thư Mục");
        btnOpenDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenDirActionPerformed(evt);
            }
        });
        pnlSyncControls.add(btnOpenDir);

        btnSync.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnSync.setForeground(new java.awt.Color(0, 0, 153));
        btnSync.setText("ĐỒNG BỘ NGAY");
        btnSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSyncActionPerformed(evt);
            }
        });
        pnlSyncControls.add(btnSync);

        chkAutoSync.setText("Tự động:");
        chkAutoSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAutoSyncActionPerformed(evt);
            }
        });
        pnlSyncControls.add(chkAutoSync);

        spnInterval.setModel(new javax.swing.SpinnerNumberModel(10, 3, 3600, 5));
        pnlSyncControls.add(spnInterval);

        lblSeconds.setText("s");
        pnlSyncControls.add(lblSeconds);

        cboChunkSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "64 KB", "128 KB", "256 KB" }));
        cboChunkSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboChunkSizeActionPerformed(evt);
            }
        });
        pnlSyncControls.add(cboChunkSize);

        btnDemoData.setText("Tạo Tệp Mẫu");
        btnDemoData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDemoDataActionPerformed(evt);
            }
        });
        pnlSyncControls.add(btnDemoData);

        btnTestConflict.setForeground(new java.awt.Color(204, 0, 0));
        btnTestConflict.setText("Giả Lập Xung Đột");
        btnTestConflict.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestConflictActionPerformed(evt);
            }
        });
        pnlSyncControls.add(btnTestConflict);

        pnlNorth.add(pnlSyncControls);

        getContentPane().add(pnlNorth, java.awt.BorderLayout.NORTH);

        pnlCenter.setLayout(new java.awt.BorderLayout());

        tblPlan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên Tệp", "Đường Dẫn Tương Đối", "Kích Thước", "Trạng Thái Đồng Bộ", "Chi Tiết Thao Tác"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollTable.setViewportView(tblPlan);

        pnlCenter.add(scrollTable, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlCenter, java.awt.BorderLayout.CENTER);

        pnlSouth.setLayout(new java.awt.BorderLayout());

        pnlProgress.setBorder(javax.swing.BorderFactory.createTitledBorder("Tiến Độ Đồng Bộ"));
        pnlProgress.setLayout(new java.awt.GridLayout(4, 1, 0, 3));

        lblFileProgress.setText("Tiến trình tệp: [Chờ đồng bộ]");
        pnlProgress.add(lblFileProgress);

        prgFile.setStringPainted(true);
        pnlProgress.add(prgFile);

        lblOverallProgress.setText("Tiến trình tổng thể: 0%");
        pnlProgress.add(lblOverallProgress);

        prgOverall.setStringPainted(true);
        pnlProgress.add(prgOverall);

        pnlSouth.add(pnlProgress, java.awt.BorderLayout.NORTH);

        pnlLogBox.setLayout(new java.awt.BorderLayout());

        txtLogs.setEditable(false);
        txtLogs.setBackground(new java.awt.Color(20, 24, 32));
        txtLogs.setColumns(20);
        txtLogs.setFont(new java.awt.Font("Consolas", 0, 12));
        txtLogs.setForeground(new java.awt.Color(51, 255, 255));
        txtLogs.setRows(6);
        scrollLog.setViewportView(txtLogs);

        pnlLogBox.add(scrollLog, java.awt.BorderLayout.CENTER);

        pnlLogActions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnClearLog.setText("Xóa Nhật Ký");
        btnClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearLogActionPerformed(evt);
            }
        });
        pnlLogActions.add(btnClearLog);

        pnlLogBox.add(pnlLogActions, java.awt.BorderLayout.SOUTH);

        pnlSouth.add(pnlLogBox, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            initClient();
            client.connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối Server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnDisconnectActionPerformed(java.awt.event.ActionEvent evt) {
        if (scheduler != null) {
            scheduler.stop();
            chkAutoSync.setSelected(false);
        }
        if (client != null) {
            client.disconnect();
        }
    }

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtRootDir.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void btnOpenDirActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            File dir = new File(txtRootDir.getText().trim());
            if (!dir.exists()) dir.mkdirs();
            Desktop.getDesktop().open(dir);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể mở thư mục: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnSyncActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            initClient();
            client.sync();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thực hiện đồng bộ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chkAutoSyncActionPerformed(java.awt.event.ActionEvent evt) {
        if (chkAutoSync.isSelected()) {
            try {
                initClient();
                int interval = (Integer) spnInterval.getValue();
                if (scheduler == null) {
                    scheduler = new AutoSyncScheduler(client);
                }
                scheduler.start(interval);
                onLog(">>> BẬT CHẾ ĐỘ ĐỒNG BỘ TỰ ĐỘNG MỖI " + interval + " GIÂY <<<");
            } catch (Exception e) {
                chkAutoSync.setSelected(false);
                JOptionPane.showMessageDialog(this, "Lỗi bật đồng bộ tự động: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (scheduler != null) {
                scheduler.stop();
                onLog(">>> ĐÃ TẮT CHẾ ĐỘ ĐỒNG BỘ TỰ ĐỘNG <<<");
            }
        }
    }

    private void cboChunkSizeActionPerformed(java.awt.event.ActionEvent evt) {
        if (client != null) {
            client.setChunkSize(getSelectedChunkSize());
        }
    }

    private void cboTransModeActionPerformed(java.awt.event.ActionEvent evt) {
        if (client != null) {
            client.setTransmissionMode(getSelectedTransMode());
        }
    }

    private void btnClearLogActionPerformed(java.awt.event.ActionEvent evt) {
        txtLogs.setText("");
    }

    private void btnDemoDataActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            File clientDir = new File(txtRootDir.getText().trim());
            if (!clientDir.exists()) clientDir.mkdirs();

            File f1 = new File(clientDir, "tai_lieu_mau.txt");
            Files.writeString(f1.toPath(), "Tài liệu mẫu đồng bộ - Nhóm 14 HUTECH - Ngày: " + new java.util.Date());

            File f2 = new File(clientDir, "bao_cao_nhom14.docx");
            Files.writeString(f2.toPath(), "Báo cáo đề tài lập trình mạng máy tính socket TCP và SSL/TLS.");

            File subDir = new File(clientDir, "tai_lieu_ky_thuat");
            subDir.mkdirs();
            File f3 = new File(subDir, "kien_truc_socket.md");
            Files.writeString(f3.toPath(), "# Kiến trúc mạng Socket đa luồng\n- Server multi-threaded\n- Chunking 64KB\n- Resume transfer");

            File fLarge = new File(clientDir, "du_lieu_lon_300KB.bin");
            byte[] data = new byte[300000]; // ~300KB -> 5 chunks
            Arrays.fill(data, (byte) 'H');
            try (FileOutputStream fos = new FileOutputStream(fLarge)) {
                fos.write(data);
            }

            JOptionPane.showMessageDialog(this, "Đã tạo 4 tệp tin mẫu (kèm thư mục con và file lớn 300KB) trong thư mục Client!\nBấm 'ĐỒNG BỘ NGAY' để thử nghiệm.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            onLog(">>> Đã sinh dữ liệu mẫu trong thư mục Client.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo dữ liệu mẫu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnTestConflictActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            File clientDir = new File(txtRootDir.getText().trim());
            File serverDir = new File("./server_data");

            if (!clientDir.exists()) clientDir.mkdirs();
            if (!serverDir.exists()) serverDir.mkdirs();

            File fClient = new File(clientDir, "xung_dot_demo.txt");
            File fServer = new File(serverDir, "xung_dot_demo.txt");

            Files.writeString(fClient.toPath(), "Nội dung Client sửa đổi khi mất mạng lúc: " + System.currentTimeMillis());
            Files.writeString(fServer.toPath(), "Nội dung Server sửa đổi khi mất mạng lúc: " + (System.currentTimeMillis() + 100));

            JOptionPane.showMessageDialog(this, "Đã tạo tệp 'xung_dot_demo.txt' với 2 nội dung khác nhau trên Client và Server!\nBây giờ bấm 'ĐỒNG BỘ NGAY' để xem quy tắc xử lý xung đột kích hoạt.", "Chuẩn Bị Demo Xung Đột", JOptionPane.INFORMATION_MESSAGE);
            onLog(">>> Đã thiết lập trạng thái xung đột tệp tin 'xung_dot_demo.txt'.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thiết lập xung đột: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSelectedChunkSize() {
        int idx = cboChunkSize.getSelectedIndex();
        return switch (idx) {
            case 1 -> 131072;  // 128KB
            case 2 -> 262144;  // 256KB
            default -> 65536;  // 64KB
        };
    }

    private String getSelectedTransMode() {
        return cboTransMode.getSelectedIndex() == 1 ? ProtocolConstants.MODE_STOP_AND_WAIT : ProtocolConstants.MODE_BATCH_WINDOW;
    }

    private void initClient() {
        if (client == null || !client.isConnected()) {
            String host = txtHost.getText().trim();
            int port = Integer.parseInt(txtPort.getText().trim());
            boolean useSsl = chkSsl.isSelected();
            File rootDir = new File(txtRootDir.getText().trim());

            client = new SyncClient(host, port, useSsl, rootDir, this);
            client.setChunkSize(getSelectedChunkSize());
            client.setTransmissionMode(getSelectedTransMode());
        }
    }

    @Override
    public void onConnected(String host, int port, boolean ssl) {
        SwingUtilities.invokeLater(() -> {
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            txtHost.setEnabled(false);
            txtPort.setEnabled(false);
            chkSsl.setEnabled(false);
        });
    }

    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(() -> {
            btnConnect.setEnabled(true);
            btnDisconnect.setEnabled(false);
            txtHost.setEnabled(true);
            txtPort.setEnabled(true);
            chkSsl.setEnabled(true);
        });
    }

    @Override
    public void onLog(String message) {
        SwingUtilities.invokeLater(() -> {
            txtLogs.append(message + "\n");
            txtLogs.setCaretPosition(txtLogs.getDocument().getLength());
        });
    }

    @Override
    public void onProgress(SyncProgress progress) {
        SwingUtilities.invokeLater(() -> {
            lblFileProgress.setText("Đang truyền [" + progress.getCurrentFile() + "]: "
                    + progress.getCurrentChunk() + "/" + progress.getTotalChunks() + " chunks (" + progress.getFilePercentage() + "%)");
            prgFile.setValue(progress.getFilePercentage());

            lblOverallProgress.setText("Tổng thể (" + progress.getCurrentFileIndex() + "/" + progress.getTotalFiles() + " files): " + progress.getOverallPercentage() + "%");
            prgOverall.setValue(progress.getOverallPercentage());
        });
    }

    @Override
    public void onSyncPlanReady(SyncPlan plan) {
        SwingUtilities.invokeLater(() -> {
            planTableModel.setRowCount(0);
            for (SyncItem item : plan.getItems()) {
                String name = new File(item.getRelativePath()).getName();
                String sizeStr = "-";
                if (item.getClientMeta() != null) {
                    sizeStr = item.getClientMeta().getFormattedSize();
                } else if (item.getServerMeta() != null) {
                    sizeStr = item.getServerMeta().getFormattedSize();
                }

                planTableModel.addRow(new Object[]{
                    name,
                    item.getRelativePath(),
                    sizeStr,
                    item.getAction().getDisplayName(),
                    item.getDetail()
                });
            }
        });
    }

    @Override
    public void onSyncFinished(boolean success, String summary) {
        SwingUtilities.invokeLater(() -> {
            lblFileProgress.setText("Hoàn tất.");
            prgFile.setValue(100);
            prgOverall.setValue(100);
            lblOverallProgress.setText("Tiến trình: 100% Hoàn tất.");
            if (success) {
                JOptionPane.showMessageDialog(this, summary, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, summary, "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClearLog;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnDemoData;
    private javax.swing.JButton btnDisconnect;
    private javax.swing.JButton btnOpenDir;
    private javax.swing.JButton btnSync;
    private javax.swing.JButton btnTestConflict;
    private javax.swing.JComboBox<String> cboChunkSize;
    private javax.swing.JComboBox<String> cboTransMode;
    private javax.swing.JCheckBox chkAutoSync;
    private javax.swing.JCheckBox chkSsl;
    private javax.swing.JLabel lblFileProgress;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblOverallProgress;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblRootDir;
    private javax.swing.JLabel lblSeconds;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlConnect;
    private javax.swing.JPanel pnlLogActions;
    private javax.swing.JPanel pnlLogBox;
    private javax.swing.JPanel pnlNorth;
    private javax.swing.JPanel pnlProgress;
    private javax.swing.JPanel pnlSouth;
    private javax.swing.JPanel pnlSyncControls;
    private javax.swing.JProgressBar prgFile;
    private javax.swing.JProgressBar prgOverall;
    private javax.swing.JScrollPane scrollLog;
    private javax.swing.JScrollPane scrollTable;
    private javax.swing.JSpinner spnInterval;
    private javax.swing.JTable tblPlan;
    private javax.swing.JTextArea txtLogs;
    private javax.swing.JTextField txtHost;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtRootDir;
    // End of variables declaration//GEN-END:variables
}
