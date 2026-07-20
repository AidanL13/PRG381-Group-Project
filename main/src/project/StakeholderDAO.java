package project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Database access for Member 4: Suppliers and Cleaners.
 * PreparedStatement is used throughout to prevent SQL injection.
 */
public class StakeholderDAO {
    private final DBConnection database;

    public StakeholderDAO(DBConnection database) {
        this.database = database;
    }

    private Connection connection() throws SQLException {
        Connection connection = database.getOpenConnection();
        if (connection == null) {
            throw new SQLException("The database connection could not be opened.");
        }
        return connection;
    }

    // ---------------- SUPPLIERS ----------------
    public List<Supplier> searchSuppliers(String keyword) throws SQLException {
        String value = keyword == null ? "" : keyword.trim().toLowerCase();
        String sql = "SELECT supplier_id, supplier_name, contact_person, phone, email, address "
                + "FROM suppliers WHERE LOWER(supplier_name) LIKE ? "
                + "OR LOWER(COALESCE(contact_person, '')) LIKE ? "
                + "OR LOWER(COALESCE(phone, '')) LIKE ? "
                + "OR LOWER(COALESCE(email, '')) LIKE ? "
                + "OR LOWER(COALESCE(address, '')) LIKE ? ORDER BY supplier_name";
        List<Supplier> suppliers = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            String pattern = "%" + value + "%";
            for (int i = 1; i <= 5; i++) ps.setString(i, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(new Supplier(rs.getInt("supplier_id"), rs.getString("supplier_name"),
                            rs.getString("contact_person"), rs.getString("phone"),
                            rs.getString("email"), rs.getString("address")));
                }
            }
        }
        return suppliers;
    }

    public int addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setSupplierParameters(ps, supplier);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE suppliers SET supplier_name=?, contact_person=?, phone=?, email=?, address=? WHERE supplier_id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            setSupplierParameters(ps, supplier);
            ps.setInt(6, supplier.getSupplierId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteSupplier(int supplierId) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            return ps.executeUpdate() == 1;
        }
    }

    private void setSupplierParameters(PreparedStatement ps, Supplier supplier) throws SQLException {
        ps.setString(1, supplier.getSupplierName());
        ps.setString(2, supplier.getContactPerson());
        ps.setString(3, supplier.getPhone());
        ps.setString(4, supplier.getEmail());
        ps.setString(5, supplier.getAddress());
    }

    // ---------------- CLEANERS ----------------
    public List<Cleaner> searchCleaners(String keyword) throws SQLException {
        String value = keyword == null ? "" : keyword.trim().toLowerCase();
        String sql = "SELECT cleaner_id, first_name, last_name, department, phone, email "
                + "FROM cleaners WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? "
                + "OR LOWER(COALESCE(department, '')) LIKE ? OR LOWER(COALESCE(phone, '')) LIKE ? "
                + "OR LOWER(COALESCE(email, '')) LIKE ? ORDER BY last_name, first_name";
        List<Cleaner> cleaners = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            String pattern = "%" + value + "%";
            for (int i = 1; i <= 5; i++) ps.setString(i, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cleaners.add(new Cleaner(rs.getInt("cleaner_id"), rs.getString("first_name"),
                            rs.getString("last_name"), rs.getString("email"),
                            rs.getString("phone"), rs.getString("department")));
                }
            }
        }
        return cleaners;
    }

    public int addCleaner(Cleaner cleaner) throws SQLException {
        String sql = "INSERT INTO cleaners (first_name, last_name, department, phone, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setCleanerParameters(ps, cleaner);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public boolean updateCleaner(Cleaner cleaner) throws SQLException {
        String sql = "UPDATE cleaners SET first_name=?, last_name=?, department=?, phone=?, email=? WHERE cleaner_id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            setCleanerParameters(ps, cleaner);
            ps.setInt(6, cleaner.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteCleaner(int cleanerId) throws SQLException {
        String sql = "DELETE FROM cleaners WHERE cleaner_id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, cleanerId);
            return ps.executeUpdate() == 1;
        }
    }

    private void setCleanerParameters(PreparedStatement ps, Cleaner cleaner) throws SQLException {
        ps.setString(1, cleaner.getFirstName());
        ps.setString(2, cleaner.getLastName());
        ps.setString(3, cleaner.getDepartment());
        ps.setString(4, cleaner.getPhone());
        ps.setString(5, cleaner.getEmail());
    }
}
