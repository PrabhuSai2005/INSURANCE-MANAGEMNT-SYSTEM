import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AgentDetailsFeature extends JFrame {
    private String username;
    private Connection con;

    public AgentDetailsFeature(String username, Connection con) {
        this.username = username;
        this.con = con;

        // Set up frame properties
        setTitle("Agent Details");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Title panel with modern blue color and stylish font
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(45, 118, 232));
        titlePanel.setPreferredSize(new Dimension(800, 60));
        JLabel titleLabel = new JLabel("Agent Details", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Modern font style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Info label with custom font and padding
        JLabel infoLabel = new JLabel("Agent Details for Admin: " + username, SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setForeground(new Color(45, 118, 232));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));  // Smaller gap
        add(infoLabel, BorderLayout.CENTER);

        // Create a panel for the circular profile photo with a gap
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20)); // Added gap
        profilePanel.setBackground(Color.WHITE);

        // Set the profile image inside the circular panel
        ImageIcon icon = new ImageIcon("C:\\Users\\PRABHU SAI\\Downloads\\insurance_management\\photos\\list_agents.png");
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        JLabel photoLabel = new JLabel(icon);
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5)); // Circular border
        photoLabel.setOpaque(true);
        profilePanel.add(photoLabel);  // Use profilePanel, not photoPanel
        add(profilePanel, BorderLayout.CENTER);

        // Create a panel for agent list with square box layout
        JPanel agentListPanel = new JPanel();
        agentListPanel.setLayout(new GridLayout(0, 1));  // GridLayout with 1 column
        agentListPanel.setBackground(Color.WHITE);
        agentListPanel.setPreferredSize(new Dimension(750, 250));  // Adjusted for square box
        agentListPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Padding for the container

        // JScrollPane to enable scrolling if many agents
        JScrollPane scrollPane = new JScrollPane(agentListPanel);
        scrollPane.setPreferredSize(new Dimension(750, 250));  // Adjusted for square box
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Fetch and display agent details for the current admin
        try {
            int adminId = getAdminId(username);
            if (adminId == -1) {
                JOptionPane.showMessageDialog(this, "Admin not found. Username: " + username, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String storedProcedure = "{CALL GetAgentsUnderAdmin(?)}";
            CallableStatement stmt = con.prepareCall(storedProcedure);
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();

            boolean agentsFound = false;
            while (rs.next()) {
                String agentId = rs.getString("agent_id");
                String userName = rs.getString("user_name");
                String email = rs.getString("email");

                // Display agent details as a list item inside the container
                JLabel agentLabel = new JLabel("<html><b>Agent ID:</b> " + agentId + "  <b>Username:</b> " + userName + "  <b>Email:</b> " + email + "</html>");
                agentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                agentLabel.setForeground(new Color(45, 118, 232));
                agentLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Reduced padding

                agentListPanel.add(agentLabel);
                // Removed or reduced the spacing
                // agentListPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Reduced gap here if necessary
                agentsFound = true;
            }

            if (!agentsFound) {
                JLabel noAgentsLabel = new JLabel("No agents found under this admin.", SwingConstants.CENTER);
                noAgentsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                noAgentsLabel.setForeground(Color.GRAY);
                agentListPanel.add(noAgentsLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the scroll pane with agent list to the bottom section of the frame
        add(scrollPane, BorderLayout.SOUTH);

        // Refresh the panel to display the agents
        agentListPanel.revalidate();
        agentListPanel.repaint();
    }

    // Helper method to get admin_id based on username
    private int getAdminId(String username) {
        int adminId = -1;
        try {
            String userQuery = "SELECT User_ID FROM User WHERE User_Name = ?";
            PreparedStatement userStmt = con.prepareStatement(userQuery);
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();

            if (userRs.next()) {
                int userId = userRs.getInt("User_ID");

                String adminQuery = "SELECT Admin_ID FROM Admins WHERE User_ID = ?";
                PreparedStatement adminStmt = con.prepareStatement(adminQuery);
                adminStmt.setInt(1, userId);
                ResultSet adminRs = adminStmt.executeQuery();

                if (adminRs.next()) {
                    adminId = adminRs.getInt("Admin_ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return adminId;
    }

    public static void main(String[] args) {
        Connection con = DatabaseConnection.getConnection();
        if (con != null) {
            new AgentDetailsFeature("Admin", con).setVisible(true);
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
