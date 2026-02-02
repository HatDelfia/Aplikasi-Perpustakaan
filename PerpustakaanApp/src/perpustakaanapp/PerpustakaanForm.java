package perpustakaanapp;

// PerpustakaanForm.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class PerpustakaanForm extends JFrame {
    private JTextField txtId, txtJudul, txtPengarang, txtCari;
    private JButton btnTambah, btnEdit, btnHapus, btnCari, btnBersih;
    private JTable table;
    private DefaultTableModel tableModel;
    private BukuDAO bukuDAO = new BukuDAO();
    private boolean isEditMode = false;

    public PerpustakaanForm() {
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("📚 Aplikasi Manajemen Perpustakaan");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Panel Input
        JPanel panelInput = new JPanel(new GridLayout(3, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Form Data Buku"));
        panelInput.setBackground(Color.WHITE);
        panelInput.setOpaque(true);

        panelInput.add(new JLabel("ID Buku:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelInput.add(txtId);

        panelInput.add(new JLabel("* Judul Buku:"));
        txtJudul = new JTextField();
        panelInput.add(txtJudul);

        panelInput.add(new JLabel("* Nama Pengarang:"));
        txtPengarang = new JTextField();
        panelInput.add(txtPengarang);

        // Panel Pencarian
        JPanel panelCari = new JPanel(new BorderLayout(5, 5));
        panelCari.setBorder(BorderFactory.createTitledBorder("Pencarian"));
        panelCari.setBackground(Color.WHITE);
        panelCari.setOpaque(true);
        
        txtCari = new JTextField(20);
        txtCari.setToolTipText("Cari berdasarkan judul atau nama pengarang");
        btnCari = new JButton("🔍 Cari");
        btnBersih = new JButton("↺ Reset");
        
        JPanel panelBtnCari = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBtnCari.add(new JLabel("Cari:"));
        panelBtnCari.add(txtCari);
        panelBtnCari.add(btnCari);
        panelBtnCari.add(btnBersih);
        
        panelCari.add(panelBtnCari, BorderLayout.CENTER);

        // Tombol Aksi
        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnTambah = new JButton(" Tambah");
        btnEdit = new JButton(" Edit");
        btnHapus = new JButton(" Hapus");
        JButton btnKeluar = new JButton(" Keluar");
        
        panelAksi.add(btnTambah);
        panelAksi.add(btnEdit);
        panelAksi.add(btnHapus);
        panelAksi.add(btnKeluar);

        // Tabel Data
        String[] kolom = {"ID", "Judul Buku", "Nama Pengarang"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel readonly
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);

        // Tooltip untuk kolom
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

        // Event Listeners
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single click
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        txtId.setText(tableModel.getValueAt(row, 0).toString());
                        txtJudul.setText(tableModel.getValueAt(row, 1).toString());
                        txtPengarang.setText(tableModel.getValueAt(row, 2).toString());
                        isEditMode = true;
                        btnTambah.setText("➕ Tambah");
                    }
                }
            }
        });

        btnTambah.addActionListener(e -> handleTambah());
        btnEdit.addActionListener(e -> handleEdit());
        btnHapus.addActionListener(e -> handleHapus());
        btnCari.addActionListener(e -> handleCari());
        btnBersih.addActionListener(e -> {
            txtCari.setText("");
            loadData();
        });
        btnKeluar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin keluar?", "Konfirmasi", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // Keyboard shortcuts
        txtCari.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleCari();
                }
            }
        });

        // Susun layout
        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelUtama.setBackground(new Color(245, 245, 245));
        
        JPanel panelKiri = new JPanel(new BorderLayout(10, 10));
        panelKiri.add(panelInput, BorderLayout.NORTH);
        panelKiri.add(panelCari, BorderLayout.CENTER);
        
        JPanel panelKanan = new JPanel(new BorderLayout());
        panelKanan.add(panelAksi, BorderLayout.NORTH);
        panelKanan.add(scrollPane, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelKiri, panelKanan);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.4);
        
        panelUtama.add(splitPane, BorderLayout.CENTER);
        add(panelUtama, BorderLayout.CENTER);

        // Style tombol
        styleButton(btnTambah, new Color(46, 204, 113));
        styleButton(btnEdit, new Color(52, 152, 219));
        styleButton(btnHapus, new Color(231, 76, 60));
        styleButton(btnKeluar, new Color(149, 165, 166));
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0); // Clear table
            List<Buku> daftarBuku = bukuDAO.getAllBuku();
            
            for (Buku b : daftarBuku) {
                Object[] row = {
                    b.getIdBuku(),
                    b.getJudulBuku(),
                    b.getNamaPengarang()
                };
                tableModel.addRow(row);
            }
            
            setTitle("📚 Aplikasi Manajemen Perpustakaan - Total Buku: " + daftarBuku.size());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal memuat data: " + e.getMessage(), 
                "Error Database", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTambah() {
        try {
            String judul = txtJudul.getText().trim();
            String pengarang = txtPengarang.getText().trim();
            
            if (judul.isEmpty() || pengarang.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Judul buku dan nama pengarang tidak boleh kosong!", 
                    "Validasi", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Buku buku = new Buku();
            buku.setJudulBuku(judul);
            buku.setNamaPengarang(pengarang);
            
            bukuDAO.tambahBuku(buku);
            JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihForm();
            loadData();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal menambah buku: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        try {
            if (!isEditMode || txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Pilih buku yang akan di-edit dari tabel terlebih dahulu!", 
                    "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String judul = txtJudul.getText().trim();
            String pengarang = txtPengarang.getText().trim();
            
            if (judul.isEmpty() || pengarang.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Judul buku dan nama pengarang tidak boleh kosong!", 
                    "Validasi", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Buku buku = new Buku();
            buku.setIdBuku(Integer.parseInt(txtId.getText()));
            buku.setJudulBuku(judul);
            buku.setNamaPengarang(pengarang);
            
            bukuDAO.updateBuku(buku);
            JOptionPane.showMessageDialog(this, "Data buku berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            bersihForm();
            loadData();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID buku tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal mengupdate buku: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleHapus() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Pilih buku yang akan dihapus dari tabel terlebih dahulu!", 
                    "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus buku ini?\n" + 
                "Judul: " + txtJudul.getText() + "\n" +
                "Pengarang: " + txtPengarang.getText(),
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (konfirmasi == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(txtId.getText());
                bukuDAO.hapusBuku(id);
                JOptionPane.showMessageDialog(this, "Buku berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                bersihForm();
                loadData();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID buku tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal menghapus buku: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCari() {
        try {
            String keyword = txtCari.getText().trim();
            if (keyword.isEmpty()) {
                loadData();
                return;
            }
            
            List<Buku> hasil = bukuDAO.cariBuku(keyword);
            tableModel.setRowCount(0);
            
            for (Buku b : hasil) {
                Object[] row = {
                    b.getIdBuku(),
                    b.getJudulBuku(),
                    b.getNamaPengarang()
                };
                tableModel.addRow(row);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Ditemukan " + hasil.size() + " buku yang sesuai dengan kata kunci \"" + keyword + "\"", 
                "Hasil Pencarian", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal melakukan pencarian: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bersihForm() {
        txtId.setText("");
        txtJudul.setText("");
        txtPengarang.setText("");
        txtJudul.requestFocus();
        isEditMode = false;
        btnTambah.setText("➕ Tambah");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel modern
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new PerpustakaanForm().setVisible(true);
        });
    }
}