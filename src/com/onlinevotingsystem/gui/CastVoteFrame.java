package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Candidate;
import com.onlinevotingsystem.services.VoteService;
import com.onlinevotingsystem.services.EligibilityService;
import com.onlinevotingsystem.services.ElectionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class CastVoteFrame extends JFrame {
    private User currentUser;
    private JComboBox<String> electionComboBox;
    private JPanel candidatesPanel;
    private JButton castVoteButton, backButton;
    private ButtonGroup candidateGroup;
    private int selectedCandidateId = -1;
    private int currentElectionId = -1;
    private Map<Integer, Candidate> candidateMap; // Store candidates by ID

    public CastVoteFrame(User user) {
        this.currentUser = user;
        this.candidateMap = new HashMap<>();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Cast Your Vote - Online Voting System");
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

        JLabel titleLabel = new JLabel("Cast Your Vote");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(Color.WHITE);

        // User Info
        JLabel userInfoLabel = new JLabel("Voter: " + currentUser.getName() + " (" + currentUser.getEmail() + ")");
        userInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(userInfoLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Election Selection
        JLabel electionLabel = new JLabel("Select Election:");
        electionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        electionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(electionLabel);

        // Safe election data loading
        String[] elections = getElectionsSafely();
        electionComboBox = new JComboBox<>(elections);
        electionComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        electionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        electionComboBox.addActionListener(new ElectionSelectionListener());
        contentPanel.add(electionComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Candidates Panel
        JLabel candidatesLabel = new JLabel("Select Candidate:");
        candidatesLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        candidatesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(candidatesLabel);

        candidatesPanel = new JPanel();
        candidatesPanel.setLayout(new BoxLayout(candidatesPanel, BoxLayout.Y_AXIS));
        candidatesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        candidatesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        candidatesPanel.setBackground(new Color(250, 250, 250));
        candidatesPanel.setPreferredSize(new Dimension(600, 300));

        JScrollPane candidatesScrollPane = new JScrollPane(candidatesPanel);
        candidatesScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        candidatesScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(candidatesScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Cast Vote Button
        castVoteButton = new JButton("Cast Vote");
        castVoteButton.setBackground(new Color(40, 167, 69));
        castVoteButton.setForeground(Color.WHITE);
        castVoteButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        castVoteButton.setMaximumSize(new Dimension(200, 45));
        castVoteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        castVoteButton.setEnabled(false);
        castVoteButton.addActionListener(new CastVoteListener());
        contentPanel.add(castVoteButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Back Button
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setMaximumSize(new Dimension(180, 35));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });
        contentPanel.add(backButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Load initial election data if available
        if (elections.length > 0) {
            loadCandidatesForElection(extractElectionId((String) elections[0]));
        } else {
            showNoElectionsMessage();
        }
    }

    private void showNoElectionsMessage() {
        candidatesPanel.removeAll();
        JLabel noElectionsLabel = new JLabel("No active elections available at the moment.");
        noElectionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noElectionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        candidatesPanel.add(noElectionsLabel);
        candidatesPanel.revalidate();
        candidatesPanel.repaint();
    }

    // Safe method to get elections
    private String[] getElectionsSafely() {
        try {
            String[] elections = ElectionService.getActiveElectionsForDisplay();
            return elections != null ? elections : new String[0];
        } catch (Exception e) {
            System.err.println("Error loading elections: " + e.getMessage());
            return new String[0];
        }
    }

    private class ElectionSelectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedElection = (String) electionComboBox.getSelectedItem();
            if (selectedElection != null && !selectedElection.isEmpty()) {
                int electionId = extractElectionId(selectedElection);
                if (electionId != -1) {
                    loadCandidatesForElection(electionId);
                }
            }
        }
    }

    private class CastVoteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedCandidateId == -1 || currentElectionId == -1) {
                JOptionPane.showMessageDialog(CastVoteFrame.this,
                        "Please select a candidate first!",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get candidate from our map
            Candidate selectedCandidate = candidateMap.get(selectedCandidateId);
            if (selectedCandidate == null) {
                JOptionPane.showMessageDialog(CastVoteFrame.this,
                        "Invalid candidate selection!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String candidateName = selectedCandidate.getName();
            String party = selectedCandidate.getParty() != null ? selectedCandidate.getParty() : "Independent";

            // Confirm vote
            int confirm = JOptionPane.showConfirmDialog(
                    CastVoteFrame.this,
                    "<html><div style='text-align: center;'>" +
                            "<h3>Confirm Your Vote</h3>" +
                            "<p>Are you sure you want to cast your vote for:</p>" +
                            "<div style='background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0;'>" +
                            "<b>🗳️ " + candidateName + "</b><br>" +
                            "🏛️ " + party + "<br>" +
                            "📋 " + (selectedCandidate.getPosition() != null ? selectedCandidate.getPosition() : "General") +
                            "</div>" +
                            "<p style='color: #dc3545;'><b>This action cannot be undone!</b></p>" +
                            "</div></html>",
                    "Confirm Your Vote",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Show processing cursor
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    // Cast the vote
                    boolean success = VoteService.castVote(currentUser.getId(), selectedCandidateId, currentElectionId);

                    setCursor(Cursor.getDefaultCursor());

                    if (success) {
                        JOptionPane.showMessageDialog(CastVoteFrame.this,
                                "<html><div style='text-align: center;'>" +
                                        "<h3 style='color: #28a745;'>🎉 Vote Cast Successfully!</h3>" +
                                        "<p>✅ Your vote has been recorded</p>" +
                                        "<p>📧 Confirmation email sent to: " + currentUser.getEmail() + "</p>" +
                                        "<p>Thank you for participating in the democratic process!</p>" +
                                        "</div></html>",
                                "Vote Confirmed",
                                JOptionPane.INFORMATION_MESSAGE);

                        // ✅ NEW: Show vote confirmation receipt instead of going back to dashboard
                        new VoteConfirmationFrame(currentUser, currentElectionId).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(CastVoteFrame.this,
                                "<html><div style='text-align: center;'>" +
                                        "<h3 style='color: #dc3545;'>❌ Failed to cast vote!</h3>" +
                                        "<p>Possible reasons:</p>" +
                                        "<ul style='text-align: left;'>" +
                                        "<li>You have already voted in this election</li>" +
                                        "<li>System error occurred</li>" +
                                        "<li>Election may be closed</li>" +
                                        "</ul>" +
                                        "</div></html>",
                                "Vote Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(CastVoteFrame.this,
                            "❌ Error casting vote: " + ex.getMessage(),
                            "Vote Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void loadCandidatesForElection(int electionId) {
        this.currentElectionId = electionId;
        candidatesPanel.removeAll();
        candidateGroup = new ButtonGroup();
        selectedCandidateId = -1;
        castVoteButton.setEnabled(false);
        candidateMap.clear();

        try {
            // Check eligibility first
            String eligibilityStatus = EligibilityService.getEligibilityStatus(currentUser.getId(), electionId);

            if (eligibilityStatus != null && eligibilityStatus.contains("❌")) {
                // Not eligible
                JLabel notEligibleLabel = new JLabel("<html>" + eligibilityStatus.replace("\n", "<br>") + "</html>");
                notEligibleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                notEligibleLabel.setForeground(Color.RED);
                notEligibleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                candidatesPanel.add(notEligibleLabel);
            } else {
                // Eligible - load candidates
                List<Candidate> candidates = VoteService.getCandidatesForElection(electionId);

                if (candidates == null || candidates.isEmpty()) {
                    JLabel noCandidatesLabel = new JLabel("No candidates available for this election.");
                    noCandidatesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    noCandidatesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    candidatesPanel.add(noCandidatesLabel);
                } else {
                    // Store candidates in map for easy access
                    for (Candidate candidate : candidates) {
                        candidateMap.put(candidate.getId(), candidate);
                    }

                    // Group by position
                    Map<String, List<Candidate>> candidatesByPosition = new HashMap<>();
                    for (Candidate candidate : candidates) {
                        String position = candidate.getPosition() != null ? candidate.getPosition() : "General";
                        candidatesByPosition
                                .computeIfAbsent(position, k -> new ArrayList<>())
                                .add(candidate);
                    }

                    for (String position : candidatesByPosition.keySet()) {
                        // Position header
                        JLabel positionLabel = new JLabel("🏛️ " + position);
                        positionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                        positionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        candidatesPanel.add(positionLabel);
                        candidatesPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                        // Candidates for this position
                        for (Candidate candidate : candidatesByPosition.get(position)) {
                            JPanel candidateCard = createCandidateCard(candidate);
                            candidatesPanel.add(candidateCard);
                            candidatesPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                        }

                        candidatesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    }

                    castVoteButton.setEnabled(true);
                }
            }
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error loading candidates: " + e.getMessage());
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(Color.RED);
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            candidatesPanel.add(errorLabel);
        }

        candidatesPanel.revalidate();
        candidatesPanel.repaint();
    }

    private JPanel createCandidateCard(Candidate candidate) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(600, 120));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Radio button for selection
        JRadioButton selectButton = new JRadioButton();
        selectButton.setActionCommand(String.valueOf(candidate.getId()));
        candidateGroup.add(selectButton);

        // Candidate info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        // Candidate name
        JLabel nameLabel = new JLabel("👤 " + candidate.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(0, 70, 140));

        // Party information
        String party = candidate.getParty() != null ? candidate.getParty() : "Independent";
        JLabel partyLabel = new JLabel("🏛️ Party: " + party);
        partyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Position
        String position = candidate.getPosition() != null ? candidate.getPosition() : "General Position";
        JLabel positionLabel = new JLabel("📊 Position: " + position);
        positionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        positionLabel.setForeground(Color.GRAY);

        // Manifesto
        JLabel manifestoLabel = new JLabel();
        if (candidate.getManifesto() != null && !candidate.getManifesto().trim().isEmpty()) {
            String manifesto = candidate.getManifesto();
            if (manifesto.length() > 120) {
                manifesto = manifesto.substring(0, 120) + "...";
            }
            manifestoLabel.setText("📋 " + manifesto);
        } else {
            manifestoLabel.setText("📋 No manifesto provided");
        }
        manifestoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        manifestoLabel.setForeground(Color.DARK_GRAY);

        // Add components to info panel
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(partyLabel);
        infoPanel.add(positionLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(manifestoLabel);

        // Layout
        card.add(selectButton, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);

        // Add some spacing
        card.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.EAST);

        // Selection listener
        selectButton.addActionListener(e -> {
            selectedCandidateId = candidate.getId();
            // Highlight selected card
            highlightSelectedCard(card);

            // Debug output
            System.out.println("Selected candidate: " + candidate.getName() + " (ID: " + candidate.getId() + ")");
        });

        // Make entire card clickable
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectButton.setSelected(true);
                selectedCandidateId = candidate.getId();
                highlightSelectedCard(card);
                System.out.println("Card clicked - Selected candidate: " + candidate.getName());
            }
        });

        return card;
    }

    private void highlightSelectedCard(JPanel selectedCard) {
        // Reset all cards to default appearance
        for (Component comp : candidatesPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel card = (JPanel) comp;
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        }

        // Highlight selected card
        selectedCard.setBackground(new Color(230, 245, 255));
        selectedCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    }

    private int extractElectionId(String electionString) {
        try {
            if (electionString == null || electionString.isEmpty()) {
                return -1;
            }

            String[] parts = electionString.split(" - ");
            if (parts.length > 0) {
                String idPart = parts[0].replace("ID: ", "").trim();
                return Integer.parseInt(idPart);
            }
        } catch (Exception e) {
            System.err.println("Error extracting election ID from: " + electionString);
        }
        return -1;
    }

    public static void main(String[] args) {
        // Test with dummy user
        User testUser = new User();
        testUser.setId(2);
        testUser.setName("Test Voter");
        testUser.setEmail("voter@example.com");
        testUser.setVerified(true);

        SwingUtilities.invokeLater(() -> {
            CastVoteFrame frame = new CastVoteFrame(testUser);
            frame.setVisible(true);
        });
    }
}