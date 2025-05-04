import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ViewComplaints extends JFrame {

    private JTable complaintsTable;
    private DefaultTableModel model;
    private int loggedInUserId;

    public ViewComplaints(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        setTitle("View Complaints");

        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel();
        model.addColumn("Complaint ID");
        model.addColumn("Customer Name");
        model.addColumn("Complaint Details");
        model.addColumn("Complaint Date");

        complaintsTable = new JTable(model);
        complaintsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        complaintsTable.setRowHeight(35);
        complaintsTable.setGridColor(new Color(220, 220, 220));
        complaintsTable.setSelectionBackground(new Color(63, 81, 181));
        complaintsTable.setSelectionForeground(Color.WHITE);
        complaintsTable.setIntercellSpacing(new Dimension(0, 0));
        complaintsTable.setShowGrid(false);

        complaintsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        complaintsTable.getTableHeader().setBackground(new Color(33, 150, 243));
        complaintsTable.getTableHeader().setForeground(Color.WHITE);
        complaintsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // ======= RESOLVE BUTTON ==========
        JButton resolveButton = new JButton("Resolve Selected Complaint");
        resolveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resolveButton.setBackground(new Color(76, 175, 80));
        resolveButton.setForeground(Color.WHITE);
        resolveButton.setFocusPainted(false);
        resolveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resolveButton.setPreferredSize(new Dimension(0, 40));

        resolveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = complaintsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ViewComplaints.this, "Please select a complaint to resolve.", "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Object complaintIdObj = model.getValueAt(selectedRow, 0);
                if (!(complaintIdObj instanceof Integer)) {
                    JOptionPane.showMessageDialog(ViewComplaints.this, "Invalid Complaint ID format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int complaintId = (Integer) complaintIdObj;

                int confirm = JOptionPane.showConfirmDialog(ViewComplaints.this,
                        "Are you sure you want to mark this complaint as resolved?",
                        "Confirm", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    resolveComplaint(complaintId, selectedRow);
                }
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(resolveButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        loadComplaints();

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void loadComplaints() {
        Connection con = null;
        PreparedStatement agentStmt = null;
        PreparedStatement complaintStmt = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnection.getConnection();

            String agentQuery = "SELECT Agent_ID FROM Agents WHERE User_ID = ?";
            agentStmt = con.prepareStatement(agentQuery);
            agentStmt.setInt(1, loggedInUserId);
            rs = agentStmt.executeQuery();

            int agentId = -1;
            if (rs.next()) {
                agentId = rs.getInt("Agent_ID");
            } else {
                JOptionPane.showMessageDialog(this, "No agent found for this user.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            rs.close();
            String complaintsQuery = "SELECT c.Complaint_ID, u.User_Name AS Customer_Name, c.Complaint_Details, c.Complaint_Date " +
                    "FROM Complaints c " +
                    "JOIN Customers cust ON c.Customer_ID = cust.Customer_ID " +
                    "JOIN User u ON cust.User_ID = u.User_ID " +
                    "WHERE c.Agent_ID = ? " +
                    "ORDER BY c.Complaint_Date DESC";
            complaintStmt = con.prepareStatement(complaintsQuery);
            complaintStmt.setInt(1, agentId);
            rs = complaintStmt.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                int complaintId = rs.getInt("Complaint_ID");
                String customerName = rs.getString("Customer_Name");
                String complaintDetails = rs.getString("Complaint_Details");
                Date complaintDate = rs.getDate("Complaint_Date");

                model.addRow(new Object[]{complaintId, customerName, complaintDetails, complaintDate});
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No complaints found for this agent.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading complaints: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (agentStmt != null) agentStmt.close();
                if (complaintStmt != null) complaintStmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing resources: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resolveComplaint(int complaintId, int rowIndex) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DatabaseConnection.getConnection();

            String deleteQuery = "DELETE FROM Complaints WHERE Complaint_ID = ?";
            stmt = con.prepareStatement(deleteQuery);
            stmt.setInt(1, complaintId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                model.removeRow(rowIndex);
                JOptionPane.showMessageDialog(this, "Complaint marked as resolved and removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No complaint was deleted. Check if the Complaint ID exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error resolving complaint: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing resources: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewComplaints(201).setVisible(true));
    }
}
