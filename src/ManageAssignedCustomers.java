import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

public class ManageAssignedCustomers extends JFrame {
    private int loggedInUserId;
    private JPanel mainPanel;
    private JPanel customerListPanel;
    private JTextField searchField;

    public ManageAssignedCustomers(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        this.mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(230, 230, 255));

        // --- Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(230, 230, 255));

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Find");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Center Container Panel ---
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(new Color(230, 230, 255));

        JPanel squareContainer = new JPanel(new BorderLayout());
        squareContainer.setPreferredSize(new Dimension(500, 400));
        squareContainer.setBackground(Color.WHITE);
        squareContainer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 130, 180), 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // --- Title ---
        JLabel title = new JLabel("List of Customers", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(70, 130, 180));
        title.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        squareContainer.add(title, BorderLayout.NORTH);

        // --- Customer List Panel ---
        customerListPanel = new JPanel();
        customerListPanel.setLayout(new BoxLayout(customerListPanel, BoxLayout.Y_AXIS));
        customerListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(customerListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);

        squareContainer.add(scrollPane, BorderLayout.CENTER);

        centerContainer.add(squareContainer);
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // Load initial customer list
        loadCustomerList("");

        // --- Search Button Logic ---
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            loadCustomerList(searchText);
        });

        // Frame Setup
        setTitle("Manage Assigned Customers");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(mainPanel);
    }

    private void loadCustomerList(String filterText) {
        customerListPanel.removeAll();
        int totalCustomers = 0;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234")) {

            int agentId = -1;
            try (PreparedStatement agentStmt = conn.prepareStatement(
                    "SELECT Agent_ID FROM Agents WHERE User_ID = ?")) {
                agentStmt.setInt(1, loggedInUserId);
                ResultSet agentResult = agentStmt.executeQuery();
                if (agentResult.next()) {
                    agentId = agentResult.getInt("Agent_ID");
                }
            }

            if (agentId == -1) {
                customerListPanel.add(new JLabel("Agent not found for the logged-in user."));
                customerListPanel.revalidate();
                customerListPanel.repaint();
                return;
            }

            try (PreparedStatement countStmt = conn.prepareStatement(
                    "SELECT Total_Customers_Assigned FROM Agent_Customer_Assignment WHERE Agent_ID = ?")) {
                countStmt.setInt(1, agentId);
                ResultSet countResult = countStmt.executeQuery();
                if (countResult.next()) {
                    totalCustomers = countResult.getInt("Total_Customers_Assigned");
                }
            }

            JLabel totalLabel = new JLabel("No. of customers assigned = " + totalCustomers);
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            totalLabel.setForeground(new Color(0, 102, 0));
            totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
            customerListPanel.add(totalLabel);

            String query = "SELECT u.User_Name, c.Customer_ID " +
                    "FROM Customers c JOIN User u ON c.User_ID = u.User_ID " +
                    "WHERE c.Agent_ID = ? AND (u.User_Name LIKE ? OR c.Customer_ID LIKE ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, agentId);
                stmt.setString(2, "%" + filterText + "%");
                stmt.setString(3, "%" + filterText + "%");
                ResultSet rs = stmt.executeQuery();

                int count = 1;
                while (rs.next()) {
                    String name = rs.getString("User_Name");
                    int id = rs.getInt("Customer_ID");

                    JLabel customerLabel = new JLabel(
                            count + ") Name: " + name + " | ID: " + id);
                    customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    customerLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                    customerListPanel.add(customerLabel);
                    count++;
                }

                if (count == 1) {
                    JLabel noneFound = new JLabel("No customers match the search.");
                    noneFound.setForeground(Color.RED);
                    customerListPanel.add(noneFound);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            customerListPanel.add(new JLabel("Error loading customers."));
        }

        customerListPanel.revalidate();
        customerListPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageAssignedCustomers(1).setVisible(true));
    }
}
