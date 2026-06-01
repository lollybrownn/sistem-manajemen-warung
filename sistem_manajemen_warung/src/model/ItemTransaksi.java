package model;

public class ItemTransaksi {
    private int id, transaksiId, jumlah;
    private Produk produk;
    private double hargaSatuan, subtotal;

    public ItemTransaksi() {}
    public ItemTransaksi(int id, int transaksiId, Produk produk, int jumlah) {
        this.id=id; this.transaksiId=transaksiId; this.produk=produk;
        this.jumlah=jumlah; this.hargaSatuan=produk.getHargaJual(); hitungSubtotal();
    }
    public void hitungSubtotal() { this.subtotal = hargaSatuan * jumlah; }

    public int getId(){ 
        return id; }
    public void setId(int v){ 
        this.id=v; }
    public int getTransaksiId(){ 
        return transaksiId; }
    public void setTransaksiId(int v){ 
        this.transaksiId=v; }
    public Produk getProduk(){ 
        return produk; }
    public void setProduk(Produk v){ 
        this.produk=v; if(v!=null) hargaSatuan=v.getHargaJual(); hitungSubtotal(); }
    public int getJumlah(){ 
        return jumlah; }
    public void setJumlah(int v){ 
        this.jumlah=v; hitungSubtotal(); }
    public double getHargaSatuan(){ 
        return hargaSatuan; }
    public void setHargaSatuan(double v){ 
        this.hargaSatuan=v; hitungSubtotal(); }
    public double getSubtotal(){ 
        return subtotal; }
    public void setSubtotal(double v){ 
        this.subtotal=v; }
}
