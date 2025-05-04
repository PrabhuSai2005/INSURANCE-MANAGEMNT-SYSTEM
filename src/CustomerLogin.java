import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.*;

public class CustomerLogin extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JLabel lblMessage;
    private JPanel panel;

    public CustomerLogin() {
        setTitle("Customer Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("C:/Users/PRABHU SAI/Downloads/insurance_management/photos/customer-login.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Panel for form elements
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        // Font settings for form
        Font labelFont = new Font("Arial", Font.PLAIN, 26);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 24);
        Font buttonFont = new Font("Arial", Font.BOLD, 22);
        Font messageFont = new Font("Arial", Font.PLAIN, 22);

        // Form elements
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(labelFont);
        tfUsername = new JTextField(15);
        tfUsername.setFont(textFieldFont);
        tfUsername.setBackground(new Color(255, 255, 255, 150));
        tfUsername.setBorder(new LineBorder(new Color(34, 139, 34), 1));

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(labelFont);
        pfPassword = new JPasswordField(15);
        pfPassword.setFont(textFieldFont);
        pfPassword.setBackground(new Color(255, 255, 255, 150));
        pfPassword.setBorder(new LineBorder(new Color(34, 139, 34), 1));

        btnLogin = new JButton("Login");
        btnLogin.setFont(buttonFont);
        btnLogin.setBackground(new Color(34, 139, 34));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34)));

        lblMessage = new JLabel("", JLabel.CENTER);
        lblMessage.setFont(messageFont);
        lblMessage.setForeground(Color.RED);

        // Layout settings for GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblUsername, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        tfUsername.setPreferredSize(new Dimension(250, 35));
        panel.add(tfUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        pfPassword.setPreferredSize(new Dimension(250, 35));
        panel.add(pfPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(lblMessage, gbc);

        backgroundPanel.add(panel, BorderLayout.WEST);
        add(backgroundPanel);

        // 🛠️ Add action listener for login button
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCustomerLogin();
            }
        });
    }

    private void handleCustomerLogin() {
        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Please enter both username and password.");
            return;
        }

        // Connect to database and validate user
        Connection con = DatabaseConnection.getConnection();
        try {
            // Check if the username exists in User table and password is correct
            String checkUserSQL = "SELECT User_ID FROM User WHERE User_Name = ? AND Password = ?";
            PreparedStatement stmt = con.prepareStatement(checkUserSQL);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt("User_ID");

                // Now check if this user is in Customers table
                String checkCustomerSQL = "SELECT Customer_ID FROM Customers WHERE User_ID = ?";
                PreparedStatement stmtCustomer = con.prepareStatement(checkCustomerSQL);
                stmtCustomer.setInt(1, userID);
                ResultSet customerRS = stmtCustomer.executeQuery();

                if (customerRS.next()) {
                    // Customer found, login successful
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome Customer.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new CustomerDashboard(userID).setVisible(true);  // Pass userID to the CustomerDashboard
                    dispose();  // Close the login screen
                } else {
                    lblMessage.setText("You are not a Customer.");
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
            new CustomerLogin().setVisible(true);
        });
    }
}
