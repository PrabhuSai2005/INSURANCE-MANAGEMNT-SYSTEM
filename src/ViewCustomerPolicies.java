import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewCustomerPolicies extends JFrame {
    private int loggedInUserId;
    private JTextField searchField;
    private JButton searchButton;
    private JTable policyTable;

    public ViewCustomerPolicies(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        setTitle("View Customer Policies");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));  // Light background color

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.setBackground(new Color(64, 158, 255));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel searchLabel = new JLabel("Enter Customer ID:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(Color.WHITE);
        searchPanel.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBackground(new Color(230, 230, 230));
        searchPanel.add(searchField);

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setFocusPainted(false);
        searchPanel.add(searchButton);

        searchButton.addActionListener(e -> searchCustomerPolicies());

        add(searchPanel, BorderLayout.NORTH);

        // Table Setup
        policyTable = new JTable();
        policyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        policyTable.setSelectionBackground(new Color(60, 179, 113));
        policyTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(policyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        JTableHeader header = policyTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 123, 255));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Policy ID");
        tableModel.addColumn("Policy Type");
        tableModel.addColumn("Start Date");
        tableModel.addColumn("Coverage Amount");
        tableModel.addColumn("Premium Amount");
        tableModel.addColumn("End Date");
        policyTable.setModel(tableModel);
    }

    private void searchCustomerPolicies() {
        String customerIdText = searchField.getText().trim();
        if (customerIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Customer ID.");
            return;
        }

        int customerId;
        try {
            customerId = Integer.parseInt(customerIdText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Customer ID must be a number.");
            return;
        }

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Policy ID");
        tableModel.addColumn("Policy Type");
        tableModel.addColumn("Start Date");
        tableModel.addColumn("Coverage Amount");
        tableModel.addColumn("Premium Amount");
        tableModel.addColumn("End Date");

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234")) {

            // Check if the customer belongs to the logged-in agent
            String checkQuery = "SELECT 1 FROM Customers " +
                    "WHERE Customer_ID = ? AND Agent_ID = (SELECT Agent_ID FROM Agents WHERE User_ID = ?)";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, customerId);
                checkStmt.setInt(2, loggedInUserId);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    fetchPolicies(customerId, conn, tableModel);
                } else {
                    JOptionPane.showMessageDialog(this, "Customer ID not found or does not belong to you.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while fetching customer policies.");
        }
    }

    private void fetchPolicies(int customerId, Connection conn, DefaultTableModel tableModel) throws SQLException {
        String policyQuery = "SELECT p.Policy_ID, pt.Type_Name, p.Start_Date, pf.Coverage_Amount, pf.Premium_Amount, pe.End_Date " +
                "FROM Policies p " +
                "JOIN PolicyTypes pt ON p.Policy_Type_ID = pt.Policy_Type_ID " +
                "JOIN PolicyFinancials pf ON p.Policy_ID = pf.Policy_ID " +
                "JOIN PolicyEndDates pe ON p.Policy_ID = pe.Policy_ID " +
                "WHERE p.Customer_ID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(policyQuery)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            boolean hasPolicies = false;
            while (rs.next()) {
                hasPolicies = true;
                Object[] row = {
                        rs.getInt("Policy_ID"),
                        rs.getString("Type_Name"),
                        rs.getDate("Start_Date"),
                        rs.getBigDecimal("Coverage_Amount"),
                        rs.getBigDecimal("Premium_Amount"),
                        rs.getDate("End_Date")
                };
                tableModel.addRow(row);
            }

            if (!hasPolicies) {
                JOptionPane.showMessageDialog(this, "No policies found for this customer.");
            }
        }

        policyTable.setModel(tableModel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewCustomerPolicies(1).setVisible(true)); // Replace 1 with actual logged-in User_ID
    }
}
