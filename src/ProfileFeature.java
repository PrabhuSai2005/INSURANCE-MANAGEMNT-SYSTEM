import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;

public class ProfileFeature extends JFrame {

    private String username;
    private JPanel mainPanel;

    public ProfileFeature(String username) {
        this.username = username;

        // Frame settings
        setTitle("Profile Details");
        setSize(900, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Title
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(45, 118, 232));
        topPanel.setPreferredSize(new Dimension(900, 80));
        JLabel titleLabel = new JLabel("PROFILE DETAILS", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        topPanel.add(titleLabel);

        // Main Panel
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(230, 230, 250));
        mainPanel.setLayout(null);

        // Profile Photo
        JLabel photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            BufferedImage originalImage = ImageIO.read(new File("C:\\Users\\PRABHU SAI\\Downloads\\insurance_management\\photos\\VIEW_PROFILE_ICON.png"));
            ImageIcon circularIcon = new ImageIcon(makeRoundedImage(originalImage, 150));
            photoLabel.setIcon(circularIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        photoLabel.setBounds(375, 20, 150, 150);
        mainPanel.add(photoLabel);

        // Profile Box
        JPanel profileBox = new JPanel();
        profileBox.setBackground(Color.WHITE);
        profileBox.setLayout(new BoxLayout(profileBox, BoxLayout.Y_AXIS));
        profileBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 118, 232), 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // Scroll Pane for Profile Box
        JScrollPane scrollPane = new JScrollPane(profileBox);
        scrollPane.setBounds(150, 190, 600, 450); // ↓ reduced height slightly from 530 to 510
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane);

        // Add panels
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Fetch and display data
        fetchProfileData(profileBox);
    }

    private void fetchProfileData(JPanel profileBox) {
        Connection con = null;
        PreparedStatement pstUser = null, pstAddress = null, pstContact = null, pstEmail = null, pstAdmin = null, pstRole = null;
        ResultSet rsUser = null, rsAddress = null, rsContact = null, rsEmail = null, rsAdmin = null, rsRole = null;

        try {
            con = DatabaseConnection.getConnection();

            String userQuery = "SELECT User_ID, User_Name, Gender, Date_of_Birth FROM User WHERE User_Name = ?";
            pstUser = con.prepareStatement(userQuery);
            pstUser.setString(1, username);
            rsUser = pstUser.executeQuery();

            if (rsUser.next()) {
                int userId = rsUser.getInt("User_ID");
                String userName = rsUser.getString("User_Name");
                String gender = rsUser.getString("Gender");
                Date dob = rsUser.getDate("Date_of_Birth");

                profileBox.add(createProfileHeading("Personal Details"));
                profileBox.add(createProfileItem("Username", userName));
                profileBox.add(createProfileItem("Gender", gender));
                profileBox.add(createProfileItem("Date of Birth", dob.toString()));

                // Address
                String addressQuery = "SELECT Street, City, State, Country FROM User_Address WHERE User_ID = ?";
                pstAddress = con.prepareStatement(addressQuery);
                pstAddress.setInt(1, userId);
                rsAddress = pstAddress.executeQuery();

                if (rsAddress.next()) {
                    String street = rsAddress.getString("Street");
                    String city = rsAddress.getString("City");
                    String state = rsAddress.getString("State");
                    String country = rsAddress.getString("Country");

                    profileBox.add(createProfileHeading("Address"));
                    profileBox.add(createProfileItem("Street", street));
                    profileBox.add(createProfileItem("City", city));
                    profileBox.add(createProfileItem("State", state));
                    profileBox.add(createProfileItem("Country", country));
                }

                // Contact
                String contactQuery = "SELECT Phone_Number FROM User_Contact WHERE User_ID = ?";
                pstContact = con.prepareStatement(contactQuery);
                pstContact.setInt(1, userId);
                rsContact = pstContact.executeQuery();

                profileBox.add(createProfileHeading("Phone Numbers"));
                while (rsContact.next()) {
                    String phone = rsContact.getString("Phone_Number");
                    profileBox.add(createProfileItem("Phone", phone));
                }

                // Emails
                String emailQuery = "SELECT Email FROM User_Email WHERE User_ID = ?";
                pstEmail = con.prepareStatement(emailQuery);
                pstEmail.setInt(1, userId);
                rsEmail = pstEmail.executeQuery();

                profileBox.add(createProfileHeading("Emails"));
                while (rsEmail.next()) {
                    String email = rsEmail.getString("Email");
                    profileBox.add(createProfileItem("Email", email));
                }

                // Admin
                String adminQuery = "SELECT Role_ID, Department_ID FROM Admins WHERE User_ID = ?";
                pstAdmin = con.prepareStatement(adminQuery);
                pstAdmin.setInt(1, userId);
                rsAdmin = pstAdmin.executeQuery();

                if (rsAdmin.next()) {
                    int roleId = rsAdmin.getInt("Role_ID");

                    String roleQuery = "SELECT Role_Name, Salary FROM Roles WHERE Role_ID = ?";
                    pstRole = con.prepareStatement(roleQuery);
                    pstRole.setInt(1, roleId);
                    rsRole = pstRole.executeQuery();

                    if (rsRole.next()) {
                        String roleName = rsRole.getString("Role_Name");
                        double salary = rsRole.getDouble("Salary");

                        profileBox.add(createProfileHeading("Role Details"));
                        profileBox.add(createProfileItem("Role", roleName));
                        profileBox.add(createProfileItem("Salary", String.valueOf(salary)));
                    }
                }

                // 🛑 Very Important - Add empty rigid space at bottom
                profileBox.add(Box.createRigidArea(new Dimension(0, 30)));

                profileBox.revalidate();
                profileBox.repaint();

            } else {
                profileBox.add(createProfileItem("Error", "User not found!"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching profile data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rsUser != null) rsUser.close();
                if (rsAddress != null) rsAddress.close();
                if (rsContact != null) rsContact.close();
                if (rsEmail != null) rsEmail.close();
                if (rsAdmin != null) rsAdmin.close();
                if (rsRole != null) rsRole.close();
                if (pstUser != null) pstUser.close();
                if (pstAddress != null) pstAddress.close();
                if (pstContact != null) pstContact.close();
                if (pstEmail != null) pstEmail.close();
                if (pstAdmin != null) pstAdmin.close();
                if (pstRole != null) pstRole.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JLabel createProfileHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(new Color(45, 118, 232));
        label.setBorder(BorderFactory.createEmptyBorder(20, 5, 10, 5));
        return label;
    }

    private JLabel createProfileItem(String key, String value) {
        JLabel label = new JLabel(key + ": " + value);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(Color.DARK_GRAY);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    private Image makeRoundedImage(BufferedImage image, int size) {
        BufferedImage masked = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = masked.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, size, size);
        g2.setClip(circle);
        g2.drawImage(image.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
        g2.dispose();
        return masked;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProfileFeature("adminUsername").setVisible(true);
        });
    }
}
