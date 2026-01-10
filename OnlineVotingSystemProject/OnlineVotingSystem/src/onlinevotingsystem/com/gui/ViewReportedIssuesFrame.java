package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Issue;
import com.onlinevotingsystem.services.IssueService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ViewReportedIssuesFrame extends JFrame {
    private User currentUser;
    private JList<String> issuesList;
    private DefaultListModel<String> listModel;
    private JTextArea detailsArea;
    private List<Issue> issues;

    public ViewReportedIssuesFrame(User user) {
        this.currentUser = user;
        initializeUI();
        loadIssues();
    }

    private void initializeUI() {
        setTitle("My Reported Issues - Online Voting System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 70, 140));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("My Reported Issues");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel - Split view
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setBackground(Color.WHITE);

        // Left Panel - Issues List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 10));

        JLabel listLabel = new JLabel("Your Reported Issues:");
        listLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        leftPanel.add(listLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        issuesList = new JList<>(listModel);
        issuesList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        issuesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issuesList.addListSelectionListener(e -> displayIssueDetails());

        JScrollPane listScrollPane = new JScrollPane(issuesList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        // Right Panel - Issue Details
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(20, 10, 20, 20));

        JLabel detailsLabel = new JLabel("Issue Details:");
        detailsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(detailsLabel, BorderLayout.NORTH);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsArea.setBackground(new Color(248, 249, 250));
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Back Button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(backButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);
    }

    private void loadIssues() {
        issues = IssueService.getIssuesByUser(currentUser.getId());
        listModel.clear();

        for (Issue issue : issues) {
            String statusIcon = getStatusIcon(issue.getStatus());
            String displayText = String.format("%s %s - %s",
                    statusIcon, issue.getTitle(), issue.getStatus());
            listModel.addElement(displayText);
        }

        if (issues.isEmpty()) {
            detailsArea.setText("You haven't reported any issues yet.\n\n" +
                    "Use the 'Report Issue' feature to report any problems " +
                    "you encounter while using the system.");
        }
    }

    private void displayIssueDetails() {
        int selectedIndex = issuesList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < issues.size()) {
            Issue issue = issues.get(selectedIndex);

            StringBuilder details = new StringBuilder();
            details.append("📋 ISSUE DETAILS\n");
            details.append("═══════════════════════════════════════\n");
            details.append("Title: ").append(issue.getTitle()).append("\n");
            details.append("Description: ").append(issue.getDescription()).append("\n\n");

            details.append("📊 BASIC INFORMATION\n");
            details.append("─────────────────────────────────────\n");
            details.append("Type: ").append(issue.getType()).append("\n");
            details.append("Priority: ").append(issue.getPriority()).append("\n");
            details.append("Status: ").append(issue.getStatus()).append("\n");

            if (issue.getElectionTitle() != null) {
                details.append("Related Election: ").append(issue.getElectionTitle()).append("\n");
            }

            details.append("Reported: ").append(issue.getCreatedAt()).append("\n\n");

            if (issue.getResolutionNote() != null && !issue.getResolutionNote().isEmpty()) {
                details.append("✅ RESOLUTION NOTE\n");
                details.append("─────────────────────────────────────\n");
                details.append(issue.getResolutionNote()).append("\n\n");

                if (issue.getResolvedAt() != null) {
                    details.append("Resolved: ").append(issue.getResolvedAt()).append("\n");
                }
            }

            detailsArea.setText(details.toString());
        }
    }

    private String getStatusIcon(String status) {
        switch (status) {
            case "PENDING": return "⏳";
            case "IN_PROGRESS": return "🔧";
            case "RESOLVED": return "✅";
            case "CLOSED": return "🔒";
            default: return "📄";
        }
    }

    public static void main(String[] args) {
        User testUser = new User();
        testUser.setId(2);
        testUser.setName("Test User");

        SwingUtilities.invokeLater(() -> {
            new ViewReportedIssuesFrame(testUser).setVisible(true);
        });
    }
}