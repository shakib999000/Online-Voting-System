package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Election;
import com.onlinevotingsystem.services.ElectionService;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import java.util.List;

public class AdminDashboard extends JFrame {
    private User currentUser;

    public AdminDashboard(User user) {
        // SECURITY: Verify user is actually admin
        if (user == null || !"ADMIN".equals(user.getRole())) {
            JOptionPane.showMessageDialog(null,
                    "❌ Access Denied!\n\nAdministrator privileges required.",
                    "Security Violation",
                    JOptionPane.ERROR_MESSAGE);
            new LoginFrame().setVisible(true);
            return;
        }

        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - Online Voting System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // User Info + Logout Button Panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(0, 70, 140));

        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName() + " (ADMIN)");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logoutUser());

        userPanel.add(userLabel);
        userPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        userPanel.add(logoutButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel - Cards Section with Scroll Pane
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Welcome card with real-time info
        JPanel welcomeCard = createWelcomeCard();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.gridheight = 1;
        contentPanel.add(welcomeCard, gbc);

        // Create Election Card - Fixed size
        JPanel createElectionCard = createCard("Create Election", "➕", "Create new elections", new Dimension(250, 120));
        createElectionCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new CreateElectionFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
        contentPanel.add(createElectionCard, gbc);


        // Verify Eligibility Card - Fixed size
        JPanel verifyEligibilityCard = createCard("Verify Eligibility", "✅", "Check voter eligibility", new Dimension(250, 120));
        verifyEligibilityCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new VerifyEligibilityFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 1; gbc.gridy = 3;
        contentPanel.add(verifyEligibilityCard, gbc);


        // Election Status Card - Fixed size
        JPanel statusCard = createCard("Election Status", "📈", "Manage election statuses", new Dimension(250, 120));
        statusCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ElectionStatusFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 1; gbc.gridy = 1;
        contentPanel.add(statusCard, gbc);

        // View Results Card - Fixed size
        JPanel viewResultsCard = createCard("View Results", "📊", "Election results and analytics", new Dimension(250, 120));
        viewResultsCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ViewResultsFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(viewResultsCard, gbc);

        // User Management Card - Fixed size
        JPanel userManagementCard = createCard("User Management", "👤", "Manage voters and admins", new Dimension(250, 120));
        userManagementCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new UserManagementFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 1; gbc.gridy = 2;
        contentPanel.add(userManagementCard, gbc);

        // Close Election Card - Fixed size
        JPanel closeElectionCard = createCard("Close Election", "⏹️", "Close ongoing elections", new Dimension(250, 120));
        closeElectionCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new CloseElectionFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(closeElectionCard, gbc);



        // Manage Issues Card - Fixed size
        JPanel manageIssuesCard = createCard("Manage Issues", "🔧", "Handle voter reported issues", new Dimension(250, 120));
        manageIssuesCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ManageIssuesFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(manageIssuesCard, gbc);

        // Database Stats Card - Fixed size
        JPanel databaseStatsCard = createCard("Database Stats", "📈", "View system statistics", new Dimension(250, 120));
        databaseStatsCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showDatabaseStatistics();
            }
        });
        gbc.gridx = 1; gbc.gridy = 4;
        contentPanel.add(databaseStatsCard, gbc);

        // Add scroll pane to content panel
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Create Button Panel
        JPanel buttonPanel = createButtonPanel();

        // Main container panel
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(240, 248, 255));
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(mainContainer, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Auto-update election statuses when dashboard opens
        new Thread(() -> {
            try {
                ElectionService.updateElectionStatuses();
                System.out.println("✅ Election statuses updated automatically");
            } catch (Exception e) {
                System.err.println("❌ Auto-update failed: " + e.getMessage());
            }
        }).start();
    }

    // Button Panel method with fixed button sizes
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 25, 15));

        // Manage Issues Button - Fixed size
        JButton manageIssuesButton = createDashboardButton("Manage Issues", "🔧", new Color(220, 53, 69), new Dimension(150, 60));
        manageIssuesButton.addActionListener(e -> {
            new ManageIssuesFrame(currentUser).setVisible(true);
            dispose();
        });
        buttonPanel.add(manageIssuesButton);

        // Manage Candidates Button - Fixed size
        JButton manageCandidatesButton = createDashboardButton("Manage Candidates", "👥", new Color(40, 167, 69), new Dimension(150, 60));
        manageCandidatesButton.addActionListener(e -> {
            openManageCandidatesFrame();
        });
        buttonPanel.add(manageCandidatesButton);

        // Refresh Stats Button - Fixed size
        JButton refreshButton = createDashboardButton("Refresh Stats", "🔄", new Color(23, 162, 184), new Dimension(150, 60));
        refreshButton.addActionListener(e -> {
            showDatabaseStatistics();
        });
        buttonPanel.add(refreshButton);

        // System Settings Button - Fixed size
        JButton settingsButton = createDashboardButton("System Settings", "⚙️", new Color(108, 117, 125), new Dimension(150, 60));
        settingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(AdminDashboard.this,
                    "System Settings feature coming soon!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(settingsButton);

        return buttonPanel;
    }

    private JButton createDashboardButton(String text, String icon, Color color, Dimension size) {
        JButton button = new JButton("<html><center><font size='4'>" + icon + "</font><br>" + text + "</center></html>");
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(size);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // Welcome card with fixed size
    private JPanel createWelcomeCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(600, 120));
        card.setMaximumSize(new Dimension(600, 120));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
        });

        JLabel emojiLabel = new JLabel("👑");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea("Manage elections, candidates, and view system reports.");
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setBackground(Color.WHITE);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add real-time info
        try {
            java.util.Map<String, Integer> stats = getDatabaseStats();
            JLabel infoLabel = new JLabel("<html><div style='text-align: center; font-size: 10px; color: #666; margin-top: 8px;'>" +
                    "Active: " + stats.getOrDefault("ongoingElections", 0) +
                    " | Votes: " + stats.getOrDefault("totalVotes", 0) +
                    " | Users: " + stats.getOrDefault("totalUsers", 0) +
                    "</div></html>");
            infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(emojiLabel);
            card.add(Box.createRigidArea(new Dimension(0, 5)));
            card.add(titleLabel);
            card.add(Box.createRigidArea(new Dimension(0, 5)));
            card.add(descArea);
            card.add(Box.createRigidArea(new Dimension(0, 5)));
            card.add(infoLabel);
        } catch (Exception e) {
            // Fallback if stats fail to load
            card.add(emojiLabel);
            card.add(Box.createRigidArea(new Dimension(0, 10)));
            card.add(titleLabel);
            card.add(Box.createRigidArea(new Dimension(0, 10)));
            card.add(descArea);
        }

        return card;
    }

    private JPanel createCard(String title, String emoji, String description, Dimension size) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(size);
        card.setMaximumSize(size);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setBackground(Color.WHITE);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(emojiLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(descArea);

        return card;
    }

    // NEW METHOD: Open Manage Candidates Frame
    private void openManageCandidatesFrame() {
        try {
            // Get all elections from database
            List<Election> elections = ElectionService.getAllElections();

            if (elections == null || elections.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No elections found. Please create an election first.",
                        "No Elections",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create election selection options
            String[] electionOptions = elections.stream()
                    .map(e -> "ID: " + e.getId() + " - " + e.getTitle() + " (" + e.getStatus() + ")")
                    .toArray(String[]::new);

            // Show election selection dialog
            String selectedElection = (String) JOptionPane.showInputDialog(
                    this,
                    "Select Election to Manage Candidates:",
                    "Manage Candidates - Select Election",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    electionOptions,
                    electionOptions[0]
            );

            if (selectedElection != null && !selectedElection.isEmpty()) {
                // Extract election ID from selection
                int electionId = extractElectionId(selectedElection);

                // Find the selected election
                Election selectedElectionObj = elections.stream()
                        .filter(e -> e.getId() == electionId)
                        .findFirst()
                        .orElse(null);

                if (selectedElectionObj != null) {
                    // Open ManageCandidatesFrame with selected election
                    new ManageCandidatesFrame(currentUser, selectedElectionObj).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error: Selected election not found!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading elections: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Helper method to extract election ID from string
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

    // NEW METHOD: Show database statistics
    private void showDatabaseStatistics() {
        try {
            // Get statistics from database
            java.util.Map<String, Integer> stats = getDatabaseStats();

            StringBuilder statsText = new StringBuilder();
            statsText.append("📊 SYSTEM STATISTICS\n");
            statsText.append("═══════════════════════════════════════\n");
            statsText.append("👥 Total Users: ").append(stats.getOrDefault("totalUsers", 0)).append("\n");
            statsText.append("👑 Admin Users: ").append(stats.getOrDefault("adminUsers", 0)).append("\n");
            statsText.append("🗳️ Voter Users: ").append(stats.getOrDefault("voterUsers", 0)).append("\n");
            statsText.append("🏛️ Total Elections: ").append(stats.getOrDefault("totalElections", 0)).append("\n");
            statsText.append("⏳ Upcoming Elections: ").append(stats.getOrDefault("upcomingElections", 0)).append("\n");
            statsText.append("🔵 Ongoing Elections: ").append(stats.getOrDefault("ongoingElections", 0)).append("\n");
            statsText.append("✅ Completed Elections: ").append(stats.getOrDefault("completedElections", 0)).append("\n");
            statsText.append("👤 Total Candidates: ").append(stats.getOrDefault("totalCandidates", 0)).append("\n");
            statsText.append("🗳️ Total Votes Cast: ").append(stats.getOrDefault("totalVotes", 0)).append("\n");

            JTextArea statsArea = new JTextArea(statsText.toString());
            statsArea.setEditable(false);
            statsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            statsArea.setBackground(new Color(248, 249, 250));

            JScrollPane scrollPane = new JScrollPane(statsArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Database Statistics", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading database statistics: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // NEW METHOD: Get database statistics
    private java.util.Map<String, Integer> getDatabaseStats() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();

        try (java.sql.Connection conn = com.onlinevotingsystem.database.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            // Total users
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            if (rs.next()) stats.put("totalUsers", rs.getInt("count"));

            // Admin users
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users WHERE role = 'ADMIN'");
            if (rs.next()) stats.put("adminUsers", rs.getInt("count"));

            // Voter users
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users WHERE role = 'VOTER'");
            if (rs.next()) stats.put("voterUsers", rs.getInt("count"));

            // Total elections
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM elections");
            if (rs.next()) stats.put("totalElections", rs.getInt("count"));

            // Elections by status
            rs = stmt.executeQuery("SELECT status, COUNT(*) as count FROM elections GROUP BY status");
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                switch (status) {
                    case "UPCOMING": stats.put("upcomingElections", count); break;
                    case "ONGOING": stats.put("ongoingElections", count); break;
                    case "COMPLETED": stats.put("completedElections", count); break;
                }
            }

            // Total candidates
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM candidates");
            if (rs.next()) stats.put("totalCandidates", rs.getInt("count"));

            // Total votes
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM votes");
            if (rs.next()) stats.put("totalVotes", rs.getInt("count"));

        } catch (java.sql.SQLException e) {
            System.err.println("❌ Error getting database stats: " + e.getMessage());
        }

        return stats;
    }

    private void logoutUser() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "Admin logged out successfully!",
                    "Logout Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        User testUser = new User();
        testUser.setId(1);
        testUser.setName("Admin User");
        testUser.setRole("ADMIN");
        testUser.setEmail("admin@ovs.com");

        SwingUtilities.invokeLater(() -> {
            new AdminDashboard(testUser).setVisible(true);
        });
    }
}