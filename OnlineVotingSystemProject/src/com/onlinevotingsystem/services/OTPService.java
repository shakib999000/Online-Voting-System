package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;

import java.sql.*;
import java.util.Random;

public class OTPService {

    // 1️⃣ Generate 6-digit OTP
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // 2️⃣ Store OTP into database
    public static boolean storeOTP(String email, String otp) {
        String sql = "INSERT INTO otp_codes (email, otp_code, expires_at) " +
                "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 10 MINUTE))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, otp);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] OTP storing failed: " + e.getMessage());
            return false;
        }
    }

    // 3️⃣ Verify OTP
    public static boolean verifyOTP(String email, String otp) {
        if (email == null || otp == null) {
            return false;
        }

        String sql = "SELECT id FROM otp_codes " +
                "WHERE email = ? AND otp_code = ? AND expires_at > NOW() AND is_used = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim());
            pstmt.setString(2, otp.trim());

            ResultSet rs = pstmt.executeQuery();

            // OTP matched
            if (rs.next()) {
                markOTPAsUsed(rs.getInt("id"));
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] OTP verification failed: " + e.getMessage());
            return false;
        }
    }

    // 4️⃣ Mark OTP as used
    private static void markOTPAsUsed(int otpId) {
        String sql = "UPDATE otp_codes SET is_used = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, otpId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Error marking OTP as used: " + e.getMessage());
        }
    }

    // 5️⃣ Clean expired OTPs
    public static void cleanExpiredOTPs() {
        String sql = "DELETE FROM otp_codes WHERE expires_at < NOW() OR is_used = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("✅ Expired OTPs cleaned successfully!");

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Error cleaning expired OTPs: " + e.getMessage());
        }
    }
}