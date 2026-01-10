package com.onlinevotingsystem.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private static final String FROM_EMAIL = "shakibpatoary34@gmail.com";
    private static final String PASSWORD = "npbx ndxg zqub hegq";

    // 1️⃣ Send OTP Email
    public static boolean sendOTPEmail(String toEmail, String otp) {
        try {
            // Enhanced email configuration with TLS fixes
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            // Additional TLS settings
            props.put("mail.smtp.ssl.enable", "false");
            props.put("mail.smtp.starttls.enable", "true");

            // Timeout settings
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");

            System.out.println("🔧 Attempting to connect to Gmail SMTP...");

            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            // Enable debug mode to see connection details
            session.setDebug(true);

            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Online Voting System - OTP Verification");

            String emailContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #2E86AB;">Online Voting System</h2>
                    <div style="background: #f8f9fa; padding: 20px; border-radius: 10px;">
                        <h3 style="color: #333;">OTP Verification Code</h3>
                        <p>Your One-Time Password (OTP) for verification is:</p>
                        <div style="text-align: center; margin: 20px 0;">
                            <span style="font-size: 32px; font-weight: bold; color: #2E86AB; 
                                       background: #e9ecef; padding: 10px 20px; border-radius: 5px;">
                                %s
                            </span>
                        </div>
                        <p><strong>This OTP will expire in 10 minutes.</strong></p>
                        <p>If you didn't request this OTP, please ignore this email.</p>
                    </div>
                    <p style="color: #666; font-size: 12px; margin-top: 20px;">
                        This is an automated message from Online Voting System.
                    </p>
                </div>
                """.formatted(otp);

            message.setContent(emailContent, "text/html; charset=utf-8");

            System.out.println("📧 Sending email to: " + toEmail);

            // Send email
            Transport.send(message);
            System.out.println("✅ Real OTP email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();

            // Fallback to demo mode
            return sendDemoOTP(toEmail, otp);
        }
    }

    // 2️⃣ Send Vote Confirmation Email
    public static boolean sendVoteConfirmationEmail(String toEmail, String subject, String htmlContent) {
        try {
            // Same email configuration as OTP email
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Vote confirmation email sent to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Vote confirmation email failed: " + e.getMessage());
            // Demo mode fallback
            return sendDemoVoteConfirmation(toEmail, subject);
        }
    }

    // 3️⃣ Send Password Reset Email
    public static boolean sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Online Voting System - Password Reset");

            String emailContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #2E86AB;">Password Reset Request</h2>
                    <div style="background: #f8f9fa; padding: 20px; border-radius: 10px;">
                        <p>You requested to reset your password. Click the link below to reset your password:</p>
                        <div style="text-align: center; margin: 20px 0;">
                            <a href="%s" style="background-color: #2E86AB; color: white; padding: 12px 24px; 
                                              text-decoration: none; border-radius: 5px; display: inline-block;">
                                Reset Password
                            </a>
                        </div>
                        <p><strong>This link will expire in 1 hour.</strong></p>
                        <p>If you didn't request this reset, please ignore this email.</p>
                    </div>
                </div>
                """.formatted(resetLink);

            message.setContent(emailContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Password reset email sent to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Password reset email failed: " + e.getMessage());
            return sendDemoOTP(toEmail, "Reset Link: " + resetLink);
        }
    }

    // 4️⃣ Send Election Notification
    public static boolean sendElectionNotification(String toEmail, String electionTitle, String messageType) {
        String subject = "";
        String content = "";

        switch (messageType) {
            case "START":
                subject = "Election Started - " + electionTitle;
                content = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                        <h2 style="color: #28a745;">🗳️ Election Started</h2>
                        <div style="background: #f8f9fa; padding: 20px; border-radius: 10px;">
                            <h3>%s has started!</h3>
                            <p>You can now cast your vote in this election.</p>
                            <p>Please login to the Online Voting System to participate.</p>
                        </div>
                    </div>
                    """.formatted(electionTitle);
                break;

            case "END":
                subject = "Election Ended - " + electionTitle;
                content = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                        <h2 style="color: #dc3545;">⏹️ Election Ended</h2>
                        <div style="background: #f8f9fa; padding: 20px; border-radius: 10px;">
                            <h3>%s has ended!</h3>
                            <p>Thank you for participating in this election.</p>
                            <p>Results will be available soon.</p>
                        </div>
                    </div>
                    """.formatted(electionTitle);
                break;

            case "REMINDER":
                subject = "Vote Reminder - " + electionTitle;
                content = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                        <h2 style="color: #ffc107;">🔔 Vote Reminder</h2>
                        <div style="background: #f8f9fa; padding: 20px; border-radius: 10px;">
                            <h3>Don't forget to vote in %s!</h3>
                            <p>The election is still ongoing. Make sure to cast your vote before it ends.</p>
                            <p>Your vote matters!</p>
                        </div>
                    </div>
                    """.formatted(electionTitle);
                break;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Election notification sent to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Election notification failed: " + e.getMessage());
            return sendDemoOTP(toEmail, "Election Notification: " + messageType + " - " + electionTitle);
        }
    }

    // ========== DEMO MODE METHODS ==========

    // Demo mode for OTP
    private static boolean sendDemoOTP(String toEmail, String otp) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("📧 DEMO EMAIL - Online Voting System");
        System.out.println("═══════════════════════════════════════");
        System.out.println("To: " + toEmail);
        System.out.println("Subject: OTP Verification Code");
        System.out.println("Message:");
        System.out.println("Your OTP for verification is: " + otp);
        System.out.println("This OTP will expire in 10 minutes.");
        System.out.println("═══════════════════════════════════════");

        // Show OTP in dialog box for easy testing
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "📧 OTP Sent Successfully!\n\n" +
                            "To: " + toEmail + "\n" +
                            "OTP: " + otp + "\n\n" +
                            "Note: Running in demo mode. In production, this would be sent via real email.",
                    "OTP Verification",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        });

        return true;
    }

    // Demo mode for vote confirmation
    private static boolean sendDemoVoteConfirmation(String toEmail, String subject) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("📧 DEMO VOTE CONFIRMATION");
        System.out.println("═══════════════════════════════════════");
        System.out.println("To: " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: Your vote has been confirmed!");
        System.out.println("═══════════════════════════════════════");
        return true;
    }

    // 5️⃣ Test email configuration
    public static void testEmailConfiguration() {
        System.out.println("🧪 Testing Email Configuration...");
        System.out.println("From Email: " + FROM_EMAIL);
        System.out.println("Password: " + (PASSWORD.isEmpty() ? "Not Set" : "******"));

        if (FROM_EMAIL.equals("your-email@gmail.com")) {
            System.out.println("❌ Please configure your email in EmailService.java");
            System.out.println("💡 Currently running in DEMO mode");
        } else {
            System.out.println("✅ Email configured - will attempt real email sending");
        }

        // Test connection
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", 587, FROM_EMAIL, PASSWORD);
            transport.close();

            System.out.println("✅ SMTP Connection Test: SUCCESS");
        } catch (Exception e) {
            System.out.println("❌ SMTP Connection Test: FAILED - " + e.getMessage());
            System.out.println("💡 Using demo mode for email delivery");
        }
    }
}