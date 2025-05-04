// Updated ManageNoticeboardFeature.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManageNoticeboardFeature extends JFrame {
    private JTextArea messageField;
    private JTextField adminIdField;
    private JComboBox<String> audienceDropdown;
    private Connection con;

    public ManageNoticeboardFeature(Connection con) {
        this.con = con;

        setTitle("Manage Noticeboard");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Post a New Notice", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(34, 45, 65));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel adminIdLabel = new JLabel("Enter Admin ID:");
        adminIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(adminIdLabel, gbc);

        gbc.gridx = 1;
        adminIdField = new JTextField(20);
        adminIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(8, 8, 8, 8)
        ));
        add(adminIdField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel targetLabel = new JLabel("Target Audience:");
        targetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(targetLabel, gbc);

        gbc.gridx = 1;
        audienceDropdown = new JComboBox<>(new String[]{"Agents", "Customers"});
        add(audienceDropdown, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel messageLabel = new JLabel("Notice Message:");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(messageLabel, gbc);

        gbc.gridx = 1;
        messageField = new JTextArea(5, 20);
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageField);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(8, 8, 8, 8)
        ));
        add(scrollPane, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton postButton = new JButton("Post Notice");
        postButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        postButton.setBackground(new Color(66, 133, 244));
        postButton.setForeground(Color.WHITE);
        postButton.setFocusPainted(false);
        postButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        postButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(postButton, gbc);

        postButton.addActionListener(e -> postNotice());
    }

    private void postNotice() {
        String adminIdText = adminIdField.getText().trim();
        if (adminIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Admin ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int adminID;
        try {
            adminID = Integer.parseInt(adminIdText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Admin ID must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a notice message.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String target = audienceDropdown.getSelectedItem().toString();

        try {
            PreparedStatement checkStmt = con.prepareStatement("SELECT COUNT(*) FROM Admins WHERE Admin_ID = ?");
            checkStmt.setInt(1, adminID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Admin ID does not exist in the database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String sql = "INSERT INTO Noticeboard (Admin_ID, Message, Posting_Date, Target_Audience) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, adminID);
            pstmt.setString(2, message);
            pstmt.setString(3, date);
            pstmt.setString(4, target);

            int inserted = pstmt.executeUpdate();
            if (inserted > 0) {
                JOptionPane.showMessageDialog(this, "Notice posted successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to post the notice.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Connection con = DatabaseConnection.getConnection();
        if (con != null) {
            new ManageNoticeboardFeature(con).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to connect to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
