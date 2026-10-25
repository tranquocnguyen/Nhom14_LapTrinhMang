package com.nhom14.filesync.gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Bộ giao diện hiện đại, xu hướng trẻ trung (GenZ / Modern SaaS UI Theme)
 * Thiết kế theo phong cách Flat Minimalist, màu sắc Indigo/Emerald/Slate
 * cá nhân hoá dành cho Nhóm 14 - Lớp 23DTHB1 - HUTECH
 *
 * @author Nguyễn Trung Trực (MSSV: 2380602385)
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class ModernTheme {

    // Bảng màu hiện đại (Modern Color Palette)
    public static final Color PRIMARY = new Color(79, 70, 229);        // Indigo 600
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202);  // Indigo 700
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255);// Indigo 50

    public static final Color SUCCESS = new Color(16, 185, 129);       // Emerald 500
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);  // Emerald 600
    public static final Color SUCCESS_LIGHT = new Color(236, 253, 245);// Emerald 50

    public static final Color DANGER = new Color(239, 68, 68);         // Rose 500
    public static final Color DANGER_HOVER = new Color(220, 38, 38);   // Rose 600
    public static final Color DANGER_LIGHT = new Color(254, 242, 242); // Rose 50

    public static final Color WARNING = new Color(245, 158, 11);       // Amber 500
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);// Amber 50

    public static final Color SLATE_900 = new Color(15, 23, 42);       // Slate 900 (Dark Header)
    public static final Color SLATE_800 = new Color(30, 41, 59);       // Slate 800
    public static final Color SLATE_700 = new Color(51, 65, 85);       // Slate 700
    public static final Color SLATE_600 = new Color(71, 85, 105);      // Slate 600
    public static final Color SLATE_200 = new Color(226, 232, 240);    // Slate 200 (Borders)
    public static final Color SLATE_100 = new Color(241, 245, 249);    // Slate 100
    public static final Color SLATE_50 = new Color(248, 250, 252);     // Slate 50 (Alt rows)

    public static final Color TERMINAL_BG = new Color(9, 13, 22);      // Terminal Deep Black
    public static final Color TERMINAL_FG = new Color(56, 189, 248);    // Cyan Sky Blue

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TERMINAL = new Font("Consolas", Font.PLAIN, 12);

    /**
     * Định dạng Button hiện đại theo loại (Primary, Success, Danger, Warning, Neutral)
     */
    public static void styleButton(JButton btn, String type) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(SLATE_200, 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));

        Color bg;
        Color fg = Color.WHITE;
        Color hover;

        switch (type.toLowerCase()) {
            case "primary" -> {
                bg = PRIMARY;
                hover = PRIMARY_HOVER;
            }
            case "success" -> {
                bg = SUCCESS;
                hover = SUCCESS_HOVER;
            }
            case "danger" -> {
                bg = DANGER;
                hover = DANGER_HOVER;
            }
            case "warning" -> {
                bg = WARNING;
                hover = new Color(217, 119, 6);
            }
            default -> { // Neutral / Outline
                bg = SLATE_100;
                fg = SLATE_800;
                hover = SLATE_200;
            }
        }

        btn.setBackground(bg);
        btn.setForeground(fg);

        // Hiệu ứng hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hover);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                }
            }
        });
    }

    /**
     * Định dạng JTable theo phong cách hiện đại (Clean Modern Table)
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_REGULAR);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(224, 231, 255)); // Indigo Tint
        table.setSelectionForeground(new Color(30, 27, 75));
        table.setGridColor(SLATE_200);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(SLATE_800);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                return lbl;
            }
        });
    }

    /**
     * Định dạng Terminal Log hiện đại (Dark Cyber Console)
     */
    public static void styleTerminalLog(JTextArea txtLog) {
        txtLog.setBackground(TERMINAL_BG);
        txtLog.setForeground(TERMINAL_FG);
        txtLog.setCaretColor(TERMINAL_FG);
        txtLog.setFont(FONT_TERMINAL);
        txtLog.setMargin(new Insets(10, 12, 10, 12));
    }

    /**
     * Định dạng ProgressBar hiện đại
     */
    public static void styleProgressBar(JProgressBar prg, Color barColor) {
        prg.setStringPainted(true);
        prg.setFont(new Font("Segoe UI", Font.BOLD, 11));
        prg.setForeground(barColor != null ? barColor : SUCCESS);
        prg.setBackground(SLATE_200);
        prg.setBorder(new EmptyBorder(1, 1, 1, 1));
        prg.setPreferredSize(new Dimension(prg.getPreferredSize().width, 22));
    }

    /**
     * Tạo Panel huy hiệu cá nhân hoá Nhóm 14
     */
    public static JPanel createTeamBadgePanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnl.setOpaque(false);

        JLabel lblBadge = new JLabel("Lớp 23DTHB1 • Nhóm 14");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(PRIMARY);
        lblBadge.setBackground(PRIMARY_LIGHT);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(199, 210, 254), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));

        JLabel lblSec = new JLabel("🔒 TLSv1.3 PKCS12");
        lblSec.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSec.setForeground(SUCCESS_HOVER);
        lblSec.setBackground(SUCCESS_LIGHT);
        lblSec.setOpaque(true);
        lblSec.setBorder(new CompoundBorder(
                new LineBorder(new Color(167, 243, 208), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));

        pnl.add(lblBadge);
        pnl.add(lblSec);
        return pnl;
    }
}
