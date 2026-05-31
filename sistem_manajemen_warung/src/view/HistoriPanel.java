package view;

import controller.TransaksiController;
import model.ItemTransaksi;
import model.Transaksi;
import util.Theme;
import util.UI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HistoriPanel extends JPanel {
    private final TransaksiController ctrl = new TransaksiController();
    private JTable            tbl;
    private DefaultTableModel mdl;
    private JLabel            lblJumlah, lblTotal;

    private static final String[] COLS = {"#","No. Transaksi","Tanggal & Jam","Kasir","Item","Total","Bayar","Kembali","Status"};

    public HistoriPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.PAGE_BG);
        buildUI(); loadData();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PAGE_BG);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(Theme.PAGE_BG);
        JLabel title = UI.lblTitle("Riwayat Transaksi"); title.setAlignmentX(LEFT_ALIGNMENT);
        lblJumlah = UI.lbl("Memuat...", Theme.F_SMALL, Theme.TEXT_GRAY); lblJumlah.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title); titleBox.add(Box.createVerticalStrut(2)); titleBox.add(lblJumlah);

        JButton btnRefresh = UI.btnOutline("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        JPanel ctrlRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        ctrlRow.setBackground(Theme.PAGE_BG);
        ctrlRow.add(btnRefresh);

        header.add(titleBox, BorderLayout.WEST);
        header.add(ctrlRow,  BorderLayout.EAST);

        // Table card
        JPanel tableCard = UI.card();
        tableCard.setLayout(new BorderLayout(0, 10));

        mdl = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        tbl = new JTable(mdl); UI.styleTable(tbl);
        int[] ws = {36,150,130,110,45,110,110,110,90};
        for (int i=0;i<ws.length;i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);

        // Status renderer
        tbl.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                setHorizontalAlignment(CENTER); setFont(Theme.F_SMALL_BOLD);
                if (!sel) setBackground(row%2==0?Theme.CARD_BG:Theme.TABLE_STRIPE);
                String s=v!=null?v.toString():"";
                setForeground("SELESAI".equals(s)?Theme.SUCCESS:"DIBATALKAN".equals(s)?Theme.DANGER:Theme.WARNING);
                return this;
            }
        });

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2 && tbl.getSelectedRow()>=0) showDetail(tbl.getSelectedRow());
            }
        });

        JScrollPane scroll = UI.scroll(tbl);

        // Footer summary
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        footer.setBackground(Theme.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER));
        lblTotal = UI.lbl("Total Pendapatan: -", Theme.F_BODY_BOLD, Theme.TEXT_DARK);
        JLabel hint = UI.lbl("  |  Double-klik baris untuk detail transaksi", Theme.F_SMALL, Theme.TEXT_GRAY);
        footer.add(lblTotal); footer.add(hint);

        tableCard.add(scroll,  BorderLayout.CENTER);
        tableCard.add(footer,  BorderLayout.SOUTH);

        add(header,    BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void loadData() {
        mdl.setRowCount(0);
        List<Transaksi> list = ctrl.getAllTransaksi();
        double total = 0; int no = 1;
        for (Transaksi t : list) {
            mdl.addRow(new Object[]{
                no++, t.getNomorTransaksi(), t.getWaktuFormatted(),
                t.getKasir()!=null?t.getKasir().getNamaLengkap():"-",
                t.getJumlahItem(), UI.rp(t.getTotalBelanja()),
                UI.rp(t.getJumlahBayar()), UI.rp(t.getKembalian()),
                t.getStatus().name()
            });
            if (t.getStatus()==Transaksi.Status.SELESAI) total += t.getTotalBelanja();
        }
        lblJumlah.setText(list.size() + " transaksi");
        lblTotal.setText("Total Pendapatan (SELESAI): " + UI.rp(total));
    }

    private void showDetail(int row) {
        String nomor = mdl.getValueAt(row, 1).toString();
        Transaksi t = ctrl.getAllTransaksi().stream()
            .filter(x -> x.getNomorTransaksi().equals(nomor)).findFirst().orElse(null);
        if (t == null) return;

        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
            "Detail Transaksi - " + nomor, true);
        dlg.setSize(520, 480); dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(0, 0));

        // Info grid
        JPanel info = new JPanel(new GridLayout(0, 2, 10, 6));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createEmptyBorder(16, 20, 14, 20));
        String[][] kv = {
            {"No. Transaksi:", t.getNomorTransaksi()},
            {"Tanggal/Jam:",   t.getWaktuFormatted()},
            {"Kasir:",         t.getKasir()!=null?t.getKasir().getNamaLengkap():"-"},
            {"Status:",        t.getStatus().name()},
            {"Total:",         UI.rp(t.getTotalBelanja())},
            {"Bayar:",         UI.rp(t.getJumlahBayar())},
            {"Kembalian:",     UI.rp(t.getKembalian())}
        };
        for (String[] pair : kv) {
            info.add(UI.lbl(pair[0], Theme.F_BODY_BOLD, Theme.TEXT_GRAY));
            JLabel v = UI.lbl(pair[1], Theme.F_BODY, Theme.TEXT_DARK);
            if ("Total:".equals(pair[0])) { v.setFont(Theme.F_SUBTITLE); v.setForeground(Theme.ACCENT); }
            info.add(v);
        }

        // Items table
        String[] iCols = {"Kode","Nama Produk","Qty","Harga","Subtotal"};
        DefaultTableModel iMdl = new DefaultTableModel(iCols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        for (ItemTransaksi item : t.getItems()) {
            iMdl.addRow(new Object[]{
                item.getProduk().getKode(), item.getProduk().getNama(),
                item.getJumlah(), UI.rp(item.getHargaSatuan()), UI.rp(item.getSubtotal())
            });
        }
        JTable iTbl = new JTable(iMdl); UI.styleTable(iTbl);
        JScrollPane iScroll = UI.scroll(iTbl);

        JButton btnClose = UI.btnPrimary("Tutup");
        btnClose.addActionListener(e -> dlg.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        btnRow.setBackground(Theme.PAGE_BG);
        btnRow.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Theme.BORDER));
        btnRow.add(btnClose);

        dlg.add(info,    BorderLayout.NORTH);
        dlg.add(iScroll, BorderLayout.CENTER);
        dlg.add(btnRow,  BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
