package project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/** Complete Member 4 Swing UI: Supplier and Cleaner CRUD, search and validation. */
public class StakeholderManagementFrame extends JFrame {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+() -]{7,20}$");

    private final DBConnection database = new DBConnection();
    private final StakeholderDAO repository = new StakeholderDAO(database);

    private final JTextField supplierId = readonlyField();
    private final JTextField supplierName = new JTextField(20);
    private final JTextField contactPerson = new JTextField(20);
    private final JTextField supplierPhone = new JTextField(20);
    private final JTextField supplierEmail = new JTextField(20);
    private final JTextField supplierAddress = new JTextField(20);
    private final JTextField supplierSearch = new JTextField(22);
    private final DefaultTableModel supplierModel = nonEditableModel(
            "ID", "Supplier Name", "Contact Person", "Phone", "Email", "Address");
    private final JTable supplierTable = new JTable(supplierModel);

    private final JTextField cleanerId = readonlyField();
    private final JTextField firstName = new JTextField(20);
    private final JTextField lastName = new JTextField(20);
    private final JTextField department = new JTextField(20);
    private final JTextField cleanerPhone = new JTextField(20);
    private final JTextField cleanerEmail = new JTextField(20);
    private final JTextField cleanerSearch = new JTextField(22);
    private final DefaultTableModel cleanerModel = nonEditableModel(
            "ID", "First Name", "Last Name", "Department", "Phone", "Email");
    private final JTable cleanerTable = new JTable(cleanerModel);

    public StakeholderManagementFrame() {
        super("Stakeholder Management - Suppliers and Cleaners");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1050, 650);
        setLocationRelativeTo(null);
        try {
            database.connect();
        } catch (ClassNotFoundException ex) {
            showError("Derby driver was not found. Check that the Derby JAR files are included.", ex);
        }
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Suppliers", buildSupplierPanel());
        tabs.addTab("Cleaners", buildCleanerPanel());
        add(tabs, BorderLayout.CENTER);
        configureTables();
        refreshSuppliers();
        refreshCleaners();
    }

    private JPanel buildSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel fields = formPanel();
        addField(fields, 0, "Supplier ID", supplierId);
        addField(fields, 1, "Supplier Name *", supplierName);
        addField(fields, 2, "Contact Person", contactPerson);
        addField(fields, 3, "Phone", supplierPhone);
        addField(fields, 4, "Email", supplierEmail);
        addField(fields, 5, "Address", supplierAddress);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        add.addActionListener(e -> addSupplier());
        update.addActionListener(e -> updateSupplier());
        delete.addActionListener(e -> deleteSupplier());
        clear.addActionListener(e -> clearSupplierForm());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear);
        JPanel north = new JPanel(new BorderLayout());
        north.add(fields, BorderLayout.CENTER);
        north.add(actions, BorderLayout.SOUTH);
        JPanel search = searchPanel(supplierSearch, () -> refreshSuppliers(), () -> {supplierSearch.setText(""); refreshSuppliers();});
        panel.add(search, BorderLayout.NORTH);
        panel.add(new JScrollPane(supplierTable), BorderLayout.CENTER);
        panel.add(north, BorderLayout.WEST);
        return panel;
    }

    private JPanel buildCleanerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel fields = formPanel();
        addField(fields, 0, "Cleaner ID", cleanerId);
        addField(fields, 1, "First Name *", firstName);
        addField(fields, 2, "Last Name *", lastName);
        addField(fields, 3, "Department", department);
        addField(fields, 4, "Phone", cleanerPhone);
        addField(fields, 5, "Email", cleanerEmail);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        add.addActionListener(e -> addCleaner());
        update.addActionListener(e -> updateCleaner());
        delete.addActionListener(e -> deleteCleaner());
        clear.addActionListener(e -> clearCleanerForm());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear);
        JPanel west = new JPanel(new BorderLayout());
        west.add(fields, BorderLayout.CENTER);
        west.add(actions, BorderLayout.SOUTH);
        panel.add(searchPanel(cleanerSearch, () -> refreshCleaners(), () -> {cleanerSearch.setText(""); refreshCleaners();}), BorderLayout.NORTH);
        panel.add(new JScrollPane(cleanerTable), BorderLayout.CENTER);
        panel.add(west, BorderLayout.WEST);
        return panel;
    }

    private void configureTables() {
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cleanerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && supplierTable.getSelectedRow() >= 0) loadSupplierSelection();
        });
        cleanerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && cleanerTable.getSelectedRow() >= 0) loadCleanerSelection();
        });
    }

    private void addSupplier() {
        if (!validateSupplier()) return;
        try {
            Supplier supplier = supplierFromForm(0);
            int id = repository.addSupplier(supplier);
            JOptionPane.showMessageDialog(this, "Supplier added successfully. ID: " + id);
            clearSupplierForm(); refreshSuppliers();
        } catch (SQLException ex) { showDatabaseError("add supplier", ex); }
    }

    private void updateSupplier() {
        Integer id = selectedId(supplierId, "Select a supplier from the table before updating.");
        if (id == null || !validateSupplier()) return;
        try {
            if (repository.updateSupplier(supplierFromForm(id))) {
                JOptionPane.showMessageDialog(this, "Supplier updated successfully.");
                clearSupplierForm(); refreshSuppliers();
            }
        } catch (SQLException ex) { showDatabaseError("update supplier", ex); }
    }

    private void deleteSupplier() {
        Integer id = selectedId(supplierId, "Select a supplier from the table before deleting.");
        if (id == null || !confirmDelete("supplier")) return;
        try {
            if (repository.deleteSupplier(id)) {
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully.");
                clearSupplierForm(); refreshSuppliers();
            }
        } catch (SQLException ex) {
            showError("The supplier could not be deleted. It may still be linked to a material.", ex);
        }
    }

    private void addCleaner() {
        if (!validateCleaner()) return;
        try {
            int id = repository.addCleaner(cleanerFromForm(0));
            JOptionPane.showMessageDialog(this, "Cleaner added successfully. ID: " + id);
            clearCleanerForm(); refreshCleaners();
        } catch (SQLException ex) { showDatabaseError("add cleaner", ex); }
    }

    private void updateCleaner() {
        Integer id = selectedId(cleanerId, "Select a cleaner from the table before updating.");
        if (id == null || !validateCleaner()) return;
        try {
            if (repository.updateCleaner(cleanerFromForm(id))) {
                JOptionPane.showMessageDialog(this, "Cleaner updated successfully.");
                clearCleanerForm(); refreshCleaners();
            }
        } catch (SQLException ex) { showDatabaseError("update cleaner", ex); }
    }

    private void deleteCleaner() {
        Integer id = selectedId(cleanerId, "Select a cleaner from the table before deleting.");
        if (id == null || !confirmDelete("cleaner")) return;
        try {
            if (repository.deleteCleaner(id)) {
                JOptionPane.showMessageDialog(this, "Cleaner deleted successfully.");
                clearCleanerForm(); refreshCleaners();
            }
        } catch (SQLException ex) {
            showError("The cleaner could not be deleted. They may have stock issuance history.", ex);
        }
    }

    private void refreshSuppliers() {
        try {
            List<Supplier> rows = repository.searchSuppliers(supplierSearch.getText());
            supplierModel.setRowCount(0);
            for (Supplier s : rows) supplierModel.addRow(new Object[]{s.getSupplierId(), s.getSupplierName(),
                s.getContactPerson(), s.getPhone(), s.getEmail(), s.getAddress()});
        } catch (SQLException ex) { showDatabaseError("load suppliers", ex); }
    }

    private void refreshCleaners() {
        try {
            List<Cleaner> rows = repository.searchCleaners(cleanerSearch.getText());
            cleanerModel.setRowCount(0);
            for (Cleaner c : rows) cleanerModel.addRow(new Object[]{c.getId(), c.getFirstName(), c.getLastName(),
                c.getDepartment(), c.getPhone(), c.getEmail()});
        } catch (SQLException ex) { showDatabaseError("load cleaners", ex); }
    }

    private boolean validateSupplier() {
        if (supplierName.getText().trim().isEmpty()) return validation("Supplier name is required.", supplierName);
        if (!validPhone(supplierPhone.getText())) return validation("Enter a valid phone number.", supplierPhone);
        if (!validEmail(supplierEmail.getText())) return validation("Enter a valid email address.", supplierEmail);
        return true;
    }

    private boolean validateCleaner() {
        if (firstName.getText().trim().isEmpty()) return validation("First name is required.", firstName);
        if (lastName.getText().trim().isEmpty()) return validation("Last name is required.", lastName);
        if (!validPhone(cleanerPhone.getText())) return validation("Enter a valid phone number.", cleanerPhone);
        if (!validEmail(cleanerEmail.getText())) return validation("Enter a valid email address.", cleanerEmail);
        return true;
    }

    private boolean validPhone(String value) { return value.trim().isEmpty() || PHONE.matcher(value.trim()).matches(); }
    private boolean validEmail(String value) { return value.trim().isEmpty() || EMAIL.matcher(value.trim()).matches(); }
    private boolean validation(String message, JTextField field) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
        field.requestFocus(); return false;
    }

    private Supplier supplierFromForm(int id) {
        return new Supplier(id, supplierName.getText().trim(), contactPerson.getText().trim(),
                supplierPhone.getText().trim(), supplierEmail.getText().trim(), supplierAddress.getText().trim());
    }

    private Cleaner cleanerFromForm(int id) {
        return new Cleaner(id, firstName.getText().trim(), lastName.getText().trim(), cleanerEmail.getText().trim(),
                cleanerPhone.getText().trim(), department.getText().trim());
    }

    private void loadSupplierSelection() {
        int r = supplierTable.getSelectedRow();
        supplierId.setText(valueAt(supplierTable, r, 0)); supplierName.setText(valueAt(supplierTable, r, 1));
        contactPerson.setText(valueAt(supplierTable, r, 2)); supplierPhone.setText(valueAt(supplierTable, r, 3));
        supplierEmail.setText(valueAt(supplierTable, r, 4)); supplierAddress.setText(valueAt(supplierTable, r, 5));
    }

    private void loadCleanerSelection() {
        int r = cleanerTable.getSelectedRow();
        cleanerId.setText(valueAt(cleanerTable, r, 0)); firstName.setText(valueAt(cleanerTable, r, 1));
        lastName.setText(valueAt(cleanerTable, r, 2)); department.setText(valueAt(cleanerTable, r, 3));
        cleanerPhone.setText(valueAt(cleanerTable, r, 4)); cleanerEmail.setText(valueAt(cleanerTable, r, 5));
    }

    private void clearSupplierForm() {
        supplierId.setText(""); supplierName.setText(""); contactPerson.setText(""); supplierPhone.setText("");
        supplierEmail.setText(""); supplierAddress.setText(""); supplierTable.clearSelection();
    }

    private void clearCleanerForm() {
        cleanerId.setText(""); firstName.setText(""); lastName.setText(""); department.setText("");
        cleanerPhone.setText(""); cleanerEmail.setText(""); cleanerTable.clearSelection();
    }

    private static JPanel formPanel() { return new JPanel(new GridBagLayout()); }
    private static void addField(JPanel panel, int row, String label, JTextField field) {
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(4,4,4,4); c.gridy = row;
        c.gridx = 0; c.anchor = GridBagConstraints.WEST; panel.add(new JLabel(label), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; panel.add(field, c);
    }
    private static JPanel searchPanel(JTextField field, Runnable search, Runnable showAll) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search"); JButton allButton = new JButton("Show All");
        searchButton.addActionListener(e -> search.run()); allButton.addActionListener(e -> showAll.run());
        field.addActionListener(e -> search.run());
        panel.add(new JLabel("Search / Filter:")); panel.add(field); panel.add(searchButton); panel.add(allButton); return panel;
    }
    private static JTextField readonlyField() { JTextField f = new JTextField(20); f.setEditable(false); return f; }
    private static DefaultTableModel nonEditableModel(String... headings) {
        return new DefaultTableModel(headings, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
    }
    private static String valueAt(JTable table, int row, int column) {
        Object value = table.getValueAt(row, column); return value == null ? "" : value.toString();
    }
    private Integer selectedId(JTextField field, String message) {
        if (field.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, message, "Selection Required", JOptionPane.WARNING_MESSAGE); return null; }
        return Integer.valueOf(field.getText().trim());
    }
    private boolean confirmDelete(String type) {
        return JOptionPane.showConfirmDialog(this, "Delete the selected " + type + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    private void showDatabaseError(String action, SQLException ex) { showError("Could not " + action + ".", ex); }
    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + "\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override public void dispose() { database.disconnect(); super.dispose(); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StakeholderManagementFrame().setVisible(true));
    }
}
