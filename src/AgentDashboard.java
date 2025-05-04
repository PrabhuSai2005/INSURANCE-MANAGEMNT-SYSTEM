import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AgentDashboard extends JFrame {

    private int loggedInUserId;
    private String loggedInUsername;

    public AgentDashboard(int loggedInUserId, String loggedInUsername) {
        this.loggedInUserId = loggedInUserId;
        this.loggedInUsername = loggedInUsername;

        setTitle("Agent Dashboard - Welcome " + loggedInUsername);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Get the screen size and set the window size to the full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);

        // Set the window to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JLabel titleLabel = new JLabel("AGENT DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(30, 144, 255));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titleLabel.setPreferredSize(new Dimension(900, 60));

        JPanel buttonPanel = new JPanel(new GridLayout(9, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buttonPanel.setPreferredSize(new Dimension(280, 0));

        JPanel featurePanel = new JPanel(new BorderLayout());
        featurePanel.setBackground(new Color(250, 250, 250));

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        JButton btnProfile = createStyledButton("My Profile", buttonFont);
        JButton btnManageCustomers = createStyledButton("Manage Assigned Customers", buttonFont);
        JButton btnCustomerProfiles = createStyledButton("Customer Profiles", buttonFont);
        JButton btnCustomerPolicies = createStyledButton("Customer Policies", buttonFont);
        JButton btnPolicyRenewal = createStyledButton("Policy Renewals", buttonFont);
        JButton btnClaims = createStyledButton("Manage Claims", buttonFont);
        JButton btnPayments = createStyledButton("Customer Payments", buttonFont);
        JButton btnNotices = createStyledButton("View Notices", buttonFont);
        JButton btnComplaints = createStyledButton("View Complaints", buttonFont);

        buttonPanel.add(btnProfile);
        buttonPanel.add(btnManageCustomers);
        buttonPanel.add(btnCustomerProfiles);
        buttonPanel.add(btnCustomerPolicies);
        buttonPanel.add(btnPolicyRenewal);
        buttonPanel.add(btnClaims);
        buttonPanel.add(btnPayments);
        buttonPanel.add(btnNotices);
        buttonPanel.add(btnComplaints);

        add(titleLabel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.WEST);

        add(featurePanel, BorderLayout.CENTER);

        btnProfile.addActionListener(e -> new ViewProfile(loggedInUserId).setVisible(true));
        btnManageCustomers.addActionListener(e -> new ManageAssignedCustomers(loggedInUserId).setVisible(true));
        btnCustomerProfiles.addActionListener(e -> new ViewCustomerProfiles(loggedInUserId).setVisible(true));
        btnCustomerPolicies.addActionListener(e -> new ViewCustomerPolicies(loggedInUserId).setVisible(true));
        btnPolicyRenewal.addActionListener(e -> new TrackPolicyRenewal(loggedInUserId).setVisible(true));
        btnClaims.addActionListener(e -> new ManageClaims(loggedInUserId).setVisible(true));
        btnPayments.addActionListener(e -> new TrackCustomerPayments(loggedInUserId).setVisible(true));
        btnNotices.addActionListener(e -> new ViewNotices(loggedInUserId).setVisible(true));
        btnComplaints.addActionListener(e -> new ViewComplaints(loggedInUserId).setVisible(true));
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
        SwingUtilities.invokeLater(() -> new AgentDashboard(1, "DemoUser").setVisible(true));
    }
}
