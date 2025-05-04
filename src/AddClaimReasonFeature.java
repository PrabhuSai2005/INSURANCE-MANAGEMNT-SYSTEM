import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddClaimReasonFeature extends JFrame {
    private JTextField reasonField;
    private Connection con;

    public AddClaimReasonFeature(Connection con) {
        this.con = con;

        setTitle("Add Claim Reason");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245)); // light background
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("➕ Add New Claim Reason", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(reasonLabel, gbc);

        reasonField = new JTextField();
        reasonField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        reasonField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true)); // rounded feel
        gbc.gridx = 1;
        add(reasonField, gbc);

        JButton addButton = new JButton("➕ Add Reason");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add hover effect
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(0, 123, 63));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(0, 153, 76));
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(addButton, gbc);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addClaimReason();
            }
        });
    }

    private void addClaimReason() {
        String reason = reasonField.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reason.", "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Check for duplicate (case-insensitive)
            String checkSql = "SELECT COUNT(*) FROM ClaimReasons WHERE LOWER(Reason) = LOWER(?)";
            PreparedStatement checkStmt = con.prepareStatement(checkSql);
            checkStmt.setString(1, reason);
            ResultSet checkResult = checkStmt.executeQuery();
            if (checkResult.next() && checkResult.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "This claim reason already exists. Please enter a new one.", "Duplicate Reason", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get next Reason_ID
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(Reason_ID) FROM ClaimReasons");
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }

            // Insert new reason
            String sql = "INSERT INTO ClaimReasons (Reason_ID, Reason) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, nextId);
            pstmt.setString(2, reason);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "🎉 Claim Reason added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add claim reason.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
