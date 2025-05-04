import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AgentManagementFeature extends JFrame {
    private String username;
    private Connection con;
    private JComboBox<String> agentComboBox;
    private JButton removeAgentButton;
    private JTextField userIdField, emailField;
    private JButton addAgentButton;

    public AgentManagementFeature(String username, Connection con) {
        this.username = username;
        this.con = con;

        setTitle("Agent Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        // Top title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(45, 118, 232));
        titlePanel.setPreferredSize(new Dimension(800, 60));
        JLabel titleLabel = new JLabel("Agent Management", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Center part
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add Agent Panel
        JPanel addAgentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        addAgentPanel.setBorder(BorderFactory.createTitledBorder("Add New Agent"));

        addAgentPanel.add(new JLabel("Agent User ID: "));
        userIdField = new JTextField();
        addAgentPanel.add(userIdField);

        addAgentPanel.add(new JLabel("Agent Email: "));
        emailField = new JTextField();
        addAgentPanel.add(emailField);

        addAgentButton = new JButton("Add Agent");
        addAgentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText().trim();
                String email = emailField.getText().trim();

                if (!userId.isEmpty() && !email.isEmpty()) {
                    addAgent(Integer.parseInt(userId), email);
                } else {
                    JOptionPane.showMessageDialog(AgentManagementFeature.this, "Please fill in both fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        addAgentPanel.add(new JLabel());
        addAgentPanel.add(addAgentButton);

        // Remove Agent Panel
        JPanel removeAgentPanel = new JPanel(new BorderLayout(10, 10));
        removeAgentPanel.setBorder(BorderFactory.createTitledBorder("Remove Existing Agent"));

        agentComboBox = new JComboBox<>();
        removeAgentPanel.add(agentComboBox, BorderLayout.CENTER);

        removeAgentButton = new JButton("Remove Agent");
        removeAgentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAgent = (String) agentComboBox.getSelectedItem();
                if (selectedAgent != null && !selectedAgent.equals("No agents available")) {
                    int agentId = Integer.parseInt(selectedAgent.split(" - ")[0]);
                    removeAgent(agentId);
                }
            }
        });
        removeAgentPanel.add(removeAgentButton, BorderLayout.SOUTH);

        centerPanel.add(addAgentPanel);
        centerPanel.add(removeAgentPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Info
        JLabel infoLabel = new JLabel("Agents Under Admin: " + username, SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoLabel.setForeground(new Color(45, 118, 232));
        add(infoLabel, BorderLayout.SOUTH);

        loadAgents();
    }

    private void loadAgents() {
        try {
            int adminId = getAdminId(username);
            if (adminId == -1) {
                JOptionPane.showMessageDialog(this, "Admin not found. Username: " + username, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            agentComboBox.removeAllItems();
            boolean found = false;

            CallableStatement stmt = con.prepareCall("{ CALL GetAgentsUnderAdmin(?) }");
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int agentId = rs.getInt("Agent_ID");
                String email = rs.getString("Email");
                agentComboBox.addItem(agentId + " - " + email);
                found = true;
            }

            if (!found) {
                agentComboBox.addItem("No agents available");
            }

            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                adminStmt.close();
                adminRs.close();
            }
            userStmt.close();
            userRs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adminId;
    }

    private void removeAgent(int agentId) {
        try {
            // Get User_ID linked with Agent_ID
            String getUserIdQuery = "SELECT User_ID FROM Agents WHERE Agent_ID = ?";
            PreparedStatement getUserStmt = con.prepareStatement(getUserIdQuery);
            getUserStmt.setInt(1, agentId);
            ResultSet rs = getUserStmt.executeQuery();

            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt("User_ID");
            }
            getUserStmt.close();
            rs.close();

            if (userId != -1) {
                // Delete Agent
                String deleteAgentQuery = "DELETE FROM Agents WHERE Agent_ID = ?";
                PreparedStatement deleteAgentStmt = con.prepareStatement(deleteAgentQuery);
                deleteAgentStmt.setInt(1, agentId);
                int agentRowsAffected = deleteAgentStmt.executeUpdate();
                deleteAgentStmt.close();

                // Delete User
                String deleteUserQuery = "DELETE FROM User WHERE User_ID = ?";
                PreparedStatement deleteUserStmt = con.prepareStatement(deleteUserQuery);
                deleteUserStmt.setInt(1, userId);
                int userRowsAffected = deleteUserStmt.executeUpdate();
                deleteUserStmt.close();

                if (agentRowsAffected > 0 && userRowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Agent and User record removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAgents();
                } else {
                    JOptionPane.showMessageDialog(this, "Agent removed successfully.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Agent not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Agent removed successfully.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAgent(int userId, String email) {
        try {
            int adminId = getAdminId(username);
            if (adminId == -1) {
                JOptionPane.showMessageDialog(this, "Admin not found. Username: " + username, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String checkUserQuery = "SELECT * FROM User WHERE User_ID = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkUserQuery);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                // Insert into User
                String insertUserQuery = "INSERT INTO User (User_ID, User_Name, Password, Gender, Date_of_Birth) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertUserStmt = con.prepareStatement(insertUserQuery);
                insertUserStmt.setInt(1, userId);
                insertUserStmt.setString(2, "Agent_" + userId);  // default username
                insertUserStmt.setString(3, "password");          // default password
                insertUserStmt.setString(4, "Unknown");
                insertUserStmt.setDate(5, new java.sql.Date(System.currentTimeMillis())); // default date
                insertUserStmt.executeUpdate();
                insertUserStmt.close();
            }
            checkStmt.close();
            rs.close();

            // Insert into Agents with Email
            String insertAgentQuery = "INSERT INTO Agents (Admin_ID, User_ID, Email) VALUES (?, ?, ?)";
            PreparedStatement insertAgentStmt = con.prepareStatement(insertAgentQuery);
            insertAgentStmt.setInt(1, adminId);
            insertAgentStmt.setInt(2, userId);
            insertAgentStmt.setString(3, email); // Set email here
            insertAgentStmt.executeUpdate();
            insertAgentStmt.close();

            JOptionPane.showMessageDialog(this, "Agent added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAgents();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while adding agent.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Establishing a database connection
        Connection con = DatabaseConnection.getConnection();
        if (con != null) {
            new AgentManagementFeature("AdminUserName", con).setVisible(true);
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
