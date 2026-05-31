package view;

import controller.ProdukController;
import controller.TransaksiController;
import controller.UserController;
import model.Produk;
import model.Transaksi;
import util.Theme;
import util.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;      
import javax.swing.table.DefaultTableCellRenderer; 
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class LaporanPanel extends JPanel {
    private final TransaksiController trxCtrl = new TransaksiController();
    private final ProdukController    prdCtrl = new ProdukController();
    private final UserController      usrCtrl = new UserController();

    public LaporanPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.PAGE_BG);
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PAGE_BG);
        header.add(UI.lblTitle("Laporan & Statistik"), BorderLayout.WEST);
        JButton btnRefresh = UI.btnOutline("Refresh");
        btnRefresh.addActionListener(e -> { removeAll(); buildUI(); revalidate(); repaint(); });
        JPanel cr = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)); cr.setBackground(Theme.PAGE_BG);
        cr.add(btnRefresh);
        header.add(cr, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(Theme.F_BODY); tabs.setBackground(Theme.PAGE_BG);
        tabs.addTab("Ringkasan",      buildRingkasan());
        tabs.addTab("Produk Terlaris",buildTerlaris());
        tabs.addTab("Stok Rendah",    buildStokRendah());

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    private JPanel buildRingkasan() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.fill=GridBagConstraints.BOTH; g.weightx=1; g.gridx=0;

        List<Transaksi> allTrx  = trxCtrl.getAllTransaksi();
        List<Transaksi> selesai = allTrx.stream().filter(t->t.getStatus()==Transaksi.Status.SELESAI).collect(Collectors.toList());

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate week  = today.minusDays(6);

        List<Transaksi> hariIni  = selesai.stream().filter(t->t.getWaktu()!=null&&t.getWaktu().toLocalDate().isEqual(today)).collect(Collectors.toList());
        List<Transaksi> mingguIni= selesai.stream().filter(t->t.getWaktu()!=null&&!t.getWaktu().toLocalDate().isBefore(week)).collect(Collectors.toList());

        double phariIni  = hariIni.stream().mapToDouble(Transaksi::getTotalBelanja).sum();
        double pminggu   = mingguIni.stream().mapToDouble(Transaksi::getTotalBelanja).sum();
        double ptotal    = selesai.stream().mapToDouble(Transaksi::getTotalBelanja).sum();

        JPanel s1 = new JPanel(new GridLayout(1, 3, 14, 0)); s1.setBackground(Theme.PAGE_BG);
        s1.add(UI.statCard("Transaksi Hari Ini",  String.valueOf(hariIni.size()),   "transaksi selesai",    Theme.ACCENT));
        s1.add(UI.statCard("Pendapatan Hari Ini", UI.rp(phariIni),                  "penjualan hari ini",   Theme.SUCCESS));
        s1.add(UI.statCard("Transaksi Minggu Ini",String.valueOf(mingguIni.size()),  "7 hari terakhir",      Theme.WARNING));

        JPanel s2 = new JPanel(new GridLayout(1, 3, 14, 0)); s2.setBackground(Theme.PAGE_BG);
        s2.add(UI.statCard("Pendapatan Minggu Ini", UI.rp(pminggu),                 "7 hari terakhir",      Theme.WARNING));
        s2.add(UI.statCard("Total Semua Transaksi", String.valueOf(selesai.size()),  "sejak awal dibuka",    Theme.SUCCESS));
        s2.add(UI.statCard("Total Pendapatan",      UI.rp(ptotal),                  "keseluruhan",          Theme.ACCENT));

        JPanel s3 = new JPanel(new GridLayout(1, 3, 14, 0)); s3.setBackground(Theme.PAGE_BG);
        s3.add(UI.statCard("Produk Aktif",   String.valueOf(prdCtrl.getProdukAktif().size()),   "produk tersedia",   Theme.ACCENT));
        s3.add(UI.statCard("Stok Rendah",    String.valueOf(prdCtrl.getProdukStokRendah().size()),"perlu restok",     Theme.DANGER));
        s3.add(UI.statCard("Jumlah Kasir",   String.valueOf(usrCtrl.getKasirList().size()),      "kasir aktif",       Theme.SUCCESS));

        g.gridy=0; g.insets=new Insets(0,0,14,0); p.add(s1,g);
        g.gridy=1; p.add(s2,g);
        g.gridy=2; p.add(s3,g);
        
        g.gridy=3; g.weighty=1; 
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        p.add(spacer, g);
        
        return p;
    }

    private JPanel buildTerlaris() {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(Theme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16,0,0,0));
        p.add(UI.lbl("Berdasarkan total kuantitas terjual (transaksi SELESAI)", Theme.F_SMALL, Theme.TEXT_GRAY), BorderLayout.NORTH);

        Map<String,int[]> map = new LinkedHashMap<>();
        for (Transaksi t : trxCtrl.getAllTransaksi()) {
            if (t.getStatus()!=Transaksi.Status.SELESAI) continue;
            for (var item : t.getItems()) {
                String key = item.getProduk().getKode()+"|"+item.getProduk().getNama();
                map.computeIfAbsent(key, k->new int[2]);
                map.get(key)[0] += item.getJumlah();
                map.get(key)[1] += (int)item.getSubtotal();
            }
        }
        List<Map.Entry<String,int[]>> sorted = new ArrayList<>(map.entrySet());
        sorted.sort((a,b)->b.getValue()[0]-a.getValue()[0]);

        String[] cols = {"#","Kode","Nama Produk","Qty Terjual","Total Pendapatan"};
        DefaultTableModel mdl = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        int no=1;
        for (Map.Entry<String,int[]> e : sorted) {
            String[] parts = e.getKey().split("\\|",2);
            mdl.addRow(new Object[]{no++, parts[0], parts.length>1?parts[1]:"-", e.getValue()[0], UI.rp(e.getValue()[1])});
        }
        if (sorted.isEmpty()) mdl.addRow(new Object[]{"","","Belum ada data penjualan","",""});

        JTable t = new JTable(mdl); UI.styleTable(t);
        
        // Perbaikan: Judul/Header Tabel Terlaris jadi Putih
        t.getTableHeader().setBackground(Color.WHITE);
        t.getTableHeader().setOpaque(true);

        t.getColumnModel().getColumn(0).setPreferredWidth(36);
        t.getColumnModel().getColumn(1).setPreferredWidth(70);
        t.getColumnModel().getColumn(2).setPreferredWidth(220);
        t.getColumnModel().getColumn(3).setPreferredWidth(90);
        t.getColumnModel().getColumn(4).setPreferredWidth(130);
        p.add(UI.scroll(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStokRendah() {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(Theme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16,0,0,0));

        List<Produk> list = prdCtrl.getProdukStokRendah();
        JLabel info = list.isEmpty()
            ? UI.lbl("Semua produk stok aman.", Theme.F_SMALL, Theme.SUCCESS)
            : UI.lbl(list.size()+" produk perlu segera direstok!", Theme.F_SMALL, Theme.DANGER);
        p.add(info, BorderLayout.NORTH);

        String[] cols = {"Kode","Nama Produk","Kategori","Stok Saat Ini","Stok Minimal","Kondisi"};
        DefaultTableModel mdl = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        for (Produk pr : list)
            mdl.addRow(new Object[]{pr.getKode(),pr.getNama(),pr.getKategori(),pr.getStok(),pr.getStokMinimal(),pr.isStokHabis()?"HABIS":"RENDAH"});
        if (list.isEmpty()) mdl.addRow(new Object[]{"","","Semua stok aman","","",""});

        JTable t = new JTable(mdl); UI.styleTable(t);
        
        // Perbaikan: Judul/Header Tabel Stok Rendah jadi Putih
        t.getTableHeader().setBackground(Color.WHITE);
        t.getTableHeader().setOpaque(true);

        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable tbl,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(tbl,v,sel,foc,row,col);
                setHorizontalAlignment(CENTER); setFont(Theme.F_SMALL_BOLD);
                if (!sel) setBackground(row%2==0?Theme.CARD_BG:Theme.TABLE_STRIPE);
                setForeground("HABIS".equals(v!=null?v.toString():"") ? Theme.DANGER : Theme.WARNING);
                return this;
            }
        });
        p.add(UI.scroll(t), BorderLayout.CENTER);
        return p;
    }
}