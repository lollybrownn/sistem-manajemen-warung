# sistem-manajemen-warung

# 🏪 Sistem Manajemen Warung

Aplikasi desktop **Point of Sale (POS) dan manajemen warung** yang dibangun menggunakan **Java Swing** dan **MySQL**. Aplikasi ini membantu proses pengelolaan produk, transaksi penjualan, akun kasir, monitoring stok, serta laporan penjualan dalam satu sistem.

Project ini menggunakan pendekatan **MVC (Model-View-Controller)** dan **DAO (Data Access Object)** untuk memisahkan tampilan, logika aplikasi, dan akses database.

## ✨ Fitur Utama

### 🔐 Authentication & Role Management
- Login menggunakan username dan password
- Mendukung dua role pengguna:
  - **Admin**
  - **Kasir**
- Tampilan dan akses menu berbeda berdasarkan role pengguna
- Validasi akun aktif sebelum pengguna dapat masuk

### 📊 Dashboard Admin
Admin dapat melihat ringkasan kondisi warung secara langsung, seperti:

- Total produk aktif
- Jumlah transaksi hari ini
- Pendapatan hari ini
- Jumlah produk dengan stok rendah
- Peringatan produk yang perlu segera direstok

### 📦 Manajemen Produk
Admin dapat melakukan pengelolaan data produk:

- Menambahkan produk
- Mengubah produk
- Menghapus/nonaktifkan produk
- Mencari produk berdasarkan nama atau kode
- Mengatur kategori produk
- Mengatur harga beli dan harga jual
- Mengatur jumlah stok
- Mengatur satuan produk
- Monitoring produk dengan stok rendah atau habis

### 👨‍💼 Manajemen Kasir
Admin dapat mengelola akun kasir melalui sistem:

- Menambahkan akun kasir
- Mengubah data kasir
- Mengatur shift:
  - Pagi
  - Sore
  - Malam
- Menonaktifkan akun kasir
- Melihat status akun kasir

### 🛒 Point of Sale (POS)
Kasir dapat melakukan transaksi penjualan melalui halaman POS.

Fitur POS meliputi:

- Pencarian produk berdasarkan nama atau kode
- Menambahkan produk ke keranjang
- Menentukan jumlah barang
- Menampilkan harga dan stok produk
- Menghapus produk dari keranjang
- Perhitungan subtotal dan total otomatis
- Input jumlah pembayaran
- Perhitungan kembalian otomatis
- Shortcut nominal pembayaran
- Membatalkan transaksi
- Membuat transaksi baru
- Menampilkan struk setelah pembayaran berhasil

### 🧾 Riwayat Transaksi
Sistem menyimpan seluruh transaksi yang dilakukan.

Informasi yang tersedia:

- Nomor transaksi
- Tanggal dan waktu transaksi
- Nama kasir
- Jumlah item
- Total transaksi
- Jumlah pembayaran
- Kembalian
- Status transaksi

Detail masing-masing transaksi juga dapat dilihat dengan melakukan **double-click** pada transaksi yang dipilih.

### 📈 Laporan & Statistik
Admin dapat melihat statistik penjualan melalui beberapa laporan:

- Ringkasan transaksi hari ini
- Pendapatan hari ini
- Transaksi tujuh hari terakhir
- Pendapatan tujuh hari terakhir
- Total seluruh transaksi
- Total pendapatan
- Jumlah produk aktif
- Jumlah kasir
- Daftar produk terlaris
- Monitoring produk dengan stok rendah

---

## 🛠️ Tech Stack

| Teknologi | Kegunaan |
|---|---|
| Java | Bahasa pemrograman utama |
| Java Swing | Desktop GUI |
| MySQL | Database |
| JDBC | Koneksi Java dengan MySQL |
| MySQL Connector/J | JDBC Driver |
| Apache Ant | Build system |
| NetBeans | IDE/project configuration |

Project saat ini dikonfigurasi menggunakan **Java 25**.

---

## 🏗️ Arsitektur

Project menggunakan kombinasi **MVC + DAO**.

```text
Model
   ↓
Controller
   ↓
DAO
   ↓
MySQL Database

View ← Controller → Model
```

### MVC

**Model**

Berisi representasi data dan business entity seperti:

```text
User
Admin
Kasir
Produk
Transaksi
ItemTransaksi
```

**View**

Menangani tampilan aplikasi menggunakan Java Swing.

Contoh:

```text
LoginFrame
AdminFrame
KasirFrame
POSPanel
ProdukPanel
KasirPanel
HistoriPanel
LaporanPanel
```

**Controller**

Menangani business logic dan komunikasi antara View dan DAO.

Contoh:

```text
AuthController
UserController
ProdukController
TransaksiController
```

### DAO

DAO digunakan untuk memisahkan proses akses database dari business logic aplikasi.

Database diakses menggunakan **JDBC dan MySQL Connector/J**.

---

## 📁 Struktur Project

```text
sistem-manajemen-warung/
│
├── sistem_manajemen_warung/
│   │
│   ├── src/
│   │   ├── controller/
│   │   ├── dao/
│   │   ├── database/
│   │   ├── model/
│   │   ├── util/
│   │   ├── view/
│   │   │
│   │   └── App.java
│   │
│   ├── nbproject/
│   └── build.xml
│
├── sql/
│   └── warung_db.sql
│
└── README.md
```

---

## 🗄️ Database

Database yang digunakan adalah:

```text
warung_db
```

Database terdiri dari beberapa tabel utama:

| Tabel | Deskripsi |
|---|---|
| `users` | Menyimpan akun Admin dan Kasir |
| `produk` | Menyimpan informasi produk dan stok |
| `transaksi` | Menyimpan transaksi penjualan |
| `item_transaksi` | Menyimpan detail produk pada setiap transaksi |

Relasi sederhananya:

```text
users
  │
  └── transaksi
        │
        └── item_transaksi
                  │
                  └── produk
```

---

## 🚀 Instalasi dan Menjalankan Project

### 1. Clone Repository

```bash
git clone https://github.com/lollybrownn/sistem-manajemen-warung.git
cd sistem-manajemen-warung
```

### 2. Setup Database

Pastikan **MySQL Server** sudah berjalan.

Import file:

```text
sql/warung_db.sql
```

Menggunakan MySQL CLI:

```bash
mysql -u root -p < sql/warung_db.sql
```

Atau import file tersebut menggunakan:

- MySQL Workbench
- phpMyAdmin
- HeidiSQL
- Database management tool lainnya

Script akan membuat database:

```text
warung_db
```

beserta tabel dan data awal yang dibutuhkan aplikasi.

### 3. Konfigurasi Database

Buka:

```text
sistem_manajemen_warung/src/database/DBConnection.java
```

Sesuaikan konfigurasi MySQL:

```java
private static final String DB_HOST = "localhost";
private static final String DB_PORT = "3306";
private static final String DB_NAME = "warung_db";
private static final String DB_USER = "root";
private static final String DB_PASS = "";
```

Sesuaikan `DB_USER` dan `DB_PASS` dengan konfigurasi MySQL pada komputer.

### 4. Tambahkan MySQL Connector/J

Project membutuhkan **MySQL Connector/J** sebagai JDBC driver.

Jika NetBeans menampilkan **broken reference**, tambahkan file MySQL Connector/J melalui:

```text
Project
→ Properties
→ Libraries
→ Add JAR/Folder
```

Kemudian pilih file:

```text
mysql-connector-j-*.jar
```

Project saat ini sebelumnya dikonfigurasi menggunakan:

```text
mysql-connector-j-9.7.0.jar
```

### 5. Buka Project

Buka folder berikut menggunakan Apache NetBeans:

```text
sistem_manajemen_warung/
```

Pastikan JDK sudah terkonfigurasi dengan benar.

### 6. Jalankan Aplikasi

Jalankan:

```text
App.java
```

atau gunakan:

```text
Run Project
```

dari NetBeans.

Saat aplikasi dijalankan, sistem akan memeriksa koneksi MySQL terlebih dahulu sebelum membuka halaman login.

---

## 🔑 Default Account

Database menyediakan beberapa akun awal untuk keperluan development/demo.

### Admin

```text
Username : admin
Password : admin123
Role     : ADMIN
```

### Kasir 1

```text
Username : kasir1
Password : kasir123
Shift    : PAGI
```

### Kasir 2

```text
Username : kasir2
Password : kasir123
Shift    : SORE
```

> Akun di atas ditujukan untuk development/demo. Jangan gunakan credential default pada aplikasi production.

---

## 🔄 Alur Aplikasi

```text
                ┌───────────────┐
                │     Login     │
                └───────┬───────┘
                        │
                ┌───────▼───────┐
                │ Role Checking │
                └───────┬───────┘
                        │
              ┌─────────┴─────────┐
              │                   │
        ┌─────▼─────┐       ┌────▼─────┐
        │   ADMIN   │       │  KASIR   │
        └─────┬─────┘       └────┬─────┘
              │                   │
       ┌──────┼───────┐      ┌───┼─────────┐
       │      │       │      │   │         │
   Dashboard Produk  Kasir   POS Histori  Produk
       │      │       │      │
       │    Histori    │   Transaksi
       │      │        │      │
       └── Laporan ────┘   Pembayaran
                              │
                            Struk
```

---

## 🎯 Tujuan Project

Project ini dikembangkan untuk mempelajari sekaligus mengimplementasikan beberapa konsep pengembangan perangkat lunak, antara lain:

- Object-Oriented Programming (OOP)
- Java Desktop Development
- Java Swing GUI
- JDBC
- Relational Database
- CRUD Operations
- Authentication & Authorization
- Role-Based Access
- MVC Architecture
- DAO Pattern
- Transaction Management

---

## 🔮 Pengembangan Selanjutnya

Beberapa fitur yang dapat dikembangkan pada versi berikutnya:

- Password hashing
- Export laporan ke PDF / Excel
- Grafik penjualan
- Filter laporan berdasarkan tanggal
- Cetak struk menggunakan thermal printer
- Barcode scanner
- Backup & restore database
- Dashboard analitik yang lebih lengkap
- Configuration file untuk database
- Unit testing

---

## 👨‍💻 Author

**Albert**

Informatics Student  
Universitas Pembangunan Nasional "Veteran" Yogyakarta

GitHub: [@lollybrownn](https://github.com/lollybrownn)

---

## 📄 License

Project ini dibuat untuk tujuan **pembelajaran dan pengembangan portfolio**.

Feel free to fork, study, and develop this project further.
