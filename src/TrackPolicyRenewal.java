import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class TrackPolicyRenewal extends JFrame {
    private int loggedInUserId;
    private int agentId;
    private DefaultTableModel model;
    private JTable renewalRequestsTable;
    private JButton btnAcceptRequest;

    public TrackPolicyRenewal(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        this.agentId = getAgentIdFromUserId(loggedInUserId);

        setTitle("📋 Track Policy Renewal");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 248, 250));
        setLayout(new BorderLayout(15, 10));

        // Heading Panel
        JLabel titleLabel = new JLabel("Policy Renewal Requests", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        titleLabel.setForeground(new Color(33, 37, 41));
        add(titleLabel, BorderLayout.NORTH);

        // Table and Scroll
        model = new DefaultTableModel();
        model.addColumn("Request ID");
        model.addColumn("Customer ID");
        model.addColumn("Policy ID");
        model.addColumn("Request Date");
        model.addColumn("Status");

        renewalRequestsTable = new JTable(model);
        renewalRequestsTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        renewalRequestsTable.setRowHeight(30);
        renewalRequestsTable.setFillsViewportHeight(true);
        renewalRequestsTable.setGridColor(new Color(230, 230, 230));
        renewalRequestsTable.setSelectionBackground(new Color(209, 232, 255));
        renewalRequestsTable.setSelectionForeground(Color.BLACK);
        renewalRequestsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        renewalRequestsTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane tableScrollPane = new JScrollPane(renewalRequestsTable);
        tableScrollPane.setBorder(new CompoundBorder(
                new EmptyBorder(10, 15, 10, 15),
                new LineBorder(new Color(200, 200, 200), 1)
        ));

        add(tableScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setBackground(new Color(245, 248, 250));

        btnAcceptRequest = new JButton("✅ Accept Selected Request");
        btnAcceptRequest.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        btnAcceptRequest.setBackground(new Color(25, 135, 84)); // Bootstrap green
        btnAcceptRequest.setForeground(Color.WHITE);
        btnAcceptRequest.setFocusPainted(false);
        btnAcceptRequest.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcceptRequest.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(25, 135, 84).darker(), 1),
                new EmptyBorder(10, 20, 10, 20)
        ));
        btnAcceptRequest.addActionListener(e -> acceptRenewalRequest());

        bottomPanel.add(btnAcceptRequest);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load Data
        loadRenewalRequests();
    }

    private int getAgentIdFromUserId(int userId) {
        int agentId = -1;
        String query = "SELECT Agent_ID FROM Agents WHERE User_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                agentId = rs.getInt("Agent_ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return agentId;
    }

    private void loadRenewalRequests() {
        String query = "SELECT * FROM RenewalRequests WHERE Agent_ID = ? AND Status = 'Pending'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, agentId);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0); // Clear old rows

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Request_ID"),
                        rs.getInt("Customer_ID"),
                        rs.getInt("Policy_ID"),
                        rs.getDate("Request_Date"),
                        rs.getString("Status")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void acceptRenewalRequest() {
        int selectedRow = renewalRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a request to accept.");
            return;
        }

        int requestId = (int) model.getValueAt(selectedRow, 0);
        int policyId = (int) model.getValueAt(selectedRow, 2);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Insert renewal
            String insertQuery = "INSERT INTO PolicyRenewal (Policy_ID, Renewal_Date) VALUES (?, CURDATE())";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, policyId);
                insertStmt.executeUpdate();
            }

            // Delete request
            String deleteQuery = "DELETE FROM RenewalRequests WHERE Request_ID = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, requestId);
                deleteStmt.executeUpdate();
            }

            loadRenewalRequests(); // Refresh table
            JOptionPane.showMessageDialog(this, "✅ Renewal request accepted.");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error accepting renewal request.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TrackPolicyRenewal(1).setVisible(true);
        });
    }
}
