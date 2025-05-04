import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class UserRegistrationForm extends JFrame {
    private JTextField tfUsername, tfGender, tfStreet, tfCity, tfState, tfCountry;
    private JPasswordField pfPassword;
    private JDateChooser dcDOB;
    private JTextField tfPhoneInput, tfEmailInput;
    private JButton btnAddPhone, btnAddEmail, btnSubmit;
    private DefaultListModel<String> phoneListModel, emailListModel;
    private JList<String> phoneList, emailList;
    private Set<String> phoneSet, emailSet;

    public UserRegistrationForm() {
        setTitle("User Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        phoneSet = new HashSet<>();
        emailSet = new HashSet<>();

        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        tfUsername = createTextField(panel, "Username:", labelFont, fieldFont, gbc, 0);
        pfPassword = new JPasswordField(20);
        createField(panel, "Password:", pfPassword, labelFont, fieldFont, gbc, 1);
        tfGender = createTextField(panel, "Gender:", labelFont, fieldFont, gbc, 2);

        JLabel lblDOB = new JLabel("Date of Birth:");
        lblDOB.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(lblDOB, gbc);
        dcDOB = new JDateChooser();
        dcDOB.setDateFormatString("yyyy-MM-dd");
        dcDOB.setFont(fieldFont);
        gbc.gridx = 1;
        panel.add(dcDOB, gbc);

        tfStreet = createTextField(panel, "Street:", labelFont, fieldFont, gbc, 4);
        tfCity = createTextField(panel, "City:", labelFont, fieldFont, gbc, 5);
        tfState = createTextField(panel, "State:", labelFont, fieldFont, gbc, 6);
        tfCountry = createTextField(panel, "Country:", labelFont, fieldFont, gbc, 7);

        // PHONE INPUT
        gbc.gridy = 8;
        panel.add(new JLabel("Phone Number:"), gbc);
        tfPhoneInput = new JTextField(15);
        gbc.gridx = 1;
        panel.add(tfPhoneInput, gbc);
        btnAddPhone = new JButton("Add Phone");
        gbc.gridx = 2;
        panel.add(btnAddPhone, gbc);

        phoneListModel = new DefaultListModel<>();
        phoneList = new JList<>(phoneListModel);
        phoneList.setVisibleRowCount(3);
        gbc.gridx = 1; gbc.gridy = 9;
        panel.add(new JScrollPane(phoneList), gbc);

        // EMAIL INPUT
        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(new JLabel("Email Address:"), gbc);
        tfEmailInput = new JTextField(15);
        gbc.gridx = 1;
        panel.add(tfEmailInput, gbc);
        btnAddEmail = new JButton("Add Email");
        gbc.gridx = 2;
        panel.add(btnAddEmail, gbc);

        emailListModel = new DefaultListModel<>();
        emailList = new JList<>(emailListModel);
        emailList.setVisibleRowCount(3);
        gbc.gridx = 1; gbc.gridy = 11;
        panel.add(new JScrollPane(emailList), gbc);

        // Submit Button
        btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 16));
        btnSubmit.setBackground(new Color(34, 139, 34));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 12; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnSubmit, gbc);

        btnAddPhone.addActionListener(e -> addUniqueToList(tfPhoneInput, phoneSet, phoneListModel));
        btnAddEmail.addActionListener(e -> addUniqueToList(tfEmailInput, emailSet, emailListModel));
        btnSubmit.addActionListener(e -> registerUser());

        JScrollPane scrollPane = new JScrollPane(panel);
        setContentPane(scrollPane);
    }

    private void addUniqueToList(JTextField inputField, Set<String> set, DefaultListModel<String> listModel) {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            if (set.contains(text)) {
                JOptionPane.showMessageDialog(this, "Duplicate entry not allowed: " + text);
            } else {
                set.add(text);
                listModel.addElement(text);
            }
        }
        inputField.setText("");
    }

    private JTextField createTextField(JPanel panel, String labelText, Font labelFont, Font fieldFont, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(label, gbc);

        JTextField textField = new JTextField(20);
        styleTextField(textField);
        textField.setFont(fieldFont);
        gbc.gridx = 1;
        panel.add(textField, gbc);
        return textField;
    }

    private void createField(JPanel panel, String labelText, JComponent field, Font labelFont, Font fieldFont, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(label, gbc);
        field.setFont(fieldFont);
        styleTextField(field);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void styleTextField(JComponent field) {
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 1, true));
        field.setPreferredSize(new Dimension(200, 30));
    }

    private void registerUser() {
        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());
        String gender = tfGender.getText();
        java.util.Date dob = dcDOB.getDate();
        String street = tfStreet.getText();
        String city = tfCity.getText();
        String state = tfState.getText();
        String country = tfCountry.getText();

        if (username.isEmpty() || password.isEmpty() || gender.isEmpty() || dob == null || street.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection con = DatabaseConnection.getConnection();
        try {
            String insertUserSQL = "INSERT INTO User (User_Name, Password, Gender, Date_of_Birth) VALUES (?, ?, ?, ?)";
            PreparedStatement stmtUser = con.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS);
            stmtUser.setString(1, username);
            stmtUser.setString(2, password);
            stmtUser.setString(3, gender);
            stmtUser.setDate(4, new java.sql.Date(dob.getTime()));
            stmtUser.executeUpdate();

            ResultSet rs = stmtUser.getGeneratedKeys();
            int userID = -1;
            if (rs.next()) {
                userID = rs.getInt(1);
            }

            String insertAddressSQL = "INSERT INTO User_Address (User_ID, Street, City, State, Country) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtAddress = con.prepareStatement(insertAddressSQL);
            stmtAddress.setInt(1, userID);
            stmtAddress.setString(2, street);
            stmtAddress.setString(3, city);
            stmtAddress.setString(4, state);
            stmtAddress.setString(5, country);
            stmtAddress.executeUpdate();

            PreparedStatement stmtContact = con.prepareStatement("INSERT INTO User_Contact (User_ID, Phone_Number) VALUES (?, ?)");
            for (String phone : phoneSet) {
                stmtContact.setInt(1, userID);
                stmtContact.setString(2, phone);
                stmtContact.executeUpdate();
            }

            PreparedStatement stmtEmail = con.prepareStatement("INSERT INTO User_Email (User_ID, Email) VALUES (?, ?)");
            for (String email : emailSet) {
                stmtEmail.setInt(1, userID);
                stmtEmail.setString(2, email);
                stmtEmail.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserRegistrationForm().setVisible(true));
    }
}

// Custom background panel
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        try {
            backgroundImage = new ImageIcon("C:\\Users\\PRABHU SAI\\Downloads\\insurance_management\\photos\\registration.jpg").getImage();
        } catch (Exception e) {
            System.out.println("Background image not found");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
