package dao.impl;

import dao.ITransaksiDao;
import database.DBConnection;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransaksiDaoImpl implements ITransaksiDao {

    private static TransaksiDaoImpl instance;
    public static TransaksiDaoImpl getInstance() {
        if (instance == null) instance = new TransaksiDaoImpl();
        return instance;
    }

    @Override
    public boolean insert(Transaksi t) {
        String sqlT = "INSERT INTO transaksi (nomor,waktu,kasir_id,kasir_nama,total_belanja,jumlah_bayar,kembalian,status,catatan) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sqlT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNomorTransaksi());
            ps.setTimestamp(2, Timestamp.valueOf(t.getWaktu() != null ? t.getWaktu() : LocalDateTime.now()));
            if (t.getKasir() != null) { ps.setInt(3, t.getKasir().getId()); ps.setString(4, t.getKasir().getNamaLengkap()); }
            else { ps.setNull(3, Types.INTEGER); ps.setNull(4, Types.VARCHAR); }
            ps.setDouble(5, t.getTotalBelanja());
            ps.setDouble(6, t.getJumlahBayar());
            ps.setDouble(7, t.getKembalian());
            ps.setString(8, t.getStatus().name());
            ps.setString(9, t.getCatatan());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) t.setId(rs.getInt(1));

            // Insert items
            insertItems(t);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private void insertItems(Transaksi t) throws SQLException {
        if (t.getItems() == null || t.getItems().isEmpty()) return;
        String sql = "INSERT INTO item_transaksi (transaksi_id,produk_id,produk_nama,produk_kode,jumlah,harga_satuan,subtotal) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (ItemTransaksi item : t.getItems()) {
                ps.setInt(1, t.getId());
                if (item.getProduk() != null) {
                    ps.setInt(2, item.getProduk().getId());
                    ps.setString(3, item.getProduk().getNama());
                    ps.setString(4, item.getProduk().getKode());
                } else {
                    ps.setNull(2, Types.INTEGER); ps.setNull(3, Types.VARCHAR); ps.setNull(4, Types.VARCHAR);
                }
                ps.setInt(5, item.getJumlah());
                ps.setDouble(6, item.getHargaSatuan());
                ps.setDouble(7, item.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public boolean update(Transaksi t) {
        String sql = "UPDATE transaksi SET status=?,catatan=?,total_belanja=?,jumlah_bayar=?,kembalian=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getStatus().name());
            ps.setString(2, t.getCatatan());
            ps.setDouble(3, t.getTotalBelanja());
            ps.setDouble(4, t.getJumlahBayar());
            ps.setDouble(5, t.getKembalian());
            ps.setInt(6, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public Optional<Transaksi> findById(int id) {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT * FROM transaksi WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Transaksi> findAll() {
        List<Transaksi> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM transaksi ORDER BY waktu DESC")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Transaksi> findByTanggal(LocalDate tanggal) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi WHERE DATE(waktu)=? ORDER BY waktu DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tanggal));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Transaksi> findByRentang(LocalDate dari, LocalDate sampai) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi WHERE DATE(waktu) BETWEEN ? AND ? ORDER BY waktu DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dari)); ps.setDate(2, Date.valueOf(sampai));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public double getTotalPendapatan(LocalDate dari, LocalDate sampai) {
        String sql = "SELECT COALESCE(SUM(total_belanja),0) FROM transaksi WHERE status='SELESAI' AND DATE(waktu) BETWEEN ? AND ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dari)); ps.setDate(2, Date.valueOf(sampai));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public int countSelesaiByTanggal(LocalDate tanggal) {
        String sql = "SELECT COUNT(*) FROM transaksi WHERE status='SELESAI' AND DATE(waktu)=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tanggal));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public String generateNomor() {
        String prefix = "TRX-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        String sql = "SELECT COUNT(*)+1 FROM transaksi WHERE nomor LIKE ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return prefix + String.format("%04d", rs.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return prefix + "0001";
    }

    private Transaksi mapRow(ResultSet rs) throws SQLException {
        Transaksi t = new Transaksi();
        t.setId(rs.getInt("id"));
        t.setNomorTransaksi(rs.getString("nomor"));
        Timestamp ts = rs.getTimestamp("waktu");
        if (ts != null) t.setWaktu(ts.toLocalDateTime());
        t.setTotalBelanja(rs.getDouble("total_belanja"));
        t.setJumlahBayar(rs.getDouble("jumlah_bayar"));
        t.setKembalian(rs.getDouble("kembalian"));
        try { t.setStatus(Transaksi.Status.valueOf(rs.getString("status"))); } catch (Exception ignored) {}
        t.setCatatan(rs.getString("catatan"));

        // Kasir dummy
        int kasirId = rs.getInt("kasir_id");
        String kasirNama = rs.getString("kasir_nama");
        Kasir k = new Kasir(kasirId, "", "", kasirNama != null ? kasirNama : "");
        t.setKasir(k);

        // Load items
        t.setItems(loadItems(t.getId()));
        return t;
    }

    private List<ItemTransaksi> loadItems(int transaksiId) {
        List<ItemTransaksi> items = new ArrayList<>();
        String sql = "SELECT * FROM item_transaksi WHERE transaksi_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, transaksiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ItemTransaksi item = new ItemTransaksi();
                item.setId(rs.getInt("id"));
                item.setTransaksiId(transaksiId);
                Produk p = new Produk();
                p.setId(rs.getInt("produk_id"));
                p.setNama(rs.getString("produk_nama"));
                p.setKode(rs.getString("produk_kode"));
                p.setHargaJual(rs.getDouble("harga_satuan"));
                item.setProduk(p);
                item.setJumlah(rs.getInt("jumlah"));
                item.setHargaSatuan(rs.getDouble("harga_satuan"));
                item.setSubtotal(rs.getDouble("subtotal"));
                items.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return items;
    }
}
