/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

 
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Mzing
 */
public class DBInitializer {
    
 
    private static final String[] CREATE_STATEMENTS = {
        "CREATE TABLE users (" +
        "    user_id        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
        "    username       VARCHAR(50)  NOT NULL UNIQUE," +
        "    password       VARCHAR(255) NOT NULL," +
        "    email          VARCHAR(100) NOT NULL UNIQUE," +
        "    full_name      VARCHAR(100) NOT NULL," +
        "    role           VARCHAR(20)  NOT NULL DEFAULT 'STOREKEEPER'" +
        "                   CHECK (role IN ('SUPERVISOR', 'STOREKEEPER'))," +
        "    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ")",
 
        "CREATE TABLE suppliers (" +
        "    supplier_id     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
        "    supplier_name   VARCHAR(100) NOT NULL," +
        "    contact_person  VARCHAR(100)," +
        "    phone           VARCHAR(20)," +
        "    email           VARCHAR(100)," +
        "    address         VARCHAR(255)," +
        "    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ")",
 
        "CREATE TABLE materials (" +
        "    material_id         INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
        "    material_name       VARCHAR(100) NOT NULL," +
        "    category            VARCHAR(50)," +
        "    quantity_available  INT NOT NULL DEFAULT 0 CHECK (quantity_available >= 0)," +
        "    reorder_level       INT NOT NULL DEFAULT 0 CHECK (reorder_level >= 0)," +
        "    unit_price          DECIMAL(10,2) DEFAULT 0 CHECK (unit_price >= 0)," +
        "    supplier_id         INT REFERENCES suppliers(supplier_id) ON DELETE SET NULL," +
        "    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ")",
 
        "CREATE TABLE cleaners (" +
        "    cleaner_id     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
        "    first_name     VARCHAR(50) NOT NULL," +
        "    last_name      VARCHAR(50) NOT NULL," +
        "    department     VARCHAR(50)," +
        "    phone          VARCHAR(20)," +
        "    email          VARCHAR(100)," +
        "    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ")",
 
        "CREATE TABLE stock_issuances (" +
        "    issuance_id      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
        "    material_id      INT NOT NULL REFERENCES materials(material_id)," +
        "    cleaner_id       INT NOT NULL REFERENCES cleaners(cleaner_id)," +
        "    quantity_issued  INT NOT NULL CHECK (quantity_issued > 0)," +
        "    issued_by        INT REFERENCES users(user_id) ON DELETE SET NULL," +
        "    issue_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
        "    notes            VARCHAR(255)" +
        ")",
 
        "CREATE INDEX idx_materials_name ON materials(material_name)",
        "CREATE INDEX idx_materials_cat ON materials(category)",
        "CREATE INDEX idx_cleaners_name ON cleaners(last_name, first_name)",
        "CREATE INDEX idx_suppliers_name ON suppliers(supplier_name)",
        "CREATE INDEX idx_issuance_date ON stock_issuances(issue_date)"
    };
 
    private static final String[] SEED_STATEMENTS = {
        "INSERT INTO users (username, password, email, full_name, role) VALUES ('admin', 'admin123', 'admin@belgiumcampus.ac.za', 'System Supervisor', 'SUPERVISOR')",
        "INSERT INTO users (username, password, email, full_name, role) VALUES ('storekeeper1', 'store123', 'store1@belgiumcampus.ac.za', 'John Store', 'STOREKEEPER')",
        "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES ('CleanCo Supplies', 'Sarah Jacobs', '0110009999', 'sales@cleanco.co.za', '12 Main Rd, Midrand')",
        "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES ('HygienePro', 'Peter Naidoo', '0119998888', 'orders@hygienepro.co.za', '45 Industrial Ave, Centurion')",
        "INSERT INTO materials (material_name, category, quantity_available, reorder_level, unit_price, supplier_id) VALUES ('All-Purpose Cleaner 5L', 'Chemicals', 40, 10, 85.50, 1)",
        "INSERT INTO materials (material_name, category, quantity_available, reorder_level, unit_price, supplier_id) VALUES ('Mop Head', 'Equipment', 15, 5, 45.00, 2)",
        "INSERT INTO materials (material_name, category, quantity_available, reorder_level, unit_price, supplier_id) VALUES ('Microfiber Cloths (Pack of 10)', 'Consumables', 8, 10, 60.00, 1)",
        "INSERT INTO cleaners (first_name, last_name, department, phone, email) VALUES ('Thabo', 'Mokoena', 'Facilities', '0821112222', 'thabo.m@belgiumcampus.ac.za')",
        "INSERT INTO cleaners (first_name, last_name, department, phone, email) VALUES ('Lindiwe', 'Dlamini', 'Residence', '0823334444', 'lindiwe.d@belgiumcampus.ac.za')"
    };
 
    /** Creates all tables if they don't already exist, then seeds sample data on first run only. */
    public static void initializeSchema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            for (String sql : CREATE_STATEMENTS) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    // X0Y32 = "table/index already exists" in Derby - safe to ignore
                    if (!"X0Y32".equals(e.getSQLState())) {
                        System.err.println("Schema creation issue: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        boolean firstRun = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) == 0) {
                firstRun = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        if (firstRun) {
            try (Statement stmt = conn.createStatement()) {
                for (String sql : SEED_STATEMENTS) {
                    stmt.execute(sql);
                }
                System.out.println("Sample data seeded into invSystemDB.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
