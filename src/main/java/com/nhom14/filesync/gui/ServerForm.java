package com.nhom14.filesync.gui;

import com.nhom14.filesync.model.FileMetadata;
import com.nhom14.filesync.server.ServerListener;
import com.nhom14.filesync.server.SyncServer;
import com.nhom14.filesync.util.FileSyncUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Giao diện điều khiển Máy chủ (Server GUI)
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 */
public class ServerForm extends javax.swing.JFrame implements ServerListener {

    private SyncServer server;
    private DefaultTableModel filesTableModel;
    private DefaultTableModel clientsTableModel;
    private String localIp = "127.0.0.1";

    public ServerForm() {
        initComponents();
        this.setSize(940, 640);
        this.setLocationRelativeTo(null);
        filesTableModel = (DefaultTableModel) tblFiles.getModel();
        clientsTableModel = (DefaultTableModel) tblClients.getModel();

        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ignored) {}
        lblStatus.setText(" ● Trạng thái: Server đang dừng  |  IP Máy chủ: " + localIp + "  |  Clients kết nối: 0  |  Bảo mật: TLSv1.3 PKCS12");
        applyModernTheme();
    }

    private void applyModernTheme() {
        getContentPane().setBackground(ModernTheme.SLATE_50);
        ModernTheme.styleButton(btnStart, "success");
        ModernTheme.styleButton(btnStop, "danger");
        ModernTheme.styleButton(btnOpenWeb, "warning");
        ModernTheme.styleButton(btnBrowse, "neutral");
        ModernTheme.styleButton(btnOpenDir, "primary");
        ModernTheme.styleButton(btnRefreshFiles, "neutral");
        ModernTheme.styleButton(btnClearLog, "neutral");

        ModernTheme.styleTable(tblFiles);
        ModernTheme.styleTable(tblClients);
        ModernTheme.styleTerminalLog(txtLogs);

        pnlTop.setBackground(Color.WHITE);
        pnlConfig1.setBackground(Color.WHITE);
        pnlConfig2.setBackground(Color.WHITE);
        pnlBottom.setBackground(ModernTheme.SLATE_900);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(ModernTheme.FONT_BOLD);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        tblFiles.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50);
                    c.setForeground(ModernTheme.SLATE_800);
                }
                if (column == 0 && value != null) {
                    setText("📄 " + value.toString());
                } else if (column == 3 && value != null) {
                    setFont(ModernTheme.FONT_TERMINAL);
                }
                return c;
            }
        });

        tblClients.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50);
                    c.setForeground(ModernTheme.SLATE_800);
                }
                if (column == 2 && value != null) {
                    setText("🟢 " + value.toString());
                    setForeground(ModernTheme.SUCCESS_HOVER);
                    setFont(ModernTheme.FONT_BOLD);
                }
                return c;
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        pnlConfig1 = new javax.swing.JPanel();
        lblPort = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        chkSsl = new javax.swing.JCheckBox();
        btnStart = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        btnOpenWeb = new javax.swing.JButton();
        pnlConfig2 = new javax.swing.JPanel();
        lblRootDir = new javax.swing.JLabel();
        txtRootDir = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        btnOpenDir = new javax.swing.JButton();
        btnRefreshFiles = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        pnlFiles = new javax.swing.JPanel();
        scrollFiles = new javax.swing.JScrollPane();
        tblFiles = new javax.swing.JTable();
        pnlClients = new javax.swing.JPanel();
        scrollClients = new javax.swing.JScrollPane();
        tblClients = new javax.swing.JTable();
        pnlLogs = new javax.swing.JPanel();
        scrollLogs = new javax.swing.JScrollPane();
        txtLogs = new javax.swing.JTextArea();
        pnlLogControls = new javax.swing.JPanel();
        btnClearLog = new javax.swing.JButton();
        pnlBottom = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Máy Chủ Đồng Bộ Tệp Tin (Sync Server) - Nhóm 14");

        pnlTop.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cấu Hình & Điều Khiển Server", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(0, 0, 153)));
        pnlTop.setLayout(new java.awt.GridLayout(2, 1, 0, 5));

        pnlConfig1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblPort.setText("Cổng Server:");
        pnlConfig1.add(lblPort);

        txtPort.setColumns(5);
        txtPort.setText("8888");
        pnlConfig1.add(txtPort);

        chkSsl.setSelected(true);
        chkSsl.setText("Kích hoạt bảo mật SSL/TLS");
        pnlConfig1.add(chkSsl);

        btnStart.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnStart.setText("Khởi Động Server");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });
        pnlConfig1.add(btnStart);

        btnStop.setText("Dừng Server");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        pnlConfig1.add(btnStop);

        btnOpenWeb.setText("Web Dashboard (Port 8088)");
        btnOpenWeb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenWebActionPerformed(evt);
            }
        });
        pnlConfig1.add(btnOpenWeb);

        pnlTop.add(pnlConfig1);

        pnlConfig2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblRootDir.setText("Thư mục:");
        pnlConfig2.add(lblRootDir);

        txtRootDir.setColumns(30);
        txtRootDir.setText("./server_data");
        pnlConfig2.add(txtRootDir);

        btnBrowse.setText("Chọn...");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        pnlConfig2.add(btnBrowse);

        btnOpenDir.setText("Mở Thư Mục");
        btnOpenDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenDirActionPerformed(evt);
            }
        });
        pnlConfig2.add(btnOpenDir);

        btnRefreshFiles.setText("Quét lại tệp");
        btnRefreshFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshFilesActionPerformed(evt);
            }
        });
        pnlConfig2.add(btnRefreshFiles);

        pnlTop.add(pnlConfig2);

        getContentPane().add(pnlTop, java.awt.BorderLayout.NORTH);

        pnlFiles.setLayout(new java.awt.BorderLayout());

        tblFiles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên Tệp", "Đường Dẫn Tương Đối", "Kích Thước", "Ngày Sửa Đổi", "Mã SHA-256"
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
        scrollFiles.setViewportView(tblFiles);

        pnlFiles.add(scrollFiles, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Tệp Tin Trên Server", pnlFiles);

        pnlClients.setLayout(new java.awt.BorderLayout());

        tblClients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Địa Chỉ IP", "Cổng (Port)", "Thời Gian Nối", "Trạng Thái"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollClients.setViewportView(tblClients);

        pnlClients.add(scrollClients, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Clients Đang Kết Nối", pnlClients);

        pnlLogs.setLayout(new java.awt.BorderLayout());

        txtLogs.setEditable(false);
        txtLogs.setBackground(new java.awt.Color(20, 24, 32));
        txtLogs.setColumns(20);
        txtLogs.setFont(new java.awt.Font("Consolas", 0, 12));
        txtLogs.setForeground(new java.awt.Color(51, 255, 102));
        txtLogs.setRows(10);
        scrollLogs.setViewportView(txtLogs);

        pnlLogs.add(scrollLogs, java.awt.BorderLayout.CENTER);

        pnlLogControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnClearLog.setText("Xóa Nhật Ký");
        btnClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearLogActionPerformed(evt);
            }
        });
        pnlLogControls.add(btnClearLog);

        pnlLogs.add(pnlLogControls, java.awt.BorderLayout.SOUTH);

        tabbedPane.addTab("Nhật Ký Server (Logs)", pnlLogs);

        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        pnlBottom.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblStatus.setText("Trạng thái: Server đang dừng | Port: - | Clients: 0");
        pnlBottom.add(lblStatus);

        getContentPane().add(pnlBottom, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int port = Integer.parseInt(txtPort.getText().trim());
            boolean useSsl = chkSsl.isSelected();
            File rootDir = new File(txtRootDir.getText().trim());

            server = new SyncServer(port, useSsl, rootDir, this);
            server.start();

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            txtPort.setEnabled(false);
            chkSsl.setEnabled(false);
            txtRootDir.setEnabled(false);
            btnBrowse.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khởi động Server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {
        if (server != null) {
            server.stop();
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            txtPort.setEnabled(true);
            chkSsl.setEnabled(true);
            txtRootDir.setEnabled(true);
            btnBrowse.setEnabled(true);
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

    private void btnRefreshFilesActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            File rootDir = new File(txtRootDir.getText().trim());
            List<FileMetadata> files = FileSyncUtil.scanDirectory(rootDir);
            onFileListUpdated(files);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi quét thư mục: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnClearLogActionPerformed(java.awt.event.ActionEvent evt) {
        txtLogs.setText("");
    }

    private void btnOpenWebActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:8088/"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể mở trình duyệt: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onServerStarted(int port, boolean ssl, String rootDir) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(" 🟢 TRẠNG THÁI: SERVER ĐANG HOẠT ĐỘNG  |  IP: " + localIp + " | Port: " + port + (ssl ? " [SSL/TLS]" : " [TCP]") + " | Clients: " + server.getActiveClients().size());
        });
    }

    @Override
    public void onServerStopped() {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText("Trạng thái: ĐÃ DỪNG | IP Máy: " + localIp + " | Clients: 0");
            clientsTableModel.setRowCount(0);
        });
    }

    @Override
    public void onClientConnected(String clientIp, int port) {
        SwingUtilities.invokeLater(() -> {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            clientsTableModel.addRow(new Object[]{clientIp, port, time, "Đang kết nối"});
            lblStatus.setText(" 🟢 TRẠNG THÁI: SERVER ĐANG HOẠT ĐỘNG  |  IP: " + localIp + " | Clients: " + server.getActiveClients().size());
        });
    }

    @Override
    public void onClientDisconnected(String clientIp, int port) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < clientsTableModel.getRowCount(); i++) {
                if (clientIp.contains((String) clientsTableModel.getValueAt(i, 0))) {
                    clientsTableModel.removeRow(i);
                    break;
                }
            }
            lblStatus.setText(" 🟢 TRẠNG THÁI: SERVER ĐANG HOẠT ĐỘNG  |  IP: " + localIp + " | Clients: " + server.getActiveClients().size());
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
    public void onFileListUpdated(List<FileMetadata> files) {
        SwingUtilities.invokeLater(() -> {
            filesTableModel.setRowCount(0);
            for (FileMetadata f : files) {
                filesTableModel.addRow(new Object[]{
                    f.getFileName(),
                    f.getRelativePath(),
                    f.getFormattedSize(),
                    f.getFormattedLastModified(),
                    f.getSha256()
                });
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClearLog;
    private javax.swing.JButton btnOpenDir;
    private javax.swing.JButton btnOpenWeb;
    private javax.swing.JButton btnRefreshFiles;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnStop;
    private javax.swing.JCheckBox chkSsl;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblRootDir;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlClients;
    private javax.swing.JPanel pnlConfig1;
    private javax.swing.JPanel pnlConfig2;
    private javax.swing.JPanel pnlFiles;
    private javax.swing.JPanel pnlLogControls;
    private javax.swing.JPanel pnlLogs;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JScrollPane scrollClients;
    private javax.swing.JScrollPane scrollFiles;
    private javax.swing.JScrollPane scrollLogs;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tblClients;
    private javax.swing.JTable tblFiles;
    private javax.swing.JTextArea txtLogs;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtRootDir;
    // End of variables declaration//GEN-END:variables
}
