package model;

public abstract class User {
    private int id;
    private String username, password, namaLengkap, role;
    private boolean aktif;

    public User(int id, String username, String password, String namaLengkap, String role) {
        this.id = id; this.username = username; this.password = password;
        this.namaLengkap = namaLengkap; this.role = role; this.aktif = true;
    }

    public abstract String getDisplayInfo();

    public int     getId()                    { return id; }
    public void    setId(int v)               { this.id = v; }
    public String  getUsername()              { return username; }
    public void    setUsername(String v)      { this.username = v; }
    public String  getPassword()              { return password; }
    public void    setPassword(String v)      { this.password = v; }
    public String  getNamaLengkap()           { return namaLengkap; }
    public void    setNamaLengkap(String v)   { this.namaLengkap = v; }
    public String  getRole()                  { return role; }
    public boolean isAktif()                  { return aktif; }
    public void    setAktif(boolean v)        { this.aktif = v; }
    @Override public String toString()        { return namaLengkap + " [" + role + "]"; }
}
