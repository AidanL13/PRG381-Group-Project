/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 *
 * @author Michael - Uni
 */

 
/**
 *
 * @author Mzing
 */
public class DBConnection {
 
    // Relative path to the existing "invSystemDB" folder in your project.
    // "create=true" is safe even if the database already exists - Derby
    // just opens it normally in that case.
    private static final String DB_URL = "jdbc:derby:invSystemDB;create=true";
 
    private Connection connection;
 
    /**
     * Loads the embedded Derby driver, opens the connection, and makes sure
     * all required tables exist (creating + seeding them on first run only).
     * Matches the existing call: db.connect();
     */
    public void connect() throws ClassNotFoundException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to embedded Derby database: invSystemDB");
            DBInitializer.initializeSchema(connection);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the Derby database.");
            e.printStackTrace();
        }
    }
 
  
    private Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
 

    public String login(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
 
   
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return hasUpper && hasDigit && hasSpecial;
    }
 
   
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 
  
    public boolean addUser(String username, String email, String password, String role) {
        if (role == null || role.trim().isEmpty()) {
            System.err.println("Cannot register user with a blank role.");
            return false;
        }
        String sql = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, username);
            ps.setString(5, role.trim().toUpperCase());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String issueStock(int materialId, int cleanerId, int quantity) {
    Connection conn = getConnection();
    if (conn == null) {
        return "Database connection is not active.";
    }

    String checkSql = "SELECT quantity_available FROM materials WHERE material_id = ?";
    String deductSql = "UPDATE materials SET quantity_available = quantity_available - ? WHERE material_id = ?";
    String insertSql = "INSERT INTO stock_issuances (material_id, cleaner_id, quantity_issued, issued_by, notes) VALUES (?, ?, ?, ?, ?)";
    String userIdSql = "SELECT user_id FROM users WHERE username = ?";

    try {
        conn.setAutoCommit(false);

        int available;
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    conn.rollback();
                    return "Material ID " + materialId + " does not exist.";
                }
                available = rs.getInt("quantity_available");
            }
        }

        if (available < quantity) {
            conn.rollback();
            return "Cannot issue " + quantity + " unit(s). Only " + available + " available.";
        }

        Integer issuedBy = null;
        if (Session.currentUser != null) {
            try (PreparedStatement ps = conn.prepareStatement(userIdSql)) {
                ps.setString(1, Session.currentUser);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        issuedBy = rs.getInt("user_id");
                    }
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(deductSql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, materialId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, materialId);
            ps.setInt(2, cleanerId);
            ps.setInt(3, quantity);
            if (issuedBy != null) {
                ps.setInt(4, issuedBy);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, null);
            ps.executeUpdate();
        }

        conn.commit();
        return "SUCCESS";

    } catch (SQLException e) {
        try {
            conn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        if ("23503".equals(e.getSQLState())) {
            return "Cleaner ID " + cleanerId + " does not exist.";
        }
        e.printStackTrace();
        return "Transaction failed: " + e.getMessage();
    } finally {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    public java.util.List<Object[]> getIssuanceHistory() {
    java.util.List<Object[]> rows = new java.util.ArrayList<>();
    Connection conn = getConnection();
    if (conn == null) return rows;

    String sql = "SELECT si.issuance_id, m.material_name, "
               + "c.first_name || ' ' || c.last_name AS cleaner_name, "
               + "si.quantity_issued, si.issue_date "
               + "FROM stock_issuances si "
               + "JOIN materials m ON si.material_id = m.material_id "
               + "JOIN cleaners c ON si.cleaner_id = c.cleaner_id "
               + "ORDER BY si.issue_date DESC";

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("issuance_id"),
                rs.getString("material_name"),
                rs.getString("cleaner_name"),
                rs.getInt("quantity_issued"),
                rs.getTimestamp("issue_date")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return rows;
}

public java.util.List<Object[]> getInventoryReport() {
    java.util.List<Object[]> rows = new java.util.ArrayList<>();
    Connection conn = getConnection();
    if (conn == null) return rows;

    String sql = "SELECT material_id, material_name, quantity_available, reorder_level "
               + "FROM materials ORDER BY material_name";

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("material_id"),
                rs.getString("material_name"),
                rs.getInt("quantity_available"),
                rs.getInt("reorder_level")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return rows;
}

public java.util.List<Object[]> getLowStockReport() {
    java.util.List<Object[]> rows = new java.util.ArrayList<>();
    Connection conn = getConnection();
    if (conn == null) return rows;

    String sql = "SELECT material_id, material_name, quantity_available, reorder_level "
               + "FROM materials WHERE quantity_available <= reorder_level "
               + "ORDER BY quantity_available ASC";

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("material_id"),
                rs.getString("material_name"),
                rs.getInt("quantity_available"),
                rs.getInt("reorder_level")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return rows;
}

public java.util.List<Object[]> getMaterialUsageReport() {
    java.util.List<Object[]> rows = new java.util.ArrayList<>();
    Connection conn = getConnection();
    if (conn == null) return rows;

    String sql = "SELECT m.material_id, m.material_name, "
               + "COALESCE(SUM(si.quantity_issued), 0) AS total_issued "
               + "FROM materials m "
               + "LEFT JOIN stock_issuances si ON m.material_id = si.material_id "
               + "GROUP BY m.material_id, m.material_name "
               + "ORDER BY total_issued DESC";

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("material_id"),
                rs.getString("material_name"),
                rs.getInt("total_issued")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return rows;
}
 
 
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
