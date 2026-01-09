package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Issue;
import com.onlinevotingsystem.services.IssueService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageIssuesFrame extends JFrame {
    private User currentUser;
    private JTable issuesTable;
    private DefaultTableModel tableModel;
    private JButton resolveButton, viewButton, refreshButton, backButton;
    private JComboBox<String> statusFilterComboBox;
    private List<Issue> currentIssues;

    public ManageIssuesFrame(User user) {
        this.currentUser = user;
        initializeUI();
        loadIssues();
    }

    private void initializeUI() {
        setTitle("Manage Voter Issues - Online Voting System");
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

        JLabel titleLabel = new JLabel("Manage Voter Issues");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Statistics Panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setBackground(new Color(0, 70, 140));

        JLabel statsLabel = new JLabel();
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(Color.WHITE);
        statsPanel.add(statsLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterPanel.add(filterLabel);

        String[] statusOptions = {"ALL", "PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED"};
        statusFilterComboBox = new JComboBox<>(statusOptions);
        statusFilterComboBox.addActionListener(e -> filterIssues());
        filterPanel.add(statusFilterComboBox);

        filterPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> loadIssues());
        filterPanel.add(refreshButton);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Create table model
        String[] columnNames = {
                "ID", "User", "Title", "Type", "Priority", "Status", "Created", "Election"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : String.class;
            }
        };

        issuesTable = new JTable(tableModel);
        issuesTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        issuesTable.setRowHeight(25);
        issuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issuesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Set column widths
        issuesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        issuesTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        issuesTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        issuesTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        issuesTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        issuesTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        issuesTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        issuesTable.getColumnModel().getColumn(7).setPreferredWidth(150);

        JScrollPane tableScrollPane = new JScrollPane(issuesTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        viewButton = new JButton("View Details");
        styleButton(viewButton, new Color(0, 123, 255));
        viewButton.setEnabled(false);
        viewButton.addActionListener(e -> viewIssueDetails());

        resolveButton = new JButton("Update Status");
        styleButton(resolveButton, new Color(40, 167, 69));
        resolveButton.setEnabled(false);
        resolveButton.addActionListener(e -> updateIssueStatus());

        backButton = new JButton("Back to Dashboard");
        styleButton(backButton, new Color(108, 117, 125));
        backButton.addActionListener(e -> {
            new AdminDashboard(currentUser).setVisible(true);
            dispose();
        });

        buttonPanel.add(viewButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(resolveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add selection listener to table
        issuesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean rowSelected = issuesTable.getSelectedRow() >= 0;
                    viewButton.setEnabled(rowSelected);
                    resolveButton.setEnabled(rowSelected);
                }
            }
        });

        // Update statistics
        updateStatistics(statsLabel);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
    }

    private void loadIssues() {
        currentIssues = IssueService.getAllIssues();
        filterIssues();
    }

    private void filterIssues() {
        String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
        List<Issue> filteredIssues = currentIssues;

        if (!"ALL".equals(selectedStatus)) {
            filteredIssues = currentIssues.stream()
                    .filter(issue -> selectedStatus.equals(issue.getStatus()))
                    .collect(Collectors.toList());
        }

        populateTable(filteredIssues);
    }

    private void populateTable(List<Issue> issues) {
        tableModel.setRowCount(0);

        for (Issue issue : issues) {
            Object[] rowData = {
                    issue.getId(),
                    issue.getUserName() != null ? issue.getUserName() : "User #" + issue.getUserId(),
                    issue.getTitle(),
                    issue.getType() != null ? issue.getTypeIcon() + " " + issue.getType() : "N/A",
                    getPriorityWithColor(issue),
                    getStatusWithColor(issue),
                    formatDate(issue.getCreatedAt()),
                    issue.getElectionTitle() != null ? issue.getElectionTitle() : "N/A"
            };
            tableModel.addRow(rowData);
        }
    }

    private String getPriorityWithColor(Issue issue) {
        if (issue.getPriority() == null) return "N/A";
        return "<html><span style='color:" + issue.getPriorityColor() + ";'>" +
                issue.getPriority() + "</span></html>";
    }

    private String getStatusWithColor(Issue issue) {
        if (issue.getStatus() == null) return "N/A";
        return "<html><span style='color:" + issue.getStatusColor() + ";'>" +
                issue.getStatus() + "</span></html>";
    }

    private String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new java.text.SimpleDateFormat("MMM dd, HH:mm").format(timestamp);
    }

    private void viewIssueDetails() {
        int selectedRow = issuesTable.getSelectedRow();
        if (selectedRow < 0) return;

        int issueId = (int) tableModel.getValueAt(selectedRow, 0);
        Issue issue = IssueService.getIssueById(issueId);

        if (issue != null) {
            showIssueDetailsDialog(issue);
        }
    }

    // ... বাকি methods (showIssueDetailsDialog, addDetailRow, updateIssueStatus, showStatusUpdateDialog) একই থাকবে ...

    private void showIssueDetailsDialog(Issue issue) {
        JDialog detailsDialog = new JDialog(this, "Issue Details", true);
        detailsDialog.setSize(500, 400);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Issue Details
        addDetailRow(contentPanel, "Issue ID:", String.valueOf(issue.getId()));
        addDetailRow(contentPanel, "User:", issue.getUserName() + " (" + issue.getUserEmail() + ")");
        addDetailRow(contentPanel, "Title:", issue.getTitle());
        addDetailRow(contentPanel, "Type:", issue.getTypeIcon() + " " + issue.getType());
        addDetailRow(contentPanel, "Priority:", getPriorityWithColor(issue));
        addDetailRow(contentPanel, "Status:", getStatusWithColor(issue));
        addDetailRow(contentPanel, "Election:", issue.getElectionTitle() != null ? issue.getElectionTitle() : "N/A");
        addDetailRow(contentPanel, "Created:", formatDate(issue.getCreatedAt()));

        if (issue.getResolvedAt() != null) {
            addDetailRow(contentPanel, "Resolved:", formatDate(issue.getResolvedAt()));
        }

        // Description
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextArea descArea = new JTextArea(issue.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(new Color(248, 249, 250));
        descArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        contentPanel.add(descScroll);

        // Resolution Note (if exists)
        if (issue.getResolutionNote() != null && !issue.getResolutionNote().isEmpty()) {
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            JLabel resLabel = new JLabel("Resolution Note:");
            resLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            resLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(resLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            JTextArea resArea = new JTextArea(issue.getResolutionNote());
            resArea.setEditable(false);
            resArea.setLineWrap(true);
            resArea.setWrapStyleWord(true);
            resArea.setBackground(new Color(230, 255, 230));
            resArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JScrollPane resScroll = new JScrollPane(resArea);
            resScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            contentPanel.add(resScroll);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(closeButton);

        detailsDialog.add(contentPanel, BorderLayout.CENTER);
        detailsDialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel keyLabel = new JLabel(label);
        keyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        keyLabel.setPreferredSize(new Dimension(100, 20));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        rowPanel.add(keyLabel, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.CENTER);
        panel.add(rowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void updateIssueStatus() {
        int selectedRow = issuesTable.getSelectedRow();
        if (selectedRow < 0) return;

        int issueId = (int) tableModel.getValueAt(selectedRow, 0);
        Issue issue = IssueService.getIssueById(issueId);

        if (issue != null) {
            showStatusUpdateDialog(issue);
        }
    }

    private void showStatusUpdateDialog(Issue issue) {
        JDialog statusDialog = new JDialog(this, "Update Issue Status", true);
        statusDialog.setSize(400, 350);
        statusDialog.setLocationRelativeTo(this);
        statusDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Update Status for Issue #" + issue.getId());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Current Status
        JLabel currentStatus = new JLabel("Current Status: " + issue.getStatus());
        currentStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        currentStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(currentStatus);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // New Status
        JLabel statusLabel = new JLabel("New Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusLabel);

        String[] statusOptions = {"PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setSelectedItem(issue.getStatus());
        statusComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        statusComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Resolution Note
        JLabel noteLabel = new JLabel("Resolution Note:");
        noteLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(noteLabel);

        JTextArea noteArea = new JTextArea(3, 30);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        if (issue.getResolutionNote() != null) {
            noteArea.setText(issue.getResolutionNote());
        }
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        noteScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(noteScroll);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(40, 167, 69));
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(e -> {
            String newStatus = (String) statusComboBox.getSelectedItem();
            String resolutionNote = noteArea.getText().trim();

            boolean success = IssueService.updateIssueStatus(
                    issue.getId(), newStatus, resolutionNote, currentUser.getId()
            );

            if (success) {
                JOptionPane.showMessageDialog(statusDialog,
                        "Issue status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadIssues();
                statusDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(statusDialog,
                        "Failed to update issue status!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> statusDialog.dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);

        contentPanel.add(buttonPanel);

        statusDialog.add(contentPanel, BorderLayout.CENTER);
        statusDialog.setVisible(true);
    }





    private void updateStatistics(JLabel statsLabel) {
        try {
            Map<String, Integer> stats = IssueService.getIssueStatistics();

            int total = stats.values().stream().mapToInt(Integer::intValue).sum();
            int pending = stats.getOrDefault("PENDING", 0);
            int inProgress = stats.getOrDefault("IN_PROGRESS", 0);
            int resolved = stats.getOrDefault("RESOLVED", 0);
            int closed = stats.getOrDefault("CLOSED", 0);

            String statsText = String.format(
                    "Total: %d | Pending: %d | In Progress: %d | Resolved: %d | Closed: %d",
                    total, pending, inProgress, resolved, closed
            );
            statsLabel.setText(statsText);
        } catch (Exception e) {
            statsLabel.setText("Statistics: Loading...");
        }
    }

    public static void main(String[] args) {
        // Test method
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.setId(1);
            testUser.setName("Test Admin");
            new ManageIssuesFrame(testUser).setVisible(true);
        });
    }
}