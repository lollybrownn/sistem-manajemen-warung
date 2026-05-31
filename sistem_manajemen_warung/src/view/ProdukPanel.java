package view;

import controller.ProdukController;
import model.Produk;
import util.Theme;
import util.UI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ProdukPanel extends JPanel {
    private final ProdukController ctrl = new ProdukController();
    private JTable            tbl;
    private DefaultTableModel mdl;
    private JTextField        txtCari;
    private JLabel            lblJumlah;

    private static final String[] COLS = {"#","Kode","Nama Produk","Kategori","Harga Beli","Harga Jual","Stok","Satuan","Status"};

    public ProdukPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.PAGE_BG);
        buildUI(); loadData();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Theme.PAGE_BG);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(Theme.PAGE_BG);
        JLabel title = UI.lblTitle("Kelola Produk"); title.setAlignmentX(LEFT_ALIGNMENT);
        lblJumlah = UI.lbl("0 produk", Theme.F_SMALL, Theme.TEXT_GRAY); lblJumlah.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title); titleBox.add(Box.createVerticalStrut(2)); titleBox.add(lblJumlah);

        JPanel ctrlRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        ctrlRow.setBackground(Theme.PAGE_BG);
        txtCari = UI.field("Cari produk...");
        txtCari.setPreferredSize(new Dimension(200, 36));
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { cari(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { cari(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        JButton btnTambah  = UI.btnPrimary("+ Tambah Produk");
        JButton btnRefresh = UI.btnOutline("Refresh");
        btnTambah.addActionListener(e  -> openForm(null));
        btnRefresh.addActionListener(e -> loadData());
        ctrlRow.add(txtCari); ctrlRow.add(btnRefresh); ctrlRow.add(btnTambah);

        header.add(titleBox, BorderLayout.WEST);
        header.add(ctrlRow,  BorderLayout.EAST);

        // Table card
        JPanel tableCard = UI.card();
        tableCard.setLayout(new BorderLayout(0, 10));

        mdl = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        tbl = new JTable(mdl); UI.styleTable(tbl);
        int[] ws = {36,70,190,90,100,100,55,55,80};
        for (int i=0;i<ws.length;i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);

        // Status renderer
        tbl.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                setHorizontalAlignment(CENTER); setFont(Theme.F_SMALL_BOLD);
                if (!sel) setBackground(row%2==0?Theme.CARD_BG:Theme.TABLE_STRIPE);
                String s = v!=null?v.toString():"";
                setForeground("Aktif".equals(s)?Theme.SUCCESS:"Habis".equals(s)?Theme.DANGER:Theme.WARNING);
                return this;
            }
        });

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2 && tbl.getSelectedRow()>=0) editSelected();
            }
        });

        JScrollPane scroll = UI.scroll(tbl);

        // Action row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setBackground(Theme.CARD_BG);
        JButton btnEdit  = UI.btnOutline("Edit");
        JButton btnHapus = UI.btnDanger("Hapus");
        btnEdit.addActionListener(e  -> { if (tbl.getSelectedRow()<0){UI.error(this,"Pilih produk!"); return;} editSelected(); });
        btnHapus.addActionListener(e -> hapusSelected());
        actions.add(btnEdit); actions.add(btnHapus);

        tableCard.add(scroll,  BorderLayout.CENTER);
        tableCard.add(actions, BorderLayout.SOUTH);

        add(header,    BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void loadData() { loadRows(ctrl.getProdukAktif()); }
    private void cari() {
        String kw = txtCari.getText().trim();
        loadRows(kw.isEmpty() ? ctrl.getProdukAktif() : ctrl.cariProduk(kw));
    }

    private void loadRows(List<Produk> list) {
        mdl.setRowCount(0);
        int no = 1;
        for (Produk p : list) {
            String status = p.isStokHabis() ? "Habis" : p.isStokRendah() ? "Rendah" : "Aktif";
            mdl.addRow(new Object[]{no++, p.getKode(), p.getNama(), p.getKategori(),
                UI.rp(p.getHargaBeli()), UI.rp(p.getHargaJual()), p.getStok(), p.getSatuan(), status});
        }
        lblJumlah.setText(list.size() + " produk");
    }

    private void editSelected() {
        String kode = mdl.getValueAt(tbl.getSelectedRow(), 1).toString();
        openForm(ctrl.getProdukByKode(kode));
    }

    private void hapusSelected() {
        if (tbl.getSelectedRow()<0) { UI.error(this,"Pilih produk terlebih dahulu!"); return; }
        String kode = mdl.getValueAt(tbl.getSelectedRow(),1).toString();
        Produk p = ctrl.getProdukByKode(kode);
        if (p==null) return;
        if (UI.confirm(this,"Hapus produk \""+p.getNama()+"\"?")) {
            if (ctrl.hapusProduk(p.getId())) { UI.success(this,"Produk dihapus!"); loadData(); }
            else UI.error(this,"Gagal menghapus produk!");
        }
    }

    private void openForm(Produk produk) {
        boolean isEdit = produk != null;
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
            isEdit?"Edit Produk":"Tambah Produk", true);
        dlg.setSize(460, 520); dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1; gc.gridx=0;

        String[] KATS = {"Makanan","Minuman","Roti","Sembako","Kebersihan","Rokok","Lainnya"};
        String[] SATS = {"pcs","kg","gram","liter","ml","botol","kotak","bungkus","lusin"};

        JTextField fKode  = UI.field("Contoh: P021");
        JTextField fNama  = UI.field("Nama produk");
        JComboBox<String> fKat  = UI.combo(KATS);
        JTextField fHBeli = UI.field("0");
        JTextField fHJual = UI.field("0");
        JTextField fStok  = UI.field("0");
        JComboBox<String> fSat  = UI.combo(SATS);

        if (isEdit) {
            fKode.setText(produk.getKode()); fNama.setText(produk.getNama());
            fKat.setSelectedItem(produk.getKategori());
            fHBeli.setText(String.valueOf((long)produk.getHargaBeli()));
            fHJual.setText(String.valueOf((long)produk.getHargaJual()));
            fStok.setText(String.valueOf(produk.getStok()));
            fSat.setSelectedItem(produk.getSatuan());
        }

        Object[][] rows = {
            {"Kode Produk *", fKode}, {"Nama Produk *", fNama},
            {"Kategori", fKat}, {"Harga Beli (Rp)", fHBeli},
            {"Harga Jual (Rp) *", fHJual}, {"Stok Awal", fStok}, {"Satuan", fSat}
        };
        for (int i=0; i<rows.length; i++) {
            gc.gridy = i*2; gc.insets = new Insets(0,0,3,0);
            form.add(UI.lbl(rows[i][0].toString(), Theme.F_BODY_BOLD, Theme.TEXT_DARK), gc);
            gc.gridy = i*2+1; gc.insets = new Insets(0,0,10,0);
            form.add((Component)rows[i][1], gc);
        }

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        btnRow.setBackground(Theme.PAGE_BG);
        btnRow.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Theme.BORDER));
        JButton btnBatal  = UI.btnOutline("Batal");
        JButton btnSimpan = UI.btnPrimary(isEdit?"Simpan Perubahan":"Tambah Produk");
        btnBatal.addActionListener(e -> dlg.dispose());
        btnSimpan.addActionListener(e -> {
            try {
                String kode=fKode.getText().trim(), nama=fNama.getText().trim();
                String kat=fKat.getSelectedItem().toString(), sat=fSat.getSelectedItem().toString();
                double hb = Double.parseDouble(fHBeli.getText().replaceAll("[^0-9.]","").isEmpty()?"0":fHBeli.getText().replaceAll("[^0-9.]",""));
                double hj = Double.parseDouble(fHJual.getText().replaceAll("[^0-9.]",""));
                int st = Integer.parseInt(fStok.getText().replaceAll("[^0-9]","").isEmpty()?"0":fStok.getText().replaceAll("[^0-9]",""));
                String hasil = isEdit ? ctrl.updateProduk(produk.getId(),kode,nama,kat,hb,hj,st,sat)
                                      : ctrl.tambahProduk(kode,nama,kat,hb,hj,st,sat);
                if ("SUCCESS".equals(hasil)) {
                    UI.success(dlg, isEdit?"Produk diperbarui!":"Produk ditambahkan!");
                    dlg.dispose(); loadData();
                } else UI.error(dlg, hasil);
            } catch (NumberFormatException ex) { UI.error(dlg,"Masukkan angka yang valid!"); }
        });
        btnRow.add(btnBatal); btnRow.add(btnSimpan);

        dlg.add(new JScrollPane(form){{setBorder(null);}}, BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
