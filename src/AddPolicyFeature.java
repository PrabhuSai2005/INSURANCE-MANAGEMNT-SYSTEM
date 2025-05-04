import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddPolicyFeature extends JFrame {
    private Connection con;
    private JTextField typeNameField, extensionYearsField, validationYearsField;
    private JButton addPolicyButton;

    public AddPolicyFeature(Connection con) {
        this.con = con;

        setTitle("Add Policy Type Extensions");
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
                Color color1 = new Color(81, 28, 5);
                Color color2 = new Color(81, 28, 5);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel);

        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Add Policy Type Extensions", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(81, 28, 5));
        titlePanel.add(titleLabel);
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(new Color(245, 245, 245));

        formPanel.add(new JLabel("Type Name:"));
        typeNameField = new JTextField();
        styleTextField(typeNameField);
        formPanel.add(typeNameField);

        formPanel.add(new JLabel("Extension Years:"));
        extensionYearsField = new JTextField();
        styleTextField(extensionYearsField);
        formPanel.add(extensionYearsField);

        formPanel.add(new JLabel("Validation Years:"));
        validationYearsField = new JTextField();
        styleTextField(validationYearsField);
        formPanel.add(validationYearsField);

        addPolicyButton = new JButton("Add Policy Type");
        styleButton(addPolicyButton);
        addPolicyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPolicyType();
            }
        });

        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(addPolicyButton);

        backgroundPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(new Color(33, 178, 241));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        field.setPreferredSize(new Dimension(250, 30));
        field.setBackground(Color.WHITE);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(33, 178, 241));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(33, 178, 241));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(33, 178, 241));
            }
        });
    }

    private void addPolicyType() {
        try {
            String typeName = typeNameField.getText().trim();
            int extensionYears = Integer.parseInt(extensionYearsField.getText().trim());
            int validationYears = Integer.parseInt(validationYearsField.getText().trim());

            if (typeName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Type Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO PolicyTypeExtensions (Type_Name, Extension_Years, Validation_Years) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, typeName);
            stmt.setInt(2, extensionYears);
            stmt.setInt(3, validationYears);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Policy Type Extension added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                typeNameField.setText("");
                extensionYearsField.setText("");
                validationYearsField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add Policy Type Extension.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            stmt.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Extension and Validation Years.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while adding Policy Type Extension.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Example usage
        Connection con = DatabaseConnection.getConnection(); // You should have a DatabaseConnection class
        if (con != null) {
            new AddPolicyFeature(con).setVisible(true);
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
