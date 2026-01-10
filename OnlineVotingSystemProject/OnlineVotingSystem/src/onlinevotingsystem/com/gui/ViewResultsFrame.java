package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.services.ResultsService;
import com.onlinevotingsystem.services.ElectionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class ViewResultsFrame extends JFrame {
    private User currentUser;
    private JComboBox<String> electionComboBox;
    private JTextArea resultsArea;
    private JButton viewButton, generateReportButton, backButton;
    private JPanel statsPanel;

    public ViewResultsFrame(User user) {
        this.currentUser = user;
        initializeUI();
        loadCompletedElections();
    }

    private void initializeUI() {
        setTitle("View Election Results - Online Voting System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 70, 140));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Election Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel electionLabel = new JLabel("Select Election:");
        electionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        electionComboBox = new JComboBox<>();
        electionComboBox.setPreferredSize(new Dimension(300, 30));
        electionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        viewButton = new JButton("View Results");
        viewButton.setBackground(new Color(0, 120, 215));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewButton.addActionListener(new ViewResultsListener());

        generateReportButton = new JButton("Generate Report");
        generateReportButton.setBackground(new Color(40, 167, 69));
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        generateReportButton.addActionListener(e -> generateReport());

        controlPanel.add(electionLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(electionComboBox);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(viewButton);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(generateReportButton);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Content Panel - Split into stats and results
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(150);
        splitPane.setBackground(Color.WHITE);

        // Statistics Panel
        statsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        statsPanel.setPreferredSize(new Dimension(800, 150));

        // Initialize with empty stats
        initializeStatsPanel();

        // Results Panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JLabel resultsLabel = new JLabel("Election Results:");
        resultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultsArea.setBackground(new Color(248, 249, 250));
        resultsArea.setText("Select an election and click 'View Results' to see the results.");

        JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
        resultsScrollPane.setPreferredSize(new Dimension(800, 400));

        resultsPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsPanel.add(resultsScrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(statsPanel);
        splitPane.setBottomComponent(resultsPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.addActionListener(e -> {
            if (currentUser != null && currentUser.getRole() != null && "ADMIN".equals(currentUser.getRole())) {
                new AdminDashboard(currentUser).setVisible(true);
            } else {
                new DashboardFrame(currentUser).setVisible(true);
            }
            dispose();
        });

        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initializeStatsPanel() {
        statsPanel.removeAll();

        // Create stat cards
        String[] statLabels = {"Total Votes", "Total Voters", "Candidates", "Turnout %", "Leading Candidate", "Leading Party", "Positions", "Status"};
        String[] statValues = {"0", "0", "0", "0%", "N/A", "N/A", "0", "No Data"};
        Color[] colors = {
                new Color(0, 123, 255), new Color(40, 167, 69),
                new Color(255, 193, 7), new Color(220, 53, 69),
                new Color(111, 66, 193), new Color(253, 126, 20),
                new Color(32, 201, 151), new Color(108, 117, 125)
        };

        for (int i = 0; i < statLabels.length; i++) {
            JPanel statCard = createStatCard(statLabels[i], statValues[i], colors[i]);
            statsPanel.add(statCard);
        }

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(150, 60));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        nameLabel.setForeground(Color.GRAY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(nameLabel);

        return card;
    }

    // ViewResultsFrame.java - loadCompletedElections() method এ এই change করুন
    private void loadCompletedElections() {
        // Use the new method that gets all elections
        String[] elections = ResultsService.getAllElectionsForDisplay();
        electionComboBox.removeAllItems();

        for (String election : elections) {
            electionComboBox.addItem(election);
        }

        if (elections.length == 0) {
            electionComboBox.addItem("No elections available");
            viewButton.setEnabled(false);
            generateReportButton.setEnabled(false);
        } else {
            viewButton.setEnabled(true);
            generateReportButton.setEnabled(true);
        }
    }

    private class ViewResultsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selected = (String) electionComboBox.getSelectedItem();
            if (selected == null || selected.startsWith("No completed")) {
                return;
            }

            // Extract election ID
            int electionId = extractElectionId(selected);
            if (electionId == -1) return;

            // Check if election has results
            if (!ResultsService.hasResults(electionId)) {
                resultsArea.setText("No votes have been cast in this election yet.\nResults will be available after voting.");
                initializeStatsPanel();
                return;
            }

            // Show loading
            viewButton.setText("Loading...");
            viewButton.setEnabled(false);

            // Load results in background
            new Thread(() -> {
                try {
                    List<Map<String, Object>> results = ResultsService.getElectionResults(electionId);
                    List<Map<String, Object>> winners = ResultsService.getWinnersByPosition(electionId);
                    Map<String, Object> stats = ResultsService.getElectionStatistics(electionId);

                    SwingUtilities.invokeLater(() -> {
                        displayResults(results, winners, stats);
                        viewButton.setText("View Results");
                        viewButton.setEnabled(true);
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultsArea.setText("Error loading results: " + ex.getMessage());
                        viewButton.setText("View Results");
                        viewButton.setEnabled(true);
                    });
                }
            }).start();
        }
    }

    private void displayResults(List<Map<String, Object>> results, List<Map<String, Object>> winners, Map<String, Object> stats) {
        if (results.isEmpty()) {
            resultsArea.setText("No results available for this election.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        String electionTitle = (String) results.get(0).get("electionTitle");

        // Header
        sb.append("🗳️ ELECTION RESULTS\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append("Election: ").append(electionTitle).append("\n");
        sb.append("Generated: ").append(new java.util.Date()).append("\n");
        sb.append("═══════════════════════════════════════\n\n");

        // Winners section
        sb.append("🏆 ELECTION WINNERS\n");
        sb.append("─────────────────────────────────────\n");
        for (Map<String, Object> winner : winners) {
            sb.append("🎯 ").append(winner.get("position")).append(": ")
                    .append(winner.get("candidateName")).append(" (")
                    .append(winner.get("party")).append(") - ")
                    .append(winner.get("voteCount")).append(" votes\n");
        }
        sb.append("\n");

        // Detailed results by position
        String currentPosition = "";
        for (Map<String, Object> result : results) {
            String position = (String) result.get("position");
            if (!position.equals(currentPosition)) {
                currentPosition = position;
                sb.append("\n📋 ").append(position.toUpperCase()).append("\n");
                sb.append("─────────────────────────────────────\n");
            }

            boolean isWinner = winners.stream()
                    .anyMatch(w -> w.get("candidateName").equals(result.get("candidateName"))
                            && w.get("position").equals(position));

            String winnerIndicator = isWinner ? "🏆 " : "• ";
            sb.append(winnerIndicator).append(result.get("candidateName")).append(" (")
                    .append(result.get("party")).append("): ")
                    .append(result.get("voteCount")).append(" votes (")
                    .append(result.get("percentage")).append("%)\n");
        }

        resultsArea.setText(sb.toString());
        updateStatsPanel(stats, winners);
    }

    private void updateStatsPanel(Map<String, Object> stats, List<Map<String, Object>> winners) {
        statsPanel.removeAll();

        String leadingCandidate = "N/A";
        String leadingParty = "N/A";
        int positions = winners.size();

        if (!winners.isEmpty()) {
            // Find candidate with most votes
            Map<String, Object> topWinner = winners.stream()
                    .max((w1, w2) -> Integer.compare((int)w1.get("voteCount"), (int)w2.get("voteCount")))
                    .orElse(winners.get(0));

            leadingCandidate = (String) topWinner.get("candidateName");
            leadingParty = (String) topWinner.get("party");
        }

        String[] statLabels = {"Total Votes", "Total Voters", "Candidates", "Turnout %", "Leading Candidate", "Leading Party", "Positions", "Status"};
        String[] statValues = {
                String.valueOf(stats.get("totalVotes")),
                String.valueOf(stats.get("totalVoters")),
                String.valueOf(stats.get("totalCandidates")),
                stats.get("turnoutPercentage") + "%",
                leadingCandidate,
                leadingParty,
                String.valueOf(positions),
                "Completed"
        };

        Color[] colors = {
                new Color(0, 123, 255), new Color(40, 167, 69),
                new Color(255, 193, 7), new Color(220, 53, 69),
                new Color(111, 66, 193), new Color(253, 126, 20),
                new Color(32, 201, 151), new Color(108, 117, 125)
        };

        for (int i = 0; i < statLabels.length; i++) {
            JPanel statCard = createStatCard(statLabels[i], statValues[i], colors[i]);
            statsPanel.add(statCard);
        }

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void generateReport() {
        String selected = (String) electionComboBox.getSelectedItem();
        if (selected == null || selected.startsWith("No completed")) {
            return;
        }

        int electionId = extractElectionId(selected);
        if (electionId == -1) return;

        String report = ResultsService.generateResultsReport(electionId);

        // Show report in dialog
        JTextArea reportArea = new JTextArea(report, 25, 60);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(reportArea);

        JOptionPane.showMessageDialog(this, scrollPane,
                "Election Results Report - ID: " + electionId,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private int extractElectionId(String electionString) {
        try {
            // Format: "ID: 1 - Election Title (25 votes)"
            String[] parts = electionString.split(" - ");
            if (parts.length > 0) {
                String idPart = parts[0].replace("ID: ", "").trim();
                return Integer.parseInt(idPart);
            }
        } catch (Exception e) {
            System.err.println("Error extracting election ID: " + e.getMessage());
        }
        return -1;
    }

    public static void main(String[] args) {
        // Test with dummy user
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setRole("ADMIN");

        SwingUtilities.invokeLater(() -> {
            new ViewResultsFrame(testUser).setVisible(true);
        });
    }
}