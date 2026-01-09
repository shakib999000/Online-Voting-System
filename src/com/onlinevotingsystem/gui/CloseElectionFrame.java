package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Election;
import com.onlinevotingsystem.services.ElectionService;
import com.onlinevotingsystem.services.VoteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CloseElectionFrame extends JFrame {
    private User currentUser;
    private JComboBox<String> electionComboBox;
    private JTextArea resultArea;
    private JButton closeButton, viewResultsButton, backButton;

    public CloseElectionFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Close Election - Online Voting System");
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
        headerPanel.setBackground(new Color(220, 53, 69));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Close Election");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        contentPanel.setBackground(Color.WHITE);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(255, 243, 205));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel warningLabel = new JLabel("⚠️ Important: Closing an election is irreversible!");
        warningLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        warningLabel.setForeground(new Color(133, 100, 4));

        JLabel infoLabel1 = new JLabel("• Election status will be changed to 'COMPLETED'");
        infoLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel infoLabel2 = new JLabel("• No more votes can be cast in this election");
        infoLabel2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel infoLabel3 = new JLabel("• Final results will be calculated and stored");
        infoLabel3.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        infoPanel.add(warningLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(infoLabel1);
        infoPanel.add(infoLabel2);
        infoPanel.add(infoLabel3);

        contentPanel.add(infoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Election Selection
        JLabel electionLabel = new JLabel("Select Election to Close:");
        electionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        electionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(electionLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        List<Election> closableElections = getClosableElections();
        String[] electionOptions = new String[closableElections.size()];

        for (int i = 0; i < closableElections.size(); i++) {
            Election election = closableElections.get(i);
            electionOptions[i] = "ID: " + election.getId() + " - " + election.getTitle() +
                    " (Votes: " + election.getTotalVotes() + ")";
        }

        electionComboBox = new JComboBox<>(electionOptions);
        electionComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        electionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        electionComboBox.addActionListener(e -> loadElectionStats());
        contentPanel.add(electionComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Results Area
        JLabel resultsLabel = new JLabel("Election Statistics:");
        resultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(resultsLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBackground(new Color(248, 249, 250));
        resultArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        resultScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(resultScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        closeButton = new JButton("Close Election");
        closeButton.setBackground(new Color(220, 53, 69));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setPreferredSize(new Dimension(150, 40));
        closeButton.setEnabled(!closableElections.isEmpty());
        closeButton.addActionListener(e -> closeElection());

        viewResultsButton = new JButton("View Detailed Results");
        viewResultsButton.setBackground(new Color(0, 123, 255));
        viewResultsButton.setForeground(Color.WHITE);
        viewResultsButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        viewResultsButton.setPreferredSize(new Dimension(180, 35));
        viewResultsButton.setEnabled(!closableElections.isEmpty());
        viewResultsButton.addActionListener(e -> viewDetailedResults());

        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(160, 35));
        backButton.addActionListener(e -> {
            new AdminDashboard(currentUser).setVisible(true);
            dispose();
        });

        buttonPanel.add(closeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(viewResultsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        if (!closableElections.isEmpty()) {
            loadElectionStats();
        } else {
            resultArea.setText("No ongoing elections available to close.\n\n" +
                    "Only elections with status 'ONGOING' can be closed.");
        }
    }

    private List<Election> getClosableElections() {
        List<Election> allElections = ElectionService.getAllElections();
        return allElections.stream()
                .filter(election -> "ONGOING".equals(election.getStatus()))
                .toList();
    }

    private void loadElectionStats() {
        int selectedIndex = electionComboBox.getSelectedIndex();
        if (selectedIndex < 0) return;

        List<Election> closableElections = getClosableElections();
        Election selectedElection = closableElections.get(selectedIndex);

        Map<String, Object> stats = ElectionService.getElectionStats(selectedElection.getId());
        var results = VoteService.getElectionResults(selectedElection.getId());

        StringBuilder statsText = new StringBuilder();
        statsText.append("🏛️ ELECTION STATISTICS\n");
        statsText.append("═══════════════════════════════════════\n");
        statsText.append("Title: ").append(selectedElection.getTitle()).append("\n");
        statsText.append("Status: ").append(selectedElection.getStatus()).append("\n");
        statsText.append("Total Candidates: ").append(stats.getOrDefault("totalCandidates", 0)).append("\n");
        statsText.append("Total Votes: ").append(stats.getOrDefault("totalVotes", 0)).append("\n");
        statsText.append("Start Date: ").append(selectedElection.getStartDate()).append("\n");
        statsText.append("End Date: ").append(selectedElection.getEndDate()).append("\n");

        statsText.append("\n📊 ELECTION RESULTS\n");
        statsText.append("═══════════════════════════════════════\n");

        if (results.isEmpty()) {
            statsText.append("No votes cast in this election yet.\n");
        } else {
            Map<String, List<VoteService.CandidateResult>> resultsByPosition = new java.util.HashMap<>();

            for (var result : results) {
                resultsByPosition
                        .computeIfAbsent(result.getPosition(), k -> new java.util.ArrayList<>())
                        .add(result);
            }

            for (var entry : resultsByPosition.entrySet()) {
                statsText.append("\n📍 ").append(entry.getKey()).append(":\n");
                statsText.append("─────────────────────────────────────\n");

                for (var result : entry.getValue()) {
                    statsText.append(String.format("• %s (%s) - %d votes (%.2f%%)\n",
                            result.getCandidateName(),
                            result.getParty() != null ? result.getParty() : "Independent",
                            result.getVoteCount(),
                            result.getPercentage()
                    ));
                }

                var winner = entry.getValue().stream()
                        .max(java.util.Comparator.comparingInt(VoteService.CandidateResult::getVoteCount))
                        .orElse(null);

                if (winner != null && winner.getVoteCount() > 0) {
                    statsText.append("🎉 WINNER: ").append(winner.getCandidateName())
                            .append(" with ").append(winner.getVoteCount()).append(" votes\n");
                }
            }
        }

        resultArea.setText(statsText.toString());
    }

    private void closeElection() {
        int selectedIndex = electionComboBox.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select an election to close!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Election> closableElections = getClosableElections();
        Election selectedElection = closableElections.get(selectedIndex);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to close this election?\n\n" +
                        "Election: " + selectedElection.getTitle() + "\n" +
                        "Total Votes: " + selectedElection.getTotalVotes() + "\n\n" +
                        "⚠️ This action cannot be undone!",
                "Confirm Election Closure",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            closeButton.setText("Closing...");
            closeButton.setEnabled(false);

            boolean success = ElectionService.closeElection(selectedElection.getId());

            if (success) {
                sendElectionClosedNotifications(selectedElection);

                JOptionPane.showMessageDialog(this,
                        "✅ Election closed successfully!\n\n" +
                                "Election: " + selectedElection.getTitle() + "\n" +
                                "Status changed to: COMPLETED\n" +
                                "No more votes can be cast in this election.",
                        "Election Closed",
                        JOptionPane.INFORMATION_MESSAGE);

                new CloseElectionFrame(currentUser).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Failed to close election!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                closeButton.setText("Close Election");
                closeButton.setEnabled(true);
            }
        }
    }

    private void viewDetailedResults() {
        int selectedIndex = electionComboBox.getSelectedIndex();
        if (selectedIndex < 0) return;

        List<Election> closableElections = getClosableElections();
        Election selectedElection = closableElections.get(selectedIndex);

        JDialog resultsDialog = new JDialog(this, "Detailed Results - " + selectedElection.getTitle(), true);
        resultsDialog.setSize(600, 500);
        resultsDialog.setLocationRelativeTo(this);

        JTextArea detailedArea = new JTextArea();
        detailedArea.setEditable(false);
        detailedArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        detailedArea.setBackground(Color.WHITE);

        StringBuilder report = new StringBuilder();
        report.append("📊 DETAILED ELECTION RESULTS\n");
        report.append("═══════════════════════════════════════\n");
        report.append("Election: ").append(selectedElection.getTitle()).append("\n");
        report.append("Generated: ").append(new java.util.Date()).append("\n");
        report.append("Total Votes: ").append(selectedElection.getTotalVotes()).append("\n\n");

        var results = VoteService.getElectionResults(selectedElection.getId());

        Map<String, List<VoteService.CandidateResult>> resultsByPosition = new java.util.HashMap<>();
        Map<String, Integer> positionTotals = new java.util.HashMap<>();

        for (var result : results) {
            resultsByPosition
                    .computeIfAbsent(result.getPosition(), k -> new java.util.ArrayList<>())
                    .add(result);
            positionTotals.merge(result.getPosition(), result.getVoteCount(), Integer::sum);
        }

        for (var entry : resultsByPosition.entrySet()) {
            String position = entry.getKey();
            int totalVotes = positionTotals.get(position);

            report.append("🏛️ POSITION: ").append(position).append("\n");
            report.append("Total Votes: ").append(totalVotes).append("\n");
            report.append("─────────────────────────────────────\n");

            entry.getValue().sort((a, b) -> Integer.compare(b.getVoteCount(), a.getVoteCount()));

            for (var result : entry.getValue()) {
                double percentage = totalVotes > 0 ? (result.getVoteCount() * 100.0 / totalVotes) : 0;
                report.append(String.format(" %-25s %-15s %4d votes %6.2f%%\n",
                        result.getCandidateName(),
                        "(" + (result.getParty() != null ? result.getParty() : "Independent") + ")",
                        result.getVoteCount(),
                        percentage
                ));
            }

            var winner = entry.getValue().get(0);
            if (winner.getVoteCount() > 0) {
                report.append("\n 🏆 WINNER: ").append(winner.getCandidateName())
                        .append(" - ").append(winner.getVoteCount()).append(" votes (")
                        .append(String.format("%.2f", totalVotes > 0 ? (winner.getVoteCount() * 100.0 / totalVotes) : 0))
                        .append("%)\n");
            }
            report.append("\n");
        }

        detailedArea.setText(report.toString());
        JScrollPane scrollPane = new JScrollPane(detailedArea);
        resultsDialog.add(scrollPane);
        resultsDialog.setVisible(true);
    }

    private void sendElectionClosedNotifications(Election election) {
        new Thread(() -> {
            try {
                System.out.println("📧 Sending election closure notifications...");
                Thread.sleep(2000); // Simulate email sending
                System.out.println("✅ Election closure notifications sent!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public static void main(String[] args) {
        User adminUser = new User();
        adminUser.setId(1);
        adminUser.setName("Admin User");
        adminUser.setRole("ADMIN");

        SwingUtilities.invokeLater(() -> {
            new CloseElectionFrame(adminUser).setVisible(true);
        });
    }
}
