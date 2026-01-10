package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Issue;
import com.onlinevotingsystem.services.IssueService;
import com.onlinevotingsystem.services.ElectionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportIssueFrame extends JFrame {
    private User currentUser;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> typeComboBox, priorityComboBox, electionComboBox;
    private JButton submitButton, clearButton, backButton;

    public ReportIssueFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Report Issue - Online Voting System");
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

        JLabel titleLabel = new JLabel("Report an Issue");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        contentPanel.setBackground(Color.WHITE);

        // User Info
        JLabel userInfoLabel = new JLabel("Reporting as: " + currentUser.getName() + " (" + currentUser.getEmail() + ")");
        userInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(userInfoLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Title Field
        JLabel titleLabelField = new JLabel("Issue Title:*");
        titleLabelField.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabelField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabelField);

        titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(titleField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Description Field
        JLabel descLabel = new JLabel("Description:*");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descLabel);

        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        contentPanel.add(descScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Type ComboBox
        JLabel typeLabel = new JLabel("Issue Type:*");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(typeLabel);

        String[] types = {"TECHNICAL", "FRAUD", "ACCESS", "OTHER"};
        typeComboBox = new JComboBox<>(types);
        typeComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        typeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(typeComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Priority ComboBox
        JLabel priorityLabel = new JLabel("Priority:*");
        priorityLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        priorityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(priorityLabel);

        String[] priorities = {"LOW", "MEDIUM", "HIGH", "URGENT"};
        priorityComboBox = new JComboBox<>(priorities);
        priorityComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        priorityComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(priorityComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Election ComboBox (Optional)
        JLabel electionLabel = new JLabel("Related Election (Optional):");
        electionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        electionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(electionLabel);

        String[] elections = ElectionService.getActiveElectionsForDisplay();
        // Add "None" option
        String[] electionOptions = new String[elections.length + 1];
        electionOptions[0] = "None";
        System.arraycopy(elections, 0, electionOptions, 1, elections.length);

        electionComboBox = new JComboBox<>(electionOptions);
        electionComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        electionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(electionComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitButton = new JButton("Submit Issue");
        submitButton.setBackground(new Color(40, 167, 69));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setPreferredSize(new Dimension(140, 40));
        submitButton.addActionListener(new SubmitIssueListener());

        clearButton = new JButton("Clear Form");
        clearButton.setBackground(new Color(255, 193, 7));
        clearButton.setForeground(Color.BLACK);
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearButton.setPreferredSize(new Dimension(120, 35));
        clearButton.addActionListener(e -> clearForm());

        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(160, 35));
        backButton.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private class SubmitIssueListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!validateForm()) {
                return;
            }

            // Create issue object
            Issue issue = new Issue();
            issue.setUserId(currentUser.getId());
            issue.setTitle(titleField.getText().trim());
            issue.setDescription(descriptionArea.getText().trim());
            issue.setType((String) typeComboBox.getSelectedItem());
            issue.setPriority((String) priorityComboBox.getSelectedItem());

            // Set election ID if selected
            String selectedElection = (String) electionComboBox.getSelectedItem();
            if (selectedElection != null && !selectedElection.equals("None")) {
                int electionId = extractElectionId(selectedElection);
                issue.setElectionId(electionId);
            }

            // Submit issue
            boolean success = IssueService.createIssue(issue);

            if (success) {
                JOptionPane.showMessageDialog(ReportIssueFrame.this,
                        "✅ Issue reported successfully!\n\n" +
                                "Your issue has been submitted and will be reviewed by our team.\n" +
                                "You can check the status in 'My Reported Issues'.",
                        "Issue Submitted",
                        JOptionPane.INFORMATION_MESSAGE);

                clearForm();
            } else {
                JOptionPane.showMessageDialog(ReportIssueFrame.this,
                        "❌ Failed to submit issue! Please try again.",
                        "Submission Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter issue title!",
                    "Title Required", JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return false;
        }

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter issue description!",
                    "Description Required", JOptionPane.WARNING_MESSAGE);
            descriptionArea.requestFocus();
            return false;
        }

        if (description.length() < 10) {
            JOptionPane.showMessageDialog(this,
                    "Please provide a more detailed description (at least 10 characters).",
                    "Description Too Short", JOptionPane.WARNING_MESSAGE);
            descriptionArea.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        typeComboBox.setSelectedIndex(0);
        priorityComboBox.setSelectedIndex(1); // MEDIUM
        electionComboBox.setSelectedIndex(0); // None
    }

    private int extractElectionId(String electionString) {
        try {
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
        // Test with user
        User testUser = new User();
        testUser.setId(2);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        SwingUtilities.invokeLater(() -> {
            new ReportIssueFrame(testUser).setVisible(true);
        });
    }
}