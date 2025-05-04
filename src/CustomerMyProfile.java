import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class CustomerMyProfile extends JFrame {

    public CustomerMyProfile(int userId) {
        setTitle("Customer Profile");
        setSize(900, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Header Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(52, 73, 94)); // Deep blue
        topPanel.setPreferredSize(new Dimension(900, 80));
        JLabel titleLabel = new JLabel("MY PROFILE", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        topPanel.add(titleLabel);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(244, 246, 247)); // light gray
        mainPanel.setLayout(null);

        // Profile Image
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

        // Profile Box Panel
        JPanel profileBox = new JPanel();
        profileBox.setBackground(Color.WHITE);
        profileBox.setLayout(new BoxLayout(profileBox, BoxLayout.Y_AXIS));
        profileBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(93, 173, 226), 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // ScrollPane for Profile Box
        JScrollPane scrollPane = new JScrollPane(profileBox);
        scrollPane.setBounds(150, 190, 600, 470);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Fetch and display customer profile data
        fetchCustomerProfile(userId, profileBox);
    }

    private void fetchCustomerProfile(int userId, JPanel profileBox) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234")) {

            String query = """
                SELECT u.User_Name, u.Gender, u.Date_of_Birth, 
                       ua.Street, ua.City, ua.State, ua.Country
                FROM User u
                JOIN Customers cu ON u.User_ID = cu.User_ID
                LEFT JOIN User_Address ua ON u.User_ID = ua.User_ID
                WHERE u.User_ID = ?""";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                profileBox.add(createProfileHeading("Personal Details"));
                profileBox.add(createProfileItem("Name", rs.getString("User_Name")));
                profileBox.add(createProfileItem("Gender", rs.getString("Gender")));
                profileBox.add(createProfileItem("Date of Birth", rs.getDate("Date_of_Birth").toString()));

                profileBox.add(createProfileHeading("Address"));
                profileBox.add(createProfileItem("Street", rs.getString("Street")));
                profileBox.add(createProfileItem("City", rs.getString("City")));
                profileBox.add(createProfileItem("State", rs.getString("State")));
                profileBox.add(createProfileItem("Country", rs.getString("Country")));
            } else {
                profileBox.add(createProfileItem("Error", "Customer not found!"));
                return;
            }
            rs.close();
            ps.close();

            // Fetch multiple phone numbers
            List<String> phoneNumbers = new ArrayList<>();
            String phoneQuery = "SELECT Phone_Number FROM User_Contact WHERE User_ID = ?";
            ps = conn.prepareStatement(phoneQuery);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                phoneNumbers.add(rs.getString("Phone_Number"));
            }
            if (!phoneNumbers.isEmpty()) {
                profileBox.add(createProfileHeading("Phone Numbers"));
                for (int i = 0; i < phoneNumbers.size(); i++) {
                    profileBox.add(createProfileItem("Phone " + (i + 1), phoneNumbers.get(i)));
                }
            }

            // Fetch multiple emails
            List<String> emails = new ArrayList<>();
            String emailQuery = "SELECT Email FROM User_Email WHERE User_ID = ?";
            ps = conn.prepareStatement(emailQuery);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                emails.add(rs.getString("Email"));
            }
            if (!emails.isEmpty()) {
                profileBox.add(createProfileHeading("Emails"));
                for (int i = 0; i < emails.size(); i++) {
                    profileBox.add(createProfileItem("Email " + (i + 1), emails.get(i)));
                }
            }

            rs.close();
            ps.close();

            profileBox.add(Box.createRigidArea(new Dimension(0, 30)));
            profileBox.revalidate();
            profileBox.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching customer profile.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createProfileHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(new Color(25, 13, 74)); // Orange Accent
        label.setBorder(BorderFactory.createEmptyBorder(20, 5, 10, 5));
        return label;
    }

    private JLabel createProfileItem(String key, String value) {
        JLabel label = new JLabel(key + ": " + value);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(52, 73, 94)); // Dark text
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
            new CustomerMyProfile(1).setVisible(true); // Replace 1 with actual customer's user ID
        });
    }
}
