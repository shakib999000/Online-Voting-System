package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.User;
import com.onlinevotingsystem.utils.PasswordHasher;

import java.sql.*;

public class UserService {

    // 1️⃣ Check if email already exists
    public static boolean isEmailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // If record exists, return true

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Email check failed: " + e.getMessage());
            return false;
        }
    }

    // 2️⃣ Register User (Voter or Admin)
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (full_name, email, password, role, phone, address, is_verified) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Hash the password before storing
            String hashedPassword = PasswordHasher.hashPassword(user.getPassword());

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setBoolean(7, true); // Mark as verified after OTP

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Registration failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 3️⃣ Login User (Voter/Admin)
    public static User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND is_verified = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");

                // Verify password
                if (PasswordHasher.checkPassword(password, storedHashedPassword)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(storedHashedPassword); // Store hashed password
                    user.setRole(rs.getString("role"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));

                    return user;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Login failed: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // 4️⃣ Reset Password
    public static boolean resetPassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = PasswordHasher.hashPassword(newPassword);

            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Password reset failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 5️⃣ Get User by Email
    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get user failed: " + e.getMessage());
        }

        return null;
    }

    // 6️⃣ Update User Profile
    public static boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, address = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getAddress());
            pstmt.setInt(4, user.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Profile update failed: " + e.getMessage());
            return false;
        }
    }

    // 7️⃣ Check if user is eligible to vote
    public static boolean isUserEligibleToVote(int userId, int electionId) {
        String sql = "SELECT is_eligible FROM voter_eligibility WHERE user_id = ? AND election_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("is_eligible");
            }

            // If no specific eligibility record, check user's general eligibility
            return checkGeneralEligibility(userId);

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Eligibility check failed: " + e.getMessage());
            return false;
        }
    }

    // 8️⃣ Check general eligibility (age, verification status)
    private static boolean checkGeneralEligibility(int userId) {
        String sql = "SELECT is_verified FROM users WHERE id = ? AND is_verified = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Eligible if user is verified

        } catch (SQLException e) {
            System.err.println("❌ [OVS] General eligibility check failed: " + e.getMessage());
            return false;
        }
    }

    // UserService.java - এই method টি add করুন
    public static User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get user by ID failed: " + e.getMessage());
        }

        return null;
    }

    // 9️⃣ Check if user has already voted in an election
    public static boolean hasUserVoted(int userId, int electionId) {
        String sql = "SELECT id FROM votes WHERE voter_id = ? AND election_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Return true if vote record exists

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote check failed: " + e.getMessage());
            return false;
        }
    }

    // 🔟 Get total user count
    public static int getTotalUsers() {
        String sql = "SELECT COUNT(*) as total FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] User count failed: " + e.getMessage());
        }

        return 0;
    }

    // 1️⃣1️⃣ Delete user (Admin only)
    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ? AND role != 'ADMIN'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] User deletion failed: " + e.getMessage());
            return false;
        }
    }
}