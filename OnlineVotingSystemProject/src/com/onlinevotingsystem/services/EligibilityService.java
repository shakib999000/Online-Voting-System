package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class EligibilityService {

    // 1️⃣ Check if user is eligible to vote in a specific election
    public static EligibilityResult checkVoterEligibility(int userId, int electionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Get user details
            User user = getUserDetails(userId, conn);
            if (user == null) {
                return new EligibilityResult(false, "User not found");
            }

            // Get election details
            ElectionInfo election = getElectionDetails(electionId, conn);
            if (election == null) {
                return new EligibilityResult(false, "Election not found");
            }

            // Check 1: Age eligibility (must be at least 18 years old)
            if (!isAgeEligible(user)) {
                return new EligibilityResult(false, "Must be at least 18 years old to vote");
            }

            // Check 2: User verification status
            if (!user.isVerified()) {
                return new EligibilityResult(false, "Account not verified. Please complete verification.");
            }

            // Check 3: Check if election is ongoing
            if (!"ONGOING".equals(election.getStatus())) {
                return new EligibilityResult(false, "Election is not currently active. Status: " + election.getStatus());
            }

            // Check 4: Check if user has already voted in this election
            if (hasUserVoted(userId, electionId, conn)) {
                return new EligibilityResult(false, "You have already voted in this election");
            }

            // Check 5: Check specific eligibility rules from voter_eligibility table
            if (!checkSpecificEligibility(userId, electionId, conn)) {
                return new EligibilityResult(false, "Not eligible to vote in this election based on eligibility rules");
            }

            // All checks passed - user is eligible
            return new EligibilityResult(true, "Eligible to vote");

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Eligibility check failed: " + e.getMessage());
            return new EligibilityResult(false, "System error during eligibility check");
        }
    }

    // 2️⃣ Get user details from database
    private static User getUserDetails(int userId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setDateOfBirth(rs.getString("date_of_birth"));
                user.setVerified(rs.getBoolean("is_verified"));
                user.setEligible(rs.getBoolean("is_eligible"));
                return user;
            }
        }
        return null;
    }

    // 3️⃣ Get election details
    private static ElectionInfo getElectionDetails(int electionId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM elections WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ElectionInfo(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("status"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date")
                );
            }
        }
        return null;
    }

    // 4️⃣ Check age eligibility (must be 18+)
    private static boolean isAgeEligible(User user) {
        // If date of birth is not available, assume eligible
        if (user.getDateOfBirth() == null || user.getDateOfBirth().isEmpty()) {
            return true;
        }

        try {
            LocalDate birthDate = LocalDate.parse(user.getDateOfBirth());
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();
            return age >= 18;
        } catch (Exception e) {
            System.err.println("❌ Error calculating age: " + e.getMessage());
            return true; // If age calculation fails, assume eligible
        }
    }

    // 5️⃣ Check if user has already voted
    private static boolean hasUserVoted(int userId, int electionId, Connection conn) throws SQLException {
        String sql = "SELECT id FROM votes WHERE voter_id = ? AND election_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    // 6️⃣ Check specific eligibility rules from voter_eligibility table
    private static boolean checkSpecificEligibility(int userId, int electionId, Connection conn) throws SQLException {
        String sql = "SELECT is_eligible FROM voter_eligibility WHERE user_id = ? AND election_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("is_eligible");
            }

            // If no specific rule exists, user is generally eligible
            return true;
        }
    }

    // 7️⃣ Mark user as eligible/ineligible for an election (Admin function)
    public static boolean setEligibility(int userId, int electionId, boolean isEligible, String reason) {
        String sql = "INSERT INTO voter_eligibility (user_id, election_id, is_eligible, reason, checked_by, checked_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE is_eligible = ?, reason = ?, checked_by = ?, checked_at = NOW()";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, electionId);
            pstmt.setBoolean(3, isEligible);
            pstmt.setString(4, reason);
            pstmt.setInt(5, 1); // Assuming admin ID 1 for system
            pstmt.setBoolean(6, isEligible);
            pstmt.setString(7, reason);
            pstmt.setInt(8, 1); // Assuming admin ID 1 for system

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Set eligibility failed: " + e.getMessage());
            return false;
        }
    }

    // 8️⃣ Get eligibility status with details
    public static String getEligibilityStatus(int userId, int electionId) {
        EligibilityResult result = checkVoterEligibility(userId, electionId);
        return result.isEligible() ?
                "✅ " + result.getMessage() :
                "❌ " + result.getMessage();
    }

    // 9️⃣ Bulk eligibility check for multiple users
    public static void checkBulkEligibility(int electionId) {
        String sql = "SELECT id, full_name, email FROM users WHERE role = 'VOTER' AND is_verified = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("📊 Eligibility Report for Election ID: " + electionId);
            System.out.println("=========================================");

            int eligibleCount = 0;
            int totalCount = 0;

            while (rs.next()) {
                totalCount++;
                int userId = rs.getInt("id");
                String userName = rs.getString("full_name");

                EligibilityResult result = checkVoterEligibility(userId, electionId);

                if (result.isEligible()) {
                    eligibleCount++;
                    System.out.println("✅ " + userName + " - ELIGIBLE");
                } else {
                    System.out.println("❌ " + userName + " - NOT ELIGIBLE: " + result.getMessage());
                }
            }

            System.out.println("=========================================");
            System.out.println("📈 Summary: " + eligibleCount + " out of " + totalCount + " voters are eligible");

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Bulk eligibility check failed: " + e.getMessage());
        }
    }
}