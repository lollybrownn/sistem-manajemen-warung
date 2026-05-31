package view;

import model.User;
import util.Theme;
import util.UI;

import javax.swing.*;
import java.awt.*;

public class KasirFrame extends BaseFrame {
    private JButton btnPOS, btnHistori, btnProduk;

    public KasirFrame(User user) {
        super(user, "Kasir");
        showPOS();
    }

    @Override
    protected void fillSidebar(JPanel sb) {
        sb.add(sidebarSection("MENU KASIR"));
        btnPOS     = navBtn("Kasir / POS",       true);
        btnHistori = navBtn("Riwayat Transaksi",  false);
        btnProduk  = navBtn("Daftar Produk",      false);

        sb.add(btnPOS); sb.add(btnHistori); sb.add(btnProduk);

        btnPOS.addActionListener(e     -> { activate(btnPOS);     showPOS(); });
        btnHistori.addActionListener(e -> { activate(btnHistori); showPanel(new HistoriPanel()); });
        btnProduk.addActionListener(e  -> { activate(btnProduk);  showPanel(new ProdukReadonlyPanel()); });

        sb.add(Box.createVerticalGlue());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bottom.setOpaque(false); bottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.add(UI.badge("KASIR", Theme.SUCCESS, Color.WHITE));
        sb.add(bottom);
    }

    private void activate(JButton active) {
        for (JButton b : new JButton[]{btnPOS, btnHistori, btnProduk}) {
            if (b == null) continue;
            boolean isAct = b == active;
            b.putClientProperty("active", isAct);
            b.setFont(isAct ? Theme.F_BODY_BOLD : Theme.F_BODY);
            b.setForeground(isAct ? Color.WHITE : new Color(148, 163, 184));
        }
        repaint();
    }

    private void showPOS() {
        showPanel(new POSPanel(currentUser));
    }
}
