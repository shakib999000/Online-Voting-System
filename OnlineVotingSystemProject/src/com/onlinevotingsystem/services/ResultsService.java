package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsService {

    // 1️⃣ Get completed elections
    public static List<Map<String, Object>> getCompletedElections() {
        List<Map<String, Object>> elections = new ArrayList<>();
        String sql = "SELECT id, title, total_votes FROM elections WHERE status = 'COMPLETED' ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> election = new HashMap<>();
                election.put("id", rs.getInt("id"));
                election.put("title", rs.getString("title"));
                election.put("totalVotes", rs.getInt("total_votes"));
                elections.add(election);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get completed elections failed: " + e.getMessage());
        }

        return elections;
    }

    // 2️⃣ Check if election has results
    public static boolean hasResults(int electionId) {
        String sql = "SELECT COUNT(*) as vote_count FROM votes WHERE election_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("vote_count") > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Results check failed: " + e.getMessage());
        }

        return false;
    }

    // 3️⃣ Get election results with detailed information
    public static List<Map<String, Object>> getElectionResults(int electionId) {
        List<Map<String, Object>> results = new ArrayList<>();

        String sql = """
            SELECT 
                e.title as electionTitle,
                c.position,
                c.name as candidateName,
                c.party,
                c.vote_count,
                ROUND((c.vote_count * 100.0 / NULLIF((
                    SELECT SUM(vote_count) 
                    FROM candidates c2 
                    WHERE c2.election_id = ? AND c2.position = c.position
                ), 0)), 2) as percentage
            FROM candidates c
            JOIN elections e ON c.election_id = e.id
            WHERE c.election_id = ? AND c.is_active = TRUE
            ORDER BY c.position, c.vote_count DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("electionTitle", rs.getString("electionTitle"));
                result.put("position", rs.getString("position"));
                result.put("candidateName", rs.getString("candidateName"));
                result.put("party", rs.getString("party"));
                result.put("voteCount", rs.getInt("vote_count"));
                result.put("percentage", rs.getDouble("percentage"));
                results.add(result);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election results fetch failed: " + e.getMessage());
        }

        return results;
    }

    // 4️⃣ Get winners by position
    public static List<Map<String, Object>> getWinnersByPosition(int electionId) {
        List<Map<String, Object>> winners = new ArrayList<>();

        String sql = """
            WITH RankedCandidates AS (
                SELECT 
                    position,
                    name as candidateName,
                    party,
                    vote_count,
                    ROW_NUMBER() OVER (PARTITION BY position ORDER BY vote_count DESC) as rank
                FROM candidates 
                WHERE election_id = ? AND is_active = TRUE
            )
            SELECT position, candidateName, party, vote_count
            FROM RankedCandidates 
            WHERE rank = 1 AND vote_count > 0
            ORDER BY position
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> winner = new HashMap<>();
                winner.put("position", rs.getString("position"));
                winner.put("candidateName", rs.getString("candidateName"));
                winner.put("party", rs.getString("party"));
                winner.put("voteCount", rs.getInt("vote_count"));
                winners.add(winner);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Winners fetch failed: " + e.getMessage());
        }

        return winners;
    }

    // 5️⃣ Get election statistics
    public static Map<String, Object> getElectionStatistics(int electionId) {
        Map<String, Object> stats = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total votes in election
            String votesSql = "SELECT COUNT(*) as total FROM votes WHERE election_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(votesSql)) {
                pstmt.setInt(1, electionId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalVotes", rs.getInt("total"));
                }
            }

            // Total voters (verified users)
            String votersSql = "SELECT COUNT(*) as total FROM users WHERE role = 'VOTER' AND is_verified = TRUE";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(votersSql)) {
                if (rs.next()) {
                    stats.put("totalVoters", rs.getInt("total"));
                }
            }

            // Total candidates
            String candidatesSql = "SELECT COUNT(*) as total FROM candidates WHERE election_id = ? AND is_active = TRUE";
            try (PreparedStatement pstmt = conn.prepareStatement(candidatesSql)) {
                pstmt.setInt(1, electionId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalCandidates", rs.getInt("total"));
                }
            }

            // Calculate turnout percentage
            int totalVotes = (int) stats.get("totalVotes");
            int totalVoters = (int) stats.get("totalVoters");
            double turnoutPercentage = totalVoters > 0 ? (totalVotes * 100.0) / totalVoters : 0.0;
            stats.put("turnoutPercentage", String.format("%.1f", turnoutPercentage));

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election statistics failed: " + e.getMessage());
        }

        return stats;
    }

    // 6️⃣ Generate comprehensive results report
    public static String generateResultsReport(int electionId) {
        StringBuilder report = new StringBuilder();

        // Get election details
        Map<String, Object> electionDetails = getElectionDetails(electionId);
        List<Map<String, Object>> results = getElectionResults(electionId);
        List<Map<String, Object>> winners = getWinnersByPosition(electionId);
        Map<String, Object> stats = getElectionStatistics(electionId);

        // Report Header
        report.append("=".repeat(80)).append("\n");
        report.append("                     ELECTION RESULTS REPORT\n");
        report.append("=".repeat(80)).append("\n\n");

        report.append("Election: ").append(electionDetails.get("title")).append("\n");
        report.append("Date: ").append(new java.util.Date()).append("\n");
        report.append("Status: ").append(electionDetails.get("status")).append("\n");
        report.append("-".repeat(80)).append("\n\n");

        // Statistics Summary
        report.append("📊 ELECTION STATISTICS\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("Total Votes Cast: %d\n", stats.get("totalVotes")));
        report.append(String.format("Total Registered Voters: %d\n", stats.get("totalVoters")));
        report.append(String.format("Voter Turnout: %s%%\n", stats.get("turnoutPercentage")));
        report.append(String.format("Total Candidates: %d\n", stats.get("totalCandidates")));
        report.append(String.format("Total Positions: %d\n", winners.size()));
        report.append("\n");

        // Winners Section
        report.append("🏆 ELECTION WINNERS\n");
        report.append("-".repeat(40)).append("\n");
        for (Map<String, Object> winner : winners) {
            report.append(String.format("📍 %s\n", winner.get("position")));
            report.append(String.format("   Winner: %s (%s)\n", winner.get("candidateName"), winner.get("party")));
            report.append(String.format("   Votes: %d\n\n", winner.get("voteCount")));
        }

        // Detailed Results by Position
        report.append("📋 DETAILED RESULTS BY POSITION\n");
        report.append("=".repeat(80)).append("\n");

        String currentPosition = "";
        for (Map<String, Object> result : results) {
            String position = (String) result.get("position");
            if (!position.equals(currentPosition)) {
                currentPosition = position;
                report.append("\n").append(position.toUpperCase()).append("\n");
                report.append("-".repeat(40)).append("\n");
            }

            boolean isWinner = winners.stream()
                    .anyMatch(w -> w.get("candidateName").equals(result.get("candidateName"))
                            && w.get("position").equals(position));

            String winnerIndicator = isWinner ? "🏆 " : "  ";
            report.append(String.format("%s %-25s %-15s %6d votes %6.1f%%\n",
                    winnerIndicator,
                    result.get("candidateName"),
                    "(" + result.get("party") + ")",
                    result.get("voteCount"),
                    result.get("percentage")));
        }

        // Footer
        report.append("\n").append("=".repeat(80)).append("\n");
        report.append("Report generated by Online Voting System\n");
        report.append("=".repeat(80)).append("\n");

        return report.toString();
    }

    // 7️⃣ Get election details
    private static Map<String, Object> getElectionDetails(int electionId) {
        Map<String, Object> details = new HashMap<>();
        String sql = "SELECT title, description, status FROM elections WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                details.put("title", rs.getString("title"));
                details.put("description", rs.getString("description"));
                details.put("status", rs.getString("status"));
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election details fetch failed: " + e.getMessage());
        }

        return details;
    }

    // 8️⃣ Get all elections for display (including completed ones)
    public static String[] getAllElectionsForDisplay() {
        List<String> elections = new ArrayList<>();
        String sql = "SELECT id, title, status, total_votes FROM elections ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String status = rs.getString("status");
                int totalVotes = rs.getInt("total_votes");

                String displayText = String.format("ID: %d - %s (%s, %d votes)",
                        id, title, status, totalVotes);
                elections.add(displayText);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get all elections failed: " + e.getMessage());
        }

        return elections.toArray(new String[0]);
    }
}