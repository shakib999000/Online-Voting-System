package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.Election;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ElectionService {

    // 1️⃣ Get active elections for display (existing method)
    public static String[] getActiveElectionsForDisplay() {
        List<String> elections = new ArrayList<>();
        String sql = "SELECT id, title, status FROM elections WHERE status IN ('UPCOMING', 'ONGOING')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String status = rs.getString("status");
                elections.add("ID: " + id + " - " + title + " (" + status + ")");
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get elections failed: " + e.getMessage());
        }

        return elections.toArray(new String[0]);
    }

    // 2️⃣ Create new election
    public static boolean createElection(Election election) {
        String sql = "INSERT INTO elections (title, description, start_date, end_date, status, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, election.getTitle());
            pstmt.setString(2, election.getDescription());
            pstmt.setTimestamp(3, election.getStartDate());
            pstmt.setTimestamp(4, election.getEndDate());
            pstmt.setString(5, election.getStatus());
            pstmt.setInt(6, election.getCreatedBy());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated election ID
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    election.setId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Election created successfully: " + election.getTitle());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election creation failed: " + e.getMessage());
        }
        return false;
    }

    // 3️⃣ Get all elections
    public static List<Election> getAllElections() {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Election election = extractElectionFromResultSet(rs);
                elections.add(election);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get elections failed: " + e.getMessage());
        }
        return elections;
    }

    // 4️⃣ Get election by ID
    public static Election getElectionById(int electionId) {
        String sql = "SELECT * FROM elections WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractElectionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get election failed: " + e.getMessage());
        }
        return null;
    }

    // 5️⃣ Update election
    public static boolean updateElection(Election election) {
        String sql = "UPDATE elections SET title = ?, description = ?, start_date = ?, " +
                "end_date = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, election.getTitle());
            pstmt.setString(2, election.getDescription());
            pstmt.setTimestamp(3, election.getStartDate());
            pstmt.setTimestamp(4, election.getEndDate());
            pstmt.setString(5, election.getStatus());
            pstmt.setInt(6, election.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election update failed: " + e.getMessage());
        }
        return false;
    }

    // 6️⃣ Delete election
    public static boolean deleteElection(int electionId) {
        String sql = "DELETE FROM elections WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election deletion failed: " + e.getMessage());
        }
        return false;
    }

    // 7️⃣ Get active elections (for voting)
    public static List<Election> getActiveElections() {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections WHERE status IN ('UPCOMING', 'ONGOING') ORDER BY start_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Election election = extractElectionFromResultSet(rs);
                elections.add(election);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get active elections failed: " + e.getMessage());
        }
        return elections;
    }

    // 8️⃣ Update election status automatically
    public static void updateElectionStatuses() {
        String sql = "UPDATE elections SET status = CASE " +
                "WHEN start_date > NOW() THEN 'UPCOMING' " +
                "WHEN end_date < NOW() THEN 'COMPLETED' " +
                "ELSE 'ONGOING' END " +
                "WHERE status != 'CANCELLED'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int updated = stmt.executeUpdate(sql);
            if (updated > 0) {
                System.out.println("✅ Updated status for " + updated + " elections");
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election status update failed: " + e.getMessage());
        }
    }

    // 9️⃣ Get elections created by specific user
    public static List<Election> getElectionsByCreator(int createdBy) {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections WHERE created_by = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, createdBy);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = extractElectionFromResultSet(rs);
                elections.add(election);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get elections by creator failed: " + e.getMessage());
        }
        return elections;
    }

    // 🔟 Close election (mark as completed)
    public static boolean closeElection(int electionId) {
        String sql = "UPDATE elections SET status = 'COMPLETED', end_date = NOW() WHERE id = ? AND status = 'ONGOING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Election closed successfully: ID " + electionId);
                return true;
            } else {
                System.out.println("❌ Cannot close election: ID " + electionId + " - Not ONGOING or doesn't exist");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Close election failed: " + e.getMessage());
        }
        return false;
    }

    // 1️⃣1️⃣ Start Election (change from UPCOMING to ONGOING) - NEW METHOD
    public static boolean startElection(int electionId) {
        String sql = "UPDATE elections SET status = 'ONGOING' WHERE id = ? AND status = 'UPCOMING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Election started successfully: ID " + electionId);
                return true;
            } else {
                System.out.println("❌ Cannot start election: ID " + electionId + " - Not UPCOMING or doesn't exist");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Error starting election: " + e.getMessage());
            return false;
        }
    }

    // 1️⃣2️⃣ Get elections by status - NEW METHOD
    public static List<Election> getElectionsByStatus(String status) {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = extractElectionFromResultSet(rs);
                elections.add(election);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get elections by status failed: " + e.getMessage());
        }
        return elections;
    }

    // 1️⃣3️⃣ Get closable elections (ONGOING only) - NEW METHOD
    public static List<Election> getClosableElections() {
        return getElectionsByStatus("ONGOING");
    }

    // 1️⃣4️⃣ Get startable elections (UPCOMING only) - NEW METHOD
    public static List<Election> getStartableElections() {
        return getElectionsByStatus("UPCOMING");
    }

    // 1️⃣5️⃣ Get election statistics
    public static Map<String, Object> getElectionStats(int electionId) {
        Map<String, Object> stats = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total candidates
            String candidatesSql = "SELECT COUNT(*) as total FROM candidates WHERE election_id = ? AND is_active = TRUE";
            try (PreparedStatement pstmt = conn.prepareStatement(candidatesSql)) {
                pstmt.setInt(1, electionId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalCandidates", rs.getInt("total"));
                }
            }

            // Total votes
            String votesSql = "SELECT COUNT(*) as total FROM votes WHERE election_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(votesSql)) {
                pstmt.setInt(1, electionId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalVotes", rs.getInt("total"));
                }
            }

            // Voter turnout (if you have total voters data)
            String votersSql = "SELECT COUNT(*) as total FROM users WHERE role = 'VOTER' AND is_verified = TRUE";
            try (PreparedStatement pstmt = conn.prepareStatement(votersSql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int totalVoters = rs.getInt("total");
                    int totalVotes = (int) stats.getOrDefault("totalVotes", 0);
                    double turnout = totalVoters > 0 ? (totalVotes * 100.0 / totalVoters) : 0;
                    stats.put("voterTurnout", String.format("%.2f%%", turnout));
                }
            }

            // Election details
            Election election = getElectionById(electionId);
            if (election != null) {
                stats.put("election", election);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election stats failed: " + e.getMessage());
        }

        return stats;
    }

    // 1️⃣6️⃣ Check if user can vote in election - NEW METHOD
    public static boolean canUserVote(int userId, int electionId) {
        String sql = "SELECT COUNT(*) as can_vote FROM users u " +
                "WHERE u.id = ? AND u.role = 'VOTER' AND u.is_verified = TRUE " +
                "AND NOT EXISTS (SELECT 1 FROM votes v WHERE v.voter_id = ? AND v.election_id = ?) " +
                "AND EXISTS (SELECT 1 FROM elections e WHERE e.id = ? AND e.status = 'ONGOING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, electionId);
            pstmt.setInt(4, electionId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("can_vote") > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Check user vote eligibility failed: " + e.getMessage());
        }
        return false;
    }

    // 1️⃣7️⃣ Get election results summary - NEW METHOD
    public static Map<String, Object> getElectionResultsSummary(int electionId) {
        Map<String, Object> summary = new HashMap<>();

        String sql = "SELECT " +
                "c.position, " +
                "c.name as candidate_name, " +
                "c.party, " +
                "c.vote_count, " +
                "ROUND((c.vote_count * 100.0 / NULLIF((SELECT SUM(vote_count) FROM candidates WHERE election_id = ?), 0)), 2) as percentage " +
                "FROM candidates c " +
                "WHERE c.election_id = ? AND c.is_active = TRUE " +
                "ORDER BY c.position, c.vote_count DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> candidateResult = new HashMap<>();
                candidateResult.put("position", rs.getString("position"));
                candidateResult.put("candidateName", rs.getString("candidate_name"));
                candidateResult.put("party", rs.getString("party"));
                candidateResult.put("voteCount", rs.getInt("vote_count"));
                candidateResult.put("percentage", rs.getDouble("percentage"));
                results.add(candidateResult);
            }

            summary.put("results", results);
            summary.put("totalCandidates", results.size());

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get election results summary failed: " + e.getMessage());
        }

        return summary;
    }

    // Helper method to extract election from ResultSet
    private static Election extractElectionFromResultSet(ResultSet rs) throws SQLException {
        Election election = new Election();
        election.setId(rs.getInt("id"));
        election.setTitle(rs.getString("title"));
        election.setDescription(rs.getString("description"));
        election.setStartDate(rs.getTimestamp("start_date"));
        election.setEndDate(rs.getTimestamp("end_date"));
        election.setStatus(rs.getString("status"));
        election.setCreatedBy(rs.getInt("created_by"));
        election.setTotalVotes(rs.getInt("total_votes"));
        election.setCreatedAt(rs.getTimestamp("created_at"));
        return election;
    }
}