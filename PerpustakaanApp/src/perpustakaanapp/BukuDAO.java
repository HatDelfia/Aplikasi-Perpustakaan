package perpustakaanapp;

// BukuDAO.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BukuDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/perpustakaan?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Sesuaikan dengan user MySQL Anda
    private static final String PASSWORD = ""; // Sesuaikan dengan password MySQL Anda

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
        }
    }

    // Koneksi database (reusable)
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // CREATE - Tambah buku baru
    public void tambahBuku(Buku buku) throws SQLException {
        String sql = "INSERT INTO buku (judul_buku, nama_pengarang) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, buku.getJudulBuku());
            stmt.setString(2, buku.getNamaPengarang());
            stmt.executeUpdate();
            
            // Ambil ID yang di-generate
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    buku.setIdBuku(generatedKeys.getInt(1));
                }
            }
        }
    }

    // READ - Ambil semua buku
    public List<Buku> getAllBuku() throws SQLException {
        List<Buku> daftarBuku = new ArrayList<>();
        String sql = "SELECT * FROM buku ORDER BY judul_buku ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Buku b = new Buku();
                b.setIdBuku(rs.getInt("id_buku"));
                b.setJudulBuku(rs.getString("judul_buku"));
                b.setNamaPengarang(rs.getString("nama_pengarang"));
                daftarBuku.add(b);
            }
        }
        return daftarBuku;
    }

    // UPDATE - Edit data buku
    public void updateBuku(Buku buku) throws SQLException {
        String sql = "UPDATE buku SET judul_buku = ?, nama_pengarang = ? WHERE id_buku = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, buku.getJudulBuku());
            stmt.setString(2, buku.getNamaPengarang());
            stmt.setInt(3, buku.getIdBuku());
            stmt.executeUpdate();
        }
    }

    // DELETE - Hapus buku
    public void hapusBuku(int idBuku) throws SQLException {
        String sql = "DELETE FROM buku WHERE id_buku = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBuku);
            stmt.executeUpdate();
        }
    }

    // SEARCH - Cari buku berdasarkan judul atau pengarang
    public List<Buku> cariBuku(String keyword) throws SQLException {
        List<Buku> hasil = new ArrayList<>();
        String sql = "SELECT * FROM buku WHERE judul_buku LIKE ? OR nama_pengarang LIKE ? ORDER BY judul_buku ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String cari = "%" + keyword + "%";
            stmt.setString(1, cari);
            stmt.setString(2, cari);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Buku b = new Buku();
                    b.setIdBuku(rs.getInt("id_buku"));
                    b.setJudulBuku(rs.getString("judul_buku"));
                    b.setNamaPengarang(rs.getString("nama_pengarang"));
                    hasil.add(b);
                }
            }
        }
        return hasil;
    }
}