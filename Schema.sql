
-- Drop tables if they exist 
DROP TABLE IF EXISTS stock_issuances CASCADE;
DROP TABLE IF EXISTS materials CASCADE;
DROP TABLE IF EXISTS cleaners CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS users CASCADE;
 

-- 1. USERS  (login / registration / role-based access)

CREATE TABLE users (
    user_id        SERIAL PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,           -- store a hashed password, never plain text
    email          VARCHAR(100) NOT NULL UNIQUE,
    full_name      VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL DEFAULT 'STOREKEEPER'
                   CHECK (role IN ('SUPERVISOR', 'STOREKEEPER')),
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
 

-- 2. SUPPLIERS

CREATE TABLE suppliers (
    supplier_id     SERIAL PRIMARY KEY,
    supplier_name   VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
 

-- 3. MATERIALS  (linked to a supplier)

CREATE TABLE materials (
    material_id         SERIAL PRIMARY KEY,
    material_name       VARCHAR(100) NOT NULL,
    category            VARCHAR(50),
    quantity_available  INT NOT NULL DEFAULT 0 CHECK (quantity_available >= 0),
    reorder_level       INT NOT NULL DEFAULT 0 CHECK (reorder_level >= 0),
    unit_price          NUMERIC(10,2) DEFAULT 0 CHECK (unit_price >= 0),
    supplier_id         INT REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
 

-- 4. CLEANERS

CREATE TABLE cleaners (
    cleaner_id     SERIAL PRIMARY KEY,
    first_name     VARCHAR(50) NOT NULL,
    last_name      VARCHAR(50) NOT NULL,
    department     VARCHAR(50),
    phone          VARCHAR(20),
    email          VARCHAR(100),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
 

-- 5. STOCK ISSUANCES  (transaction table - links everything)

CREATE TABLE stock_issuances (
    issuance_id      SERIAL PRIMARY KEY,
    material_id      INT NOT NULL REFERENCES materials(material_id) ON DELETE RESTRICT,
    cleaner_id       INT NOT NULL REFERENCES cleaners(cleaner_id) ON DELETE RESTRICT,
    quantity_issued  INT NOT NULL CHECK (quantity_issued > 0),
    issued_by        INT REFERENCES users(user_id) ON DELETE SET NULL,
    issue_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes            VARCHAR(255)
);
 

-- Helpful indexes for search & filtering (used by Member 3 & 5)

CREATE INDEX idx_materials_name     ON materials(material_name);
CREATE INDEX idx_materials_category ON materials(category);
CREATE INDEX idx_cleaners_name      ON cleaners(last_name, first_name);
CREATE INDEX idx_suppliers_name     ON suppliers(supplier_name);
CREATE INDEX idx_issuance_date      ON stock_issuances(issue_date);
 

-- Seed data (optional - lets the team test immediately)

INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', 'admin123', 'admin@belgiumcampus.ac.za', 'System Supervisor', 'SUPERVISOR'),
('storekeeper1', 'store123', 'store1@belgiumcampus.ac.za', 'John Store', 'STOREKEEPER');
 
INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES
('CleanCo Supplies', 'Sarah Jacobs', '0110009999', 'sales@cleanco.co.za', '12 Main Rd, Midrand'),
('HygienePro', 'Peter Naidoo', '0119998888', 'orders@hygienepro.co.za', '45 Industrial Ave, Centurion');
 
INSERT INTO materials (material_name, category, quantity_available, reorder_level, unit_price, supplier_id) VALUES
('All-Purpose Cleaner 5L', 'Chemicals', 40, 10, 85.50, 1),
('Mop Head', 'Equipment', 15, 5, 45.00, 2),
('Microfiber Cloths (Pack of 10)', 'Consumables', 8, 10, 60.00, 1);
 
INSERT INTO cleaners (first_name, last_name, department, phone, email) VALUES
('Thabo', 'Mokoena', 'Facilities', '0821112222', 'thabo.m@belgiumcampus.ac.za'),
('Lindiwe', 'Dlamini', 'Residence', '0823334444', 'lindiwe.d@belgiumcampus.ac.za');
