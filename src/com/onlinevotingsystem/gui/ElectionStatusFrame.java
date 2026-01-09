package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.services.ElectionStatusService;
import com.onlinevotingsystem.services.ElectionService;
import javax.swing.table.DefaultTableCellRenderer; // এই লাইনটি যোগ করুন
import javax.swing.table.TableCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ElectionStatusFrame extends JFrame {
    private User currentUser;
    private JTable electionsTable;
    private JButton refreshButton, viewDetailsButton, changeStatusButton, backButton;
    private JLabel summaryLabel;
    private JProgressBar overallProgressBar;

    public ElectionStatusFrame(User user) {
        this.currentUser = user;
        initializeUI();
        loadElectionsData();
    }

    private void initializeUI() {
        setTitle("Election Status Management - Online Voting System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Election Status Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Summary panel in header
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(new Color(0, 70, 140));

        summaryLabel = new JLabel("Loading...");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryLabel.setForeground(Color.WHITE);

        summaryPanel.add(summaryLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(summaryPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Progress Bar
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(Color.WHITE);
        progressPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel progressLabel = new JLabel("Overall System Status:");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setStringPainted(true);
        overallProgressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        overallProgressBar.setBackground(new Color(233, 236, 239));
        overallProgressBar.setForeground(new Color(40, 167, 69));

        progressPanel.add(progressLabel, BorderLayout.WEST);
        progressPanel.add(overallProgressBar, BorderLayout.CENTER);

        mainPanel.add(progressPanel, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        refreshButton = new JButton("🔄 Refresh");
        refreshButton.setBackground(new Color(0, 120, 215));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.addActionListener(e -> loadElectionsData());

        viewDetailsButton = new JButton("📊 View Details");
        viewDetailsButton.setBackground(new Color(40, 167, 69));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewDetailsButton.addActionListener(e -> viewElectionDetails());

        changeStatusButton = new JButton("⚙️ Change Status");
        changeStatusButton.setBackground(new Color(255, 193, 7));
        changeStatusButton.setForeground(Color.BLACK);
        changeStatusButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changeStatusButton.setEnabled(currentUser.getRole().equals("ADMIN"));
        changeStatusButton.addActionListener(e -> changeElectionStatus());

        controlPanel.add(refreshButton);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(viewDetailsButton);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(changeStatusButton);

        mainPanel.add(controlPanel, BorderLayout.CENTER);

        // Elections Table
        String[] columnNames = {"ID", "Title", "Status", "Start Date", "End Date", "Candidates", "Votes", "Progress"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) return Integer.class; // Progress column
                return String.class;
            }
        };

        electionsTable = new JTable(model);
        electionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        electionsTable.setRowHeight(30);
        electionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        electionsTable.getTableHeader().setBackground(new Color(248, 249, 250));
        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer for status column
        electionsTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        JScrollPane tableScrollPane = new JScrollPane(electionsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.addActionListener(e -> {
            if (currentUser.getRole().equals("ADMIN")) {
                new AdminDashboard(currentUser).setVisible(true);
            } else {
                new DashboardFrame(currentUser).setVisible(true);
            }
            dispose();
        });

        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadElectionsData() {
        refreshButton.setText("Loading...");
        refreshButton.setEnabled(false);

        new Thread(() -> {
            try {
                // Update statuses first
                ElectionStatusService.updateAllElectionStatuses();

                // Get all elections with status
                List<Map<String, Object>> elections = ElectionStatusService.getAllElectionsWithStatus();

                SwingUtilities.invokeLater(() -> {
                    updateElectionsTable(elections);
                    updateSummary(elections);
                    refreshButton.setText("🔄 Refresh");
                    refreshButton.setEnabled(true);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error loading elections: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    refreshButton.setText("🔄 Refresh");
                    refreshButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void updateElectionsTable(List<Map<String, Object>> elections) {
        DefaultTableModel model = (DefaultTableModel) electionsTable.getModel();
        model.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Map<String, Object> election : elections) {
            String startDate = dateFormat.format((java.util.Date) election.get("startDate"));
            String endDate = dateFormat.format((java.util.Date) election.get("endDate"));

            Object[] rowData = {
                    election.get("id"),
                    election.get("title"),
                    election.get("status") + " " + election.get("statusIcon"),
                    startDate,
                    endDate,
                    election.get("candidateCount") + " candidates",
                    election.get("voteCount") + " votes",
                    calculateProgress(election)
            };
            model.addRow(rowData);
        }
    }

    private int calculateProgress(Map<String, Object> election) {
        String status = (String) election.get("status");
        switch (status) {
            case "UPCOMING": return 0;
            case "ONGOING": return 50;
            case "COMPLETED": return 100;
            case "CANCELLED": return 0;
            default: return 0;
        }
    }

    private void updateSummary(List<Map<String, Object>> elections) {
        int total = elections.size();
        long upcoming = elections.stream().filter(e -> "UPCOMING".equals(e.get("status"))).count();
        long ongoing = elections.stream().filter(e -> "ONGOING".equals(e.get("status"))).count();
        long completed = elections.stream().filter(e -> "COMPLETED".equals(e.get("status"))).count();
        long cancelled = elections.stream().filter(e -> "CANCELLED".equals(e.get("status"))).count();

        String summary = String.format("Total: %d | ⏰ Upcoming: %d | ✅ Ongoing: %d | 🏁 Completed: %d | ❌ Cancelled: %d",
                total, upcoming, ongoing, completed, cancelled);

        summaryLabel.setText(summary);

        // Update overall progress
        int progress = total > 0 ? (int) ((completed * 100) / total) : 0;
        overallProgressBar.setValue(progress);
        overallProgressBar.setString(progress + "% Complete");
    }

    private void viewElectionDetails() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an election first!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int electionId = (int) electionsTable.getValueAt(selectedRow, 0);
        Map<String, Object> statusInfo = ElectionStatusService.getElectionStatus(electionId);

        // Create details dialog
        JDialog detailsDialog = new JDialog(this, "Election Details", true);
        detailsDialog.setSize(500, 400);
        detailsDialog.setLocationRelativeTo(this);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Status overview
        JPanel statusPanel = createStatusPanel(statusInfo);
        detailsPanel.add(statusPanel, BorderLayout.NORTH);

        // Timeline
        List<Map<String, Object>> timeline = ElectionStatusService.getElectionTimeline(electionId);
        JPanel timelinePanel = createTimelinePanel(timeline);
        detailsPanel.add(new JScrollPane(timelinePanel), BorderLayout.CENTER);

        detailsDialog.add(detailsPanel);
        detailsDialog.setVisible(true);
    }

    private JPanel createStatusPanel(Map<String, Object> statusInfo) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Status Overview"));

        String[] labels = {"Title", "Status", "Start Date", "End Date", "Total Votes", "Candidates", "Progress", "Description"};
        Object[] values = {
                statusInfo.get("title"),
                statusInfo.get("status") + " " + getStatusIcon((String) statusInfo.get("status")),
                statusInfo.get("startDate"),
                statusInfo.get("endDate"),
                statusInfo.get("totalVotes"),
                statusInfo.get("candidateCount"),
                String.format("%.1f%%", statusInfo.get("progress")),
                statusInfo.get("description")
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            JLabel value = new JLabel(String.valueOf(values[i]));
            value.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            panel.add(label);
            panel.add(value);
        }

        return panel;
    }

    private JPanel createTimelinePanel(List<Map<String, Object>> timeline) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Election Timeline"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Map<String, Object> event : timeline) {
            JPanel eventPanel = new JPanel(new BorderLayout());
            eventPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            eventPanel.setMaximumSize(new Dimension(400, 50));

            JLabel iconLabel = new JLabel((String) event.get("icon"));
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

            JLabel eventLabel = new JLabel((String) event.get("description"));
            eventLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            JLabel timeLabel = new JLabel(dateFormat.format((java.util.Date) event.get("timestamp")));
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            timeLabel.setForeground(Color.GRAY);

            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.add(eventLabel, BorderLayout.CENTER);
            textPanel.add(timeLabel, BorderLayout.SOUTH);

            eventPanel.add(iconLabel, BorderLayout.WEST);
            eventPanel.add(textPanel, BorderLayout.CENTER);
            eventPanel.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.EAST);

            panel.add(eventPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        return panel;
    }

    private void changeElectionStatus() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an election first!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int electionId = (int) electionsTable.getValueAt(selectedRow, 0);
        String currentStatus = ((String) electionsTable.getValueAt(selectedRow, 2)).split(" ")[0];

        // Create status change dialog
        JDialog statusDialog = new JDialog(this, "Change Election Status", true);
        statusDialog.setSize(400, 300);
        statusDialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel currentStatusLabel = new JLabel("Current Status: " + currentStatus);
        currentStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel newStatusLabel = new JLabel("New Status:");
        String[] statusOptions = {"UPCOMING", "ONGOING", "COMPLETED", "CANCELLED"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setSelectedItem(currentStatus);

        JLabel reasonLabel = new JLabel("Reason for change:");
        JTextArea reasonArea = new JTextArea(3, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        JButton applyButton = new JButton("Apply Change");
        applyButton.setBackground(new Color(40, 167, 69));
        applyButton.setForeground(Color.WHITE);
        applyButton.addActionListener(e -> {
            String newStatus = (String) statusComboBox.getSelectedItem();
            String reason = reasonArea.getText().trim();

            if (reason.isEmpty()) {
                reason = "Status changed by admin";
            }

            boolean success = ElectionStatusService.setElectionStatus(electionId, newStatus, reason);
            if (success) {
                JOptionPane.showMessageDialog(statusDialog,
                        "Status changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                statusDialog.dispose();
                loadElectionsData();
            } else {
                JOptionPane.showMessageDialog(statusDialog,
                        "Failed to change status!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(currentStatusLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(newStatusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(reasonLabel);
        formPanel.add(new JScrollPane(reasonArea));
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(applyButton);

        dialogPanel.add(formPanel, BorderLayout.CENTER);
        statusDialog.add(dialogPanel);
        statusDialog.setVisible(true);
    }

    private String getStatusIcon(String status) {
        switch (status) {
            case "UPCOMING": return "⏰";
            case "ONGOING": return "✅";
            case "COMPLETED": return "🏁";
            case "CANCELLED": return "❌";
            default: return "❓";
        }
    }

    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof String) {
                String status = (String) value;
                if (status.contains("UPCOMING")) {
                    c.setBackground(new Color(255, 243, 205)); // Light orange
                    c.setForeground(new Color(133, 100, 4));
                } else if (status.contains("ONGOING")) {
                    c.setBackground(new Color(212, 237, 218)); // Light green
                    c.setForeground(new Color(21, 87, 36));
                } else if (status.contains("COMPLETED")) {
                    c.setBackground(new Color(209, 236, 241)); // Light blue
                    c.setForeground(new Color(12, 84, 96));
                } else if (status.contains("CANCELLED")) {
                    c.setBackground(new Color(248, 215, 218)); // Light red
                    c.setForeground(new Color(114, 28, 36));
                }
            }

            return c;
        }
    }

    public static void main(String[] args) {
        User adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setRole("ADMIN");

        SwingUtilities.invokeLater(() -> {
            new ElectionStatusFrame(adminUser).setVisible(true);
        });
    }
}