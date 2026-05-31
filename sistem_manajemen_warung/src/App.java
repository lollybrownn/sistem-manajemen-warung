import database.DBConnection;
import util.Theme;
import view.LoginFrame;

import javax.swing.*;

/**
 * App - Entry point Sistem Manajemen Warung
 *
 * Arsitektur MVC + DAO:
 *   Model      : User(Admin/Kasir), Produk, Transaksi, ItemTransaksi
 *   View       : LoginFrame, AdminFrame, KasirFrame, POSPanel, ProdukPanel, ...
 *   Controller : AuthController, UserController, ProdukController, TransaksiController
 *   DAO        : IUserDao, IProdukDao, ITransaksiDao  (impl: MySQL via JDBC)
 *   Database   : MySQL (warung_db) via mysql-connector-j
 */
public class App {
    public static void main(String[] args) {
        setLookAndFeel();

        // Test koneksi MySQL sebelum buka UI
        try {
            DBConnection.getConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Gagal terhubung ke MySQL!\n\n" +
                e.getMessage() + "\n\n" +
                "Pastikan:\n" +
                "1. MySQL server sedang berjalan\n" +
                "2. Database 'warung_db' sudah dibuat (jalankan sql/warung_db.sql)\n" +
                "3. Username/password di DBConnection.java sudah benar\n" +
                "4. File mysql-connector-j-*.jar ada di folder lib/",
                "Koneksi Database Gagal",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(laf.getName())) {
                    UIManager.setLookAndFeel(laf.getClassName());
                    // Override supaya sesuai theme kita
                    UIManager.put("control",              Theme.PAGE_BG);
                    UIManager.put("nimbusBase",           Theme.SIDEBAR_BG);
                    UIManager.put("Table.background",     Theme.CARD_BG);
                    UIManager.put("ScrollPane.background",Theme.CARD_BG);
                    return;
                }
            }
            // Fallback: system
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }
}
