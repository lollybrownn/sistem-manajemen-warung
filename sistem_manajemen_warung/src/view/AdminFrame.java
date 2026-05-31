package view;

import controller.*;
import model.User;
import util.Theme;
import util.UI;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends BaseFrame {
    private JButton btnDash, btnProduk, btnKasir, btnTrx, btnLaporan;
    private final JButton[] allBtns;
    private final TransaksiController trxCtrl = new TransaksiController();
    private final ProdukController    prdCtrl = new ProdukController();

    public AdminFrame(User user) {
        super(user, "Admin Dashboard");
        allBtns = new JButton[]{btnDash, btnProduk, btnKasir, btnTrx, btnLaporan};
        showDashboard();
    }

    @Override
    protected void fillSidebar(JPanel sb) {
        sb.add(sidebarSection("MENU UTAMA"));
        btnDash    = navBtn("Dashboard",         true);
        btnProduk  = navBtn("Kelola Produk",     false);
        btnKasir   = navBtn("Kelola Kasir",      false);
        btnTrx     = navBtn("Riwayat Transaksi", false);
        btnLaporan = navBtn("Laporan",           false);

        // Simpan ke field (constructor belum jalan penuh, jadi pakai local ref)
        sb.add(btnDash); sb.add(btnProduk); sb.add(btnKasir); sb.add(btnTrx); sb.add(btnLaporan);

        btnDash.addActionListener(e    -> { activate(btnDash);    showDashboard(); });
        btnProduk.addActionListener(e  -> { activate(btnProduk);  showPanel(new ProdukPanel()); });
        btnKasir.addActionListener(e   -> { activate(btnKasir);   showPanel(new KasirPanel()); });
        btnTrx.addActionListener(e     -> { activate(btnTrx);     showPanel(new HistoriPanel()); });
        btnLaporan.addActionListener(e -> { activate(btnLaporan); showPanel(new LaporanPanel()); });

        sb.add(Box.createVerticalGlue());

        // Role badge bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bottom.setOpaque(false); bottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.add(UI.badge("ADMIN", Theme.ACCENT, Color.WHITE));
        sb.add(bottom);
    }

    private void activate(JButton active) {
        for (JButton b : new JButton[]{btnDash, btnProduk, btnKasir, btnTrx, btnLaporan}) {
            if (b == null) continue;
            boolean isAct = b == active;
            b.putClientProperty("active", isAct);
            b.setFont(isAct ? Theme.F_BODY_BOLD : Theme.F_BODY);
            b.setForeground(isAct ? Color.WHITE : new Color(148,163,184));
        }
        repaint();
    }

    private void showDashboard() {
        JPanel p = new JPanel(new BorderLayout(0, 18));
        p.setBackground(Theme.PAGE_BG);

        // Title
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(Theme.PAGE_BG);
        titleRow.add(UI.lblTitle("Dashboard"), BorderLayout.WEST);
        JLabel sub = UI.lbl("Selamat datang, " + currentUser.getNamaLengkap(), Theme.F_BODY, Theme.TEXT_GRAY);
        titleRow.add(sub, BorderLayout.SOUTH);

        // Stats
        int    totalProduk = prdCtrl.getProdukAktif().size();
        int    stokRendah  = prdCtrl.getProdukStokRendah().size();
        int    trxHariIni  = trxCtrl.getJumlahTrxHariIni();
        double pendHariIni = trxCtrl.getPendapatanHariIni();

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setBackground(Theme.PAGE_BG);
        stats.add(UI.statCard("Total Produk",         String.valueOf(totalProduk), "Produk aktif",          Theme.ACCENT));
        stats.add(UI.statCard("Transaksi Hari Ini",   String.valueOf(trxHariIni),  "Transaksi selesai",     Theme.SUCCESS));
        stats.add(UI.statCard("Pendapatan Hari Ini",  UI.rp(pendHariIni),          "Total penjualan hari ini",Theme.WARNING));
        stats.add(UI.statCard("Stok Rendah",          String.valueOf(stokRendah),  "Perlu segera direstok", Theme.DANGER));

        // Warning stok
        JPanel warnArea = new JPanel(new BorderLayout()); warnArea.setBackground(Theme.PAGE_BG);
        if (stokRendah > 0) {
            JPanel warn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            warn.setBackground(Theme.WARNING_LIGHT);
            warn.setBorder(new javax.swing.border.LineBorder(Theme.WARNING, 1, true));
            warn.add(UI.lbl("!", Theme.F_BODY_BOLD, Theme.WARNING));
            warn.add(UI.lbl(stokRendah + " produk memiliki stok rendah — segera lakukan restok!", Theme.F_BODY, Theme.TEXT_DARK));
            warnArea.add(warn);
        }

        // Quick actions card
        JPanel actCard = UI.card(); actCard.setLayout(new BorderLayout(0,12));
        JLabel actTitle = UI.lbl("Aksi Cepat", Theme.F_SUBTITLE, Theme.TEXT_DARK);
        JPanel actBtns  = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actBtns.setBackground(Theme.CARD_BG);
        JButton b1 = UI.btnPrimary("+ Tambah Produk");
        JButton b2 = UI.btnOutline("+ Tambah Kasir");
        JButton b3 = UI.btnOutline("Lihat Transaksi");
        b1.addActionListener(e -> { activate(btnProduk); showPanel(new ProdukPanel()); });
        b2.addActionListener(e -> { activate(btnKasir);  showPanel(new KasirPanel()); });
        b3.addActionListener(e -> { activate(btnTrx);    showPanel(new HistoriPanel()); });
        actBtns.add(b1); actBtns.add(b2); actBtns.add(b3);
        actCard.add(actTitle, BorderLayout.NORTH);
        actCard.add(new JSeparator(){{setForeground(Theme.BORDER);}}, BorderLayout.CENTER);
        actCard.add(actBtns,  BorderLayout.SOUTH);

        JPanel topSection = new JPanel(new BorderLayout(0,14));
        topSection.setBackground(Theme.PAGE_BG);
        topSection.add(titleRow, BorderLayout.NORTH);
        topSection.add(stats,    BorderLayout.CENTER);
        if (stokRendah > 0) topSection.add(warnArea, BorderLayout.SOUTH);

        p.add(topSection, BorderLayout.NORTH);
        p.add(actCard,    BorderLayout.CENTER);
        showPanel(p);
    }
}
