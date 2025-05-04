import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.Timer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboard extends JFrame {

    private JPanel featurePanel;
    private String username;
    private Connection con;

    public AdminDashboard(String username) {
        this.username = username;
        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        con = DatabaseConnection.getConnection();

        // Top panel with title
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(45, 118, 232));
        topPanel.setPreferredSize(new Dimension(1000, 80));
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        topPanel.add(titleLabel);

        // Side menu with buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setPreferredSize(new Dimension(300, 0));

        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        String[] buttonNames = {
                "View Profile",
                "View Agent Details",
                "Manage Agents",
                "Add New Role",
                "Add New Department",
                "Add New Policy Type Extensions",
                "Add New Claim Reason",
                "Add New Payment Method",
                "Manage Noticeboard",
                "View Complaints"
        };

        for (String name : buttonNames) {
            JButton button = new JButton(name);
            setButtonStyle(button, buttonFont);
            buttonPanel.add(button);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openFeature(name);
                }
            });
        }

        // Main feature panel (right side)
        featurePanel = new JPanel();
        featurePanel.setBackground(new Color(245, 245, 245));
        featurePanel.setLayout(new BorderLayout());

        // -------------------------
        // Right Side Content Starts
        // -------------------------

        // Welcome Message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(245, 245, 245));
        JLabel welcomeLabel = new JLabel("Welcome  " + username , SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(45, 118, 232));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Clock
        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 18));
        clockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateClock(clockLabel);
        startClockThread(clockLabel);

        // Dynamic Summary
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("📊 Admin Summary"));
        updateSummary(summaryPanel);
        summaryPanel.setBackground(Color.WHITE);

        // To-Do List
        DefaultListModel<String> todoModel = new DefaultListModel<>();
        JList<String> todoList = new JList<>(todoModel);
        JScrollPane scrollPane = new JScrollPane(todoList);
        scrollPane.setPreferredSize(new Dimension(200, 150));

        JTextField todoInput = new JTextField();
        todoInput.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton addTodoButton = new JButton("Add Task ➕");
        addTodoButton.setBackground(new Color(45, 118, 232));
        addTodoButton.setForeground(Color.WHITE);
        addTodoButton.setFocusPainted(false);
        addTodoButton.addActionListener(e -> {
            String task = todoInput.getText().trim();
            if (!task.isEmpty()) {
                todoModel.addElement(task);
                todoInput.setText("");
            }
        });

        JButton removeTodoButton = new JButton("Remove Selected ❌");
        removeTodoButton.setBackground(new Color(204, 0, 0));
        removeTodoButton.setForeground(Color.WHITE);
        removeTodoButton.setFocusPainted(false);
        removeTodoButton.addActionListener(e -> {
            int selectedIndex = todoList.getSelectedIndex();
            if (selectedIndex != -1) {
                todoModel.remove(selectedIndex);
            }
        });

        JPanel todoPanel = new JPanel(new BorderLayout(10, 10));
        todoPanel.setBorder(BorderFactory.createTitledBorder("📋 Admin To-Do List"));
        todoPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(todoInput, BorderLayout.CENTER);
        inputPanel.add(addTodoButton, BorderLayout.EAST);
        todoPanel.add(inputPanel, BorderLayout.SOUTH);
        todoPanel.add(removeTodoButton, BorderLayout.NORTH);

        // Arrange all in vertical layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(clockLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(summaryPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(todoPanel);

        // Add to main feature panel
        featurePanel.add(welcomePanel, BorderLayout.NORTH);
        featurePanel.add(centerPanel, BorderLayout.CENTER);

        // -------------------------
        // Right Side Content Ends
        // -------------------------

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.WEST);
        add(featurePanel, BorderLayout.CENTER);
    }

    private void setButtonStyle(JButton button, Font font) {
        button.setFont(font);
        button.setBackground(new Color(45, 118, 232));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 40));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
    }

    private void openFeature(String featureName) {
        if (featureName.equals("View Profile")) {
            ProfileFeature profile = new ProfileFeature(username);
            profile.setVisible(true);
            return;
        }

        if (featureName.equals("View Agent Details")) {
            AgentDetailsFeature agentDetails = new AgentDetailsFeature(username, con);
            agentDetails.setVisible(true);
            return;
        }

        if (featureName.equals("Manage Agents")) {
            AgentManagementFeature manageAgents = new AgentManagementFeature(username, con);
            manageAgents.setVisible(true);
            return;
        }

        if (featureName.equals("Add New Role")) {
            AddRoleFeature addRoleFeature = new AddRoleFeature(con);
            addRoleFeature.setVisible(true);
            return;
        }

        if (featureName.equals("Add New Department")) {
            AddDepartmentFeature addDepartmentFeature = new AddDepartmentFeature(con);
            addDepartmentFeature.setVisible(true);
            return;
        }

        if (featureName.equals("Add New Policy Type Extensions")) {
            AddPolicyFeature addPolicyFeature = new AddPolicyFeature(con);
            addPolicyFeature.setVisible(true);
            return;
        }

        if (featureName.equals("Add New Claim Reason")) {
            AddClaimReasonFeature addClaimReasonFeature = new AddClaimReasonFeature(con);
            addClaimReasonFeature.setVisible(true);
            return;
        }

        if (featureName.equals("Add New Payment Method")) {
            AddPaymentMethodFeature addPaymentMethodFeature = new AddPaymentMethodFeature(con);
            addPaymentMethodFeature.setVisible(true);
            return;
        }

        if (featureName.equals("Manage Noticeboard")) {
            ManageNoticeboardFeature manageNoticeboard = new ManageNoticeboardFeature(con);
            manageNoticeboard.setVisible(true);
            return;
        }

        if (featureName.equals("View Complaints")) {
            ViewComplaintsFeature viewComplaints = new ViewComplaintsFeature(con);
            viewComplaints.setVisible(true);
            return;
        }

        // Fallback
        featurePanel.removeAll();
        JLabel label = new JLabel("Feature: " + featureName, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        featurePanel.add(label, BorderLayout.CENTER);
        featurePanel.revalidate();
        featurePanel.repaint();
    }

    private void updateClock(JLabel label) {
        // Format the current time as "h:mm" (for 12-hour clock without leading zeros)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedTime = LocalTime.now().format(formatter);
        label.setText("Time: " + formattedTime);
    }

    private void startClockThread(JLabel label) {
        Timer timer = new Timer(1000, e -> updateClock(label));
        timer.start();
    }

    private void updateSummary(JPanel summaryPanel) {
        try {
            // Get the adminId dynamically using the username from the User table
            String adminIdQuery = "SELECT a.Admin_ID FROM Admins a " +
                    "JOIN User u ON a.User_ID = u.User_ID " +
                    "WHERE u.User_Name = ?";
            PreparedStatement stmtAdmin = con.prepareStatement(adminIdQuery);
            stmtAdmin.setString(1, username);  // Use the logged-in username to fetch adminId
            ResultSet rsAdmin = stmtAdmin.executeQuery();

            int adminId = -1;
            if (rsAdmin.next()) {
                adminId = rsAdmin.getInt("Admin_ID");  // Ensure correct column name
            }

            if (adminId != -1) {
                // Clear the existing content in summaryPanel
                summaryPanel.removeAll();

                // Get the number of agents under this admin
                String agentQuery = "SELECT COUNT(*) AS agentCount FROM Agents WHERE Admin_ID = ?";  // Use Admin_ID
                PreparedStatement stmtAgents = con.prepareStatement(agentQuery);
                stmtAgents.setInt(1, adminId);
                ResultSet rsAgents = stmtAgents.executeQuery();
                if (rsAgents.next()) {
                    int agentCount = rsAgents.getInt("agentCount");
                    System.out.println("Agent Count: " + agentCount);  // Debugging output
                    JLabel agentsLabel = new JLabel("👨‍💼 Agents: " + agentCount);
                    summaryPanel.add(agentsLabel);
                }

                // Get the number of policies handled by agents under this admin
                String policyQuery = "SELECT COUNT(*) AS policiesCount FROM Policies p " +
                        "JOIN Customers c ON p.Customer_ID = c.Customer_ID " +
                        "WHERE c.Agent_ID IN (SELECT Agent_ID FROM Agents WHERE Admin_ID = ?)";
                PreparedStatement stmtPolicies = con.prepareStatement(policyQuery);
                stmtPolicies.setInt(1, adminId);
                ResultSet rsPolicies = stmtPolicies.executeQuery();
                if (rsPolicies.next()) {
                    int policiesCount = rsPolicies.getInt("policiesCount");
                    System.out.println("Policies Count: " + policiesCount);  // Debugging output
                    JLabel policiesLabel = new JLabel("📜 Policies: " + policiesCount);
                    summaryPanel.add(policiesLabel);
                }

                // Get the total number of claims under this admin
                String claimsQuery = "SELECT COUNT(*) AS claimsCount " +
                        "FROM Claims c " +
                        "JOIN Policies p ON c.Policy_ID = p.Policy_ID " +
                        "JOIN Customers cu ON p.Customer_ID = cu.Customer_ID " +
                        "WHERE cu.Agent_ID IN (SELECT Agent_ID FROM Agents WHERE Admin_ID = ?)";
                PreparedStatement stmtClaims = con.prepareStatement(claimsQuery);
                stmtClaims.setInt(1, adminId);
                ResultSet rsClaims = stmtClaims.executeQuery();
                if (rsClaims.next()) {
                    int claimsCount = rsClaims.getInt("claimsCount");
                    System.out.println("Claims Count: " + claimsCount);  // Debugging output
                    JLabel claimsLabel = new JLabel("📝 Claims: " + claimsCount);
                    summaryPanel.add(claimsLabel);
                }


                // Get the total amount paid for claims under this admin
                String amountQuery = "SELECT SUM(c.Claim_Amount) AS totalAmount " +
                        "FROM Claims c " +
                        "JOIN Policies p ON c.Policy_ID = p.Policy_ID " + // Join Policies to Claims
                        "JOIN Customers cu ON p.Customer_ID = cu.Customer_ID " + // Join Customers to Policies
                        "JOIN Agents a ON cu.Agent_ID = a.Agent_ID " + // Join Agents to Customers
                        "WHERE a.Admin_ID = ?"; // Filter by Admin_ID
                PreparedStatement stmtAmount = con.prepareStatement(amountQuery);
                stmtAmount.setInt(1, adminId);
                ResultSet rsAmount = stmtAmount.executeQuery();
                if (rsAmount.next()) {
                    double totalAmount = rsAmount.getDouble("totalAmount");
                    System.out.println("Total Claim Amount: " + totalAmount);  // Debugging output
                    JLabel totalAmountLabel = new JLabel("💰 Total Claims Amount: " + totalAmount);
                    summaryPanel.add(totalAmountLabel);
                }


                // Refresh the summary panel
                summaryPanel.revalidate();
                summaryPanel.repaint();

            } else {
                JOptionPane.showMessageDialog(this, "Admin not found", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching summary data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username = "admin";  // Use a test username
            AdminDashboard dashboard = new AdminDashboard(username);
            dashboard.setVisible(true);
        });
    }
}
