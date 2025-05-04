import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class CustomerViewPolicies extends JFrame {

    public CustomerViewPolicies(int userId) {
        setTitle("My Policies");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("MY POLICY DETAILS", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(0, 102, 204));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setPreferredSize(new Dimension(900, 60));
        add(headerLabel, BorderLayout.NORTH);

        // Table Columns
        String[] columns = {"Policy Type", "Start Date", "Coverage", "Premium", "Amount Left", "End Date", "Renewal Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Load Data
        loadPolicyDetails(userId, model);
    }

    private void loadPolicyDetails(int userId, DefaultTableModel model) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/InsuranceDB2", "root", "1234")) {

            // Step 1: Fetch customer ID from user ID
            String getCustomerIdQuery = "SELECT Customer_ID FROM Customers WHERE User_ID = ?";
            PreparedStatement ps1 = conn.prepareStatement(getCustomerIdQuery);
            ps1.setInt(1, userId);
            ResultSet rs1 = ps1.executeQuery();

            if (!rs1.next()) {
                JOptionPane.showMessageDialog(this, "Customer not found for the given User ID", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int customerId = rs1.getInt("Customer_ID");

            // Step 2: Fetch policies using the correct customerId
            String query = """
                SELECT pt.Type_Name, p.Start_Date, pf.Coverage_Amount, pf.Premium_Amount, pf.Amount_Left,
                       ped.End_Date, pr.Renewal_Date
                FROM Policies p
                JOIN PolicyTypes pt ON p.Policy_Type_ID = pt.Policy_Type_ID
                LEFT JOIN PolicyFinancials pf ON p.Policy_ID = pf.Policy_ID
                LEFT JOIN PolicyEndDates ped ON p.Policy_ID = ped.Policy_ID
                LEFT JOIN PolicyRenewal pr ON p.Policy_ID = pr.Policy_ID
                WHERE p.Customer_ID = ?
                ORDER BY p.Start_Date DESC
                """;

            PreparedStatement ps2 = conn.prepareStatement(query);
            ps2.setInt(1, customerId);
            ResultSet rs2 = ps2.executeQuery();

            boolean found = false;
            while (rs2.next()) {
                found = true;
                String type = rs2.getString("Type_Name");
                Date start = rs2.getDate("Start_Date");
                double coverage = rs2.getDouble("Coverage_Amount");
                double premium = rs2.getDouble("Premium_Amount");
                double left = rs2.getDouble("Amount_Left");
                Date end = rs2.getDate("End_Date");
                Date renewal = rs2.getDate("Renewal_Date");

                model.addRow(new Object[]{
                        type,
                        start,
                        coverage,
                        premium,
                        left,
                        (end != null ? end.toString() : "Not Ended"),
                        (renewal != null ? renewal.toString() : "No Renewal")
                });
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No policies found for this customer.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading policies", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Test the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerViewPolicies(3).setVisible(true)); // Pass User_ID here
    }
}
