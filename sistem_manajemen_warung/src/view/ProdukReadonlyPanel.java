package view;

import controller.ProdukController;
import model.Produk;
import util.Theme;
import util.UI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ProdukReadonlyPanel extends JPanel {
    private final ProdukController ctrl = new ProdukController();
    private DefaultTableModel mdl;
    private JTextField txtCari;

    private static final String[] COLS = {"Kode","Nama Produk","Kategori","Harga Jual","Stok","Satuan"};

    public ProdukReadonlyPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.PAGE_BG);
        buildUI(); loadData();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PAGE_BG);
        JLabel title = UI.lblTitle("Daftar Produk"); 
        txtCari = UI.field("Cari produk...");
        txtCari.setPreferredSize(new Dimension(220, 36));
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { cari(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { cari(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        JPanel cr = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); cr.setBackground(Theme.PAGE_BG);
        cr.add(txtCari);
        header.add(title, BorderLayout.WEST); header.add(cr, BorderLayout.EAST);

        JPanel card = UI.card(); card.setLayout(new BorderLayout());
        mdl = new DefaultTableModel(COLS,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable tbl = new JTable(mdl); UI.styleTable(tbl);
        int[] ws = {70,200,90,110,60,60};
        for (int i=0;i<ws.length;i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);
        card.add(UI.scroll(tbl), BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private void loadData() {
        List<Produk> list = ctrl.getProdukAktif();
        render(list);
    }

    private void cari() {
        String kw = txtCari.getText().trim();
        render(kw.isEmpty() ? ctrl.getProdukAktif() : ctrl.cariProduk(kw));
    }

    private void render(List<Produk> list) {
        mdl.setRowCount(0);
        for (Produk p : list)
            mdl.addRow(new Object[]{p.getKode(), p.getNama(), p.getKategori(),
                UI.rp(p.getHargaJual()), p.isStokHabis()?"HABIS":p.getStok(), p.getSatuan()});
    }
}
