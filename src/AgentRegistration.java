import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AgentRegistration extends JFrame {
    private JTextField userIdField, emailField, adminIdField;
    private JButton submitButton;

    public AgentRegistration() {
        setTitle("Agent Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Agent Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38)); // Big Title
        titleLabel.setForeground(new Color(0, 51, 102)); // Nice blue color
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
        userIdLabel.setForeground(new Color(0, 51, 102));
        formPanel.add(userIdLabel, gbc);

        gbc.gridx = 1;
        userIdField = new JTextField(22);
        userIdField.setFont(new Font("Arial", Font.PLAIN, 24));
        formPanel.add(userIdField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 26));
        emailLabel.setForeground(new Color(0, 51, 102));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(22);
        emailField.setFont(new Font("Arial", Font.PLAIN, 24));
        formPanel.add(emailField, gbc);

        // Admin ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel adminIdLabel = new JLabel("Admin ID:");
        adminIdLabel.setFont(new Font("Arial", Font.BOLD, 26));
        adminIdLabel.setForeground(new Color(0, 51, 102));
        formPanel.add(adminIdLabel, gbc);

        gbc.gridx = 1;
        adminIdField = new JTextField(22);
        adminIdField.setFont(new Font("Arial", Font.PLAIN, 24));
        formPanel.add(adminIdField, gbc);

        // Register Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        submitButton = new JButton("Register Agent");
        submitButton.setFont(new Font("Arial", Font.BOLD, 26));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(0, 51, 102));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setFocusPainted(false);
        formPanel.add(submitButton, gbc);

        formWrapperPanel.add(formPanel);
        backgroundPanel.add(formWrapperPanel, BorderLayout.CENTER);

        setContentPane(backgroundPanel);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertAgentData();
            }
        });
    }

    private void insertAgentData() {
        String userId = userIdField.getText();
        String email = emailField.getText();
        String adminId = adminIdField.getText();

        if (userId.isEmpty() || email.isEmpty() || adminId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!isUserValid(conn, userId)) {
                JOptionPane.showMessageDialog(this, "Invalid User ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isAdminValid(conn, adminId)) {
                JOptionPane.showMessageDialog(this, "Invalid Admin ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO Agents (User_ID, Email, Admin_ID) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(userId));
                stmt.setString(2, email);
                stmt.setInt(3, Integer.parseInt(adminId));

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Agent Registered Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error while inserting data , check for Duplicate values.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private boolean isAdminValid(Connection conn, String adminId) throws SQLException {
        String query = "SELECT * FROM Admins WHERE Admin_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(adminId));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("C:\\Users\\PRABHU SAI\\Downloads\\insurance_management\\photos\\agent_reg.jpg").getImage();
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
            new AgentRegistration().setVisible(true);
        });
    }
}
