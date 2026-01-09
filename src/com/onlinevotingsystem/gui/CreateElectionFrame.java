package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Election;
import com.onlinevotingsystem.services.ElectionService;
import com.onlinevotingsystem.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateElectionFrame extends JFrame {
    private User currentUser;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField startDateField, startTimeField;
    private JTextField endDateField, endTimeField;
    private JButton createButton, clearButton, backButton;

    public CreateElectionFrame(User user) {
        this.currentUser = user;

        // ✅ Security check - verify user is actually admin
        if (!isUserAuthorizedAdmin()) {
            JOptionPane.showMessageDialog(null,
                    "❌ Access Denied!\n\nOnly authorized administrators can access this feature.",
                    "Security Violation",
                    JOptionPane.ERROR_MESSAGE);
            new AdminDashboard(currentUser).setVisible(true);
            dispose();
            return;
        }

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Create New Election - Online Voting System");
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

        JLabel titleLabel = new JLabel("Create New Election");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        contentPanel.setBackground(Color.WHITE);

        // Admin Info
        JLabel adminInfoLabel = new JLabel("👑 Creating election as: " + currentUser.getName() + " (" + currentUser.getEmail() + ")");
        adminInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        adminInfoLabel.setForeground(new Color(0, 100, 0));
        adminInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(adminInfoLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Title Field
        JLabel titleLabelField = new JLabel("Election Title:*");
        titleLabelField.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabelField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabelField);

        titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        contentPanel.add(titleField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Description Field
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descLabel);

        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        contentPanel.add(descScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Date and Time Panel
        JPanel datetimePanel = new JPanel();
        datetimePanel.setLayout(new GridLayout(2, 3, 15, 10));
        datetimePanel.setBackground(Color.WHITE);
        datetimePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        datetimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Start Date/Time
        JLabel startDateLabel = new JLabel("Start Date:*");
        startDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        startDateField = new JTextField(getCurrentDate());
        startDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        startTimeField = new JTextField("09:00");
        startTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // End Date/Time
        JLabel endDateLabel = new JLabel("End Date:*");
        endDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        endDateField = new JTextField(getTomorrowDate());
        endDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        endTimeField = new JTextField("17:00");
        endTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add components to datetime panel
        datetimePanel.add(startDateLabel);
        datetimePanel.add(startDateField);
        datetimePanel.add(startTimeField);
        datetimePanel.add(endDateLabel);
        datetimePanel.add(endDateField);
        datetimePanel.add(endTimeField);

        contentPanel.add(datetimePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Date format hint
        JLabel dateHint = new JLabel("📅 Date Format: YYYY-MM-DD (e.g., 2024-12-25) | Time Format: HH:MM (24-hour)");
        dateHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dateHint.setForeground(Color.GRAY);
        dateHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(dateHint);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        createButton = new JButton("Create Election");
        createButton.setBackground(new Color(40, 167, 69));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createButton.setPreferredSize(new Dimension(150, 40));
        createButton.addActionListener(new CreateElectionListener());

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
            new AdminDashboard(currentUser).setVisible(true);
            dispose();
        });

        buttonPanel.add(createButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    // ✅ SECURITY: Verify user is authorized admin
    private boolean isUserAuthorizedAdmin() {
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return false;
        }

        // Double-check in database
        String sql = "SELECT id FROM users WHERE id = ? AND role = 'ADMIN' AND is_verified = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Returns true if user is verified admin in database

        } catch (SQLException ex) {
            System.err.println("❌ Error verifying admin authorization: " + ex.getMessage());
            return false;
        }
    }

    private class CreateElectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!validateForm()) {
                return;
            }

            try {
                // Create election object
                Election election = new Election();
                election.setTitle(titleField.getText().trim());
                election.setDescription(descriptionArea.getText().trim());
                election.setStatus("UPCOMING");

                // ✅ SECURE: Get verified admin ID
                int actualAdminId = getActualAdminUserId();
                if (actualAdminId == -1) {
                    return; // Error already shown in getActualAdminUserId()
                }
                election.setCreatedBy(actualAdminId);

                // Parse dates
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Timestamp startDate = new Timestamp(dateFormat.parse(
                        startDateField.getText() + " " + startTimeField.getText()).getTime());
                Timestamp endDate = new Timestamp(dateFormat.parse(
                        endDateField.getText() + " " + endTimeField.getText()).getTime());

                election.setStartDate(startDate);
                election.setEndDate(endDate);

                // Validate dates
                if (endDate.before(startDate)) {
                    JOptionPane.showMessageDialog(CreateElectionFrame.this,
                            "❌ End date cannot be before start date!",
                            "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (startDate.before(new Timestamp(System.currentTimeMillis()))) {
                    JOptionPane.showMessageDialog(CreateElectionFrame.this,
                            "❌ Start date cannot be in the past!",
                            "Invalid Start Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Show confirmation
                int confirm = JOptionPane.showConfirmDialog(
                        CreateElectionFrame.this,
                        "<html><div style='text-align: center;'>" +
                                "<h3>Confirm Election Creation</h3>" +
                                "<div style='background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0;'>" +
                                "<p><b>Title:</b> " + election.getTitle() + "</p>" +
                                "<p><b>Start:</b> " + startDate + "</p>" +
                                "<p><b>End:</b> " + endDate + "</p>" +
                                "<p><b>Created by:</b> " + currentUser.getName() + "</p>" +
                                "</div>" +
                                "<p style='color: red;'><b>⚠️ This action cannot be undone!</b></p>" +
                                "</div></html>",
                        "Confirm Election Creation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    createButton.setText("Creating...");
                    createButton.setEnabled(false);
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    // Create election in database
                    boolean success = ElectionService.createElection(election);

                    setCursor(Cursor.getDefaultCursor());
                    createButton.setText("Create Election");
                    createButton.setEnabled(true);

                    if (success) {
                        JOptionPane.showMessageDialog(CreateElectionFrame.this,
                                "<html><div style='text-align: center;'>" +
                                        "<h3 style='color: green;'>✅ Election Created Successfully!</h3>" +
                                        "<div style='background: #d4edda; padding: 15px; border-radius: 5px; margin: 10px 0;'>" +
                                        "<p><b>Election ID:</b> " + election.getId() + "</p>" +
                                        "<p><b>Title:</b> " + election.getTitle() + "</p>" +
                                        "<p><b>Status:</b> " + election.getStatus() + "</p>" +
                                        "</div>" +
                                        "<p>You can now add candidates to this election.</p>" +
                                        "</div></html>",
                                "Election Created",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Optionally open candidate management
                        int option = JOptionPane.showConfirmDialog(CreateElectionFrame.this,
                                "Would you like to add candidates to this election now?",
                                "Add Candidates",
                                JOptionPane.YES_NO_OPTION);

                        if (option == JOptionPane.YES_OPTION) {
                            new ManageCandidatesFrame(currentUser, election).setVisible(true);
                        } else {
                            new AdminDashboard(currentUser).setVisible(true);
                        }
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(CreateElectionFrame.this,
                                "<html><div style='text-align: center;'>" +
                                        "<h3 style='color: red;'>❌ Failed to Create Election</h3>" +
                                        "<div style='background: #f8d7da; padding: 15px; border-radius: 5px; margin: 10px 0;'>" +
                                        "<p>Possible reasons:</p>" +
                                        "<ul style='text-align: left;'>" +
                                        "<li>Database connection issue</li>" +
                                        "<li>System error</li>" +
                                        "<li>Insufficient permissions</li>" +
                                        "</ul>" +
                                        "</div>" +
                                        "<p>Please check system logs for details.</p>" +
                                        "</div></html>",
                                "Creation Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (Exception ex) {
                createButton.setText("Create Election");
                createButton.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());

                JOptionPane.showMessageDialog(CreateElectionFrame.this,
                        "❌ Invalid date format!\n\nPlease use:\n• Date: YYYY-MM-DD (e.g., 2024-12-25)\n• Time: HH:MM (e.g., 09:00)\n\nError: " + ex.getMessage(),
                        "Invalid Date Format",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // ✅ SECURE METHOD: Get actual admin user ID from database
    private int getActualAdminUserId() {
        // Double security check
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "❌ Access Denied!\n\nOnly authorized administrators can create elections.",
                    "Security Violation",
                    JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        // Verify the user actually exists in database and is admin
        String sql = "SELECT id, role FROM users WHERE id = ? AND role = 'ADMIN' AND is_verified = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int adminId = rs.getInt("id");
                System.out.println("✅ Verified admin user ID: " + adminId);
                return adminId;
            } else {
                System.err.println("❌ User is not authorized as admin in database!");
                JOptionPane.showMessageDialog(this,
                        "❌ Authorization Failed!\n\nYour account is not authorized to create elections.\nPlease contact system administrator.",
                        "Admin Authorization Required",
                        JOptionPane.ERROR_MESSAGE);
                return -1;
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error verifying admin user: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "❌ System Error!\n\nCannot verify administrator privileges.",
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private boolean validateForm() {
        String title = titleField.getText().trim();
        String startDate = startDateField.getText().trim();
        String startTime = startTimeField.getText().trim();
        String endDate = endDateField.getText().trim();
        String endTime = endTimeField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "❌ Please enter election title!",
                    "Title Required", JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return false;
        }

        if (title.length() < 5) {
            JOptionPane.showMessageDialog(this,
                    "❌ Election title must be at least 5 characters long!",
                    "Title Too Short", JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return false;
        }

        if (startDate.isEmpty() || startTime.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "❌ Please enter start date and time!",
                    "Start Date Required", JOptionPane.WARNING_MESSAGE);
            startDateField.requestFocus();
            return false;
        }

        if (endDate.isEmpty() || endTime.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "❌ Please enter end date and time!",
                    "End Date Required", JOptionPane.WARNING_MESSAGE);
            endDateField.requestFocus();
            return false;
        }

        // Validate date format
        if (!isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {
            JOptionPane.showMessageDialog(this,
                    "❌ Invalid date format! Please use YYYY-MM-DD format.",
                    "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate time format
        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
            JOptionPane.showMessageDialog(this,
                    "❌ Invalid time format! Please use HH:MM format (24-hour).",
                    "Invalid Time Format", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isValidDateFormat(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private boolean isValidTimeFormat(String time) {
        return time.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]");
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        startDateField.setText(getCurrentDate());
        startTimeField.setText("09:00");
        endDateField.setText(getTomorrowDate());
        endTimeField.setText("17:00");
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new java.util.Date());
    }

    private String getTomorrowDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dateFormat.format(calendar.getTime());
    }

    public static void main(String[] args) {
        // Test with admin user
        User adminUser = new User();
        adminUser.setId(1);
        adminUser.setName("Admin User");
        adminUser.setRole("ADMIN");
        adminUser.setEmail("admin@ovs.com");

        SwingUtilities.invokeLater(() -> {
            new CreateElectionFrame(adminUser).setVisible(true);
        });
    }
}