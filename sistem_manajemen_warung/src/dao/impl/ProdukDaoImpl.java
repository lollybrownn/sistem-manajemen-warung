package dao.impl;

import dao.IProdukDao;
import database.DBConnection;
import model.Produk;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdukDaoImpl implements IProdukDao {

    private static ProdukDaoImpl instance;
    public static ProdukDaoImpl getInstance() {
        if (instance == null) instance = new ProdukDaoImpl();
        return instance;
    }

    @Override
    public boolean insert(Produk p) {
        String sql = "INSERT INTO produk (kode,nama,kategori,harga_beli,harga_jual,stok,stok_minimal,satuan,aktif) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getKode()); ps.setString(2, p.getNama());
            ps.setString(3, p.getKategori()); ps.setDouble(4, p.getHargaBeli());
            ps.setDouble(5, p.getHargaJual()); ps.setInt(6, p.getStok());
            ps.setInt(7, p.getStokMinimal()); ps.setString(8, p.getSatuan());
            ps.setBoolean(9, p.isAktif());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) p.setId(rs.getInt(1));
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Produk p) {
        String sql = "UPDATE produk SET kode=?,nama=?,kategori=?,harga_beli=?,harga_jual=?,stok=?,stok_minimal=?,satuan=?,aktif=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getKode()); ps.setString(2, p.getNama());
            ps.setString(3, p.getKategori()); ps.setDouble(4, p.getHargaBeli());
            ps.setDouble(5, p.getHargaJual()); ps.setInt(6, p.getStok());
            ps.setInt(7, p.getStokMinimal()); ps.setString(8, p.getSatuan());
            ps.setBoolean(9, p.isAktif()); ps.setInt(10, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        return findById(id).map(p -> { p.setAktif(false); return update(p); }).orElse(false);
    }

    @Override
    public Optional<Produk> findById(int id) {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT * FROM produk WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public Optional<Produk> findByKode(String kode) {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT * FROM produk WHERE kode=? AND aktif=1")) {
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Produk> findAll() {
        List<Produk> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM produk ORDER BY kategori, nama")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Produk> findAllActive() {
        List<Produk> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM produk WHERE aktif=1 ORDER BY kategori, nama")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Produk> findByKeyword(String keyword) {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk WHERE aktif=1 AND (nama LIKE ? OR kode LIKE ? OR kategori LIKE ?) ORDER BY nama";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Produk> findByKategori(String kategori) {
        List<Produk> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT * FROM produk WHERE aktif=1 AND kategori=? ORDER BY nama")) {
            ps.setString(1, kategori);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Produk> findStokRendah() {
        List<Produk> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM produk WHERE aktif=1 AND stok <= stok_minimal ORDER BY stok")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<String> findAllKategori() {
        List<String> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT kategori FROM produk WHERE aktif=1 ORDER BY kategori")) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateStok(int id, int delta) {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("UPDATE produk SET stok = stok + ? WHERE id=? AND stok + ? >= 0")) {
            ps.setInt(1, delta); ps.setInt(2, id); ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean isKodeExists(String kode) {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM produk WHERE kode=? AND aktif=1")) {
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Produk mapRow(ResultSet rs) throws SQLException {
        Produk p = new Produk(
            rs.getInt("id"), rs.getString("kode"), rs.getString("nama"),
            rs.getString("kategori"), rs.getDouble("harga_beli"),
            rs.getDouble("harga_jual"), rs.getInt("stok"), rs.getString("satuan")
        );
        p.setStokMinimal(rs.getInt("stok_minimal"));
        p.setAktif(rs.getBoolean("aktif"));
        return p;
    }
}
