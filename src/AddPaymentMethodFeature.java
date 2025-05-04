import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddPaymentMethodFeature extends JFrame {
    private JTextField paymentMethodField;
    private Connection con;

    public AddPaymentMethodFeature(Connection con) {
        this.con = con;

        setTitle("Add Payment Method");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("💳 Add New Payment Method", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        JLabel paymentMethodLabel = new JLabel("Payment Method Name:");
        paymentMethodLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(paymentMethodLabel, gbc);

        paymentMethodField = new JTextField();
        paymentMethodField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        paymentMethodField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
        gbc.gridx = 1;
        add(paymentMethodField, gbc);

        JButton addButton = new JButton("💳 Add Payment Method");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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
                addPaymentMethod();
            }
        });
    }

    private void addPaymentMethod() {
        String paymentMethodName = paymentMethodField.getText().trim();

        if (paymentMethodName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a payment method name.", "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Check for duplicate (case-insensitive)
            String checkSql = "SELECT COUNT(*) FROM Payment_Methods WHERE LOWER(Payment_Method_Name) = LOWER(?)";
            PreparedStatement checkStmt = con.prepareStatement(checkSql);
            checkStmt.setString(1, paymentMethodName);
            ResultSet checkResult = checkStmt.executeQuery();
            if (checkResult.next() && checkResult.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "This payment method already exists. Please enter a new one.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get the next ID
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(Payment_Method_ID) FROM Payment_Methods");
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }

            // Insert new payment method
            String sql = "INSERT INTO Payment_Methods (Payment_Method_ID, Payment_Method_Name) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, nextId);
            pstmt.setString(2, paymentMethodName);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "🎉 Payment Method added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add payment method.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
