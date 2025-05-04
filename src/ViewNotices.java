import javax.swing.*;
import javax.swing.border.EmptyBorder;
import net.proteanit.sql.DbUtils;
import java.awt.*;
import java.sql.*;

public class ViewNotices extends JFrame {

    public ViewNotices(int userId) {
        setTitle("📢 View Notices");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header panel with title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(33, 150, 243)); // Blue
        JLabel titleLabel = new JLabel("Notice Board");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Table setup
        JTable noticeTable = new JTable();
        noticeTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noticeTable.setRowHeight(28);
        noticeTable.setGridColor(new Color(200, 200, 200));
        noticeTable.setSelectionBackground(new Color(255, 224, 178)); // Light orange
        noticeTable.setSelectionForeground(Color.BLACK);
        noticeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        noticeTable.getTableHeader().setBackground(new Color(63, 81, 181)); // Indigo
        noticeTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(noticeTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Footer or padding panel
        JPanel footer = new JPanel();
        footer.setBackground(Color.LIGHT_GRAY);
        add(footer, BorderLayout.SOUTH);

        try {
            Connection con = DatabaseConnection.getConnection();

            // Determine user type
            String userType = getUserType(con, userId);

            if (userType == null) {
                JOptionPane.showMessageDialog(this, "User ID not found.");
                return;
            }

            // Query to fetch notices
            String query = """
                SELECT 
                    n.Notice_ID,
                    n.Message,
                    n.Posting_Date,
                    a.Admin_ID,
                    u.User_Name AS Admin_Name
                FROM Noticeboard n
                JOIN Admins a ON n.Admin_ID = a.Admin_ID
                JOIN User u ON a.User_ID = u.User_ID
                WHERE n.Target_Audience = ? 
                ORDER BY n.Posting_Date DESC;
            """;

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, userType); // 'Agents' or 'Customers'
            ResultSet rs = stmt.executeQuery();

            noticeTable.setModel(DbUtils.resultSetToTableModel(rs));
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage());
        }
    }

    private String getUserType(Connection con, int userId) throws SQLException {
        // Check if user is an agent
        String agentCheck = "SELECT * FROM Agents WHERE User_ID = ?";
        PreparedStatement stmt = con.prepareStatement(agentCheck);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return "Agents";
        }

        // Check if user is a customer
        String customerCheck = "SELECT * FROM Customers WHERE User_ID = ?";
        stmt = con.prepareStatement(customerCheck);
        stmt.setInt(1, userId);
        rs = stmt.executeQuery();

        if (rs.next()) {
            return "Customers";
        }

        return null;
    }
}