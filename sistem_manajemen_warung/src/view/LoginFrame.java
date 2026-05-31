package view;

import controller.AuthController;
import model.User;
import util.Theme;
import util.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private final AuthController auth = new AuthController();
    private JTextField    txtUser;
    private JPasswordField txtPass;
    private JLabel        lblError;
    private JButton       btnLogin;

    public LoginFrame() {
        setTitle("Warung Saya - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));

        // ── LEFT: branding ──────────────────────────────────────────────────
        JPanel left = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, Theme.SIDEBAR_BG, getWidth(), getHeight(), new Color(30,58,138)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative circles
                g2.setColor(new Color(255,255,255,8));
                g2.fillOval(-80,-80,280,280);
                g2.fillOval(getWidth()-120,getHeight()-160,240,240);
            }
        };
        GridBagConstraints gb = new GridBagConstraints();
        gb.gridx=0; gb.fill=GridBagConstraints.HORIZONTAL; gb.weightx=1;
        gb.insets = new Insets(0, 36, 0, 36);

        // Logo
        JPanel logoCircle = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,20)); g2.fillOval(0,0,70,70);
                g2.setColor(Theme.ACCENT_LIGHT); g2.setFont(new Font("Segoe UI",Font.BOLD,32));
                g2.drawString("W", 17, 50);
            }
        };
        logoCircle.setPreferredSize(new Dimension(70,70)); logoCircle.setOpaque(false);
        gb.gridy=0; gb.insets=new Insets(0,36,0,36); gb.anchor=GridBagConstraints.CENTER;
        gb.fill=GridBagConstraints.NONE;
        left.add(logoCircle, gb);

        gb.gridy=1; gb.insets=new Insets(16,36,0,36); gb.fill=GridBagConstraints.HORIZONTAL;
        JLabel lblApp = new JLabel("Warung Saya", SwingConstants.CENTER);
        lblApp.setFont(new Font("Segoe UI",Font.BOLD,26)); lblApp.setForeground(Color.WHITE);
        left.add(lblApp, gb);

        gb.gridy=2; gb.insets=new Insets(6,36,0,36);
        JLabel lblSub = new JLabel("Sistem Manajemen Warung", SwingConstants.CENTER);
        lblSub.setFont(Theme.F_BODY); lblSub.setForeground(new Color(148,163,184));
        left.add(lblSub, gb);

        gb.gridy=3; gb.insets=new Insets(28,48,0,48);

        // ── RIGHT: form ─────────────────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout()); right.setBackground(new Color(248,250,252));
        JPanel form  = new JPanel(); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(new Color(248,250,252));
        form.setBorder(BorderFactory.createEmptyBorder(0,44,0,44));

        JLabel lbTitle = new JLabel("Selamat Datang");
        lbTitle.setFont(new Font("Segoe UI",Font.BOLD,24)); lbTitle.setForeground(Theme.TEXT_DARK);
        lbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbSub = UI.lbl("Masuk ke akun Anda", Theme.F_BODY, Theme.TEXT_GRAY);
        lbSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUser = UI.field("Masukkan username");
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        txtPass = UI.passField();
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        lblError = new JLabel(" ");
        lblError.setFont(Theme.F_SMALL); lblError.setForeground(Theme.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = UI.btnPrimary("Masuk");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addActionListener(e -> doLogin());
        txtPass.addActionListener(e -> doLogin());
        txtUser.addActionListener(e -> txtPass.requestFocus());

        form.add(lbTitle);
        form.add(Box.createVerticalStrut(6));
        form.add(lbSub);
        form.add(Box.createVerticalStrut(28));
        form.add(UI.lbl("Username", Theme.F_BODY_BOLD, Theme.TEXT_DARK));
        form.add(Box.createVerticalStrut(6));
        form.add(txtUser);
        form.add(Box.createVerticalStrut(14));
        form.add(UI.lbl("Password", Theme.F_BODY_BOLD, Theme.TEXT_DARK));
        form.add(Box.createVerticalStrut(6));
        form.add(txtPass);
        form.add(Box.createVerticalStrut(6));
        form.add(lblError);
        form.add(Box.createVerticalStrut(16));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(20));

        right.add(form);
        root.add(left); root.add(right);
        return root;
    }

    private void doLogin() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.isEmpty()||p.isEmpty()) { lblError.setText("Username & password wajib diisi!"); return; }
        btnLogin.setEnabled(false); btnLogin.setText("Memproses...");
        new SwingWorker<User,Void>() {
            protected User doInBackground() { return auth.login(u, p); }
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        dispose();
                        if ("ADMIN".equals(user.getRole())) new AdminFrame(user).setVisible(true);
                        else new KasirFrame(user).setVisible(true);
                    } else {
                        lblError.setText("Username atau password salah!");
                        txtPass.setText(""); btnLogin.setEnabled(true); btnLogin.setText("Masuk");
                    }
                } catch (Exception ex) {
                    lblError.setText("Error: " + ex.getMessage());
                    btnLogin.setEnabled(true); btnLogin.setText("Masuk");
                }
            }
        }.execute();
    }
}
