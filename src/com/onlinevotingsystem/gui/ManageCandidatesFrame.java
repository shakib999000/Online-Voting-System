package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.entities.Election;
import com.onlinevotingsystem.entities.Candidate;
import com.onlinevotingsystem.services.CandidateService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageCandidatesFrame extends JFrame {
    private User currentUser;
    private Election currentElection;
    private JTextField nameField, emailField, partyField, positionField;
    private JTextArea manifestoArea;
    private JButton addButton, updateButton, deleteButton, backButton;
    private JList<String> candidateList;
    private DefaultListModel<String> listModel;
    private List<Candidate> candidates;
    private int selectedCandidateId = -1;

    public ManageCandidatesFrame(User user, Election election) {
        this.currentUser = user;
        this.currentElection = election;
        initializeUI();
        loadCandidates();
    }

    private void initializeUI() {
        setTitle("Manage Candidates - " + currentElection.getTitle());
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Manage Candidates");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel electionLabel = new JLabel("Election: " + currentElection.getTitle());
        electionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        electionLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(electionLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel - Split into left and right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setBackground(Color.WHITE);

        // Left Panel - Candidate List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 10));

        JLabel listLabel = new JLabel("Candidates List:");
        listLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        leftPanel.add(listLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        candidateList = new JList<>(listModel);
        candidateList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        candidateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candidateList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedCandidate();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(candidateList);
        listScrollPane.setPreferredSize(new Dimension(380, 400));
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        // Right Panel - Candidate Form
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(20, 10, 20, 20));

        JLabel formLabel = new JLabel("Add/Edit Candidate:");
        formLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(formLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name Field
        JLabel nameLabel = new JLabel("Candidate Name:*");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(nameField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Email Field
        JLabel emailLabel = new JLabel("Email:*");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(emailField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Party Field
        JLabel partyLabel = new JLabel("Party:");
        partyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        partyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(partyLabel);

        partyField = new JTextField();
        partyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        partyField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(partyField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Position Field
        JLabel positionLabel = new JLabel("Position:*");
        positionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        positionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(positionLabel);

        positionField = new JTextField();
        positionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        positionField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(positionField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Manifesto Area
        JLabel manifestoLabel = new JLabel("Manifesto:");
        manifestoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        manifestoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(manifestoLabel);

        manifestoArea = new JTextArea(4, 20);
        manifestoArea.setLineWrap(true);
        manifestoArea.setWrapStyleWord(true);
        manifestoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane manifestoScrollPane = new JScrollPane(manifestoArea);
        manifestoScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        rightPanel.add(manifestoScrollPane);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addButton = new JButton("Add Candidate");
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(new AddCandidateListener());

        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(255, 193, 7));
        updateButton.setForeground(Color.BLACK);
        updateButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        updateButton.setPreferredSize(new Dimension(100, 35));
        updateButton.setEnabled(false);
        updateButton.addActionListener(new UpdateCandidateListener());

        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new DeleteCandidateListener());

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(updateButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(deleteButton);

        rightPanel.add(buttonPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back Button
        backButton = new JButton("Back to Elections");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(180, 35));
        backButton.addActionListener(e -> {
            new AdminDashboard(currentUser).setVisible(true);
            dispose();
        });
        rightPanel.add(backButton);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);
    }

    private void loadCandidates() {
        candidates = CandidateService.getCandidatesForElection(currentElection.getId());
        listModel.clear();

        for (Candidate candidate : candidates) {
            String displayText = String.format("%s (%s) - %s",
                    candidate.getName(),
                    candidate.getParty() != null ? candidate.getParty() : "Independent",
                    candidate.getPosition());
            listModel.addElement(displayText);
        }

        clearForm();
    }

    private void displaySelectedCandidate() {
        int selectedIndex = candidateList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < candidates.size()) {
            Candidate candidate = candidates.get(selectedIndex);
            selectedCandidateId = candidate.getId();

            nameField.setText(candidate.getName());
            emailField.setText(candidate.getEmail());
            partyField.setText(candidate.getParty() != null ? candidate.getParty() : "");
            positionField.setText(candidate.getPosition());
            manifestoArea.setText(candidate.getManifesto() != null ? candidate.getManifesto() : "");

            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            addButton.setEnabled(false);
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        partyField.setText("");
        positionField.setText("");
        manifestoArea.setText("");

        selectedCandidateId = -1;
        candidateList.clearSelection();

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        addButton.setEnabled(true);
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String position = positionField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter candidate name!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter candidate email!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        if (position.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter candidate position!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            positionField.requestFocus();
            return false;
        }

        return true;
    }

    private class AddCandidateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!validateForm()) return;

            Candidate candidate = new Candidate(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    partyField.getText().trim(),
                    positionField.getText().trim(),
                    currentElection.getId()
            );
            candidate.setManifesto(manifestoArea.getText().trim());

            boolean success = CandidateService.addCandidate(candidate);
            if (success) {
                JOptionPane.showMessageDialog(ManageCandidatesFrame.this,
                        "Candidate added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCandidates();
            } else {
                JOptionPane.showMessageDialog(ManageCandidatesFrame.this,
                        "Failed to add candidate!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdateCandidateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!validateForm() || selectedCandidateId == -1) return;

            Candidate candidate = new Candidate(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    partyField.getText().trim(),
                    positionField.getText().trim(),
                    currentElection.getId()
            );
            candidate.setId(selectedCandidateId);
            candidate.setManifesto(manifestoArea.getText().trim());

            boolean success = CandidateService.updateCandidate(candidate);
            if (success) {
                JOptionPane.showMessageDialog(ManageCandidatesFrame.this,
                        "Candidate updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCandidates();
            } else {
                JOptionPane.showMessageDialog(ManageCandidatesFrame.this,
                        "Failed to update candidate!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteCandidateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedCandidateId == -1) return;

            int confirm = JOptionPane.showConfirmDialog(
                    ManageCandidatesFrame.this,
                    "Are you sure you want to delete this candidate?\nThis action cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = CandidateService.deleteCandidate(selectedCandidateId);
                if (success) {
                    JOptionPane.showMessageDialog(ManageCandidatesFrame.this,
                            "Candidate deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCandidates();
                } else {
                    JOptionPane.showMessageDialog(ManageCandidatesFrame.this,
                            "Failed to delete candidate!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Test with dummy data
        User adminUser = new User();
        adminUser.setId(1);
        adminUser.setName("Admin User");
        adminUser.setRole("ADMIN");

        Election testElection = new Election();
        testElection.setId(1);
        testElection.setTitle("Test Election");

        SwingUtilities.invokeLater(() -> {
            new ManageCandidatesFrame(adminUser, testElection).setVisible(true);
        });
    }
}