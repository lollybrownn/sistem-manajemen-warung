package controller;

import dao.DaoFactory;
import dao.IProdukDao;
import model.Produk;
import java.util.List;

public class ProdukController {
    private final IProdukDao dao = DaoFactory.getProdukDao();

    public List<Produk> getAllProduk()           { return dao.findAll(); }
    public List<Produk> getProdukAktif()         { return dao.findAllActive(); }
    public Produk       getProdukById(int id)    { return dao.findById(id).orElse(null); }
    public Produk       getProdukByKode(String k){ return dao.findByKode(k).orElse(null); }
    public List<Produk> cariProduk(String kw)    { return dao.findByKeyword(kw); }
    public List<Produk> getProdukStokRendah()    { return dao.findStokRendah(); }
    public List<String> getKategoriList()        { return dao.findAllKategori(); }

    public String tambahProduk(String kode, String nama, String kat,
                                double hBeli, double hJual, int stok, String satuan) {
        if (kode.isBlank())  return "Kode produk tidak boleh kosong!";
        if (nama.isBlank())  return "Nama produk tidak boleh kosong!";
        if (hJual <= 0)      return "Harga jual harus lebih dari 0!";
        if (stok < 0)        return "Stok tidak boleh negatif!";
        if (dao.isKodeExists(kode)) return "Kode produk sudah digunakan!";
        Produk p = new Produk(0, kode.toUpperCase(), nama, kat, hBeli, hJual, stok, satuan);
        return dao.insert(p) ? "SUCCESS" : "Gagal menyimpan produk!";
    }

    public String updateProduk(int id, String kode, String nama, String kat,
                                double hBeli, double hJual, int stok, String satuan) {
        if (nama.isBlank()) return "Nama produk tidak boleh kosong!";
        if (hJual <= 0)     return "Harga jual harus lebih dari 0!";
        Produk p = dao.findById(id).orElse(null);
        if (p == null) return "Produk tidak ditemukan!";
        p.setKode(kode.toUpperCase()); p.setNama(nama); p.setKategori(kat);
        p.setHargaBeli(hBeli); p.setHargaJual(hJual); p.setStok(stok); p.setSatuan(satuan);
        return dao.update(p) ? "SUCCESS" : "Gagal memperbarui produk!";
    }

    public boolean hapusProduk(int id)           { return dao.delete(id); }
    public boolean updateStok(int id, int delta)  { return dao.updateStok(id, delta); }
}
