import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerRegistration extends JFrame {
    private JTextField userIdField, regDateField, agentIdField;
    private JButton submitButton;

    public CustomerRegistration() {
        setTitle("Customer Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Customer Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38)); // Big Title
        titleLabel.setForeground(new Color(81, 28, 5)); // Green color for customer
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Big top margin
        JPanel formWrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 120)); // 120 margin
        formWrapperPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // User ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setFont(new Font("Arial", Font.BOLD, 26));
        userIdLabel.setForeground(new Color(81, 28, 5));
        formPanel.add(userIdLabel, gbc);

        gbc.gridx = 1;
        userIdField = new JTextField(22);
        userIdField.setFont(new Font("Arial", Font.PLAIN, 24));
        formPanel.add(userIdField, gbc);

        // Registration Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel regDateLabel = new JLabel("Registration Date:");
        regDateLabel.setFont(new Font("Arial", Font.BOLD, 26));
        regDateLabel.setForeground(new Color(81, 28, 5));
        formPanel.add(regDateLabel, gbc);

        gbc.gridx = 1;
        regDateField = new JTextField(22);
        regDateField.setFont(new Font("Arial", Font.PLAIN, 24));
        formPanel.add(regDateField, gbc);

        // Agent ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel agentIdLabel = new JLabel("Agent ID:");
        agentIdLabel.setFont(new Font("Arial", Font.BOLD, 26));
        agentIdLabel.setForeground(new Color(81, 28, 5));
        formPanel.add(agentIdLabel, gbc);

        gbc.gridx = 1;
        agentIdField = new JTextField(22);
        agentIdField.setFont(new Font("Arial", Font.PLAIN, 24));
        formPanel.add(agentIdField, gbc);

        // Register Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        submitButton = new JButton("Register Customer");
        submitButton.setFont(new Font("Arial", Font.BOLD, 26));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(81, 28, 5));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setFocusPainted(false);
        formPanel.add(submitButton, gbc);

        formWrapperPanel.add(formPanel);
        backgroundPanel.add(formWrapperPanel, BorderLayout.CENTER);

        setContentPane(backgroundPanel);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertCustomerData();
            }
        });
    }

    private void insertCustomerData() {
        String userId = userIdField.getText();
        String regDate = regDateField.getText();
        String agentId = agentIdField.getText();

        if (userId.isEmpty() || regDate.isEmpty() || agentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!isUserValid(conn, userId)) {
                JOptionPane.showMessageDialog(this, "Invalid User ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isAgentValid(conn, agentId)) {
                JOptionPane.showMessageDialog(this, "Invalid Agent ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO Customers (User_ID, Registration_Date, Agent_ID) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(userId));
                stmt.setString(2, regDate);
                stmt.setInt(3, Integer.parseInt(agentId));

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Customer Registered Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error while inserting data , check for duplicate values.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean isUserValid(Connection conn, String userId) throws SQLException {
        String query = "SELECT * FROM User WHERE User_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private boolean isAgentValid(Connection conn, String agentId) throws SQLException {
        String query = "SELECT * FROM Agents WHERE Agent_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(agentId));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("C:\\Users\\PRABHU SAI\\Downloads\\insurance_management\\photos\\customer_reg.png").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomerRegistration().setVisible(true);
        });
    }
}
