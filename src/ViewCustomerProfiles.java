import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewCustomerProfiles extends JFrame {
    private int loggedInUserId;
    private JTextField searchField;
    private JButton searchButton;
    private JEditorPane profileDetailsArea;

    public ViewCustomerProfiles(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        setTitle("View Customer Profiles");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for searching by Customer ID
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.add(new JLabel("Enter Customer ID:"));

        searchField = new JTextField(20);
        searchPanel.add(searchField);

        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Area to display customer profile details
        profileDetailsArea = new JEditorPane();
        profileDetailsArea.setContentType("text/html");
        profileDetailsArea.setEditable(false);
        profileDetailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        profileDetailsArea.setBackground(new Color(250, 250, 250));
        add(new JScrollPane(profileDetailsArea), BorderLayout.CENTER);

        // Attach action listener to search button
        searchButton.addActionListener(e -> searchCustomerById());
    }

    private void searchCustomerById() {
        String input = searchField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Customer ID.");
            return;
        }

        int customerId;
        try {
            customerId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Customer ID format.");
            return;
        }

        profileDetailsArea.setText("");

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234")) {

            String query = "SELECT u.User_ID, u.User_Name, c.User_ID AS Customer_User_ID " +
                    "FROM Customers c JOIN User u ON c.User_ID = u.User_ID " +
                    "WHERE c.Customer_ID = ? AND c.Agent_ID = (SELECT Agent_ID FROM Agents WHERE User_ID = ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, customerId);
                stmt.setInt(2, loggedInUserId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int customerUserId = rs.getInt("Customer_User_ID");
                    String customerUsername = rs.getString("User_Name");
                    fetchCustomerDetails(customerId, customerUserId, customerUsername, conn);
                } else {
                    profileDetailsArea.setText("<html><body><p style='color: red;'>No customer found with Customer ID: " + customerId + "</p></body></html>");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while fetching customer details.");
        }
    }

    private void fetchCustomerDetails(int customerId, int customerUserId, String customerUsername, Connection conn) throws SQLException {
        StringBuilder profileDetails = new StringBuilder();
        profileDetails.append("<html><body style='font-family: Arial, sans-serif;'>");
        profileDetails.append("<h2 style='color: #333;'>Customer Name: ").append(customerUsername)
                .append(" (Customer ID: ").append(customerId).append(")</h2>");

        // Address
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT Street, City, State, Country FROM User_Address WHERE User_ID = ?")) {
            stmt.setInt(1, customerUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                profileDetails.append("<div style='background-color: #f1f1f1; padding: 10px; margin-bottom: 20px; border-radius: 5px;'>")
                        .append("<h3 style='color: #0056b3;'>Address:</h3>")
                        .append("<p><b>Street:</b> ").append(rs.getString("Street")).append("</p>")
                        .append("<p><b>City:</b> ").append(rs.getString("City")).append("</p>")
                        .append("<p><b>State:</b> ").append(rs.getString("State")).append("</p>")
                        .append("<p><b>Country:</b> ").append(rs.getString("Country")).append("</p></div>");
            }
        }

        // Contact Numbers
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT Phone_Number FROM User_Contact WHERE User_ID = ?")) {
            stmt.setInt(1, customerUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                profileDetails.append("<div style='background-color: #f1f1f1; padding: 10px; margin-bottom: 20px; border-radius: 5px;'>")
                        .append("<h3 style='color: #0056b3;'>Contact Info:</h3>");
                do {
                    profileDetails.append("<p><b>Phone:</b> ").append(rs.getString("Phone_Number")).append("</p>");
                } while (rs.next());
                profileDetails.append("</div>");
            }
        }

        // Email
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT Email FROM User_Email WHERE User_ID = ?")) {
            stmt.setInt(1, customerUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                profileDetails.append("<div style='background-color: #f1f1f1; padding: 10px; margin-bottom: 20px; border-radius: 5px;'>")
                        .append("<h3 style='color: #0056b3;'>Email:</h3>");
                do {
                    profileDetails.append("<p>").append(rs.getString("Email")).append("</p>");
                } while (rs.next());
                profileDetails.append("</div>");
            }
        }

        // Policies
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT pt.Type_Name, p.Start_Date, pf.Coverage_Amount, pf.Premium_Amount " +
                        "FROM Policies p " +
                        "JOIN PolicyTypes pt ON p.Policy_Type_ID = pt.Policy_Type_ID " +
                        "JOIN PolicyFinancials pf ON p.Policy_ID = pf.Policy_ID " +
                        "WHERE p.Customer_ID = ?")) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                profileDetails.append("<div style='background-color: #f1f1f1; padding: 10px; margin-bottom: 20px; border-radius: 5px;'>")
                        .append("<h3 style='color: #0056b3;'>Policies:</h3>");
                do {
                    profileDetails.append("<p><b>Policy Type:</b> ").append(rs.getString("Type_Name")).append("</p>")
                            .append("<p><b>Start Date:</b> ").append(rs.getDate("Start_Date")).append("</p>")
                            .append("<p><b>Coverage Amount:</b> ").append(rs.getBigDecimal("Coverage_Amount")).append("</p>")
                            .append("<p><b>Premium Amount:</b> ").append(rs.getBigDecimal("Premium_Amount")).append("</p><br>");
                } while (rs.next());
                profileDetails.append("</div>");
            }
        }

        profileDetails.append("</body></html>");
        profileDetailsArea.setText(profileDetails.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewCustomerProfiles(1).setVisible(true));  // Replace 1 with actual logged-in Agent's User_ID
    }
}
