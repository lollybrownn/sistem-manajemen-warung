package controller;

import dao.DaoFactory;
import dao.IProdukDao;
import dao.ITransaksiDao;
import model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TransaksiController {
    private final ITransaksiDao trxDao  = DaoFactory.getTransaksiDao();
    private final IProdukDao    prodDao = DaoFactory.getProdukDao();

    public Transaksi mulaiTransaksi(User kasir) {
        return new Transaksi(0, trxDao.generateNomor(), kasir);
    }

    public String tambahItem(Transaksi trx, Produk produk, int jumlah) {
        if (produk == null)               return "Produk tidak ditemukan!";
        if (jumlah <= 0)                  return "Jumlah harus lebih dari 0!";
        if (produk.getStok() < jumlah)    return "Stok tidak cukup! (stok: " + produk.getStok() + ")";
        for (ItemTransaksi item : trx.getItems()) {
            if (item.getProduk().getId() == produk.getId()) {
                int total = item.getJumlah() + jumlah;
                if (produk.getStok() < total) return "Stok tidak cukup! (stok: " + produk.getStok() + ")";
                item.setJumlah(total); trx.hitungTotal(); return "SUCCESS";
            }
        }
        trx.tambahItem(new ItemTransaksi(0, trx.getId(), produk, jumlah));
        return "SUCCESS";
    }

    public void hapusItem(Transaksi trx, int idx) { trx.hapusItem(idx); }

    public String prosesBayar(Transaksi trx, double bayar) {
        if (trx.getItems().isEmpty())       return "Keranjang masih kosong!";
        if (bayar < trx.getTotalBelanja())
            return String.format("Uang kurang Rp %,.0f", trx.getTotalBelanja() - bayar);
        // Kurangi stok
        for (ItemTransaksi item : trx.getItems()) {
            if (!prodDao.updateStok(item.getProduk().getId(), -item.getJumlah()))
                return "Stok produk \"" + item.getProduk().getNama() + "\" tidak mencukupi!";
        }
        trx.bayar(bayar);
        trx.setWaktu(LocalDateTime.now());
        return trxDao.insert(trx) ? "SUCCESS" : "Gagal menyimpan transaksi!";
    }

    public boolean batalkan(Transaksi trx) {
        trx.setStatus(Transaksi.Status.DIBATALKAN);
        return trx.getId() > 0 ? trxDao.update(trx) : true;
    }

    public List<Transaksi> getAllTransaksi(){ 
        return trxDao.findAll(); }
    public List<Transaksi> getTransaksiHariIni(){ 
        return trxDao.findByTanggal(LocalDate.now()); }
    public double getPendapatanHariIni(){ 
        LocalDate t=LocalDate.now(); return trxDao.getTotalPendapatan(t,t); }
    public int    getJumlahTrxHariIni(){ 
        return trxDao.countSelesaiByTanggal(LocalDate.now()); }
    public double getTotalPendapatanAll(){
        return trxDao.findAll().stream()
            .filter(t -> t.getStatus()==Transaksi.Status.SELESAI)
            .mapToDouble(Transaksi::getTotalBelanja).sum();
    }
}
