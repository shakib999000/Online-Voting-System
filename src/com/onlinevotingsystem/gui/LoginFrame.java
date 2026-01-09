package com.onlinevotingsystem.gui;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.services.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, forgotPasswordButton;

    public LoginFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Online Voting System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
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

        // Right Panel - Login Form (Illustration Style)
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


        // Main login container
        JPanel loginContainer = new JPanel();
        loginContainer.setLayout(new BoxLayout(loginContainer, BoxLayout.Y_AXIS));
        loginContainer.setBackground(Color.WHITE);
        loginContainer.setBorder(BorderFactory.createEmptyBorder(40, 50, 30, 50));
        loginContainer.setPreferredSize(new Dimension(400, 550));

        // Title with emoji
        JLabel title = new JLabel("Welcome Back!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 0, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginContainer.add(title);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(0, 0, 0));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginContainer.add(subtitle);
        loginContainer.add(Box.createRigidArea(new Dimension(0, 40)));

        // Email Field
        emailField = createStyledTextField("Email Address");
        loginContainer.add(emailField);
        loginContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Password Field
        passwordField = createStyledPasswordField("Password");
        loginContainer.add(passwordField);
        loginContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Forgot Password
        forgotPasswordButton = new JButton("Forgot password?");
        forgotPasswordButton.setForeground(new Color(0, 0, 0));
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.addActionListener(e -> {
            new ForgotPasswordFrame().setVisible(true);
            dispose();
        });

        // Forgot password hover effect
        forgotPasswordButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                forgotPasswordButton.setForeground(new Color(142, 142, 142));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                forgotPasswordButton.setForeground(new Color(0, 0, 0));
            }
        });

        loginContainer.add(forgotPasswordButton);
        loginContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // Login Button
        loginButton = new JButton("Sign In");
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setBackground(new Color(0, 0, 0));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new LoginAction());

        // Login button hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(142, 142, 142));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0, 0, 0));
            }
        });

        loginContainer.add(loginButton);
        loginContainer.add(Box.createRigidArea(new Dimension(0, 25)));

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

        loginContainer.add(dividerPanel);
        loginContainer.add(Box.createRigidArea(new Dimension(0, 25)));




        // Register Section
        JPanel registerPanel = new JPanel();
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setMaximumSize(new Dimension(300, 50));
        registerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        noAccountLabel.setForeground(new Color(0, 0, 0));

        registerButton = new JButton("Sign up");
        registerButton.setForeground(new Color(0, 0, 0));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> {
            new RegistrationFrame().setVisible(true);
            dispose();
        });



        // Register button hover effect
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setForeground(new Color(142, 142, 142));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setForeground(new Color(0, 0, 0));
            }
        });

        registerPanel.add(noAccountLabel);
        registerPanel.add(registerButton);

        loginContainer.add(registerPanel);

        // Add security info
        loginContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel securityInfo = new JLabel("<html><div style='text-align: center; color: #666; font-size: 12px;'>🔒 Your information is secure and encrypted</div></html>");
        securityInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginContainer.add(securityInfo);

        // Add login container to right panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(loginContainer, gbc);

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
                BorderFactory.createLineBorder(new Color(219, 219, 219)),
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

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String password = String.valueOf(passwordField.getPassword());

            // Check if placeholder text is still there
            if (email.equals("Email Address") || email.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Please enter your email address!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (String.valueOf(passwordField.getPassword()).equals("Password") || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Please enter your password!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = UserService.loginUser(email, password);

            if (user != null) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Login Successful! Welcome " + user.getName());

                if ("ADMIN".equals(user.getRole())) {
                    new AdminDashboard(user).setVisible(true);
                } else {
                    new DashboardFrame(user).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Invalid email or password!",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}