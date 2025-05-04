import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddDepartmentFeature extends JFrame {
    private Connection con;
    private JTextField deptIdField, deptNameField, locationField;
    private JButton addDeptButton;

    public AddDepartmentFeature(Connection con) {
        this.con = con;

        setTitle("Add New Department");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background panel with gradient
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(0, 51, 102);
                Color color2 = new Color(0, 102, 204);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel);

        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Add New Department", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 51, 102));
        titlePanel.add(titleLabel);
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(new Color(245, 245, 245));

        formPanel.add(new JLabel("Department ID:"));
        deptIdField = new JTextField();
        styleTextField(deptIdField);
        formPanel.add(deptIdField);

        formPanel.add(new JLabel("Department Name:"));
        deptNameField = new JTextField();
        styleTextField(deptNameField);
        formPanel.add(deptNameField);

        formPanel.add(new JLabel("Location:"));
        locationField = new JTextField();
        styleTextField(locationField);
        formPanel.add(locationField);

        addDeptButton = new JButton("Add Department");
        styleButton(addDeptButton);
        addDeptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDepartment();
            }
        });

        formPanel.add(new JLabel()); // empty cell
        formPanel.add(addDeptButton);

        backgroundPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(new Color(50, 50, 50));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        field.setPreferredSize(new Dimension(250, 30));
        field.setBackground(Color.WHITE);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 51, 102));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 102, 204));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 51, 102));
            }
        });
    }

    private void addDepartment() {
        try {
            int deptId = Integer.parseInt(deptIdField.getText().trim());
            String deptName = deptNameField.getText().trim();
            String location = locationField.getText().trim();

            if (deptName.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO Departments (Department_ID, Department_Name, Location) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, deptId);
            stmt.setString(2, deptName);
            stmt.setString(3, location);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Department added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                deptIdField.setText("");
                deptNameField.setText("");
                locationField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add department.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            stmt.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Department ID.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while adding department , Check for Duplicate Department or Duplicate Role ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Example usage
        Connection con = DatabaseConnection.getConnection(); // Make sure you have this method
        if (con != null) {
            new AddDepartmentFeature(con).setVisible(true);
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
