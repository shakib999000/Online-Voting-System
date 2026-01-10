package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.services.UserManagementService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class UserManagementFrame extends JFrame {
    private User currentUser;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilterComboBox;
    private JButton searchButton, refreshButton, promoteButton, demoteButton, verifyButton, suspendButton, activateButton, deleteButton, backButton;
    private List<User> currentUsers;

    public UserManagementFrame(User user) {
        this.currentUser = user;
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setTitle("User Management - Online Voting System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // User Statistics
        Map<String, Object> stats = UserManagementService.getUserStatistics();
        JLabel statsLabel = new JLabel(String.format("Users: %d | Voters: %d | Admins: %d",
                stats.get("totalUsers"), stats.get("totalVoters"), stats.get("totalAdmins")));
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Search Field
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 120, 215));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.addActionListener(new SearchListener());

        // Role Filter
        JLabel roleLabel = new JLabel("Filter by Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        String[] roles = {"ALL", "VOTER", "ADMIN"};
        roleFilterComboBox = new JComboBox<>(roles);
        roleFilterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleFilterComboBox.addActionListener(new FilterListener());

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> loadUsers());

        controlPanel.add(searchLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        controlPanel.add(searchField);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(searchButton);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(roleLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        controlPanel.add(roleFilterComboBox);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(refreshButton);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(0, 20, 10, 20));

        // Create table model
        String[] columns = {"ID", "Name", "Email", "Role", "Phone", "Verified", "Eligible", "Registered"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userTable.setRowHeight(25);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());

        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Action Buttons Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(10, 20, 15, 20));

        promoteButton = new JButton("Promote to Admin");
        promoteButton.setBackground(new Color(40, 167, 69));
        promoteButton.setForeground(Color.WHITE);
        promoteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        promoteButton.addActionListener(new PromoteListener());

        demoteButton = new JButton("Demote to Voter");
        demoteButton.setBackground(new Color(255, 193, 7));
        demoteButton.setForeground(Color.BLACK);
        demoteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        demoteButton.addActionListener(new DemoteListener());

        verifyButton = new JButton("Verify Account");
        verifyButton.setBackground(new Color(23, 162, 184));
        verifyButton.setForeground(Color.WHITE);
        verifyButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        verifyButton.addActionListener(new VerifyListener());

        suspendButton = new JButton("Suspend Account");
        suspendButton.setBackground(new Color(220, 53, 69));
        suspendButton.setForeground(Color.WHITE);
        suspendButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        suspendButton.addActionListener(new SuspendListener());

        activateButton = new JButton("Activate Account");
        activateButton.setBackground(new Color(40, 167, 69));
        activateButton.setForeground(Color.WHITE);
        activateButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activateButton.addActionListener(new ActivateListener());

        deleteButton = new JButton("Delete Account");
        deleteButton.setBackground(new Color(108, 117, 125));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteButton.addActionListener(new DeleteListener());

        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.addActionListener(e -> {
            new AdminDashboard(currentUser).setVisible(true);
            dispose();
        });

        actionPanel.add(promoteButton);
        actionPanel.add(demoteButton);
        actionPanel.add(verifyButton);
        actionPanel.add(suspendButton);
        actionPanel.add(activateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        actionPanel.add(backButton);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // Initially disable action buttons
        updateButtonStates();
    }

    private void loadUsers() {
        currentUsers = UserManagementService.getAllUsers();
        populateTable(currentUsers);
    }

    private void populateTable(List<User> users) {
        tableModel.setRowCount(0); // Clear existing data

        for (User user : users) {
            Object[] rowData = {
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone() != null ? user.getPhone() : "N/A",
                    user.isVerified() ? "✅" : "❌",
                    user.isEligible() ? "✅" : "❌",
                    user.getCreatedAt()
            };
            tableModel.addRow(rowData);
        }
    }

    private void updateButtonStates() {
        int selectedRow = userTable.getSelectedRow();
        boolean hasSelection = selectedRow >= 0;

        if (hasSelection) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String role = (String) tableModel.getValueAt(selectedRow, 3);
            boolean isVerified = tableModel.getValueAt(selectedRow, 5).equals("✅");
            boolean isEligible = tableModel.getValueAt(selectedRow, 6).equals("✅");

            // Enable/disable buttons based on selection
            promoteButton.setEnabled(hasSelection && role.equals("VOTER"));
            demoteButton.setEnabled(hasSelection && role.equals("ADMIN") && userId != currentUser.getId());
            verifyButton.setEnabled(hasSelection && !isVerified);
            suspendButton.setEnabled(hasSelection && isEligible);
            activateButton.setEnabled(hasSelection && !isEligible);
            deleteButton.setEnabled(hasSelection && role.equals("VOTER"));
        } else {
            // No selection - disable all action buttons
            promoteButton.setEnabled(false);
            demoteButton.setEnabled(false);
            verifyButton.setEnabled(false);
            suspendButton.setEnabled(false);
            activateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    private User getSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            return currentUsers.stream()
                    .filter(user -> user.getId() == userId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // Action Listeners
    private class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                List<User> searchResults = UserManagementService.searchUsers(keyword);
                populateTable(searchResults);
            } else {
                loadUsers();
            }
        }
    }

    private class FilterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedRole = (String) roleFilterComboBox.getSelectedItem();
            if ("ALL".equals(selectedRole)) {
                loadUsers();
            } else {
                List<User> filteredUsers = UserManagementService.getUsersByRole(selectedRole);
                populateTable(filteredUsers);
            }
        }
    }

    private class PromoteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            User selectedUser = getSelectedUser();
            if (selectedUser != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        UserManagementFrame.this,
                        "Promote " + selectedUser.getName() + " to Admin?\n\nThis will give them full administrative privileges.",
                        "Confirm Promotion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = UserManagementService.updateUserRole(selectedUser.getId(), "ADMIN");
                    if (success) {
                        JOptionPane.showMessageDialog(UserManagementFrame.this,
                                "User promoted to Admin successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(UserManagementFrame.this,
                                "Failed to promote user!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private class DemoteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            User selectedUser = getSelectedUser();
            if (selectedUser != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        UserManagementFrame.this,
                        "Demote " + selectedUser.getName() + " to Voter?\n\nThis will remove their administrative privileges.",
                        "Confirm Demotion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = UserManagementService.updateUserRole(selectedUser.getId(), "VOTER");
                    if (success) {
                        JOptionPane.showMessageDialog(UserManagementFrame.this,
                                "User demoted to Voter successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(UserManagementFrame.this,
                                "Failed to demote user!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private class VerifyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            User selectedUser = getSelectedUser();
            if (selectedUser != null) {
                boolean success = UserManagementService.verifyUserAccount(selectedUser.getId());
                if (success) {
                    JOptionPane.showMessageDialog(UserManagementFrame.this,
                            "User account verified successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(UserManagementFrame.this,
                            "Failed to verify user account!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class SuspendListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            User selectedUser = getSelectedUser();
            if (selectedUser != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        UserManagementFrame.this,
                        "Suspend " + selectedUser.getName() + "'s account?\n\nThis will prevent them from voting.",
                        "Confirm Suspension",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = UserManagementService.suspendUserAccount(selectedUser.getId());
                    if (success) {
                        JOptionPane.showMessageDialog(UserManagementFrame.this,
                                "User account suspended successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(UserManagementFrame.this,
                                "Failed to suspend user account!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private class ActivateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            User selectedUser = getSelectedUser();
            if (selectedUser != null) {
                boolean success = UserManagementService.activateUserAccount(selectedUser.getId());
                if (success) {
                    JOptionPane.showMessageDialog(UserManagementFrame.this,
                            "User account activated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(UserManagementFrame.this,
                            "Failed to activate user account!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            User selectedUser = getSelectedUser();
            if (selectedUser != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        UserManagementFrame.this,
                        "Permanently delete " + selectedUser.getName() + "'s account?\n\nThis action cannot be undone!",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = UserManagementService.deleteUserAccount(selectedUser.getId());
                    if (success) {
                        JOptionPane.showMessageDialog(UserManagementFrame.this,
                                "User account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(UserManagementFrame.this,
                                "Failed to delete user account!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // Test with admin user
        User adminUser = new User();
        adminUser.setId(1);
        adminUser.setName("Admin User");

        SwingUtilities.invokeLater(() -> {
            new UserManagementFrame(adminUser).setVisible(true);
        });
    }
}