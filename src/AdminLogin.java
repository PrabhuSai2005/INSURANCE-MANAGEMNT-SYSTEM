import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.*;

public class AdminLogin extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JLabel lblMessage;
    private JPanel panel;

    public AdminLogin() {
        setTitle("Admin Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in FULL SCREEN mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel with BorderLayout
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("C:/Users/PRABHU SAI/Downloads/insurance_management/photos/admin-login.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Create the login panel (this will hold the form components)
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false); // Make this panel transparent to show background image

        // Font settings
        Font labelFont = new Font("Arial", Font.PLAIN, 26);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 24);
        Font buttonFont = new Font("Arial", Font.BOLD, 22);
        Font messageFont = new Font("Arial", Font.PLAIN, 22);

        // Username Label and TextField
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(labelFont);
        tfUsername = new JTextField(15);
        tfUsername.setFont(textFieldFont);
        tfUsername.setBackground(new Color(255, 255, 255, 150));
        tfUsername.setBorder(new LineBorder(new Color(34, 139, 34), 1));

        // Password Label and TextField
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(labelFont);
        pfPassword = new JPasswordField(15);
        pfPassword.setFont(textFieldFont);
        pfPassword.setBackground(new Color(255, 255, 255, 150));
        pfPassword.setBorder(new LineBorder(new Color(34, 139, 34), 1));

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setFont(buttonFont);
        btnLogin.setBackground(new Color(34, 139, 34));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34)));

        // Message Label
        lblMessage = new JLabel("", JLabel.CENTER);
        lblMessage.setFont(messageFont);
        lblMessage.setForeground(Color.RED);

        // GridBagConstraints for aligning components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Username field placement
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblUsername, gbc);

        // Username text field placement
        gbc.gridx = 1;
        gbc.gridy = 0;
        tfUsername.setPreferredSize(new Dimension(250, 35));
        panel.add(tfUsername, gbc);

        // Password field placement
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPassword, gbc);

        // Password text field placement
        gbc.gridx = 1;
        gbc.gridy = 1;
        pfPassword.setPreferredSize(new Dimension(250, 35));
        panel.add(pfPassword, gbc);

        // Login button placement
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        // Message label placement
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblMessage, gbc);

        // Add the login panel on top of the background image
        backgroundPanel.add(panel, BorderLayout.WEST);

        // Add background panel to the frame
        add(backgroundPanel);

        // Add button action listener
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAdminLogin();
            }
        });
    }

    private void handleAdminLogin() {
        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Please enter both username and password.");
            return;
        }

        Connection con = DatabaseConnection.getConnection();
        try {
            // Check if the username exists in the User table and validate the password
            String checkUserSQL = "SELECT User_ID FROM User WHERE User_Name = ? AND Password = ?";
            PreparedStatement stmt = con.prepareStatement(checkUserSQL);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt("User_ID");

                // Now check if this user is in the Admins table
                String checkAdminSQL = "SELECT Admin_ID FROM Admins WHERE User_ID = ?";
                PreparedStatement stmtAdmin = con.prepareStatement(checkAdminSQL);
                stmtAdmin.setInt(1, userID);
                ResultSet adminRS = stmtAdmin.executeQuery();

                if (adminRS.next()) {
                    // Admin found, login successful
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome Admin.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Proceed to the Admin Dashboard, passing the username to the dashboard
                    new AdminDashboard(username).setVisible(true);
                    dispose();
                } else {
                    lblMessage.setText("You are not an Admin.");
                }

            } else {
                lblMessage.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblMessage.setText("Database error, try again.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminLogin().setVisible(true);
        });
    }
}
