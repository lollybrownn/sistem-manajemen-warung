package controller;

import dao.DaoFactory;
import dao.IUserDao;
import model.Admin;
import model.Kasir;
import model.User;
import java.util.List;

public class UserController {
    private final IUserDao dao = DaoFactory.getUserDao();

    public List<User> getAllUsers()         { return dao.findAll(); }
    public List<User> getKasirList()        { return dao.findByRole("KASIR"); }
    public User       getUserById(int id)   { return dao.findById(id).orElse(null); }

    public String tambahKasir(String username, String password, String nama, String shift) {
        if (username.isBlank())      return "Username tidak boleh kosong!";
        if (password.length() < 6)  return "Password minimal 6 karakter!";
        if (nama.isBlank())          return "Nama lengkap tidak boleh kosong!";
        if (dao.isUsernameExists(username)) return "Username sudah digunakan!";
        Kasir k = new Kasir(0, username.trim(), password, nama.trim(), shift);
        return dao.insert(k) ? "SUCCESS" : "Gagal menyimpan data kasir!";
    }

    public String updateKasir(int id, String username, String password, String nama, String shift) {
        if (nama.isBlank()) return "Nama lengkap tidak boleh kosong!";
        User u = dao.findById(id).orElse(null);
        if (u == null) return "Kasir tidak ditemukan!";
        if (dao.isUsernameExists(username) &&
            dao.findByUsername(username).map(User::getId).orElse(-1) != id)
            return "Username sudah digunakan!";
        u.setUsername(username.trim());
        if (!password.isBlank()) u.setPassword(password);
        u.setNamaLengkap(nama.trim());
        if (u instanceof Kasir) ((Kasir) u).setShift(shift);
        return dao.update(u) ? "SUCCESS" : "Gagal memperbarui data!";
    }

    public boolean hapusKasir(int id) { return dao.delete(id); }
}
