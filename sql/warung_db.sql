-- =====================================================
--  Sistem Manajemen Warung - MySQL Database Schema
--  Jalankan file ini di MySQL Workbench / phpMyAdmin
-- =====================================================

CREATE DATABASE IF NOT EXISTS warung_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE warung_db;

-- ---- Tabel Users (Admin & Kasir) ----
CREATE TABLE IF NOT EXISTS users (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(100) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    role         ENUM('ADMIN','KASIR') NOT NULL,
    aktif        TINYINT(1) DEFAULT 1,
    no_telp      VARCHAR(20),
    email        VARCHAR(100),
    shift        ENUM('PAGI','SORE','MALAM'),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---- Tabel Produk ----
CREATE TABLE IF NOT EXISTS produk (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    kode         VARCHAR(20)  NOT NULL UNIQUE,
    nama         VARCHAR(100) NOT NULL,
    kategori     VARCHAR(50)  NOT NULL,
    harga_beli   DECIMAL(12,2) DEFAULT 0,
    harga_jual   DECIMAL(12,2) NOT NULL,
    stok         INT DEFAULT 0,
    stok_minimal INT DEFAULT 5,
    satuan       VARCHAR(20)  NOT NULL,
    aktif        TINYINT(1) DEFAULT 1,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---- Tabel Transaksi ----
CREATE TABLE IF NOT EXISTS transaksi (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nomor         VARCHAR(30) NOT NULL UNIQUE,
    waktu         DATETIME DEFAULT CURRENT_TIMESTAMP,
    kasir_id      INT,
    kasir_nama    VARCHAR(100),
    total_belanja DECIMAL(12,2) DEFAULT 0,
    jumlah_bayar  DECIMAL(12,2) DEFAULT 0,
    kembalian     DECIMAL(12,2) DEFAULT 0,
    status        ENUM('PROSES','SELESAI','DIBATALKAN') DEFAULT 'PROSES',
    catatan       VARCHAR(255),
    FOREIGN KEY (kasir_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---- Tabel Item Transaksi ----
CREATE TABLE IF NOT EXISTS item_transaksi (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    transaksi_id INT NOT NULL,
    produk_id    INT,
    produk_nama  VARCHAR(100),
    produk_kode  VARCHAR(20),
    jumlah       INT NOT NULL,
    harga_satuan DECIMAL(12,2) NOT NULL,
    subtotal     DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (transaksi_id) REFERENCES transaksi(id) ON DELETE CASCADE,
    FOREIGN KEY (produk_id)    REFERENCES produk(id)    ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================
--  DATA AWAL (Seed)
-- =====================================================

-- Admin & Kasir default
INSERT INTO users (username, password, nama_lengkap, role, no_telp, email) VALUES
('admin',  'admin123',  'Administrator',  'ADMIN', '08123456789', 'admin@warung.com');

INSERT INTO users (username, password, nama_lengkap, role, shift) VALUES
('kasir1', 'kasir123', 'Budi Santoso',  'KASIR', 'PAGI'),
('kasir2', 'kasir123', 'Siti Rahayu',   'KASIR', 'SORE');

-- Produk awal
INSERT INTO produk (kode, nama, kategori, harga_beli, harga_jual, stok, satuan) VALUES
('P001', 'Indomie Goreng',          'Makanan',    1500,  2500,  100, 'pcs'),
('P002', 'Indomie Rebus',           'Makanan',    1500,  2500,   80, 'pcs'),
('P003', 'Mie Sedaap Goreng',       'Makanan',    1800,  3000,   60, 'pcs'),
('P004', 'Aqua 600ml',              'Minuman',    2000,  3500,   50, 'botol'),
('P005', 'Le Minerale 600ml',       'Minuman',    2000,  3500,   40, 'botol'),
('P006', 'Teh Botol Sosro',         'Minuman',    3000,  5000,   40, 'botol'),
('P007', 'Kopi Good Day Sachet',    'Minuman',    1500,  2000,   60, 'pcs'),
('P008', 'Susu Ultra 200ml',        'Minuman',    3500,  5500,   30, 'kotak'),
('P009', 'Roti Tawar Sari Roti',    'Roti',       8000, 12000,   20, 'bungkus'),
('P010', 'Roti Sobek Sari Roti',    'Roti',       9000, 13000,   15, 'bungkus'),
('P011', 'Gula Pasir 1kg',          'Sembako',   12000, 15000,   30, 'kg'),
('P012', 'Minyak Goreng Bimoli 1L', 'Sembako',   15000, 19000,   25, 'liter'),
('P013', 'Beras 5kg',               'Sembako',   55000, 65000,   15, 'kg'),
('P014', 'Telur 1kg',               'Sembako',   25000, 30000,   20, 'kg'),
('P015', 'Sabun Lifebuoy',          'Kebersihan',  3000,  5000,  35, 'pcs'),
('P016', 'Sampo Sunsilk Sachet',    'Kebersihan',  1500,  2500,  50, 'pcs'),
('P017', 'Pasta Gigi Pepsodent',    'Kebersihan',  9000, 12000,  25, 'pcs'),
('P018', 'Deterjen Rinso Sachet',   'Kebersihan',  2000,  3500,  40, 'pcs'),
('P019', 'Rokok Sampoerna Mild',    'Rokok',      18000, 23000,  20, 'bungkus'),
('P020', 'Rokok Gudang Garam',      'Rokok',      19000, 24000,  15, 'bungkus');
