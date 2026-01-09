package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import javax.swing.*;
import java.awt.*;
import javax.swing.BorderFactory;

public class DashboardFrame extends JFrame {
    private User currentUser;

    public DashboardFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Online Voting System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Online Voting System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // User Info + Logout Button Panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(0, 70, 140));

        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
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

        // Welcome card - Fixed size
        JPanel welcomeCard = createCard("Welcome to Online Voting", "🗳️",
                "You can participate in ongoing elections and view results.", new Dimension(500, 120));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.gridheight = 1;
        contentPanel.add(welcomeCard, gbc);

        // Feature cards with fixed sizes
        JPanel voteCard = createCard("Cast Your Vote", "✓", "Participate in ongoing elections", new Dimension(250, 120));
        voteCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new CastVoteFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
        contentPanel.add(voteCard, gbc);

        JPanel eligibilityCard = createCard("Verify Eligibility", "🔍", "Check your voting eligibility", new Dimension(250, 120));
        eligibilityCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new VerifyEligibilityFrame(currentUser).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 1; gbc.gridy = 1;
        contentPanel.add(eligibilityCard, gbc);

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

        // Profile Card - Fixed size
        JPanel profileCard = createCard("My Profile", "👤", "View and update your profile", new Dimension(250, 120));
        profileCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(DashboardFrame.this,
                        "Profile feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.gridx = 1; gbc.gridy = 2;
        contentPanel.add(profileCard, gbc);

        // Add scroll pane to content panel
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Button Panel তৈরি করুন
        JPanel buttonPanel = createButtonPanel();

        // Main container panel for both cards and buttons
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(240, 248, 255));
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(mainContainer, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // Button Panel তৈরি করার method - Fixed button sizes
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 25, 15));

        // Report Issue Button - Fixed size
        JButton reportIssueButton = createDashboardButton("Report Issue", "📝", new Color(255, 193, 7), new Dimension(150, 60));
        reportIssueButton.addActionListener(e -> {
            new ReportIssueFrame(currentUser).setVisible(true);
            dispose();
        });
        buttonPanel.add(reportIssueButton);

        // Help & Support Button - Fixed size
        JButton helpButton = createDashboardButton("Help & Support", "❓", new Color(0, 123, 255), new Dimension(150, 60));
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(DashboardFrame.this,
                    "For help, please contact support@onlinevotingsystem.com",
                    "Help & Support", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(helpButton);

        // Feedback Button - Fixed size
        JButton feedbackButton = createDashboardButton("Give Feedback", "💬", new Color(40, 167, 69), new Dimension(150, 60));
        feedbackButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(DashboardFrame.this,
                    "Feedback feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(feedbackButton);

        // View Election Results Button - Fixed size
        JButton resultsButton = createDashboardButton("View Results", "🏆", new Color(108, 117, 125), new Dimension(150, 60));
        resultsButton.addActionListener(e -> {
            new ElectionResultsFrame(currentUser).setVisible(true);
            dispose();
        });
        buttonPanel.add(resultsButton);

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

    private void logoutUser() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Show confirmation message
            JOptionPane.showMessageDialog(
                    this,
                    "You have been logged out successfully!",
                    "Logout Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Redirect to login
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        // Test with a dummy user
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setRole("VOTER");
        testUser.setEmail("test@example.com");

        SwingUtilities.invokeLater(() -> {
            new DashboardFrame(testUser).setVisible(true);
        });
    }
}