package model;

public class Kasir extends User {
    private String shift;
    public Kasir(int id, String username, String password, String namaLengkap) {
        super(id, username, password, namaLengkap, "KASIR"); this.shift = "PAGI";
    }
    public Kasir(int id, String username, String password, String namaLengkap, String shift) {
        super(id, username, password, namaLengkap, "KASIR");
        this.shift = (shift == null || shift.isEmpty()) ? "PAGI" : shift;
    }
    @Override public String getDisplayInfo() { return "Kasir: " + getNamaLengkap() + " | Shift: " + shift; }
    public String getShift()       { return shift; }
    public void   setShift(String v){ this.shift = v; }
}
