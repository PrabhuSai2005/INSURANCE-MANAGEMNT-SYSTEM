import javax.swing.*;
import javax.swing.border.EmptyBorder;
import net.proteanit.sql.DbUtils;
import java.awt.*;
import java.sql.*;

public class CustomerViewNotices extends JFrame {
    public CustomerViewNotices(int userId) {
        setTitle("📢 Customer Notice Board");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(3, 155, 229)); // Blue
        JLabel titleLabel = new JLabel("Notices for Customers");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(titleLabel);
        add(header, BorderLayout.NORTH);

        // Table setup
        JTable noticeTable = new JTable();
        noticeTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noticeTable.setRowHeight(26);
        noticeTable.setSelectionBackground(new Color(200, 230, 201)); // Light green
        noticeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        noticeTable.getTableHeader().setBackground(new Color(56, 142, 60)); // Dark green
        noticeTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(noticeTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Load notices intended for "Customers"
        try {
            Connection con = DatabaseConnection.getConnection();

            // Query to fetch only customer-targeted notices without Target_Audience column
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
                WHERE n.Target_Audience = 'Customers'
                ORDER BY n.Posting_Date DESC;
            """;

            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            noticeTable.setModel(DbUtils.resultSetToTableModel(rs));

            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage());
        }
    }
}
