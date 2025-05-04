import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerDashboard extends JFrame {

    private int customerUserId;

    public CustomerDashboard(int customerUserId) {
        this.customerUserId = customerUserId;

        setTitle("My Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizing the window on startup
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("MY DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(30, 144, 255));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titleLabel.setPreferredSize(new Dimension(900, 60));

        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buttonPanel.setPreferredSize(new Dimension(280, 0));

        JPanel featurePanel = new JPanel(new BorderLayout());
        featurePanel.setBackground(new Color(250, 250, 250));

        // Add a motivational caption above the image
        JLabel captionLabel = new JLabel("Secure Your Future! More Policies, More Peace of Mind.", SwingConstants.CENTER);
        captionLabel.setFont(new Font("Segoe UI", Font.BOLD, 45));
        captionLabel.setForeground(new Color(18, 76, 5));
        captionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        captionLabel.setPreferredSize(new Dimension(900, 40));

        // Load and add the image to the featurePanel
        ImageIcon dashboardIcon = new ImageIcon("C:\\Users\\PRABHU SAI\\Downloads\\insurance_management\\photos\\CUSTOMER_DASHBOARD.jpg");
        Image scaledImage = dashboardIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        featurePanel.add(captionLabel, BorderLayout.NORTH);  // Add caption above the image
        featurePanel.add(imageLabel, BorderLayout.CENTER);

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        JButton btnProfile = createStyledButton("My Profile", buttonFont);
        JButton btnPolicies = createStyledButton("My Policies", buttonFont);
        JButton btnRenew = createStyledButton("Request Renewal", buttonFont);
        JButton btnClaim = createStyledButton("Request Claim", buttonFont);
        JButton btnNotices = createStyledButton("View Notices", buttonFont);
        JButton btnComplaint = createStyledButton("File Complaint", buttonFont);

        buttonPanel.add(btnProfile);
        buttonPanel.add(btnPolicies);
        buttonPanel.add(btnRenew);
        buttonPanel.add(btnClaim);
        buttonPanel.add(btnNotices);
        buttonPanel.add(btnComplaint);

        add(titleLabel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.WEST);

        add(featurePanel, BorderLayout.CENTER);

        btnProfile.addActionListener(e -> new CustomerMyProfile(customerUserId).setVisible(true));
        btnPolicies.addActionListener(e -> new CustomerViewPolicies(customerUserId).setVisible(true));
        btnRenew.addActionListener(e -> new CustomerRequestRenewal(customerUserId).setVisible(true));
        btnClaim.addActionListener(e -> new CustomerRequestClaim(customerUserId).setVisible(true));
        btnNotices.addActionListener(e -> new CustomerViewNotices(customerUserId).setVisible(true));
        btnComplaint.addActionListener(e -> new CustomerFileComplaint().setVisible(true));
    }

    private JButton createStyledButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 90, 150)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(100, 160, 210));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDashboard(1).setVisible(true));
    }
}
