package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.services.OTPService;
import com.onlinevotingsystem.services.UserService;
import com.onlinevotingsystem.services.EmailService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ForgotPasswordFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField newPasswordField, confirmPasswordField;
    private JButton sendOtpButton, resetButton, backButton;
    private JLabel otpStatusLabel;

    public ForgotPasswordFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Forgot Password - Online Voting System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main Panel with Split Layout
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left Panel - Logo and Welcome Section with Glacier Gradient
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Glacier gradient - icy blue to light blue
                Color c1 = new Color(240, 248, 255); // Alice Blue
                Color c2 = new Color(173, 216, 230); // Light Blue
                Color c3 = new Color(135, 206, 250); // Light Sky Blue
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c3);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(550, 700));

        // Logo Content
        JPanel logoContent = new JPanel();
        logoContent.setOpaque(false);
        logoContent.setLayout(new BoxLayout(logoContent, BoxLayout.Y_AXIS));
        logoContent.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Big Logo
        JLabel logo = new JLabel("🗳️", JLabel.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setForeground(new Color(0, 70, 140));

        // System Name
        JLabel systemName = new JLabel("Online Voting System", JLabel.CENTER);
        systemName.setFont(new Font("Segoe UI", Font.BOLD, 36));
        systemName.setForeground(new Color(0, 70, 140));
        systemName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline
        JLabel tagline = new JLabel("<html><div style='text-align: center;'>Secure • Transparent • Efficient<br>Digital Voting Platform</div></html>", JLabel.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        tagline.setForeground(new Color(0, 100, 180));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Features List
        JPanel featuresPanel = new JPanel();
        featuresPanel.setOpaque(false);
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] features = {
                "✓ Secure & Encrypted Voting",
                "✓ Real-time Results",
                "✓ Easy-to-Use Interface",
                "✓ 24/7 Accessibility"
        };

        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            featureLabel.setForeground(new Color(0, 80, 160));
            featureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            featuresPanel.add(featureLabel);
            featuresPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        logoContent.add(logo);
        logoContent.add(Box.createRigidArea(new Dimension(0, 30)));
        logoContent.add(systemName);
        logoContent.add(Box.createRigidArea(new Dimension(0, 20)));
        logoContent.add(tagline);
        logoContent.add(Box.createRigidArea(new Dimension(0, 40)));
        logoContent.add(featuresPanel);

        leftPanel.add(logoContent);

        // Right Panel - Forgot Password Form with Glacier Gradient
        JPanel rightPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Glacier gradient - icy blue to light blue
                Color c1 = new Color(240, 248, 255); // Alice Blue
                Color c2 = new Color(173, 216, 230); // Light Blue
                Color c3 = new Color(135, 206, 250); // Light Sky Blue
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c3);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setPreferredSize(new Dimension(550, 700));

        // Main forgot password container - Increased height for better spacing
        JPanel forgotPasswordContainer = new JPanel();
        forgotPasswordContainer.setLayout(new BoxLayout(forgotPasswordContainer, BoxLayout.Y_AXIS));
        forgotPasswordContainer.setBackground(Color.WHITE);
        forgotPasswordContainer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        forgotPasswordContainer.setPreferredSize(new Dimension(400, 600)); // Increased height

        // Title with emoji
        JLabel title = new JLabel("Reset Password ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 0, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordContainer.add(title);

        JLabel subtitle = new JLabel("Enter your email to receive OTP");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(0, 0, 0));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordContainer.add(subtitle);
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // Email Field
        emailField = createStyledTextField("Email Address");
        forgotPasswordContainer.add(emailField);
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Send OTP Button
        sendOtpButton = new JButton("Send OTP Code");
        sendOtpButton.setMaximumSize(new Dimension(300, 45));
        sendOtpButton.setBackground(new Color(0, 0, 0));
        sendOtpButton.setForeground(Color.WHITE);
        sendOtpButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendOtpButton.setFocusPainted(false);
        sendOtpButton.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        sendOtpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendOtpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendOtpButton.addActionListener(e -> sendOTP());

        // Send OTP button hover effect
        sendOtpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sendOtpButton.setBackground(new Color(142, 142, 142));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sendOtpButton.setBackground(new Color(0, 0, 0));
            }
        });

        forgotPasswordContainer.add(sendOtpButton);
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // OTP Status Label
        otpStatusLabel = new JLabel(" ");
        otpStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        otpStatusLabel.setForeground(new Color(0, 150, 0));
        otpStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordContainer.add(otpStatusLabel);
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // New Password Field
        newPasswordField = createStyledPasswordField("New Password");
        forgotPasswordContainer.add(newPasswordField);
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Confirm Password Field
        confirmPasswordField = createStyledPasswordField("Confirm Password");
        forgotPasswordContainer.add(confirmPasswordField);
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Reset Password Button
        resetButton = new JButton("Reset Password");
        resetButton.setMaximumSize(new Dimension(300, 45));
        resetButton.setBackground(new Color(0, 0, 0));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetPassword());

        // Reset button hover effect
        resetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                resetButton.setBackground(new Color(142, 142, 142));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                resetButton.setBackground(new Color(0, 0, 0));
            }
        });

        forgotPasswordContainer.add(resetButton);
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Divider with OR text
        JPanel dividerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        dividerPanel.setBackground(Color.WHITE);
        dividerPanel.setMaximumSize(new Dimension(300, 20));

        JSeparator leftDivider = new JSeparator(SwingConstants.HORIZONTAL);
        leftDivider.setPreferredSize(new Dimension(120, 1));
        leftDivider.setForeground(new Color(219, 219, 219));

        JLabel orLabel = new JLabel("OR");
        orLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        orLabel.setForeground(new Color(142, 142, 142));

        JSeparator rightDivider = new JSeparator(SwingConstants.HORIZONTAL);
        rightDivider.setPreferredSize(new Dimension(120, 1));
        rightDivider.setForeground(new Color(0, 0, 0));

        dividerPanel.add(leftDivider);
        dividerPanel.add(orLabel);
        dividerPanel.add(rightDivider);

        forgotPasswordContainer.add(dividerPanel);
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back to Login Section
        JPanel backPanel = new JPanel();
        backPanel.setBackground(Color.WHITE);
        backPanel.setMaximumSize(new Dimension(300, 50));
        backPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JLabel rememberLabel = new JLabel("Remember your password?");
        rememberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        rememberLabel.setForeground(new Color(0, 0, 0));

        backButton = new JButton("Sign in");
        backButton.setForeground(new Color(0, 0, 0));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // Back button hover effect
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setForeground(new Color(142, 142, 142));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setForeground(new Color(0, 0, 0));
            }
        });

        backPanel.add(rememberLabel);
        backPanel.add(backButton);

        forgotPasswordContainer.add(backPanel);

        // Add security info
        forgotPasswordContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel securityInfo = new JLabel("<html><div style='text-align: center; color: #666; font-size: 12px;'>🔒 Your information is secure and encrypted</div></html>");
        securityInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordContainer.add(securityInfo);

        // Create Scroll Pane with proper sizing
        JScrollPane scrollPane = new JScrollPane(forgotPasswordContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(450, 600));

        // Add scroll pane to right panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(scrollPane, gbc);

        // Add panels to main panel
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        field.setBackground(new Color(250, 250, 250));
        field.setText(placeholder);
        field.setForeground(new Color(142, 142, 142));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0)),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(142, 142, 142));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0)),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(300, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        field.setBackground(new Color(250, 250, 250));
        field.setEchoChar((char) 0); // Show text initially
        field.setText(placeholder);
        field.setForeground(new Color(142, 142, 142));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0)),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(new Color(142, 142, 142));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0)),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
            }
        });

        return field;
    }

    private void sendOTP() {
        String email = emailField.getText().trim();

        // Check if placeholder text is still there
        if (email.equals("Email Address") || email.isEmpty()) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Please enter your email address!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!UserService.isEmailExists(email)) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Email not found in our system!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable button and show loading
        sendOtpButton.setText("Sending OTP...");
        sendOtpButton.setEnabled(false);

        // Simulate OTP sending (in real app, this would be async)
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate network delay

                String otp = OTPService.generateOTP();
                boolean stored = OTPService.storeOTP(email, otp);
                boolean sent = EmailService.sendOTPEmail(email, otp);

                SwingUtilities.invokeLater(() -> {
                    if (stored && sent) {
                        otpStatusLabel.setText("✓ OTP sent to your email! Check your inbox.");
                        otpStatusLabel.setForeground(new Color(0, 150, 0));
                        JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                                "OTP has been sent to your email address!",
                                "OTP Sent", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        otpStatusLabel.setText("✗ Failed to send OTP. Please try again.");
                        otpStatusLabel.setForeground(Color.RED);
                    }

                    sendOtpButton.setText("Send OTP Code");
                    sendOtpButton.setEnabled(true);
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void resetPassword() {
        String email = emailField.getText().trim();
        String newPassword = String.valueOf(newPasswordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());

        // Check if placeholder text is still there
        if (email.equals("Email Address") || email.isEmpty()) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Please enter your email address!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (String.valueOf(newPasswordField.getPassword()).equals("New Password") || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Please enter your new password!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (String.valueOf(confirmPasswordField.getPassword()).equals("Confirm Password") || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Please confirm your password!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Passwords do not match!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Password must be at least 6 characters long!",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verify OTP
        String otp = JOptionPane.showInputDialog(ForgotPasswordFrame.this,
                "Enter the 6-digit OTP sent to your email:",
                "OTP Verification", JOptionPane.QUESTION_MESSAGE);

        if (otp == null || otp.trim().isEmpty()) {
            return; // User cancelled
        }

        if (!OTPService.verifyOTP(email, otp.trim())) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Invalid OTP! Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Reset password
        resetButton.setText("Resetting...");
        resetButton.setEnabled(false);

        boolean success = UserService.resetPassword(email, newPassword);

        if (success) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Password reset successfully! You can now login with your new password.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
                    "Password reset failed! Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);

            resetButton.setText("Reset Password");
            resetButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ForgotPasswordFrame frame = new ForgotPasswordFrame();
            frame.setVisible(true);
        });
    }
}