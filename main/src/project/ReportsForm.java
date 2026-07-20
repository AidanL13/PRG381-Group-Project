package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Displays the 4 required reports: Inventory, Low-Stock, Issuance History,
 * and Material Usage. Pick a report from the dropdown and click Generate.
 *
 * @author Baatile
 */
public class ReportsForm extends JFrame {

    private final DBConnection db;
    private JComboBox<String> reportSelector;
    private JTable table;

    public ReportsForm() {
        db = new DBConnection();
        buildUI();
    }

    private void buildUI() {
        setTitle("Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        reportSelector = new JComboBox<>(new String[]{
            "Inventory Report",
            "Low-Stock Report",
            "Issuance History",
            "Material Usage Report"
        });
        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(e -> generateReport());

        topPanel.add(new JLabel("Select report:"));
        topPanel.add(reportSelector);
        topPanel.add(generateButton);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load the first report automatically on open
        generateReport();
    }

    private void generateReport() {
        String selected = (String) reportSelector.getSelectedItem();
        DefaultTableModel model;

        switch (selected) {
            case "Inventory Report":
                model = new DefaultTableModel(
                    new Object[]{"Material ID", "Material Name", "Available Qty", "Reorder Level"}, 0);
                fillModel(model, db.getInventoryReport());
                break;

            case "Low-Stock Report":
                model = new DefaultTableModel(
                    new Object[]{"Material ID", "Material Name", "Available Qty", "Reorder Level"}, 0);
                fillModel(model, db.getLowStockReport());
                break;

            case "Issuance History":
                model = new DefaultTableModel(
                    new Object[]{"Issuance ID", "Material", "Cleaner", "Qty Issued", "Date"}, 0);
                fillModel(model, db.getIssuanceHistory());
                break;

            case "Material Usage Report":
                model = new DefaultTableModel(
                    new Object[]{"Material ID", "Material Name", "Total Issued"}, 0);
                fillModel(model, db.getMaterialUsageReport());
                break;

            default:
                model = new DefaultTableModel();
        }

        table.setModel(model);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No data found for this report.",
                "Empty Report",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void fillModel(DefaultTableModel model, List<Object[]> rows) {
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new ReportsForm().setVisible(true));
    }
}
