package dao.impl;

import dao.IUserDao;
import database.DBConnection;
import model.Admin;
import model.Kasir;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements IUserDao {

    private static UserDaoImpl instance;
    public static UserDaoImpl getInstance() {
        if (instance == null) instance = new UserDaoImpl();
        return instance;
    }

    @Override
    public boolean insert(User u) {
        String sql = "INSERT INTO users (username,password,nama_lengkap,role,aktif,no_telp,email,shift) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNamaLengkap());
            ps.setString(4, u.getRole());
            ps.setBoolean(5, u.isAktif());
            if (u instanceof Admin) {
                Admin a = (Admin) u;
                ps.setString(6, a.getNoTelp());
                ps.setString(7, a.getEmail());
                ps.setNull(8, Types.VARCHAR);
            } else if (u instanceof Kasir) {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
                ps.setString(8, ((Kasir) u).getShift());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.VARCHAR);
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) u.setId(rs.getInt(1));
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(User u) {
        String sql = "UPDATE users SET username=?,password=?,nama_lengkap=?,aktif=?,no_telp=?,email=?,shift=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNamaLengkap());
            ps.setBoolean(4, u.isAktif());
            if (u instanceof Admin) {
                Admin a = (Admin) u;
                ps.setString(5, a.getNoTelp());
                ps.setString(6, a.getEmail());
                ps.setNull(7, Types.VARCHAR);
            } else if (u instanceof Kasir) {
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
                ps.setString(7, ((Kasir) u).getShift());
            } else {
                ps.setNull(5, Types.VARCHAR); ps.setNull(6, Types.VARCHAR); ps.setNull(7, Types.VARCHAR);
            }
            ps.setInt(8, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        return findById(id).map(u -> { u.setAktif(false); return update(u); }).orElse(false);
    }

    @Override
    public Optional<User> findById(int id) {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT * FROM users WHERE username=? AND aktif=1")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY role, nama_lengkap")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<User> findByRole(String role) {
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT * FROM users WHERE role=? ORDER BY nama_lengkap")) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<User> findAllActive() {
        List<User> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users WHERE aktif=1 ORDER BY role, nama_lengkap")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean isUsernameExists(String username) {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM users WHERE username=? AND aktif=1")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int    id    = rs.getInt("id");
        String uname = rs.getString("username");
        String pass  = rs.getString("password");
        String nama  = rs.getString("nama_lengkap");
        String role  = rs.getString("role");
        boolean aktif = rs.getBoolean("aktif");

        User user;
        if ("ADMIN".equals(role)) {
            user = new Admin(id, uname, pass, nama,
                rs.getString("no_telp"), rs.getString("email"));
        } else {
            user = new Kasir(id, uname, pass, nama, rs.getString("shift"));
        }
        user.setAktif(aktif);
        return user;
    }
}
