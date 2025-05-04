import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainLoginDashboard extends JFrame {
    private JButton btnAgentLogin;
    private JButton btnCustomerLogin;
    private JButton btnAdminLogin;
    private JLabel lblTitle;

    public MainLoginDashboard() {
        setTitle("Login Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in full screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the background panel with an image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("C:/Users/PRABHU SAI/Downloads/insurance_management/photos/MAIN_LOGIN_DASH.png"); // Updated image path
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Create the panel for the buttons and title
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Stack components vertically
        panel.setOpaque(false); // Make the panel transparent

        // Set margins to align them to the left with some spacing
        panel.setBorder(BorderFactory.createEmptyBorder(100, 30, 30, 30)); // Adds gap from the top

        Font buttonFont = new Font("Arial", Font.BOLD, 40); // Updated font for buttons
        Font titleFont = new Font("Arial", Font.BOLD, 50); // Updated font for title

        // Title Label
        lblTitle = new JLabel("Insurance Management System");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(Color.BLACK);

        // Helper method to create stylish buttons
        btnAgentLogin = createStylishButton("Login as Agent", new Color(145, 36, 218));
        btnCustomerLogin = createStylishButton("Login as Customer", new Color(34, 139, 34));
        btnAdminLogin = createStylishButton("Login as Admin", new Color(255, 69, 0));

        // Add components to the panel
        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 60))); // More gap between title and buttons
        panel.add(btnAgentLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 60))); // Add more gap between buttons
        panel.add(btnAdminLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 60))); // Add more gap between buttons
        panel.add(btnCustomerLogin);

        // Add the panel to the background
        backgroundPanel.add(panel, BorderLayout.WEST); // Align the panel to the left side

        // Add the background panel to the frame
        add(backgroundPanel);

        // Button action listeners
        btnAgentLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AgentLogin().setVisible(true);
                dispose();
            }
        });

        btnCustomerLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerLogin().setVisible(true);
                dispose();
            }
        });

        btnAdminLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminLogin().setVisible(true);
                dispose();
            }
        });
    }

    // Helper method to create stylish buttons
    private JButton createStylishButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 40)); // Stylish font with larger size
        button.setBackground(bgColor); // Set the button background color
        button.setForeground(Color.WHITE); // White text color
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Add more padding for larger buttons

        // Rounded corners and shadow effect
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(bgColor, 2));

        // Add shadow to the button
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            protected void paintButtonBorder(Graphics g, JComponent c, Rectangle shape) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.BLACK);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(4));
                g2.draw(shape);
                g2.dispose();
            }
        });

        // Hover effect: Smooth transition on hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainLoginDashboard().setVisible(true);
        });
    }
}
