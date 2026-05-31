package view;

import controller.UserController;
import model.Kasir;
import model.User;
import util.Theme;
import util.UI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class KasirPanel extends JPanel {
    private final UserController ctrl = new UserController();
    private JTable            tbl;
    private DefaultTableModel mdl;

    private static final String[] COLS = {"#","Username","Nama Lengkap","Shift","Status"};

    public KasirPanel() {
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
        JLabel title = UI.lblTitle("Kelola Kasir"); title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub   = UI.lbl("Manajemen akun kasir", Theme.F_SMALL, Theme.TEXT_GRAY); sub.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title); titleBox.add(Box.createVerticalStrut(2)); titleBox.add(sub);

        JPanel ctrlRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        ctrlRow.setBackground(Theme.PAGE_BG);
        JButton btnTambah  = UI.btnPrimary("+ Tambah Kasir");
        JButton btnRefresh = UI.btnOutline("Refresh");
        btnTambah.addActionListener(e  -> openForm(null));
        btnRefresh.addActionListener(e -> loadData());
        ctrlRow.add(btnRefresh); ctrlRow.add(btnTambah);

        header.add(titleBox, BorderLayout.WEST);
        header.add(ctrlRow,  BorderLayout.EAST);

        // Table card
        JPanel tableCard = UI.card();
        tableCard.setLayout(new BorderLayout(0, 10));

        mdl = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        tbl = new JTable(mdl); UI.styleTable(tbl);
        int[] ws = {36,120,200,80,80};
        for (int i=0;i<ws.length;i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2 && tbl.getSelectedRow()>=0) editSelected();
            }
        });

        // Status renderer
        tbl.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                setHorizontalAlignment(CENTER); setFont(Theme.F_SMALL_BOLD);
                if (!sel) setBackground(row%2==0?Theme.CARD_BG:Theme.TABLE_STRIPE);
                setForeground("Aktif".equals(v!=null?v.toString():"") ? Theme.SUCCESS : Theme.TEXT_GRAY);
                return this;
            }
        });

        JScrollPane scroll = UI.scroll(tbl);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setBackground(Theme.CARD_BG);
        JButton btnEdit   = UI.btnOutline("Edit");
        JButton btnHapus  = UI.btnDanger("Nonaktifkan");
        btnEdit.addActionListener(e  -> { if(tbl.getSelectedRow()<0){UI.error(this,"Pilih kasir!"); return;} editSelected(); });
        btnHapus.addActionListener(e -> hapusSelected());
        actions.add(btnEdit); actions.add(btnHapus);

        tableCard.add(scroll,  BorderLayout.CENTER);
        tableCard.add(actions, BorderLayout.SOUTH);

        add(header,    BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void loadData() {
        mdl.setRowCount(0); int no=1;
        for (User u : ctrl.getKasirList()) {
            String shift = u instanceof Kasir ? ((Kasir)u).getShift() : "-";
            mdl.addRow(new Object[]{no++, u.getUsername(), u.getNamaLengkap(), shift, u.isAktif()?"Aktif":"Nonaktif"});
        }
    }

    private void editSelected() {
        String uname = mdl.getValueAt(tbl.getSelectedRow(),1).toString();
        ctrl.getKasirList().stream().filter(u->u.getUsername().equals(uname)).findFirst().ifPresent(this::openForm);
    }

    private void hapusSelected() {
        if (tbl.getSelectedRow()<0) { UI.error(this,"Pilih kasir terlebih dahulu!"); return; }
        String uname = mdl.getValueAt(tbl.getSelectedRow(),1).toString();
        ctrl.getKasirList().stream().filter(u->u.getUsername().equals(uname)).findFirst().ifPresent(u -> {
            if (UI.confirm(this,"Nonaktifkan kasir \""+u.getNamaLengkap()+"\"?")) {
                if (ctrl.hapusKasir(u.getId())) { UI.success(this,"Kasir dinonaktifkan!"); loadData(); }
                else UI.error(this,"Gagal!");
            }
        });
    }

    private void openForm(User kasir) {
        boolean isEdit = kasir != null;
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
            isEdit?"Edit Kasir":"Tambah Kasir", true);
        dlg.setSize(400, 380); dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1; gc.gridx=0;

        JTextField    fUser  = UI.field("Username");
        JPasswordField fPass = UI.passField();
        JTextField    fNama  = UI.field("Nama lengkap");
        JComboBox<String> fShift = UI.combo(new String[]{"PAGI","SORE","MALAM"});

        if (isEdit) {
            fUser.setText(kasir.getUsername()); fNama.setText(kasir.getNamaLengkap());
            if (kasir instanceof Kasir) fShift.setSelectedItem(((Kasir)kasir).getShift());
        }

        Object[][] rows = {
            {"Username *", fUser},
            {isEdit?"Password (kosong = tidak diubah)":"Password *", fPass},
            {"Nama Lengkap *", fNama}, {"Shift", fShift}
        };
        for (int i=0;i<rows.length;i++) {
            gc.gridy=i*2; gc.insets=new Insets(0,0,3,0);
            form.add(UI.lbl(rows[i][0].toString(), Theme.F_BODY_BOLD, Theme.TEXT_DARK), gc);
            gc.gridy=i*2+1; gc.insets=new Insets(0,0,12,0);
            form.add((Component)rows[i][1], gc);
        }

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        btnRow.setBackground(Theme.PAGE_BG);
        btnRow.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Theme.BORDER));
        JButton btnBatal  = UI.btnOutline("Batal");
        JButton btnSimpan = UI.btnPrimary(isEdit?"Simpan":"Tambah Kasir");
        btnBatal.addActionListener(e -> dlg.dispose());
        btnSimpan.addActionListener(e -> {
            String u2=fUser.getText().trim(), p2=new String(fPass.getPassword());
            String n2=fNama.getText().trim(), s2=fShift.getSelectedItem().toString();
            String hasil = isEdit ? ctrl.updateKasir(kasir.getId(),u2,p2,n2,s2)
                                  : ctrl.tambahKasir(u2,p2,n2,s2);
            if ("SUCCESS".equals(hasil)) {
                UI.success(dlg, isEdit?"Data diperbarui!":"Kasir ditambahkan!");
                dlg.dispose(); loadData();
            } else UI.error(dlg, hasil);
        });
        btnRow.add(btnBatal); btnRow.add(btnSimpan);

        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
