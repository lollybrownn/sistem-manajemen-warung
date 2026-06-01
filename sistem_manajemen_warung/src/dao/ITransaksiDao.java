package dao;

import model.Transaksi;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ITransaksiDao {
    boolean insert(Transaksi t);
    boolean update(Transaksi t);
    Optional<Transaksi> findById(int id);
    List<Transaksi>findAll();
    List<Transaksi>findByTanggal(LocalDate tanggal);
    List<Transaksi>findByRentang(LocalDate dari, LocalDate sampai);
    double getTotalPendapatan(LocalDate dari, LocalDate sampai);
    int countSelesaiByTanggal(LocalDate tanggal);
    String generateNomor();
}
