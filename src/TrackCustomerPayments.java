import javax.swing.*;
import javax.swing.table.JTableHeader; // ✅ This one was missing
import java.awt.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;


public class TrackCustomerPayments extends JFrame {

    public TrackCustomerPayments(int agentUserId) {
        setTitle("Track Customer Payments");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(97, 171, 227));

        // Title Label
        JLabel titleLabel = new JLabel("Customer Payments", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(2, 18, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        JTable paymentTable = new JTable();
        styleTable(paymentTable);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadCustomerPayments(agentUserId, paymentTable);
    }

    private void loadCustomerPayments(int agentUserId, JTable table) {
        String url = "jdbc:mysql://127.0.0.1:3306/InsuranceDB2";
        String username = "root";
        String password = "1234";

        try (Connection con = DriverManager.getConnection(url, username, password)) {
            String query = """
                SELECT 
                    p.Payment_ID, 
                    c.Customer_ID, 
                    u.User_Name AS Customer_Name, 
                    p.Payment_Date, 
                    p.Payment_Amount, 
                    pm.Payment_Method_Name
                FROM Agents a
                JOIN Customers c ON a.Agent_ID = c.Agent_ID
                JOIN Payment p ON c.Customer_ID = p.Customer_ID
                JOIN Payment_Methods pm ON p.Payment_Method_ID = pm.Payment_Method_ID
                JOIN User u ON c.User_ID = u.User_ID
                WHERE a.User_ID = ?
                ORDER BY p.Payment_Date DESC;
            """;

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, agentUserId);
            ResultSet rs = stmt.executeQuery();

            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading payments:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(103, 121, 221)); // Indigo
        table.setSelectionForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(97, 171, 227)); // Blue
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }

    // Test run
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TrackCustomerPayments(201).setVisible(true));
    }
}
