import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddRoleFeature extends JFrame {
    private Connection con;
    private JComboBox<String> rolesComboBox;
    private JTextField roleIdField, roleNameField, salaryField;
    private JButton addRoleButton;

    public AddRoleFeature(Connection con) {
        this.con = con;

        // Set title with dark blue color (Note: This changes the frame title but not the text color in the title bar)
        setTitle("Add New Role");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background gradient with dark blue color
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(0, 51, 102);  // Dark blue color
                Color color2 = new Color(0, 102, 204); // Lighter blue for gradient effect
                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel);

        // Title Panel with dark blue title text
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Add New Role", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 51, 102)); // Dark blue color for the title text
        titlePanel.add(titleLabel);
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel for ComboBox (Show Existing Roles)
        JPanel rolePanel = new JPanel(new BorderLayout());
        rolesComboBox = new JComboBox<>();
        loadRoles();
        rolePanel.add(rolesComboBox, BorderLayout.CENTER);
        backgroundPanel.add(rolePanel, BorderLayout.CENTER);

        // Panel for adding a new role (ID, Name, Salary)
        JPanel addRolePanel = new JPanel(new GridLayout(5, 2, 10, 10));
        addRolePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addRolePanel.setBackground(new Color(245, 245, 245)); // Light background for the form

        addRolePanel.add(new JLabel("Role ID: "));
        roleIdField = new JTextField();
        styleTextField(roleIdField);
        addRolePanel.add(roleIdField);

        addRolePanel.add(new JLabel("Role Name: "));
        roleNameField = new JTextField();
        styleTextField(roleNameField);
        addRolePanel.add(roleNameField);

        addRolePanel.add(new JLabel("Salary: "));
        salaryField = new JTextField();
        styleTextField(salaryField);
        addRolePanel.add(salaryField);

        addRoleButton = new JButton("Add Role");
        styleButton(addRoleButton);
        addRoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int roleId = Integer.parseInt(roleIdField.getText().trim());
                String roleName = roleNameField.getText().trim();
                String salary = salaryField.getText().trim();

                if (!roleName.isEmpty() && !salary.isEmpty()) {
                    addRole(roleId, roleName, salary);
                } else {
                    JOptionPane.showMessageDialog(AddRoleFeature.this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addRolePanel.add(new JLabel());
        addRolePanel.add(addRoleButton);
        backgroundPanel.add(addRolePanel, BorderLayout.SOUTH);
    }

    // Style the text fields for modern look
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(new Color(50, 50, 50));  // Dark text color for visibility
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        field.setPreferredSize(new Dimension(250, 30));
        field.setBackground(Color.WHITE);  // White background for text fields
    }

    // Style the buttons with hover effects
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 51, 102));  // Dark blue color
        button.setForeground(Color.WHITE);  // White text for the button
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));

        // Hover effect for button
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 102, 204)); // Lighter blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 51, 102)); // Revert to dark blue
            }
        });
    }

    // Load existing roles from the database
    private void loadRoles() {
        try {
            Statement stmt = con.createStatement();
            String query = "SELECT Role_Name FROM Roles";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String roleName = rs.getString("Role_Name");
                rolesComboBox.addItem(roleName);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add new role to the database
    private void addRole(int roleId, String roleName, String salary) {
        try {
            String query = "INSERT INTO Roles (Role_ID, Role_Name, Salary) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, roleId);
            stmt.setString(2, roleName);
            stmt.setBigDecimal(3, new java.math.BigDecimal(salary));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Role added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRoles();  // Refresh the roles list after adding a new role
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add role.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while adding role , Check for Duplicate Role OR Role ID error .", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Establishing a database connection
        Connection con = DatabaseConnection.getConnection();  // Assumed method to get DB connection
        if (con != null) {
            new AddRoleFeature(con).setVisible(true);
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
