package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.services.VoteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Map;

public class VoteConfirmationFrame extends JFrame {
    private User currentUser;
    private int electionId;
    private JTextArea confirmationArea;
    private JButton printButton, closeButton, saveButton;

    public VoteConfirmationFrame(User user, int electionId) {
        this.currentUser = user;
        this.electionId = electionId;
        initializeUI();
        loadConfirmationDetails();
    }

    private void initializeUI() {
        setTitle("Vote Confirmation - Online Voting System");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(40, 167, 69));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("✅ Vote Confirmation Receipt");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        contentPanel.setBackground(Color.WHITE);

        // Confirmation Text Area
        confirmationArea = new JTextArea(20, 50);
        confirmationArea.setEditable(false);
        confirmationArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        confirmationArea.setBackground(Color.WHITE);
        confirmationArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JScrollPane scrollPane = new JScrollPane(confirmationArea);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        printButton = new JButton("🖨️ Print Receipt");
        printButton.setBackground(new Color(0, 123, 255));
        printButton.setForeground(Color.WHITE);
        printButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printButton.setPreferredSize(new Dimension(150, 40));
        printButton.addActionListener(e -> printConfirmation());

        saveButton = new JButton("💾 Save as Text");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        saveButton.setPreferredSize(new Dimension(140, 35));
        saveButton.addActionListener(e -> saveConfirmation());

        closeButton = new JButton("Close");
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });

        buttonPanel.add(printButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(closeButton);

        contentPanel.add(buttonPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void loadConfirmationDetails() {
        Map<String, String> confirmation = VoteService.getVoteConfirmationDetails(currentUser.getId(), electionId);

        if (confirmation.isEmpty()) {
            confirmationArea.setText("Error: Could not load vote confirmation details.");
            return;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("╔══════════════════════════════════════════════════╗\n");
        receipt.append("║              VOTE CONFIRMATION RECEIPT           ║\n");
        receipt.append("╠══════════════════════════════════════════════════╣\n");
        receipt.append("║                                                  ║\n");
        receipt.append("║  🗳️  ONLINE VOTING SYSTEM                       ║\n");
        receipt.append("║  📧 Digital Voting Platform                     ║\n");
        receipt.append("║                                                  ║\n");
        receipt.append("╠══════════════════════════════════════════════════╣\n");
        receipt.append("║                                                  ║\n");
        receipt.append(String.format("║  👤 Voter: %-35s ║\n",
                truncate(confirmation.get("voterName"), 35)));
        receipt.append(String.format("║  📧 Email: %-35s ║\n",
                truncate(confirmation.get("voterEmail"), 35)));
        receipt.append("║                                                  ║\n");
        receipt.append(String.format("║  🏛️  Election: %-31s ║\n",
                truncate(confirmation.get("electionTitle"), 31)));
        receipt.append("║                                                  ║\n");
        receipt.append(String.format("║  ✅ Voted For: %-30s ║\n",
                truncate(confirmation.get("candidateName"), 30)));
        receipt.append(String.format("║  🏛️  Party: %-33s ║\n",
                truncate(confirmation.get("candidateParty"), 33)));
        receipt.append(String.format("║  📊 Position: %-30s ║\n",
                truncate(confirmation.get("candidatePosition"), 30)));
        receipt.append("║                                                  ║\n");

        // Format timestamp
        String votedAt = formatTimestamp(confirmation.get("votedAt"));
        receipt.append(String.format("║  🕒 Vote Time: %-30s ║\n", votedAt));
        receipt.append("║                                                  ║\n");
        receipt.append("║  📝 Status: ✅ VOTE CONFIRMED                   ║\n");
        receipt.append("║  🔒 This is an official digital receipt         ║\n");
        receipt.append("║                                                  ║\n");
        receipt.append("╠══════════════════════════════════════════════════╣\n");
        receipt.append("║                                                  ║\n");
        receipt.append("║  💡 Important Notes:                            ║\n");
        receipt.append("║  • This receipt confirms your vote was counted  ║\n");
        receipt.append("║  • Keep this for your records                  ║\n");
        receipt.append("║  • Votes are final and cannot be changed       ║\n");
        receipt.append("║                                                  ║\n");
        receipt.append("╚══════════════════════════════════════════════════╝\n");
        receipt.append("\n");
        receipt.append("Generated by Online Voting System\n");
        receipt.append("Thank you for participating in the democratic process! 🇧🇩");

        confirmationArea.setText(receipt.toString());
    }

    private String truncate(String text, int length) {
        if (text == null) return "N/A";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }

    private String formatTimestamp(String timestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            return outputFormat.format(inputFormat.parse(timestamp));
        } catch (Exception e) {
            return timestamp;
        }
    }

    private void printConfirmation() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Vote Confirmation Receipt");

            PageFormat pageFormat = job.defaultPage();
            Paper paper = pageFormat.getPaper();
            paper.setSize(612, 792); // A4 size
            paper.setImageableArea(72, 72, 468, 648); // margins
            pageFormat.setPaper(paper);

            job.setPrintable(new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE;
                    }

                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                    // Draw the receipt
                    String[] lines = confirmationArea.getText().split("\n");
                    int y = 50;
                    for (String line : lines) {
                        g2d.drawString(line, 50, y);
                        y += 15;
                    }

                    return PAGE_EXISTS;
                }
            }, pageFormat);

            if (job.printDialog()) {
                job.print();
                JOptionPane.showMessageDialog(this,
                        "Receipt sent to printer successfully!",
                        "Print Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this,
                    "Print failed: " + e.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveConfirmation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Vote Confirmation");
        fileChooser.setSelectedFile(new java.io.File("vote_confirmation.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.write(confirmationArea.getText());
                JOptionPane.showMessageDialog(this,
                        "Confirmation saved successfully!",
                        "Save Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Save failed: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Test with sample data
        User testUser = new User();
        testUser.setId(1);
        testUser.setName("John Doe");
        testUser.setEmail("voter@example.com");

        SwingUtilities.invokeLater(() -> {
            new VoteConfirmationFrame(testUser, 1).setVisible(true);
        });
    }
}