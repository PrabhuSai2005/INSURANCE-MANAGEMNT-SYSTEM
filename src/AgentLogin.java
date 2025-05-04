import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.*;

public class AgentLogin extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JLabel lblMessage;
    private JPanel panel;

    public AgentLogin() {
        setTitle("Agent Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("C:/Users/PRABHU SAI/Downloads/insurance_management/photos/agent_login.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        Font labelFont = new Font("Arial", Font.PLAIN, 26);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 24);
        Font buttonFont = new Font("Arial", Font.BOLD, 22);
        Font messageFont = new Font("Arial", Font.PLAIN, 22);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(labelFont);
        tfUsername = new JTextField(15);
        tfUsername.setFont(textFieldFont);
        tfUsername.setBackground(new Color(255, 255, 255, 150));
        tfUsername.setBorder(new LineBorder(new Color(145, 36, 218), 1));

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(labelFont);
        pfPassword = new JPasswordField(15);
        pfPassword.setFont(textFieldFont);
        pfPassword.setBackground(new Color(255, 255, 255, 150));
        pfPassword.setBorder(new LineBorder(new Color(145, 36, 218), 1));

        btnLogin = new JButton("Login");
        btnLogin.setFont(buttonFont);
        btnLogin.setBackground(new Color(145, 36, 218));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(145, 36, 218)));

        lblMessage = new JLabel("", JLabel.CENTER);
        lblMessage.setFont(messageFont);
        lblMessage.setForeground(Color.RED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblUsername, gbc);

        gbc.gridx = 1;
        tfUsername.setPreferredSize(new Dimension(250, 35));
        panel.add(tfUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPassword, gbc);

        gbc.gridx = 1;
        pfPassword.setPreferredSize(new Dimension(250, 35));
        panel.add(pfPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblMessage, gbc);

        backgroundPanel.add(panel, BorderLayout.WEST);
        add(backgroundPanel);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAgentLogin();
            }
        });
    }

    private void handleAgentLogin() {
        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Please enter both username and password.");
            return;
        }

        Connection con = DatabaseConnection.getConnection();
        try {
            String checkUserSQL = "SELECT User_ID FROM User WHERE User_Name = ? AND Password = ?";
            PreparedStatement stmt = con.prepareStatement(checkUserSQL);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt("User_ID");

                String checkAgentSQL = "SELECT Agent_ID FROM Agents WHERE User_ID = ?";
                PreparedStatement stmtAgent = con.prepareStatement(checkAgentSQL);
                stmtAgent.setInt(1, userID);
                ResultSet agentRS = stmtAgent.executeQuery();

                if (agentRS.next()) {
                    int agentID = agentRS.getInt("Agent_ID");
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome Agent.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new AgentDashboard(userID, username).setVisible(true);  // ✅ Pass both userID and username
                    dispose();
                } else {
                    lblMessage.setText("You are not an Agent.");
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
        SwingUtilities.invokeLater(() -> new AgentLogin().setVisible(true));
    }
}