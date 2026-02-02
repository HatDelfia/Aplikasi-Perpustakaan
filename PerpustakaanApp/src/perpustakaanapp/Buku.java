package perpustakaanapp;


public class Buku {
    private int idBuku;
    private String judulBuku;
    private String namaPengarang;

    // Constructor default
    public Buku() {}

    // Constructor lengkap
    public Buku(int idBuku, String judulBuku, String namaPengarang) {
        this.idBuku = idBuku;
        this.judulBuku = judulBuku;
        this.namaPengarang = namaPengarang;
    }

    // Getter dan Setter
    public int getIdBuku() { return idBuku; }
    public void setIdBuku(int idBuku) { this.idBuku = idBuku; }

    public String getJudulBuku() { return judulBuku; }
    public void setJudulBuku(String judulBuku) { this.judulBuku = judulBuku; }

    public String getNamaPengarang() { return namaPengarang; }
    public void setNamaPengarang(String namaPengarang) { this.namaPengarang = namaPengarang; }

    @Override
    public String toString() {
        return "ID: " + idBuku + " | " + judulBuku + " - " + namaPengarang;
    }
}