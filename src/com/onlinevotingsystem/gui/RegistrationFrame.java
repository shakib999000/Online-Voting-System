package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.services.OTPService;
import com.onlinevotingsystem.services.UserService;
import com.onlinevotingsystem.services.EmailService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RegistrationFrame extends JFrame {

    private JTextField nameField, emailField, phoneField, nidField, ageField, addressField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, backButton;

    public RegistrationFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Online Voting System - Registration");
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

        // Right Panel - Registration Form with Glacier Gradient
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

        // Main registration container with Scroll Pane
        JPanel registerContainer = new JPanel();
        registerContainer.setLayout(new BoxLayout(registerContainer, BoxLayout.Y_AXIS));
        registerContainer.setBackground(Color.WHITE);
        registerContainer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        registerContainer.setPreferredSize(new Dimension(450, 800)); // Increased height for scrolling

        // Title
        JLabel title = new JLabel("Create New Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 0, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerContainer.add(title);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // Form Fields in single column (like login)
        // Name Field
        nameField = createStyledTextField("Full Name");
        registerContainer.add(nameField);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Email Field
        emailField = createStyledTextField("Email Address");
        registerContainer.add(emailField);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Field
        passwordField = createStyledPasswordField("Password");
        registerContainer.add(passwordField);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Confirm Password Field
        confirmPasswordField = createStyledPasswordField("Confirm Password");
        registerContainer.add(confirmPasswordField);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Phone Field
        phoneField = createStyledTextField("Phone Number");
        registerContainer.add(phoneField);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // NID Field
        nidField = createStyledTextField("NID Number");
        registerContainer.add(nidField);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Age Field
        ageField = createStyledTextField("Age");
        registerContainer.add(ageField);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Address Field
        addressField = createStyledTextField("Address");
        registerContainer.add(addressField);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // Register Button
        registerButton = new JButton("Register Account");
        registerButton.setMaximumSize(new Dimension(300, 45));
        registerButton.setBackground(new Color(0, 0, 0));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> registerUser());

        // Register button hover effect
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(142, 142, 142));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(0, 0, 0));
            }
        });

        registerContainer.add(registerButton);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Divider with OR text
        JPanel dividerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        dividerPanel.setBackground(Color.WHITE);
        dividerPanel.setMaximumSize(new Dimension(300, 20));

        JSeparator leftDivider = new JSeparator(SwingConstants.HORIZONTAL);
        leftDivider.setPreferredSize(new Dimension(120, 1));
        leftDivider.setForeground(new Color(0, 0, 0));

        JLabel orLabel = new JLabel("OR");
        orLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        orLabel.setForeground(new Color(142, 142, 142));

        JSeparator rightDivider = new JSeparator(SwingConstants.HORIZONTAL);
        rightDivider.setPreferredSize(new Dimension(120, 1));
        rightDivider.setForeground(new Color(0, 0, 0));

        dividerPanel.add(leftDivider);
        dividerPanel.add(orLabel);
        dividerPanel.add(rightDivider);

        registerContainer.add(dividerPanel);
        registerContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back to Login Section
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setMaximumSize(new Dimension(300, 50));
        loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        haveAccountLabel.setForeground(new Color(0, 0, 0));

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

        loginPanel.add(haveAccountLabel);
        loginPanel.add(backButton);

        registerContainer.add(loginPanel);

        // Add security info
        registerContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel securityInfo = new JLabel("<html><div style='text-align: center; color: #666; font-size: 12px;'>🔒 Your information is secure and encrypted</div></html>");
        securityInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerContainer.add(securityInfo);

        // Create Scroll Pane for the registration container
        JScrollPane scrollPane = new JScrollPane(registerContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(450, 650));

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

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0)),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
            }
            public void focusLost(FocusEvent evt) {
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

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
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
            public void focusLost(FocusEvent evt) {
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

    private void registerUser() {
        // Get values from form fields
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String nid = nidField.getText().trim();
        String ageStr = ageField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPass = String.valueOf(confirmPasswordField.getPassword());
        String address = addressField.getText().trim();

        // Check if placeholder text is still there
        if (name.equals("Full Name") || name.isEmpty()) {
            showError("Please enter your full name!");
            return;
        }

        if (email.equals("Email Address") || email.isEmpty()) {
            showError("Please enter your email address!");
            return;
        }

        if (String.valueOf(passwordField.getPassword()).equals("Password") || password.isEmpty()) {
            showError("Please enter your password!");
            return;
        }

        if (String.valueOf(confirmPasswordField.getPassword()).equals("Confirm Password") || confirmPass.isEmpty()) {
            showError("Please confirm your password!");
            return;
        }

        if (phone.equals("Phone Number") || phone.isEmpty()) {
            showError("Please enter your phone number!");
            return;
        }

        if (nid.equals("NID Number") || nid.isEmpty()) {
            showError("Please enter your NID number!");
            return;
        }

        if (ageStr.equals("Age") || ageStr.isEmpty()) {
            showError("Please enter your age!");
            return;
        }

        if (address.equals("Address") || address.isEmpty()) {
            showError("Please enter your address!");
            return;
        }

        // Validate age
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 18) {
                JOptionPane.showMessageDialog(this,
                        "You must be at least 18 years old to register!",
                        "Age Restriction", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid age!",
                    "Invalid Age", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate password
        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match!",
                    "Password Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long!",
                    "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address!",
                    "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if email already exists
        if (UserService.isEmailExists(email)) {
            JOptionPane.showMessageDialog(this,
                    "This email is already registered! Please use a different email.",
                    "Email Exists", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show loading state
        registerButton.setText("Processing...");
        registerButton.setEnabled(false);
        backButton.setEnabled(false);

        // OTP Verification Process
        new Thread(() -> {
            try {
                // Generate and send OTP
                String otp = OTPService.generateOTP();
                boolean otpStored = OTPService.storeOTP(email, otp);
                boolean emailSent = EmailService.sendOTPEmail(email, otp);

                SwingUtilities.invokeLater(() -> {
                    if (!otpStored || !emailSent) {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                                "Failed to send OTP. Please try again.",
                                "OTP Error", JOptionPane.ERROR_MESSAGE);
                        resetButtons();
                        return;
                    }

                    // Show OTP input dialog
                    String enteredOtp = JOptionPane.showInputDialog(
                            RegistrationFrame.this,
                            "A 6-digit OTP has been sent to your email.\nPlease enter the OTP to complete registration:",
                            "OTP Verification",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (enteredOtp == null) {
                        // User cancelled
                        resetButtons();
                        return;
                    }

                    // Verify OTP
                    if (!OTPService.verifyOTP(email, enteredOtp.trim())) {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                                "Invalid OTP! Registration cancelled.",
                                "OTP Error", JOptionPane.ERROR_MESSAGE);
                        resetButtons();
                        return;
                    }

                    // Create user object and register
                    User newUser = new User(name, email, password, "VOTER", phone, address);
                    //newUser.setNid(nid);
                    newUser.setAge(age);

                    if (UserService.registerUser(newUser)) {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                                "Registration Successful!\nYou can now login with your credentials.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Redirect to login
                        new LoginFrame().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                                "Registration Failed! Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        resetButtons();
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(RegistrationFrame.this,
                            "An error occurred during registration: " + e.getMessage(),
                            "System Error", JOptionPane.ERROR_MESSAGE);
                    resetButtons();
                });
            }
        }).start();
    }

    private void resetButtons() {
        registerButton.setText("Register Account");
        registerButton.setEnabled(true);
        backButton.setEnabled(true);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(RegistrationFrame.this,
                message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegistrationFrame frame = new RegistrationFrame();
            frame.setVisible(true);
        });
    }
}