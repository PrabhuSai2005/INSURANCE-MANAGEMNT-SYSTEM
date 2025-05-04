import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;

public class ViewProfile extends JFrame {

    public ViewProfile(int userId) {
        setTitle("Agent Profile");
        setSize(900, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Header Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(5, 44, 76));
        topPanel.setPreferredSize(new Dimension(900, 80));
        JLabel titleLabel = new JLabel("AGENT PROFILE", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        topPanel.add(titleLabel);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(230, 230, 250));
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
                BorderFactory.createLineBorder(new Color(45, 118, 232), 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // ScrollPane for Profile Box
        JScrollPane scrollPane = new JScrollPane(profileBox);
        scrollPane.setBounds(150, 190, 600, 450);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Fetch and display agent profile data
        fetchAgentProfile(userId, profileBox);
    }

    private void fetchAgentProfile(int userId, JPanel profileBox) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234")) {

            String query = """
                SELECT u.User_Name, u.Gender, u.Date_of_Birth, 
                       ua.Street, ua.City, ua.State, ua.Country, 
                       c.Phone_Number, e.Email
                FROM User u
                JOIN Agents a ON u.User_ID = a.User_ID
                LEFT JOIN User_Address ua ON u.User_ID = ua.User_ID
                LEFT JOIN User_Email e ON u.User_ID = e.User_ID
                LEFT JOIN User_Contact c ON u.User_ID = c.User_ID
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

                profileBox.add(createProfileHeading("Phone Numbers"));
                profileBox.add(createProfileItem("Phone", rs.getString("Phone_Number") != null ? rs.getString("Phone_Number") : "N/A"));

                profileBox.add(createProfileHeading("Emails"));
                profileBox.add(createProfileItem("Email", rs.getString("Email") != null ? rs.getString("Email") : "N/A"));

                profileBox.add(Box.createRigidArea(new Dimension(0, 30)));
                profileBox.revalidate();
                profileBox.repaint();
            } else {
                profileBox.add(createProfileItem("Error", "Agent not found!"));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching agent profile.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
            new ViewProfile(1).setVisible(true); // Replace 1 with actual agent's user ID
        });
    }
}
