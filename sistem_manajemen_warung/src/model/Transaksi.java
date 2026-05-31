package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    public enum Status { PROSES, SELESAI, DIBATALKAN }

    private int id;
    private String nomorTransaksi, catatan;
    private LocalDateTime waktu;
    private User kasir;
    private List<ItemTransaksi> items = new ArrayList<>();
    private double totalBelanja, jumlahBayar, kembalian;
    private Status status = Status.PROSES;

    public Transaksi() { this.waktu = LocalDateTime.now(); }
    public Transaksi(int id, String nomor, User kasir) {
        this(); this.id=id; this.nomorTransaksi=nomor; this.kasir=kasir;
    }

    public void tambahItem(ItemTransaksi item) { items.add(item); hitungTotal(); }
    public void hapusItem(int idx) {
        if (idx >= 0 && idx < items.size()) { items.remove(idx); hitungTotal(); }
    }
    public void hitungTotal() {
        totalBelanja = items.stream().mapToDouble(ItemTransaksi::getSubtotal).sum();
    }
    public void bayar(double bayar) {
        this.jumlahBayar = bayar;
        this.kembalian   = bayar - totalBelanja;
        if (kembalian >= 0) status = Status.SELESAI;
    }
    public String getWaktuFormatted() {
        return waktu == null ? "-" : waktu.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    public String getTanggalFormatted() {
        return waktu == null ? "-" : waktu.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public int      getId()                       { return id; }
    public void     setId(int v)                  { this.id=v; }
    public String   getNomorTransaksi()            { return nomorTransaksi; }
    public void     setNomorTransaksi(String v)    { this.nomorTransaksi=v; }
    public LocalDateTime getWaktu()               { return waktu; }
    public void     setWaktu(LocalDateTime v)      { this.waktu=v; }
    public User     getKasir()                    { return kasir; }
    public void     setKasir(User v)              { this.kasir=v; }
    public List<ItemTransaksi> getItems()          { return items; }
    public void     setItems(List<ItemTransaksi> v){ this.items=v; hitungTotal(); }
    public double   getTotalBelanja()              { return totalBelanja; }
    public void     setTotalBelanja(double v)      { this.totalBelanja=v; }
    public double   getJumlahBayar()               { return jumlahBayar; }
    public void     setJumlahBayar(double v)       { this.jumlahBayar=v; }
    public double   getKembalian()                 { return kembalian; }
    public void     setKembalian(double v)         { this.kembalian=v; }
    public Status   getStatus()                   { return status; }
    public void     setStatus(Status v)            { this.status=v; }
    public String   getCatatan()                  { return catatan; }
    public void     setCatatan(String v)           { this.catatan=v; }
    public int      getJumlahItem()                { return items.size(); }
}
