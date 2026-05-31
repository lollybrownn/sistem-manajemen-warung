package view;

import controller.AuthController;
import model.User;
import util.Theme;
import util.UI;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseFrame extends JFrame {
    protected final User currentUser;
    protected JPanel contentArea;
    private JLabel lblClock;
    private Timer  clockTimer;

    public BaseFrame(User user, String title) {
        this.currentUser = user;
        setTitle("Warung Saya | " + title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 700);
        setMinimumSize(new Dimension(960, 580));
        setLocationRelativeTo(null);
        buildLayout();
        startClock();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                clockTimer.stop();
                database.DBConnection.closeConnection();
            }
        });
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.PAGE_BG);
        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.PAGE_BG);
        center.add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Theme.PAGE_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.add(contentArea, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── TOP BAR ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.HEADER_BG);
        bar.setPreferredSize(new Dimension(0, Theme.TOPBAR_H));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));

        // Left: logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JPanel logoIcon = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT); g2.fillRoundRect(0,8,30,30,6,6);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI",Font.BOLD,14));
                g2.drawString("W", 8, 28);
            }
        };
        logoIcon.setOpaque(false); logoIcon.setPreferredSize(new Dimension(30, Theme.TOPBAR_H));

        JLabel lblLogo = new JLabel("  Warung Saya");
        lblLogo.setFont(Theme.F_LOGO); lblLogo.setForeground(Theme.TEXT_DARK);
        logoPanel.add(logoIcon); logoPanel.add(lblLogo);

        // Right: clock + user
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));

        lblClock = UI.lbl("", Theme.F_SMALL, Theme.TEXT_GRAY);

        // Avatar
        JPanel avatar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color ac = "ADMIN".equals(currentUser.getRole()) ? Theme.ACCENT : Theme.SUCCESS;
                g2.setColor(ac); g2.fillOval(0,4,30,30);
                g2.setColor(Color.WHITE); g2.setFont(Theme.F_BODY_BOLD);
                String ini = currentUser.getNamaLengkap().substring(0,1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini, (30-fm.stringWidth(ini))/2, 24);
            }
        };
        avatar.setOpaque(false); avatar.setPreferredSize(new Dimension(30, Theme.TOPBAR_H));

        JPanel userInfo = new JPanel(new GridLayout(2,1));
        userInfo.setOpaque(false);
        userInfo.add(UI.lbl(currentUser.getNamaLengkap(), Theme.F_SMALL_BOLD, Theme.TEXT_DARK));
        userInfo.add(UI.lbl("ADMIN".equals(currentUser.getRole())?"Administrator":"Kasir", Theme.F_SMALL, Theme.TEXT_GRAY));

        JSeparator vs = new JSeparator(JSeparator.VERTICAL);
        vs.setPreferredSize(new Dimension(1, 24)); vs.setForeground(Theme.BORDER);

        JButton btnOut = UI.btnOutline("Keluar");
        btnOut.setFont(Theme.F_SMALL); btnOut.addActionListener(e -> doLogout());

        rightPanel.add(lblClock); rightPanel.add(vs);
        rightPanel.add(avatar); rightPanel.add(userInfo); rightPanel.add(btnOut);

        bar.add(logoPanel, BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    // ── SIDEBAR ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Theme.SIDEBAR_BG); g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(Theme.SIDEBAR_W, 0));
        sidebar.setOpaque(false);
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, new Color(30,41,59)));
        fillSidebar(sidebar);
        return sidebar;
    }

    /** Override di subclass untuk isi nav items */
    protected abstract void fillSidebar(JPanel sidebar);

    // ── NAV BUTTON ────────────────────────────────────────────────────────────
    protected JButton navBtn(String label, boolean active) {
        JButton b = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean act  = Boolean.TRUE.equals(getClientProperty("active"));
                boolean hov  = getModel().isRollover();
                if (act) {
                    g2.setColor(new Color(37,99,235,220));
                    g2.fillRoundRect(6, 3, getWidth()-12, getHeight()-6, 8, 8);
                } else if (hov) {
                    g2.setColor(new Color(255,255,255,10));
                    g2.fillRoundRect(6, 3, getWidth()-12, getHeight()-6, 8, 8);
                }
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.putClientProperty("active", active);
        b.setFont(active ? Theme.F_BODY_BOLD : Theme.F_BODY);
        b.setForeground(active ? Color.WHITE : new Color(148,163,184));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(b.getClientProperty("active"))) b.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(b.getClientProperty("active"))) b.setForeground(new Color(148,163,184));
            }
        });
        return b;
    }

    protected void setActive(JButton active, JButton... all) {
        for (JButton b : all) {
            boolean isActive = b == active;
            b.putClientProperty("active", isActive);
            b.setFont(isActive ? Theme.F_BODY_BOLD : Theme.F_BODY);
            b.setForeground(isActive ? Color.WHITE : new Color(148,163,184));
        }
        repaint();
    }

    protected JLabel sidebarSection(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.F_SMALL_BOLD); l.setForeground(new Color(71,85,105));
        l.setBorder(BorderFactory.createEmptyBorder(18, 18, 6, 18));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ── CONTENT ───────────────────────────────────────────────────────────────
    protected void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate(); contentArea.repaint();
    }

    // ── CLOCK ─────────────────────────────────────────────────────────────────
    private void startClock() {
        clockTimer = new Timer(1000, e -> lblClock.setText(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm:ss"))));
        clockTimer.setInitialDelay(0); clockTimer.start();
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────────
    private void doLogout() {
        if (UI.confirm(this, "Yakin ingin keluar?")) {
            clockTimer.stop(); new AuthController().logout();
            dispose(); new LoginFrame().setVisible(true);
        }
    }
}
