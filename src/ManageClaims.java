import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ManageClaims extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private int agentId;
    private Connection conn;

    public ManageClaims(int loggedInUserId) {
        setTitle("Manage Claims");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set modern UI theme
        UIManager.put("Table.alternateRowColor", new Color(245, 245, 245));
        UIManager.put("Table.background", Color.WHITE);

        conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        agentId = fetchAgentId(loggedInUserId);
        if (agentId == -1) {
            JOptionPane.showMessageDialog(this, "Agent ID not found for the given user.");
            return;
        }

        // Table column headers
        tableModel = new DefaultTableModel(new String[]{
                "Claim ID", "Customer ID", "Policy ID", "Amount",
                "Date", "Reason", "Status", "Action"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(60, 120, 180));
        table.getTableHeader().setForeground(Color.WHITE);

        // Column width customization
        table.getColumnModel().getColumn(7).setPreferredWidth(120);
        table.setSelectionBackground(new Color(200, 230, 255));
        table.setGridColor(new Color(230, 230, 230));

        table.getColumn("Action").setCellEditor(new ActionDropdownEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        loadPendingClaims();
    }

    private int fetchAgentId(int userId) {
        try {
            String query = "SELECT Agent_ID FROM Agents WHERE User_ID = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Agent_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void loadPendingClaims() {
        try {
            String query = "SELECT pc.Pending_Claim_ID, pc.Customer_ID, pc.Policy_ID, pc.Claim_Amount, " +
                    "pc.Claim_Date, cr.Reason, pc.Status " +
                    "FROM PendingClaims pc " +
                    "JOIN Customers c ON pc.Customer_ID = c.Customer_ID " +
                    "JOIN ClaimReasons cr ON pc.Reason_ID = cr.Reason_ID " +
                    "WHERE c.Agent_ID = ? AND pc.Status = 'Pending'";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, agentId);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("Pending_Claim_ID"));
                row.add(rs.getInt("Customer_ID"));
                row.add(rs.getInt("Policy_ID"));
                row.add(rs.getDouble("Claim_Amount"));
                row.add(rs.getDate("Claim_Date"));
                row.add(rs.getString("Reason"));
                row.add(rs.getString("Status"));
                row.add("Select");
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class ActionDropdownEditor extends DefaultCellEditor {
        private JComboBox<String> comboBox;
        private int selectedRow = -1;

        public ActionDropdownEditor() {
            super(new JComboBox<>());
            comboBox = new JComboBox<>(new String[]{"Select", "Accept", "Reject"});
            comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            comboBox.setBackground(new Color(255, 255, 255));
            comboBox.setForeground(new Color(33, 33, 33));

            comboBox.addActionListener(e -> handleAction((String) comboBox.getSelectedItem()));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            comboBox.setSelectedItem("Select");
            return comboBox;
        }

        private void handleAction(String action) {
            if (selectedRow < 0 || "Select".equals(action)) return;

            int pendingClaimId = (int) tableModel.getValueAt(selectedRow, 0);
            int policyId = (int) tableModel.getValueAt(selectedRow, 2);
            double claimAmount = (double) tableModel.getValueAt(selectedRow, 3);
            Date claimDate = (Date) tableModel.getValueAt(selectedRow, 4);
            String reason = (String) tableModel.getValueAt(selectedRow, 5);

            try {
                if (action.equals("Accept")) {
                    conn.setAutoCommit(false);

                    String reasonQuery = "SELECT Reason_ID FROM ClaimReasons WHERE Reason = ?";
                    PreparedStatement reasonPs = conn.prepareStatement(reasonQuery);
                    reasonPs.setString(1, reason);
                    ResultSet reasonRs = reasonPs.executeQuery();
                    int reasonId = -1;
                    if (reasonRs.next()) {
                        reasonId = reasonRs.getInt("Reason_ID");
                    } else {
                        JOptionPane.showMessageDialog(null, "Reason not found.");
                        conn.rollback();
                        return;
                    }

                    String insertQuery = "INSERT INTO Claims (Policy_ID, Claim_Amount, Claim_Date, Reason_ID) " +
                            "VALUES (?, ?, ?, ?)";
                    PreparedStatement insertPs = conn.prepareStatement(insertQuery);
                    insertPs.setInt(1, policyId);
                    insertPs.setDouble(2, claimAmount);
                    insertPs.setDate(3, new java.sql.Date(claimDate.getTime()));
                    insertPs.setInt(4, reasonId);
                    insertPs.executeUpdate();

                    String deleteQuery = "DELETE FROM PendingClaims WHERE Pending_Claim_ID = ?";
                    PreparedStatement deletePs = conn.prepareStatement(deleteQuery);
                    deletePs.setInt(1, pendingClaimId);
                    deletePs.executeUpdate();

                    conn.commit();
                    conn.setAutoCommit(true);

                    JOptionPane.showMessageDialog(null, "✅ Claim accepted.");
                } else if (action.equals("Reject")) {
                    String deleteQuery = "DELETE FROM PendingClaims WHERE Pending_Claim_ID = ?";
                    PreparedStatement deletePs = conn.prepareStatement(deleteQuery);
                    deletePs.setInt(1, pendingClaimId);
                    deletePs.executeUpdate();

                    JOptionPane.showMessageDialog(null, "❌ Claim rejected.");
                }

                loadPendingClaims();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "⚠ Error processing claim.");
            }
        }

        @Override
        public Object getCellEditorValue() {
            return comboBox.getSelectedItem();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int loggedInUserId = 1; // Replace with actual login
            ManageClaims mc = new ManageClaims(loggedInUserId);
            mc.setVisible(true);
        });
    }
}
