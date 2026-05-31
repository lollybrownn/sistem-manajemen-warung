package view;

import controller.ProdukController;
import controller.TransaksiController;
import model.*;
import util.Theme;
import util.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class POSPanel extends JPanel {
    private final TransaksiController trxCtrl = new TransaksiController();
    private final ProdukController    prdCtrl = new ProdukController();
    private final User                kasir;
    private Transaksi                 transaksi;

    // Left panel controls
    private JTextField    txtCari;
    private JComboBox<String> cbProduk;
    private JSpinner      spQty;
    private JLabel        lblInfoNama, lblInfoHarga, lblInfoStok;

    // Right panel controls
    private JTable           tblKeranjang;
    private DefaultTableModel mdlKeranjang;
    private JLabel           lblNoTrx, lblTotal;
    private JTextField       txtBayar;
    private JLabel           lblKembalian;

    private static final String[] COLS = {"Kode", "Nama Produk", "Qty", "Harga", "Subtotal"};

    public POSPanel(User kasir) {
        this.kasir = kasir;
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.PAGE_BG);
        buildUI();
        mulaiTransaksiBaru();
    }

    private void buildUI() {
        // Header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PAGE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(Theme.PAGE_BG);
        JLabel title = UI.lblTitle("Point of Sale (POS)");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNoTrx = UI.lbl("No. Transaksi: -", Theme.F_SMALL, Theme.TEXT_GRAY);
        lblNoTrx.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBox.add(title); titleBox.add(Box.createVerticalStrut(2)); titleBox.add(lblNoTrx);

        JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerBtns.setBackground(Theme.PAGE_BG);
        JButton btnBaru = UI.btnOutline("Transaksi Baru");
        JButton btnBatal = UI.btnDanger("Batalkan");
        btnBaru.addActionListener(e -> mulaiTransaksiBaru());
        btnBatal.addActionListener(e -> batalkan());
        headerBtns.add(btnBaru); headerBtns.add(btnBatal);

        header.add(titleBox, BorderLayout.WEST);
        header.add(headerBtns, BorderLayout.EAST);

        // Main split: LEFT (input) | RIGHT (cart + payment)
        // Gunakan GridLayout supaya tidak ada yang kepotong
        JPanel main = new JPanel(new GridLayout(1, 2, 14, 0));
        main.setBackground(Theme.PAGE_BG);
        main.add(buildLeftPanel());
        main.add(buildRightPanel());

        add(header, BorderLayout.NORTH);
        add(main,   BorderLayout.CENTER);
    }

    // ── LEFT: Pilih Produk ────────────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setBackground(Theme.PAGE_BG);

        // ---- Card: Pilih Produk ----
        JPanel cardPilih = UI.card();
        cardPilih.setLayout(new BorderLayout(0, 0));

        JLabel cardTitle = UI.lbl("Pilih Produk", Theme.F_SUBTITLE, Theme.TEXT_DARK);
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setBackground(Theme.CARD_BG);
        cardHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        cardHeader.add(cardTitle, BorderLayout.WEST);

        JPanel cardBody = new JPanel();
        cardBody.setLayout(new BoxLayout(cardBody, BoxLayout.Y_AXIS));
        cardBody.setBackground(Theme.CARD_BG);

        // Cari
        addLabelRow(cardBody, "Cari Produk");
        txtCari = UI.field("Ketik nama / kode...");
        txtCari.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtCari.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardBody.add(txtCari);
        cardBody.add(Box.createVerticalStrut(10));

        // Dropdown
        addLabelRow(cardBody, "Pilih dari Daftar");
        cbProduk = new JComboBox<>();
        cbProduk.setFont(Theme.F_BODY);
        cbProduk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cbProduk.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardBody.add(cbProduk);
        cardBody.add(Box.createVerticalStrut(10));

        // Qty
        addLabelRow(cardBody, "Jumlah");
        spQty = UI.spinner(1, 1, 9999);
        spQty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        spQty.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardBody.add(spQty);
        cardBody.add(Box.createVerticalStrut(14));

        JButton btnTambah = UI.btnSuccess("+ Tambah ke Keranjang");
        btnTambah.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnTambah.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTambah.addActionListener(e -> tambahItem());
        cardBody.add(btnTambah);

        JPanel cardContent = new JPanel(new BorderLayout(0, 0));
        cardContent.setBackground(Theme.CARD_BG);
        cardContent.add(cardHeader, BorderLayout.NORTH);
        cardContent.add(cardBody,   BorderLayout.CENTER);
        cardPilih.add(cardContent, BorderLayout.CENTER);

        // ---- Card: Info Produk ----
        JPanel cardInfo = UI.card();
        cardInfo.setLayout(new BorderLayout(0, 0));

        JLabel infoTitle = UI.lbl("Info Produk Dipilih", Theme.F_SUBTITLE, Theme.TEXT_DARK);
        JPanel infoHeader = new JPanel(new BorderLayout());
        infoHeader.setBackground(Theme.CARD_BG);
        infoHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        infoHeader.add(infoTitle, BorderLayout.WEST);

        JPanel infoBody = new JPanel(new GridLayout(3, 1, 0, 6));
        infoBody.setBackground(Theme.CARD_BG);
        lblInfoNama  = UI.lbl("— Belum ada produk dipilih —", Theme.F_BODY, Theme.TEXT_GRAY);
        lblInfoHarga = UI.lbl("", Theme.F_BODY, Theme.TEXT_GRAY);
        lblInfoStok  = UI.lbl("", Theme.F_BODY, Theme.TEXT_GRAY);
        infoBody.add(lblInfoNama); infoBody.add(lblInfoHarga); infoBody.add(lblInfoStok);

        JPanel infoContent = new JPanel(new BorderLayout());
        infoContent.setBackground(Theme.CARD_BG);
        infoContent.add(infoHeader, BorderLayout.NORTH);
        infoContent.add(infoBody,   BorderLayout.CENTER);
        cardInfo.add(infoContent, BorderLayout.CENTER);

        // Listeners
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshDropdown(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshDropdown(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        cbProduk.addActionListener(e -> updateInfoProduk());

        wrap.add(cardPilih, BorderLayout.CENTER);
        wrap.add(cardInfo,  BorderLayout.SOUTH);
        return wrap;
    }

    private void addLabelRow(JPanel parent, String text) {
        JLabel lbl = UI.lbl(text, Theme.F_BODY_BOLD, Theme.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
    }

    // ── RIGHT: Keranjang + Pembayaran ─────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setBackground(Theme.PAGE_BG);

        // ---- Card: Keranjang ----
        JPanel cardKeranjang = UI.card();
        cardKeranjang.setLayout(new BorderLayout(0, 10));

        JPanel kHeader = new JPanel(new BorderLayout());
        kHeader.setBackground(Theme.CARD_BG);
        kHeader.add(UI.lbl("Keranjang Belanja", Theme.F_SUBTITLE, Theme.TEXT_DARK), BorderLayout.WEST);
        JButton btnHapus = UI.btnDanger("Hapus Item");
        btnHapus.addActionListener(e -> hapusItem());
        kHeader.add(btnHapus, BorderLayout.EAST);

        mdlKeranjang = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKeranjang = new JTable(mdlKeranjang);
        UI.styleTable(tblKeranjang);
        int[] kW = {70, 160, 45, 100, 105};
        for (int i = 0; i < kW.length; i++)
            tblKeranjang.getColumnModel().getColumn(i).setPreferredWidth(kW[i]);

        JScrollPane kScroll = UI.scroll(tblKeranjang);
        // Beri preferredSize supaya tidak kepotong
        kScroll.setPreferredSize(new Dimension(0, 180));

        cardKeranjang.add(kHeader,  BorderLayout.NORTH);
        cardKeranjang.add(kScroll,  BorderLayout.CENTER);

        // ---- Card: Pembayaran ----
        JPanel cardBayar = UI.card();
        cardBayar.setLayout(new BorderLayout(0, 0));

        JPanel bayarBody = new JPanel();
        bayarBody.setLayout(new BoxLayout(bayarBody, BoxLayout.Y_AXIS));
        bayarBody.setBackground(Theme.CARD_BG);

        // Total box
        JPanel totalBox = new JPanel(new BorderLayout());
        totalBox.setBackground(new Color(239, 246, 255));
        totalBox.setBorder(new CompoundBorder(
            new LineBorder(new Color(191, 219, 254), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        totalBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        totalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalBox.add(UI.lbl("TOTAL BELANJA", Theme.F_SMALL_BOLD, Theme.TEXT_GRAY), BorderLayout.WEST);
        lblTotal = UI.lbl("Rp 0", new Font("Segoe UI", Font.BOLD, 20), Theme.ACCENT);
        totalBox.add(lblTotal, BorderLayout.EAST);
        bayarBody.add(totalBox);
        bayarBody.add(Box.createVerticalStrut(12));

        // Jumlah bayar
        addLabelBoxed(bayarBody, "Jumlah Bayar (Rp)");
        txtBayar = UI.field("Masukkan nominal...");
        txtBayar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtBayar.setAlignmentX(Component.LEFT_ALIGNMENT);
        bayarBody.add(txtBayar);
        bayarBody.add(Box.createVerticalStrut(8));

        // Tombol cepat
        JPanel qPanel = new JPanel(new GridLayout(1, 4, 6, 0));
        qPanel.setBackground(Theme.CARD_BG);
        qPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        qPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        long[] qAmounts = {5000, 10000, 20000, 50000};
        for (long amt : qAmounts) {
            String label = amt >= 1000 ? "+" + (amt / 1000) + "rb" : "+" + amt;
            JButton qb = UI.btnOutline(label);
            qb.setFont(Theme.F_SMALL); qb.setMargin(new Insets(2, 4, 2, 4));
            final long a = amt;
            qb.addActionListener(e -> {
                try {
                    long cur = Long.parseLong(txtBayar.getText().replaceAll("[^0-9]", ""));
                    txtBayar.setText(String.valueOf(cur + a));
                } catch (Exception ex) {
                    txtBayar.setText(String.valueOf(a));
                }
                hitungKembalian();
            });
            qPanel.add(qb);
        }
        bayarBody.add(qPanel);
        bayarBody.add(Box.createVerticalStrut(12));

        // Kembalian
        addLabelBoxed(bayarBody, "Kembalian");
        lblKembalian = UI.lbl("Rp 0", Theme.F_SUBTITLE, Theme.SUCCESS);
        lblKembalian.setAlignmentX(Component.LEFT_ALIGNMENT);
        bayarBody.add(lblKembalian);
        bayarBody.add(Box.createVerticalStrut(14));

        // Tombol bayar
        JButton btnBayar = UI.btnSuccess("PROSES PEMBAYARAN");
        btnBayar.setFont(Theme.F_SUBTITLE);
        btnBayar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnBayar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBayar.addActionListener(e -> prosesBayar());
        bayarBody.add(btnBayar);

        txtBayar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { hitungKembalian(); }
        });

        cardBayar.add(UI.lbl("Pembayaran", Theme.F_SUBTITLE, Theme.TEXT_DARK), BorderLayout.NORTH);
        cardBayar.add(new JSeparator(){{setForeground(Theme.BORDER); setBorder(BorderFactory.createEmptyBorder(8,0,8,0));}}, BorderLayout.CENTER);
        cardBayar.add(bayarBody, BorderLayout.SOUTH);

        wrap.add(cardKeranjang, BorderLayout.CENTER);
        wrap.add(cardBayar,     BorderLayout.SOUTH);
        return wrap;
    }

    private void addLabelBoxed(JPanel parent, String text) {
        JLabel lbl = UI.lbl(text, Theme.F_BODY_BOLD, Theme.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
    }

    // ── LOGIC ─────────────────────────────────────────────────────────────────
    private void mulaiTransaksiBaru() {
        transaksi = trxCtrl.mulaiTransaksi(kasir);
        lblNoTrx.setText("No. Transaksi: " + transaksi.getNomorTransaksi());
        mdlKeranjang.setRowCount(0);
        lblTotal.setText("Rp 0");
        lblKembalian.setText("Rp 0");
        txtBayar.setText("");
        spQty.setValue(1);
        refreshDropdown();
    }

    private void refreshDropdown() {
        String kw = txtCari.getText().trim();
        List<Produk> list = kw.isEmpty() ? prdCtrl.getProdukAktif() : prdCtrl.cariProduk(kw);
        cbProduk.removeAllItems();
        for (Produk p : list) {
            if (!p.isStokHabis())
                cbProduk.addItem("[" + p.getKode() + "] " + p.getNama() + " - " + UI.rp(p.getHargaJual()));
        }
        updateInfoProduk();
    }

    private void updateInfoProduk() {
        Produk p = getProdukDipilih();
        if (p == null) {
            lblInfoNama.setText("— Belum ada produk dipilih —");
            lblInfoNama.setForeground(Theme.TEXT_GRAY);
            lblInfoHarga.setText(""); lblInfoStok.setText("");
        } else {
            lblInfoNama.setFont(Theme.F_BODY_BOLD);
            lblInfoNama.setForeground(Theme.TEXT_DARK);
            lblInfoNama.setText(p.getNama());
            lblInfoHarga.setText("Harga: " + UI.rp(p.getHargaJual()) + " / " + p.getSatuan());
            lblInfoStok.setText("Stok tersedia: " + p.getStok() + " " + p.getSatuan());
            lblInfoStok.setForeground(p.isStokRendah() ? Theme.DANGER : Theme.SUCCESS);
        }
    }

    private Produk getProdukDipilih() {
        String sel = (String) cbProduk.getSelectedItem();
        if (sel == null || sel.isEmpty()) return null;
        try {
            String kode = sel.substring(1, sel.indexOf("]")).trim();
            return prdCtrl.getProdukByKode(kode);
        } catch (Exception e) { return null; }
    }

    private void tambahItem() {
        Produk p = getProdukDipilih();
        if (p == null) { UI.error(this, "Pilih produk terlebih dahulu!"); return; }
        int qty = (Integer) spQty.getValue();
        String hasil = trxCtrl.tambahItem(transaksi, p, qty);
        if ("SUCCESS".equals(hasil)) {
            refreshKeranjang(); spQty.setValue(1);
        } else {
            UI.error(this, hasil);
        }
    }

    private void hapusItem() {
        int row = tblKeranjang.getSelectedRow();
        if (row < 0) { UI.error(this, "Pilih item yang ingin dihapus!"); return; }
        trxCtrl.hapusItem(transaksi, row);
        refreshKeranjang();
    }

    private void refreshKeranjang() {
        mdlKeranjang.setRowCount(0);
        for (ItemTransaksi item : transaksi.getItems()) {
            mdlKeranjang.addRow(new Object[]{
                item.getProduk().getKode(),
                item.getProduk().getNama(),
                item.getJumlah(),
                UI.rp(item.getHargaSatuan()),
                UI.rp(item.getSubtotal())
            });
        }
        lblTotal.setText(UI.rp(transaksi.getTotalBelanja()));
        hitungKembalian();
    }

    private void hitungKembalian() {
        try {
            String raw = txtBayar.getText().replaceAll("[^0-9]", "");
            double bayar = raw.isEmpty() ? 0 : Double.parseDouble(raw);
            double kembalian = bayar - transaksi.getTotalBelanja();
            lblKembalian.setText(UI.rp(Math.max(0, kembalian)));
            lblKembalian.setForeground(kembalian < 0 ? Theme.DANGER : Theme.SUCCESS);
        } catch (Exception e) { lblKembalian.setText("Rp 0"); }
    }

    private void prosesBayar() {
        if (transaksi.getItems().isEmpty()) { UI.error(this, "Keranjang masih kosong!"); return; }
        String raw = txtBayar.getText().replaceAll("[^0-9]", "");
        if (raw.isEmpty()) { UI.error(this, "Masukkan jumlah pembayaran!"); return; }
        double bayar = Double.parseDouble(raw);
        String hasil = trxCtrl.prosesBayar(transaksi, bayar);
        if ("SUCCESS".equals(hasil)) {
            tampilStruk();
            mulaiTransaksiBaru();
        } else {
            UI.error(this, hasil);
        }
    }

    private void batalkan() {
        if (!UI.confirm(this, "Batalkan transaksi ini?")) return;
        trxCtrl.batalkan(transaksi);
        mulaiTransaksiBaru();
    }

    private void tampilStruk() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Struk Pembayaran", true);
        dlg.setSize(360, 500); dlg.setLocationRelativeTo(this);

        JTextArea ta = new JTextArea();
        ta.setFont(Theme.F_MONO); ta.setEditable(false);
        ta.setBackground(Color.WHITE);
        ta.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        StringBuilder sb = new StringBuilder();
        sb.append("======================================\n");
        sb.append("           WARUNG SAYA\n");
        sb.append("       Sistem Manajemen Warung\n");
        sb.append("--------------------------------------\n");
        sb.append(String.format("No  : %s%n", transaksi.getNomorTransaksi()));
        sb.append(String.format("Tgl : %s%n", transaksi.getWaktuFormatted()));
        sb.append(String.format("Kasir: %s%n", kasir.getNamaLengkap()));
        sb.append("--------------------------------------\n");
        for (ItemTransaksi item : transaksi.getItems()) {
            sb.append(String.format("%-18s%n", item.getProduk().getNama()));
            sb.append(String.format("  %3dx %-10s = %10s%n",
                item.getJumlah(), UI.rp(item.getHargaSatuan()), UI.rp(item.getSubtotal())));
        }
        sb.append("--------------------------------------\n");
        sb.append(String.format("%-20s %10s%n", "TOTAL",    UI.rp(transaksi.getTotalBelanja())));
        sb.append(String.format("%-20s %10s%n", "BAYAR",    UI.rp(transaksi.getJumlahBayar())));
        sb.append(String.format("%-20s %10s%n", "KEMBALI",  UI.rp(transaksi.getKembalian())));
        sb.append("--------------------------------------\n");
        sb.append("    Terima kasih sudah berbelanja!\n");
        sb.append("======================================\n");
        ta.setText(sb.toString());

        JButton btnClose = UI.btnPrimary("Tutup");
        btnClose.addActionListener(e -> dlg.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(btnClose);

        dlg.setLayout(new BorderLayout());
        dlg.add(new JScrollPane(ta), BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
