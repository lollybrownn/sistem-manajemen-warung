package dao;

import model.Produk;
import java.util.List;
import java.util.Optional;

public interface IProdukDao {
    boolean         insert(Produk p);
    boolean         update(Produk p);
    boolean         delete(int id);
    Optional<Produk> findById(int id);
    Optional<Produk> findByKode(String kode);
    List<Produk>    findAll();
    List<Produk>    findAllActive();
    List<Produk>    findByKeyword(String keyword);
    List<Produk>    findByKategori(String kategori);
    List<Produk>    findStokRendah();
    List<String>    findAllKategori();
    boolean         updateStok(int id, int delta);
    boolean         isKodeExists(String kode);
}
