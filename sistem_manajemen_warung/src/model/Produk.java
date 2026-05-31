package model;

public class Produk {
    private int id, stok, stokMinimal;
    private String kode, nama, kategori, satuan;
    private double hargaBeli, hargaJual;
    private boolean aktif;

    public Produk() { aktif = true; stokMinimal = 5; }
    public Produk(int id, String kode, String nama, String kategori,
                  double hargaBeli, double hargaJual, int stok, String satuan) {
        this(); this.id=id; this.kode=kode; this.nama=nama; this.kategori=kategori;
        this.hargaBeli=hargaBeli; this.hargaJual=hargaJual; this.stok=stok; this.satuan=satuan;
    }

    public boolean isStokRendah() { return stok > 0 && stok <= stokMinimal; }
    public boolean isStokHabis()  { return stok <= 0; }
    public double  getKeuntungan(){ return hargaJual - hargaBeli; }

    public int    getId()                  { return id; }
    public void   setId(int v)             { this.id=v; }
    public String getKode()                { return kode; }
    public void   setKode(String v)        { this.kode=v; }
    public String getNama()                { return nama; }
    public void   setNama(String v)        { this.nama=v; }
    public String getKategori()            { return kategori; }
    public void   setKategori(String v)    { this.kategori=v; }
    public String getSatuan()              { return satuan; }
    public void   setSatuan(String v)      { this.satuan=v; }
    public double getHargaBeli()           { return hargaBeli; }
    public void   setHargaBeli(double v)   { this.hargaBeli=v; }
    public double getHargaJual()           { return hargaJual; }
    public void   setHargaJual(double v)   { this.hargaJual=v; }
    public int    getStok()                { return stok; }
    public void   setStok(int v)           { this.stok=v; }
    public int    getStokMinimal()         { return stokMinimal; }
    public void   setStokMinimal(int v)    { this.stokMinimal=v; }
    public boolean isAktif()              { return aktif; }
    public void   setAktif(boolean v)     { this.aktif=v; }
    @Override public String toString()    { return "[" + kode + "] " + nama; }
}
