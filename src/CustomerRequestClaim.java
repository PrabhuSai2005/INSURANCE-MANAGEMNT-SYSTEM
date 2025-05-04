import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class CustomerRequestClaim extends JFrame {
    private JTextField claimAmountField;
    private JComboBox<String> reasonComboBox;
    private JTextArea claimDetailsArea;
    private JButton submitButton;
    private int customerUserId;

    public CustomerRequestClaim(int userId) {
        setTitle("Request Policy Claim");
        setSize(450, 350);
        setLocationRelativeTo(null);
        this.customerUserId = userId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set modern font and background color
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 14));

        // Layout and Background
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

        // Title Label
        JLabel titleLabel = new JLabel("Claim Request for User ID: " + userId, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(32, 54, 93));  // Dark Blue color for the title
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel (styling improvements)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Add space around the form

        // Claim Amount
        formPanel.add(new JLabel("Claim Amount:"));
        claimAmountField = new JTextField();
        claimAmountField.setBackground(new Color(245, 245, 245));  // Light grey background
        claimAmountField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));  // Subtle border
        claimAmountField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(claimAmountField);

        // Claim Reason
        formPanel.add(new JLabel("Claim Reason:"));
        reasonComboBox = new JComboBox<>();
        populateClaimReasons();  // Populate claim reasons dynamically from the database
        reasonComboBox.setBackground(new Color(245, 245, 245));
        reasonComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(reasonComboBox);

        // Claim Details
        formPanel.add(new JLabel("Claim Details:"));
        claimDetailsArea = new JTextArea();
        claimDetailsArea.setRows(3);
        claimDetailsArea.setLineWrap(true);
        claimDetailsArea.setWrapStyleWord(true);
        claimDetailsArea.setBackground(new Color(245, 245, 245));
        claimDetailsArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scrollPane = new JScrollPane(claimDetailsArea);
        formPanel.add(scrollPane);

        // Submit Button
        submitButton = new JButton("Submit Claim");
        submitButton.setBackground(new Color(32, 54, 93));  // Dark blue button
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setPreferredSize(new Dimension(200, 40));
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitClaimRequest(customerUserId);
            }
        });

        // Hover effect for Submit Button
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(51, 102, 153));  // Lighter Blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(32, 54, 93));  // Original Blue
            }
        });

        formPanel.add(submitButton);
        add(formPanel, BorderLayout.CENTER);

        // Custom border and shadow effect for the form
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    // Method to fetch claim reasons from the database and populate the JComboBox
    private void populateClaimReasons() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT Reason FROM ClaimReasons";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                ArrayList<String> claimReasons = new ArrayList<>();
                while (rs.next()) {
                    claimReasons.add(rs.getString("Reason"));
                }

                // Populate the JComboBox with claim reasons from the database
                for (String reason : claimReasons) {
                    reasonComboBox.addItem(reason);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching claim reasons from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to handle claim submission
    private void submitClaimRequest(int userId) {
        String claimAmountStr = claimAmountField.getText();
        String reason = (String) reasonComboBox.getSelectedItem();
        String claimDetails = claimDetailsArea.getText();

        // Validate the inputs
        if (claimAmountStr.isEmpty() || claimDetails.isEmpty() || reason == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double claimAmount = 0;
        try {
            claimAmount = Double.parseDouble(claimAmountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid claim amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int reasonId = getClaimReasonId(reason);
        if (reasonId == -1) {
            JOptionPane.showMessageDialog(this, "Invalid claim reason.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Database operations
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Fetch Customer ID using User ID
            int customerId = getCustomerIdByUserId(userId);

            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch Policy ID for the customer (assuming one-to-one relationship)
            int policyId = getPolicyIdByUserId(userId);
            if (policyId == -1) {
                JOptionPane.showMessageDialog(this, "Policy not found for this user.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert into PendingClaims
            String insertQuery = "INSERT INTO PendingClaims (Customer_ID, Policy_ID, Claim_Amount, Claim_Date, Reason_ID, Status) " +
                    "VALUES (?, ?, ?, CURDATE(), ?, 'Pending')";

            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setInt(1, customerId); // Set Customer_ID
                stmt.setInt(2, policyId); // Set Policy_ID
                stmt.setDouble(3, claimAmount); // Set Claim_Amount
                stmt.setInt(4, reasonId); // Set Reason_ID

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Claim request submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit claim request.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fetch Claim Reason ID from the database
    private int getClaimReasonId(String reason) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Reason_ID FROM ClaimReasons WHERE Reason = ?")) {
            stmt.setString(1, reason);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Reason_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Fetch Customer_ID from the database using User_ID
    private int getCustomerIdByUserId(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Customer_ID FROM Customers WHERE User_ID = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Customer_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Fetch Policy_ID using User_ID
    private int getPolicyIdByUserId(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Policy_ID FROM Policies WHERE Customer_ID = (SELECT Customer_ID FROM Customers WHERE User_ID = ?)")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Policy_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
