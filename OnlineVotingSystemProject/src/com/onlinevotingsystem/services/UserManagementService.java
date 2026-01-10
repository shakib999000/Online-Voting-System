package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManagementService {

    // 1️⃣ Get all users
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get all users failed: " + e.getMessage());
        }
        return users;
    }

    // 2️⃣ Get users by role
    public static List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get users by role failed: " + e.getMessage());
        }
        return users;
    }

    // 3️⃣ Search users
    public static List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE full_name LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Search users failed: " + e.getMessage());
        }
        return users;
    }

    // 4️⃣ Update user role
    public static boolean updateUserRole(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Update user role failed: " + e.getMessage());
        }
        return false;
    }

    // 5️⃣ Verify user account
    public static boolean verifyUserAccount(int userId) {
        String sql = "UPDATE users SET is_verified = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Verify user failed: " + e.getMessage());
        }
        return false;
    }

    // 6️⃣ Suspend user account
    public static boolean suspendUserAccount(int userId) {
        String sql = "UPDATE users SET is_eligible = FALSE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Suspend user failed: " + e.getMessage());
        }
        return false;
    }

    // 7️⃣ Activate user account
    public static boolean activateUserAccount(int userId) {
        String sql = "UPDATE users SET is_eligible = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Activate user failed: " + e.getMessage());
        }
        return false;
    }

    // 8️⃣ Delete user account
    public static boolean deleteUserAccount(int userId) {
        String sql = "DELETE FROM users WHERE id = ? AND role != 'ADMIN'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Delete user failed: " + e.getMessage());
        }
        return false;
    }

    // 9️⃣ Get user statistics
    public static Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total users
            String totalSql = "SELECT COUNT(*) as total FROM users";
            try (PreparedStatement pstmt = conn.prepareStatement(totalSql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalUsers", rs.getInt("total"));
                }
            }

            // Total voters
            String votersSql = "SELECT COUNT(*) as total FROM users WHERE role = 'VOTER'";
            try (PreparedStatement pstmt = conn.prepareStatement(votersSql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalVoters", rs.getInt("total"));
                }
            }

            // Total admins
            String adminsSql = "SELECT COUNT(*) as total FROM users WHERE role = 'ADMIN'";
            try (PreparedStatement pstmt = conn.prepareStatement(adminsSql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalAdmins", rs.getInt("total"));
                }
            }

            // Verified users
            String verifiedSql = "SELECT COUNT(*) as total FROM users WHERE is_verified = TRUE";
            try (PreparedStatement pstmt = conn.prepareStatement(verifiedSql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("verifiedUsers", rs.getInt("total"));
                }
            }

            // Active users
            String activeSql = "SELECT COUNT(*) as total FROM users WHERE is_eligible = TRUE";
            try (PreparedStatement pstmt = conn.prepareStatement(activeSql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("activeUsers", rs.getInt("total"));
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] User statistics failed: " + e.getMessage());
        }

        return stats;
    }

    // 🔟 Get user voting history
    public static List<Map<String, Object>> getUserVotingHistory(int userId) {
        List<Map<String, Object>> history = new ArrayList<>();

        String sql = """
            SELECT 
                e.title as election_title,
                c.name as candidate_name,
                c.party as candidate_party,
                v.voted_at
            FROM votes v
            JOIN elections e ON v.election_id = e.id
            JOIN candidates c ON v.candidate_id = c.id
            WHERE v.voter_id = ?
            ORDER BY v.voted_at DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> vote = new HashMap<>();
                vote.put("electionTitle", rs.getString("election_title"));
                vote.put("candidateName", rs.getString("candidate_name"));
                vote.put("candidateParty", rs.getString("candidate_party"));
                vote.put("votedAt", rs.getTimestamp("voted_at"));

                history.add(vote);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] User voting history failed: " + e.getMessage());
        }

        return history;
    }

    // Helper method to extract user from ResultSet
    private static User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setEligible(rs.getBoolean("is_eligible"));
        user.setDateOfBirth(rs.getString("date_of_birth"));
        user.setNidNumber(rs.getString("nid_number"));

        return user;
    }
}