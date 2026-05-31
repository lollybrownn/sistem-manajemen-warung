package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Singleton koneksi MySQL
 * Ubah DB_HOST, DB_USER, DB_PASS sesuai konfigurasi MySQL kamu
 */
public class DBConnection {

    // ── KONFIGURASI MySQL — SESUAIKAN DI SINI ────────────────────────────────
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "warung_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";          // ganti dengan password MySQL kamu
    // ─────────────────────────────────────────────────────────────────────────

    private static final String URL =
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
        + "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true"
        + "&useUnicode=true&characterEncoding=UTF-8";

    private static Connection connection = null;

    private DBConnection() {}

    /**
     * Mendapatkan koneksi MySQL (Singleton).
     * Kalau koneksi terputus, akan otomatis reconnect.
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, DB_USER, DB_PASS);
                System.out.println("[DB] Koneksi MySQL berhasil ke database: " + DB_NAME);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver MySQL tidak ditemukan!\n" +
                "Pastikan file 'mysql-connector-j-8.x.x.jar' ada di folder lib/\n" +
                "Download: https://dev.mysql.com/downloads/connector/j/", e);
        }
        return connection;
    }

    /** Tutup koneksi (panggil saat aplikasi ditutup) */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi MySQL ditutup.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Cek apakah koneksi aktif */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}
