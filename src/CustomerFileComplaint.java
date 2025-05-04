import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerFileComplaint extends JFrame {
    private JTextArea complaintDetailsArea;
    private JTextField customerIdField;  // To input Customer ID
    private JTextField agentIdField;     // To input Agent ID

    public CustomerFileComplaint() {
        setTitle("File Complaint");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header with gradient background
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(170, 15, 33), getWidth(), getHeight(), new Color(255, 88, 80));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(getWidth(), 80));
        JLabel titleLabel = new JLabel("File a Complaint");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titleLabel);
        add(header, BorderLayout.NORTH);

        // Form Panel with styled components
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(new Color(248, 248, 248));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Label and TextField for Customer ID
        JLabel customerIdLabel = new JLabel("Customer ID:");
        customerIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(customerIdLabel);

        customerIdField = new JTextField(20);
        customerIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerIdField.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204)));
        customerIdField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(customerIdField);

        // Label and TextField for Agent ID
        JLabel agentIdLabel = new JLabel("Agent ID:");
        agentIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(agentIdLabel);

        agentIdField = new JTextField(20);
        agentIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        agentIdField.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204)));
        agentIdField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(agentIdField);

        // Label and TextArea for Complaint Details
        JLabel complaintLabel = new JLabel("Complaint Reason:");
        complaintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(complaintLabel);

        complaintDetailsArea = new JTextArea(5, 20);
        complaintDetailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        complaintDetailsArea.setLineWrap(true);
        complaintDetailsArea.setWrapStyleWord(true);
        complaintDetailsArea.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204)));
        JScrollPane scrollPane = new JScrollPane(complaintDetailsArea);
        formPanel.add(scrollPane);

        // Add formPanel to the center
        add(formPanel, BorderLayout.CENTER);

        // Submit Button with hover effect
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));  // Center the button horizontally
        JButton submitButton = new JButton("Submit Complaint");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.setBackground(new Color(29, 161, 242));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        submitButton.setOpaque(true);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitComplaint();
            }
        });

        // Add mouse listener for hover effect
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(20, 138, 210));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(29, 161, 242));
            }
        });

        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);  // Position the button at the bottom
    }

    // Method to submit a complaint
    private void submitComplaint() {
        String complaintDetails = complaintDetailsArea.getText().trim();
        String customerIdText = customerIdField.getText().trim();
        String agentIdText = agentIdField.getText().trim();

        if (customerIdText.isEmpty() || agentIdText.isEmpty() || complaintDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide Customer ID, Agent ID, and Complaint details.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customerId = Integer.parseInt(customerIdText);
        int agentId = Integer.parseInt(agentIdText);

        // Insert complaint into database
        try (Connection con = DatabaseConnection.getConnection()) {
            String insertQuery = "INSERT INTO Complaints (Customer_ID, Agent_ID, Complaint_Details, Complaint_Date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(insertQuery);
            stmt.setInt(1, customerId);
            stmt.setInt(2, agentId);
            stmt.setString(3, complaintDetails);
            stmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Complaint filed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the form after successful complaint submission
            } else {
                JOptionPane.showMessageDialog(this, "Failed to file complaint. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method for testing the class
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerFileComplaint().setVisible(true));
    }
}
