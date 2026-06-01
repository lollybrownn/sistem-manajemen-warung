package model;

public class Admin extends User {
    private String noTelp, email;

    public Admin(int id, String username, String password, String namaLengkap) {
        super(id, username, password, namaLengkap, "ADMIN");
    }
    public Admin(int id, String username, String password, String namaLengkap, String noTelp, String email) {
        super(id, username, password, namaLengkap, "ADMIN");
        this.noTelp = noTelp; this.email = email;
    }
    @Override public String getDisplayInfo() { return "Administrator: " + getNamaLengkap(); }
    public String getNoTelp(){ return noTelp; }
    public void   setNoTelp(String v){ this.noTelp = v; }
    public String getEmail(){ return email; }
    public void   setEmail(String v){ this.email = v; }
}
