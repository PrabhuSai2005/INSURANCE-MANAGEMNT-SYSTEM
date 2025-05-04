import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminRegistration extends JFrame {
    private JTextField userIdField, roleIdField, departmentIdField;
    private JButton submitButton;

    public AdminRegistration() {
        setTitle("Admin Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Admin Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38)); // Even bigger title
        titleLabel.setForeground(new Color(149, 80, 7));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Big top margin
        JPanel formWrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 120)); // 120 top margin
        formWrapperPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Bigger gap
        gbc.anchor = GridBagConstraints.WEST;

        // User ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setFont(new Font("Arial", Font.BOLD, 26)); // Even bigger label font
        userIdLabel.setForeground(new Color(149, 80, 7));
        formPanel.add(userIdLabel, gbc);

        gbc.gridx = 1;
        userIdField = new JTextField(22);
        userIdField.setFont(new Font("Arial", Font.PLAIN, 24)); // Bigger input font
        formPanel.add(userIdField, gbc);

        // Role ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel roleIdLabel = new JLabel("Role ID:");
        roleIdLabel.setFont(new Font("Arial", Font.BOLD, 26));
        roleIdLabel.setForeground(new Color(149, 80, 7));
        formPanel.add(roleIdLabel, gbc);

        gbc.gridx = 1;
        roleIdField = new JTextField(22);
        roleIdField.setFont(new Font("Arial", Font.PLAIN, 24));
        formPanel.add(roleIdField, gbc);

        // Department ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel departmentIdLabel = new JLabel("Department ID:");
        departmentIdLabel.setFont(new Font("Arial", Font.BOLD, 26));
        departmentIdLabel.setForeground(new Color(149, 80, 7));
        formPanel.add(departmentIdLabel, gbc);

        gbc.gridx = 1;
        departmentIdField = new JTextField(22);
        departmentIdField.setFont(new Font("Arial", Font.PLAIN, 24));
        formPanel.add(departmentIdField, gbc);

        // Submit Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        submitButton = new JButton("Register Admin");
        submitButton.setFont(new Font("Arial", Font.BOLD, 26)); // Bigger button font
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(149, 80, 7));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setFocusPainted(false);
        formPanel.add(submitButton, gbc);

        formWrapperPanel.add(formPanel);
        backgroundPanel.add(formWrapperPanel, BorderLayout.CENTER);

        setContentPane(backgroundPanel);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertAdminData();
            }
        });
    }

    private void insertAdminData() {
        String userId = userIdField.getText();
        String roleId = roleIdField.getText();
        String departmentId = departmentIdField.getText();

        if (userId.isEmpty() || roleId.isEmpty() || departmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!isUserValid(conn, userId)) {
                JOptionPane.showMessageDialog(this, "Invalid User ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isRoleValid(conn, roleId)) {
                JOptionPane.showMessageDialog(this, "Invalid Role ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isDepartmentValid(conn, departmentId)) {
                JOptionPane.showMessageDialog(this, "Invalid Department ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO Admins (User_ID, Role_ID, Department_ID) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(userId));
                stmt.setInt(2, Integer.parseInt(roleId));
                stmt.setInt(3, Integer.parseInt(departmentId));

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Admin Registered Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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

    private boolean isRoleValid(Connection conn, String roleId) throws SQLException {
        String query = "SELECT * FROM Roles WHERE Role_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(roleId));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private boolean isDepartmentValid(Connection conn, String departmentId) throws SQLException {
        String query = "SELECT * FROM Departments WHERE Department_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(departmentId));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("C:\\Users\\PRABHU SAI\\Downloads\\insurance_management\\photos\\admin_reg.jpg").getImage();
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
            new AdminRegistration().setVisible(true);
        });
    }
}
