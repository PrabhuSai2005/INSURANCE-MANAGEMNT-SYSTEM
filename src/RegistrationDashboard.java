import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class RegistrationDashboard extends JFrame {

    private JButton userButton, customerButton, agentButton, adminButton;

    public RegistrationDashboard() {
        setTitle("Registration Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in FULL SCREEN mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with background image
        JPanel panel = new JPanel(new BorderLayout()) {
            private Image backgroundImage;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);  // Stretch the image to fill the panel
                }
            }

            @Override
            public void updateUI() {
                try {
                    backgroundImage = ImageIO.read(new File("C:\\Users\\PRABHU SAI\\Downloads\\insurance_management\\photos\\main_reg_dash.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.updateUI();
            }
        };
        panel.setOpaque(false);

        // Container panel to hold the components on the right side
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 130); // 130 right margin (moved nicely towards left)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;

        // Title Label
        JLabel titleLabel = new JLabel("Main Registration Dashboard", SwingConstants.RIGHT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(6, 40, 74)); // Blue color for title
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(titleLabel, gbc);

        // User Registration Button
        gbc.gridy = 1;
        userButton = new JButton("User Registration Form");
        userButton.setFont(new Font("Arial", Font.BOLD, 24));
        userButton.setBackground(new Color(0, 102, 204));
        userButton.setForeground(Color.WHITE);
        rightPanel.add(userButton, gbc);

        // Customer Registration Button
        gbc.gridy = 2;
        customerButton = new JButton("Customer Registration");
        customerButton.setFont(new Font("Arial", Font.BOLD, 24));
        customerButton.setBackground(new Color(0, 153, 0));
        customerButton.setForeground(Color.WHITE);
        rightPanel.add(customerButton, gbc);

        // Agent Registration Button
        gbc.gridy = 3;
        agentButton = new JButton("Agent Registration");
        agentButton.setFont(new Font("Arial", Font.BOLD, 24));
        agentButton.setBackground(new Color(255, 153, 0));
        agentButton.setForeground(Color.WHITE);
        rightPanel.add(agentButton, gbc);

        // Admin Registration Button
        gbc.gridy = 4;
        adminButton = new JButton("Admin Registration");
        adminButton.setFont(new Font("Arial", Font.BOLD, 24));
        adminButton.setBackground(new Color(204, 0, 0));
        adminButton.setForeground(Color.WHITE);
        rightPanel.add(adminButton, gbc);

        // Add rightPanel to the main panel
        panel.add(rightPanel, BorderLayout.EAST);

        add(panel);

        // Action Listeners
        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UserRegistrationForm().setVisible(true);
            }
        });

        customerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CustomerRegistration().setVisible(true);
            }
        });

        agentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AgentRegistration().setVisible(true);
            }
        });

        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminRegistration().setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegistrationDashboard().setVisible(true);
        });
    }
}
