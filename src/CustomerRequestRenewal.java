import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import java.util.ArrayList;

public class CustomerRequestRenewal extends JFrame {
    private int customerUserId;
    private JComboBox<String> policyComboBox;
    private JTextArea policyDetailsArea;
    private JButton btnRenew;

    public CustomerRequestRenewal(int userId) {
        this.customerUserId = userId;

        setTitle("✨ Customer Policy Renewal Portal ✨");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Main panel with padding and color
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 250)); // Soft light background

        JLabel heading = new JLabel("Request Policy Renewal");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setForeground(new Color(44, 62, 80));
        mainPanel.add(heading);
        mainPanel.add(Box.createVerticalStrut(20));

        int customerId = getCustomerIdFromUserId(customerUserId);
        ArrayList<String> policies = getPoliciesForCustomer(customerId);

        // ComboBox for policy selection
        JLabel lblSelect = new JLabel("Select Policy:");
        lblSelect.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        policyComboBox = new JComboBox<>(policies.toArray(new String[0]));
        policyComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        policyComboBox.setBackground(Color.WHITE);
        policyComboBox.setToolTipText("Choose a policy to view and renew");
        mainPanel.add(lblSelect);
        mainPanel.add(policyComboBox);
        mainPanel.add(Box.createVerticalStrut(15));

        // Text area for policy details
        JLabel lblDetails = new JLabel("Policy Details:");
        lblDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        policyDetailsArea = new JTextArea(6, 30);
        policyDetailsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        policyDetailsArea.setEditable(false);
        policyDetailsArea.setLineWrap(true);
        policyDetailsArea.setWrapStyleWord(true);
        policyDetailsArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollPane = new JScrollPane(policyDetailsArea);
        mainPanel.add(lblDetails);
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(20));

        // Stylish Renew Button
        btnRenew = new JButton("🚀 Request Renewal");
        btnRenew.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRenew.setBackground(new Color(52, 152, 219));
        btnRenew.setForeground(Color.WHITE);
        btnRenew.setFocusPainted(false);
        btnRenew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRenew.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRenew.setBorder(new EmptyBorder(10, 20, 10, 20));

        mainPanel.add(btnRenew);
        add(mainPanel);

        btnRenew.addActionListener(e -> {
            String selectedPolicy = (String) policyComboBox.getSelectedItem();
            int policyId = Integer.parseInt(selectedPolicy.split(":")[0].trim());
            requestRenewal(policyId);
        });

        policyComboBox.addActionListener(e -> updatePolicyDetails());

        updatePolicyDetails(); // Initial display
        setVisible(true);
    }

    private int getCustomerIdFromUserId(int userId) {
        int customerId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234");
             PreparedStatement stmt = conn.prepareStatement("SELECT Customer_ID FROM Customers WHERE User_ID = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                customerId = rs.getInt("Customer_ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return customerId;
    }

    private ArrayList<String> getPoliciesForCustomer(int customerId) {
        ArrayList<String> policies = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234");
             PreparedStatement stmt = conn.prepareStatement("SELECT Policy_ID, Start_Date FROM Policies WHERE Customer_ID = ?")) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int policyId = rs.getInt("Policy_ID");
                Date startDate = rs.getDate("Start_Date");
                policies.add(policyId + ": " + startDate.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return policies;
    }

    private void updatePolicyDetails() {
        String selectedPolicy = (String) policyComboBox.getSelectedItem();
        if (selectedPolicy == null) return;
        int policyId = Integer.parseInt(selectedPolicy.split(":")[0].trim());
        String policyDetails = getPolicyDetails(policyId);
        policyDetailsArea.setText(policyDetails);
    }

    private String getPolicyDetails(int policyId) {
        StringBuilder details = new StringBuilder();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.Policy_ID, pt.Type_Name, pf.Coverage_Amount, pf.Premium_Amount, pe.End_Date " +
                             "FROM Policies p " +
                             "JOIN PolicyTypes pt ON p.Policy_Type_ID = pt.Policy_Type_ID " +
                             "JOIN PolicyFinancials pf ON p.Policy_ID = pf.Policy_ID " +
                             "JOIN PolicyEndDates pe ON p.Policy_ID = pe.Policy_ID " +
                             "WHERE p.Policy_ID = ?")) {
            stmt.setInt(1, policyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                details.append("Policy ID       : ").append(rs.getInt("Policy_ID")).append("\n");
                details.append("Policy Type     : ").append(rs.getString("Type_Name")).append("\n");
                details.append("Coverage Amount : ₹").append(rs.getBigDecimal("Coverage_Amount")).append("\n");
                details.append("Premium Amount  : ₹").append(rs.getBigDecimal("Premium_Amount")).append("\n");
                details.append("End Date        : ").append(rs.getDate("End_Date")).append("\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return details.toString();
    }

    private void requestRenewal(int policyId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO RenewalRequests (Policy_ID, Customer_ID, Agent_ID, Status) " +
                             "SELECT ?, p.Customer_ID, a.Agent_ID, 'Pending' " +
                             "FROM Policies p " +
                             "JOIN Customers c ON p.Customer_ID = c.Customer_ID " +
                             "JOIN Agents a ON c.Agent_ID = a.Agent_ID " +
                             "WHERE p.Policy_ID = ?")) {
            stmt.setInt(1, policyId);
            stmt.setInt(2, policyId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "✅ Renewal Request Submitted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to submit request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CustomerRequestRenewal(301);
    }
}
