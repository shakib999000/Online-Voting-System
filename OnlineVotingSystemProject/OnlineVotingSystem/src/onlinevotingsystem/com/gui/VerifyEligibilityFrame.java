package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.services.EligibilityService;
import com.onlinevotingsystem.services.ElectionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerifyEligibilityFrame extends JFrame {
    private User currentUser;
    private JComboBox<String> electionComboBox;
    private JTextArea resultArea;
    private JButton verifyButton, backButton;

    public VerifyEligibilityFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Verify Voting Eligibility - Online Voting System");
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

        JLabel titleLabel = new JLabel("Verify Voting Eligibility");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        contentPanel.setBackground(Color.WHITE);

        // User Info
        JLabel userInfoLabel = new JLabel("Voter: " + currentUser.getName() + " (" + currentUser.getEmail() + ")");
        userInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(userInfoLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Election Selection
        JLabel electionLabel = new JLabel("Select Election:");
        electionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        electionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(electionLabel);

        // Get available elections
        String[] elections = ElectionService.getActiveElectionsForDisplay();
        electionComboBox = new JComboBox<>(elections);
        electionComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        electionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(electionComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Verify Button
        verifyButton = new JButton("Check Eligibility");
        verifyButton.setBackground(new Color(0, 120, 215));
        verifyButton.setForeground(Color.WHITE);
        verifyButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        verifyButton.setMaximumSize(new Dimension(200, 40));
        verifyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        verifyButton.addActionListener(new VerifyButtonListener());
        contentPanel.add(verifyButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Result Area
        JLabel resultLabel = new JLabel("Eligibility Result:");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(resultLabel);

        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        resultArea.setBackground(new Color(250, 250, 250));
        resultArea.setText("Select an election and click 'Check Eligibility' to verify your voting status.");

        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        contentPanel.add(resultScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back Button
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(100, 100, 100));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setMaximumSize(new Dimension(180, 35));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });
        contentPanel.add(backButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private class VerifyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedElection = (String) electionComboBox.getSelectedItem();
            if (selectedElection == null || selectedElection.isEmpty()) {
                JOptionPane.showMessageDialog(VerifyEligibilityFrame.this,
                        "Please select an election first!",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Extract election ID from the display string
            int electionId = extractElectionId(selectedElection);
            if (electionId == -1) {
                JOptionPane.showMessageDialog(VerifyEligibilityFrame.this,
                        "Invalid election selection!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Show loading
            verifyButton.setText("Checking...");
            verifyButton.setEnabled(false);

            // Perform eligibility check in background thread
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Simulate processing time

                    String result = EligibilityService.getEligibilityStatus(currentUser.getId(), electionId);

                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Eligibility Check Result:\n\n");
                        resultArea.append("Voter: " + currentUser.getName() + "\n");
                        resultArea.append("Election: " + selectedElection + "\n");
                        resultArea.append("Status: " + result + "\n\n");

                        if (result.contains("✅")) {
                            resultArea.append("You can proceed to vote in this election!");
                            resultArea.setBackground(new Color(230, 255, 230)); // Green background
                        } else {
                            resultArea.append("Please contact system administrator for more details.");
                            resultArea.setBackground(new Color(255, 230, 230)); // Red background
                        }

                        verifyButton.setText("Check Eligibility");
                        verifyButton.setEnabled(true);
                    });

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    private int extractElectionId(String electionString) {
        try {
            // Assuming format: "ID: 1 - Student Union Election 2024"
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
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setVerified(true);

        SwingUtilities.invokeLater(() -> {
            VerifyEligibilityFrame frame = new VerifyEligibilityFrame(testUser);
            frame.setVisible(true);
        });
    }
}