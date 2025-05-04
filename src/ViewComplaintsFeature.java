import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewComplaintsFeature extends JFrame {

    private Connection con;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField agentIdField;
    private JButton fetchButton;
    private JButton resolveButton;

    public ViewComplaintsFeature(Connection con) {
        this.con = con;

        setTitle("View Complaints");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // Light blue background

        // Top panel: title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(new Color(223, 23, 77)); // Red

        JLabel titleLabel = new JLabel("View Complaints");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        // Input panel: agent ID input + buttons
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255));

        JLabel agentLabel = new JLabel("Enter Agent ID:");
        agentLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        agentLabel.setForeground(new Color(223, 23, 77));
        inputPanel.add(agentLabel);

        agentIdField = new JTextField(12);
        agentIdField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        inputPanel.add(agentIdField);

        fetchButton = new JButton("Fetch Complaints");
        fetchButton.setBackground(new Color(223, 23, 77));
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        fetchButton.setFocusPainted(false);
        fetchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inputPanel.add(fetchButton);

        resolveButton = new JButton("Resolve Selected Complaint");
        resolveButton.setBackground(new Color(34, 139, 34)); // Green
        resolveButton.setForeground(Color.WHITE);
        resolveButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        resolveButton.setFocusPainted(false);
        resolveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inputPanel.add(resolveButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(inputPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        // Table for complaints
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Complaint ID", "Customer ID", "Complaint Details", "Complaint Date"});

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(223, 23, 77));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 16));
        header.setBackground(new Color(223, 23, 77));
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Action listeners
        fetchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchComplaints();
            }
        });

        resolveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resolveSelectedComplaint();
            }
        });
    }

    private void fetchComplaints() {
        String agentIdText = agentIdField.getText().trim();

        if (agentIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Agent ID!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int agentId = Integer.parseInt(agentIdText);

            String query = "SELECT Complaint_ID, Customer_ID, Complaint_Details, Complaint_Date FROM Complaints WHERE Agent_ID = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, agentId);

            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0); // Clear old data

            boolean found = false;
            while (rs.next()) {
                found = true;
                int complaintId = rs.getInt("Complaint_ID");
                int customerId = rs.getInt("Customer_ID");
                String details = rs.getString("Complaint_Details");
                Date date = rs.getDate("Complaint_Date");

                tableModel.addRow(new Object[]{complaintId, customerId, details, date});
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No complaints found for Agent ID: " + agentId, "Info", JOptionPane.INFORMATION_MESSAGE);
            }

            ps.close();
            rs.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Agent ID must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching complaints!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resolveSelectedComplaint() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to resolve!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to resolve this complaint?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int complaintId = (int) tableModel.getValueAt(selectedRow, 0);

            try {
                String deleteQuery = "DELETE FROM Complaints WHERE Complaint_ID = ?";
                PreparedStatement ps = con.prepareStatement(deleteQuery);
                ps.setInt(1, complaintId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Complaint resolved and removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to resolve the complaint!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error while resolving complaint!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
