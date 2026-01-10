package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.User;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.sql.*;

public class EmailNotificationService {

    // Email configuration - Update these with your SMTP settings
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "shakibpatoary34@gmail.com"; // Update this
    private static final String EMAIL_PASSWORD = "npbx ndxg zqub hegq"; // Update this
    private static final String FROM_EMAIL = "shakibpatoary34@gmail.com"; // Update this

    // For testing without actual email
    private static final boolean ENABLE_EMAIL_SENDING = false;

    /**
     * Send vote confirmation email to voter
     */
    public static boolean sendVoteConfirmationEmail(int voterId, String candidateName, String electionTitle) {
        if (!ENABLE_EMAIL_SENDING) {
            System.out.println("📧 [SIMULATED] Vote confirmation email would be sent to voter ID: " + voterId);
            System.out.println("📧 Candidate: " + candidateName + ", Election: " + electionTitle);
            return true;
        }

        try {
            // Get voter email from database
            String voterEmail = getVoterEmail(voterId);
            if (voterEmail == null || voterEmail.isEmpty()) {
                System.err.println("❌ Voter email not found for ID: " + voterId);
                return false;
            }

            String subject = "✅ Vote Confirmation - Online Voting System";
            String body = generateVoteConfirmationEmailBody(candidateName, electionTitle);

            return sendEmail(voterEmail, subject, body);

        } catch (Exception e) {
            System.err.println("❌ Error sending vote confirmation email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send election reminder email
     */
    public static boolean sendElectionReminderEmail(int voterId, String electionTitle, String endDate) {
        if (!ENABLE_EMAIL_SENDING) {
            System.out.println("📧 [SIMULATED] Election reminder email would be sent to voter ID: " + voterId);
            return true;
        }

        try {
            String voterEmail = getVoterEmail(voterId);
            if (voterEmail == null || voterEmail.isEmpty()) {
                return false;
            }

            String subject = "🗳️ Election Reminder - " + electionTitle;
            String body = generateElectionReminderEmailBody(electionTitle, endDate);

            return sendEmail(voterEmail, subject, body);

        } catch (Exception e) {
            System.err.println("❌ Error sending election reminder email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send registration confirmation email
     */
    public static boolean sendRegistrationConfirmationEmail(String toEmail, String userName, String verificationCode) {
        if (!ENABLE_EMAIL_SENDING) {
            System.out.println("📧 [SIMULATED] Registration confirmation email would be sent to: " + toEmail);
            return true;
        }

        try {
            String subject = "👋 Welcome to Online Voting System - Verify Your Account";
            String body = generateRegistrationEmailBody(userName, verificationCode);

            return sendEmail(toEmail, subject, body);

        } catch (Exception e) {
            System.err.println("❌ Error sending registration email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get voter email from database
     */
    private static String getVoterEmail(int voterId) {
        String sql = "SELECT email FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voterId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("email");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching voter email: " + e.getMessage());
        }

        return null;
    }

    /**
     * Generate vote confirmation email body
     */
    private static String generateVoteConfirmationEmailBody(String candidateName, String electionTitle) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 10px 10px; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #666; }
                    .confirmation-box { background: white; padding: 15px; border-left: 4px solid #28a745; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Vote Confirmed</h1>
                        <p>Online Voting System</p>
                    </div>
                    <div class="content">
                        <h2>Thank You for Voting!</h2>
                        <p>Your vote has been successfully recorded in our system.</p>
                        
                        <div class="confirmation-box">
                            <h3>🗳️ Vote Details:</h3>
                            <p><strong>Election:</strong> %s</p>
                            <p><strong>Candidate:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Status:</strong> <span style="color: #28a745;">✓ CONFIRMED</span></p>
                        </div>
                        
                        <p>Your participation strengthens our democratic process. This email serves as confirmation that your vote has been counted.</p>
                        
                        <p><strong>Important:</strong> This vote is final and cannot be changed.</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message. Please do not reply to this email.</p>
                        <p>© 2024 Online Voting System. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                escapeHtml(electionTitle),
                escapeHtml(candidateName),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * Generate election reminder email body
     */
    private static String generateElectionReminderEmailBody(String electionTitle, String endDate) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #ff7e5f 0%, #feb47b 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 10px 10px; }
                    .urgent { background: #fff3cd; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🗳️ Election Reminder</h1>
                        <p>Don't Forget to Vote!</p>
                    </div>
                    <div class="content">
                        <h2>Election Ending Soon!</h2>
                        <p>The election <strong>"%s"</strong> is closing soon.</p>
                        
                        <div class="urgent">
                            <h3>⏰ Deadline: %s</h3>
                            <p>Make sure to cast your vote before the election ends!</p>
                        </div>
                        
                        <p>Your vote matters! Participate in the democratic process by making your voice heard.</p>
                        
                        <p><a href="#" style="background: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">Vote Now</a></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(escapeHtml(electionTitle), endDate);
    }

    /**
     * Generate registration confirmation email body
     */
    private static String generateRegistrationEmailBody(String userName, String verificationCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 10px 10px; }
                    .verification-code { background: #e9ecef; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>👋 Welcome to OVS</h1>
                        <p>Online Voting System</p>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>Thank you for registering with the Online Voting System. To complete your registration, please verify your email address using the code below:</p>
                        
                        <div class="verification-code">%s</div>
                        
                        <p>Enter this verification code in the application to activate your account.</p>
                        
                        <p><strong>Note:</strong> This code will expire in 24 hours.</p>
                        
                        <p>If you didn't create an account with us, please ignore this email.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(escapeHtml(userName), verificationCode);
    }

    /**
     * Send email using SMTP
     */
    private static boolean sendEmail(String toEmail, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Escape HTML for safety
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Test email functionality
     */
    public static void main(String[] args) {
        // Test vote confirmation email
        boolean success = sendVoteConfirmationEmail(1, "John Doe", "Student Union Election 2024");
        System.out.println("Test email sent: " + success);
    }
}