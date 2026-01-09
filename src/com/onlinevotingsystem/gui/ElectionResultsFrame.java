package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Election;
import com.onlinevotingsystem.entities.Candidate;
import com.onlinevotingsystem.services.ElectionService;
import com.onlinevotingsystem.services.VoteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ElectionResultsFrame extends JFrame {
    private User currentUser;
    private JComboBox<String> electionComboBox;
    private JTextArea resultsArea;
    private JButton viewButton, printButton, backButton;

    public ElectionResultsFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Election Results - Online Voting System");
        setSize(900, 700);
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

        JLabel titleLabel = new JLabel("🏆 Election Results & Winners");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(Color.WHITE);

        // Election Selection
        JLabel electionLabel = new JLabel("Select Election to View Results:");
        electionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        electionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(electionLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        List<Election> completedElections = ElectionService.getElectionsByStatus("COMPLETED");
        String[] electionOptions = completedElections.stream()
                .map(e -> "ID: " + e.getId() + " - " + e.getTitle())
                .toArray(String[]::new);

        electionComboBox = new JComboBox<>(electionOptions);
        electionComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        electionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(electionComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Results Area
        resultsArea = new JTextArea(20, 60);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        resultsArea.setBackground(new Color(248, 249, 250));
        resultsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
        resultsScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        resultsScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(resultsScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        viewButton = new JButton("View Results");
        viewButton.setBackground(new Color(0, 123, 255));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewButton.setPreferredSize(new Dimension(140, 40));
        viewButton.addActionListener(e -> viewElectionResults());

        printButton = new JButton("Print Results");
        printButton.setBackground(new Color(40, 167, 69));
        printButton.setForeground(Color.WHITE);
        printButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        printButton.setPreferredSize(new Dimension(130, 35));
        printButton.addActionListener(e -> printResults());

        backButton = new JButton("Back");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });

        buttonPanel.add(viewButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(printButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Auto-load results if only one election
        if (completedElections.size() == 1) {
            viewElectionResults();
        }
    }

    private void viewElectionResults() {
        int selectedIndex = electionComboBox.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an election first!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Election> completedElections = ElectionService.getElectionsByStatus("COMPLETED");
        Election selectedElection = completedElections.get(selectedIndex);

        // Get detailed results
        Map<String, Object> results = VoteService.getDetailedElectionResults(selectedElection.getId());

        @SuppressWarnings("unchecked")
        Map<String, Candidate> winners = (Map<String, Candidate>) results.get("winners");
        @SuppressWarnings("unchecked")
        List<VoteService.CandidateResult> allResults = (List<VoteService.CandidateResult>) results.get("allResults");
        int totalVotes = (int) results.get("totalVotes");

        StringBuilder resultsText = new StringBuilder();
        resultsText.append("╔══════════════════════════════════════════════════════════════╗\n");
        resultsText.append("║                     🏆 ELECTION RESULTS 🏆                   ║\n");
        resultsText.append("╠══════════════════════════════════════════════════════════════╣\n");
        resultsText.append("║                                                              ║\n");
        resultsText.append(String.format("║  Election: %-50s ║\n",
                truncate(selectedElection.getTitle(), 50)));
        resultsText.append(String.format("║  Total Votes: %-45d ║\n", totalVotes));
        resultsText.append(String.format("║  Status: %-49s ║\n", selectedElection.getStatus()));
        resultsText.append("║                                                              ║\n");
        resultsText.append("╠══════════════════════════════════════════════════════════════╣\n");
        resultsText.append("║                                                              ║\n");
        resultsText.append("║                      🎉 DECLARED WINNERS 🎉                  ║\n");
        resultsText.append("║                                                              ║\n");

        if (winners.isEmpty()) {
            resultsText.append("║            No winners declared - No votes cast            ║\n");
        } else {
            for (Map.Entry<String, Candidate> entry : winners.entrySet()) {
                Candidate winner = entry.getValue();
                resultsText.append("║  ┌────────────────────────────────────────────────────────┐  ║\n");
                resultsText.append(String.format("║  │ 🏛️  POSITION: %-40s │  ║\n",
                        truncate(entry.getKey(), 40)));
                resultsText.append(String.format("║  │ 🥇 WINNER: %-42s │  ║\n",
                        truncate(winner.getName(), 42)));
                resultsText.append(String.format("║  │ 🏛️  PARTY: %-43s │  ║\n",
                        truncate(winner.getParty() != null ? winner.getParty() : "Independent", 43)));
                resultsText.append(String.format("║  │ 📊 VOTES: %-44d │  ║\n", winner.getVoteCount()));
                double percentage = totalVotes > 0 ? (winner.getVoteCount() * 100.0 / totalVotes) : 0;
                resultsText.append(String.format("║  │ 📈 VOTE SHARE: %-37.2f%% │  ║\n", percentage));
                resultsText.append("║  └────────────────────────────────────────────────────────┘  ║\n");
            }
        }

        resultsText.append("║                                                              ║\n");
        resultsText.append("╠══════════════════════════════════════════════════════════════╣\n");
        resultsText.append("║                                                              ║\n");
        resultsText.append("║                     📊 DETAILED RESULTS 📊                   ║\n");
        resultsText.append("║                                                              ║\n");

        // Group results by position
        Map<String, List<VoteService.CandidateResult>> resultsByPosition = new java.util.HashMap<>();
        for (VoteService.CandidateResult result : allResults) {
            resultsByPosition.computeIfAbsent(result.getPosition(), k -> new java.util.ArrayList<>())
                    .add(result);
        }

        for (Map.Entry<String, List<VoteService.CandidateResult>> entry : resultsByPosition.entrySet()) {
            resultsText.append(String.format("║  📍 %-60s ║\n",
                    truncate(entry.getKey().toUpperCase(), 60)));
            resultsText.append("║  ┌────────────────────────────────────────────────────────────┐  ║\n");

            for (VoteService.CandidateResult result : entry.getValue()) {
                double percentage = totalVotes > 0 ? (result.getVoteCount() * 100.0 / totalVotes) : 0;
                String winnerIndicator = result.getVoteCount() > 0 &&
                        result.getVoteCount() == entry.getValue().get(0).getVoteCount() ? "🏆 " : "  ";

                resultsText.append(String.format("║  │ %s%-25s %-15s %4d votes %6.2f%% │  ║\n",
                        winnerIndicator,
                        truncate(result.getCandidateName(), 25),
                        truncate("(" + (result.getParty() != null ? result.getParty() : "Independent") + ")", 15),
                        result.getVoteCount(),
                        percentage));
            }
            resultsText.append("║  └────────────────────────────────────────────────────────────┘  ║\n");
        }

        resultsText.append("║                                                              ║\n");
        resultsText.append("║  📝 Note: Results are final and officially declared          ║\n");
        resultsText.append("║                                                              ║\n");
        resultsText.append("╚══════════════════════════════════════════════════════════════╝\n");

        resultsArea.setText(resultsText.toString());
    }

    private void printResults() {
        try {
            // Simple print implementation
            if (resultsArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No results to print! Please view results first.",
                        "Print Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // You can implement actual printing here
            JOptionPane.showMessageDialog(this,
                    "Print functionality would be implemented here!\n\n" +
                            "The results are ready for printing or saving.",
                    "Print Ready",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Print failed: " + e.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }

    public static void main(String[] args) {
        User testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");

        SwingUtilities.invokeLater(() -> {
            new ElectionResultsFrame(testUser).setVisible(true);
        });
    }
}