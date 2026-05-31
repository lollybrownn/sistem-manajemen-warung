package util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;

public class UI {

    private static final NumberFormat RUPIAH = NumberFormat.getInstance(new Locale("id","ID"));
    public static String rp(double v) { return "Rp " + RUPIAH.format(v); }

    // ── BUTTON ────────────────────────────────────────────────────────────────
    public static JButton btn(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            private boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS, Theme.RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.F_BODY_BOLD); b.setForeground(fg);
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }
    public static JButton btnPrimary(String t) { return btn(t, Theme.ACCENT, Color.WHITE); }
    public static JButton btnDanger(String t)  { return btn(t, Theme.DANGER, Color.WHITE); }
    public static JButton btnSuccess(String t) { return btn(t, Theme.SUCCESS, Color.WHITE); }
    public static JButton btnOutline(String t) {
        JButton b = btn(t, Color.WHITE, Theme.TEXT_DARK);
        b.setBorder(new CompoundBorder(
            new LineBorder(Theme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(7, 17, 7, 17)));
        return b;
    }

    // ── TEXT FIELD ────────────────────────────────────────────────────────────
    public static JTextField field(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Theme.TEXT_LIGHT);
                    g2.setFont(Theme.F_BODY);
                    g2.drawString(placeholder, 12, getHeight()/2 + 5);
                }
            }
        };
        styleField(f); return f;
    }

    public static JPasswordField passField() {
        JPasswordField f = new JPasswordField();
        styleField(f); return f;
    }

    private static void styleField(JTextField f) {
        f.setFont(Theme.F_BODY); f.setBackground(Color.WHITE);
        f.setForeground(Theme.TEXT_DARK); f.setCaretColor(Theme.ACCENT);
        f.setBorder(fieldBorder(Theme.BORDER));
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 38));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.setBorder(fieldBorder(Theme.BORDER_FOCUS)); }
            public void focusLost(FocusEvent e)   { f.setBorder(fieldBorder(Theme.BORDER)); }
        });
    }

    private static Border fieldBorder(Color c) {
        return new CompoundBorder(
            new LineBorder(c, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    public static JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(Theme.F_BODY); c.setBackground(Color.WHITE);
        c.setForeground(Theme.TEXT_DARK);
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 38));
        return c;
    }

    public static JSpinner spinner(int val, int min, int max) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, 1));
        sp.setFont(Theme.F_BODY);
        ((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setFont(Theme.F_BODY);
        sp.setPreferredSize(new Dimension(80, 38));
        return sp;
    }

    // ── LABEL ─────────────────────────────────────────────────────────────────
    public static JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); return l;
    }
    public static JLabel lblTitle(String t)  { return lbl(t, Theme.F_TITLE,      Theme.TEXT_DARK); }
    public static JLabel lblBody(String t)   { return lbl(t, Theme.F_BODY,       Theme.TEXT_DARK); }
    public static JLabel lblSmall(String t)  { return lbl(t, Theme.F_SMALL,      Theme.TEXT_GRAY); }
    public static JLabel lblBold(String t)   { return lbl(t, Theme.F_BODY_BOLD,  Theme.TEXT_DARK); }

    public static JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        l.setFont(Theme.F_SMALL_BOLD); l.setForeground(fg); l.setOpaque(false);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setBorder(BorderFactory.createEmptyBorder(2, 9, 2, 9)); return l;
    }

    // ── PANEL ─────────────────────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel(); p.setBackground(Theme.CARD_BG);
        p.setBorder(new CompoundBorder(
            new ShadowBorder(), BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static JPanel statCard(String title, String value, String sub, Color accent) {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(Theme.CARD_BG);
        p.setBorder(new CompoundBorder(new ShadowBorder(),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;

        // Accent bar
        JPanel bar = new JPanel(); bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(32, 4)); bar.setMaximumSize(new Dimension(32, 4));
        g.gridy=0; g.insets=new Insets(0,0,12,0); p.add(bar, g);

        g.gridy=1; g.insets=new Insets(0,0,4,0);
        p.add(lbl(value, new Font("Segoe UI", Font.BOLD, 22), Theme.TEXT_DARK), g);

        g.gridy=2; g.insets=new Insets(0,0,2,0);
        p.add(lbl(title, Theme.F_BODY_BOLD, Theme.TEXT_GRAY), g);

        g.gridy=3; g.insets=new Insets(0,0,0,0);
        p.add(lbl(sub, Theme.F_SMALL, Theme.TEXT_LIGHT), g);
        return p;
    }

    public static JPanel row(int hgap) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, 0));
        p.setOpaque(false); return p;
    }

    // ── TABLE ─────────────────────────────────────────────────────────────────
    public static void styleTable(JTable t) {
        t.setFont(Theme.F_BODY); t.setRowHeight(34);
        t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,0));
        t.setBackground(Theme.CARD_BG); t.setForeground(Theme.TEXT_DARK);
        t.setSelectionBackground(Theme.TABLE_SELECT);
        t.setSelectionForeground(Theme.TEXT_DARK);

        JTableHeader h = t.getTableHeader();
        h.setFont(Theme.F_SMALL_BOLD); h.setBackground(Theme.TABLE_STRIPE);
        h.setForeground(Theme.TEXT_GRAY); h.setReorderingAllowed(false);
        h.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Theme.BORDER));
        h.setPreferredSize(new Dimension(0, 36));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl,val,sel,foc,row,col);
                if (!sel) c.setBackground(row%2==0 ? Theme.CARD_BG : Theme.TABLE_STRIPE);
                setBorder(BorderFactory.createEmptyBorder(0,12,0,12));
                return c;
            }
        });
    }

    public static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(new LineBorder(Theme.BORDER, 1, true));
        sp.getViewport().setBackground(Theme.CARD_BG);
        sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new ThinScrollBarUI());
        return sp;
    }

    // ── SEPARATOR ─────────────────────────────────────────────────────────────
    public static JSeparator sep() {
        JSeparator s = new JSeparator(); s.setForeground(Theme.BORDER); return s;
    }

    // ── DIALOGS ───────────────────────────────────────────────────────────────
    public static void error(Component p, String msg)   { JOptionPane.showMessageDialog(p, msg, "Error",    JOptionPane.ERROR_MESSAGE); }
    public static void success(Component p, String msg) { JOptionPane.showMessageDialog(p, msg, "Berhasil", JOptionPane.INFORMATION_MESSAGE); }
    public static boolean confirm(Component p, String msg) {
        return JOptionPane.showConfirmDialog(p, msg, "Konfirmasi",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // ── INNER: Shadow Border ──────────────────────────────────────────────────
    public static class ShadowBorder extends AbstractBorder {
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0,0,0,10)); g2.fillRoundRect(x+1,y+1,w-1,h-1,Theme.RADIUS,Theme.RADIUS);
            g2.setColor(Theme.BORDER); g2.drawRoundRect(x,y,w-1,h-1,Theme.RADIUS,Theme.RADIUS);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(1,1,2,2); }
    }

    // ── INNER: Thin ScrollBar ─────────────────────────────────────────────────
    public static class ThinScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(180,190,210); trackColor = new Color(240,242,248);
        }
        @Override protected JButton createDecreaseButton(int o) { return emptyBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return emptyBtn(); }
        private JButton emptyBtn() {
            JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6);
            g2.dispose();
        }
    }
}
