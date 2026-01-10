package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.Vote;
import com.onlinevotingsystem.entities.Candidate;
import com.onlinevotingsystem.entities.Election;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteService {

    // 1️⃣ Cast a vote - WITH EMAIL NOTIFICATION
    public static boolean castVote(int voterId, int candidateId, int electionId) {
        // First check if already voted
        if (hasUserVoted(voterId, electionId)) {
            System.err.println("❌ User has already voted in this election");
            return false;
        }

        String sql = "INSERT INTO votes (voter_id, candidate_id, election_id, voted_at) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voterId);
            pstmt.setInt(2, candidateId);
            pstmt.setInt(3, electionId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update candidate vote count
                updateCandidateVoteCount(candidateId);
                System.out.println("✅ Vote cast successfully for voter ID: " + voterId);

                // ✅ EMAIL NOTIFICATION SEND - Vote successful হলে
                sendVoteConfirmationNotification(voterId, candidateId, electionId);

                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote casting failed: " + e.getMessage());
            if (e.getMessage().contains("unique_vote")) {
                System.err.println("❌ User has already voted in this election");
            }
        }
        return false;
    }

    // ✅ NEW METHOD: Vote confirmation email send
    private static void sendVoteConfirmationNotification(int voterId, int candidateId, int electionId) {
        try {
            // Get candidate details
            Candidate candidate = CandidateService.getCandidateById(candidateId);
            if (candidate == null) {
                System.err.println("❌ Candidate not found for email notification");
                return;
            }

            // Get election details
            Election election = ElectionService.getElectionById(electionId);
            if (election == null) {
                System.err.println("❌ Election not found for email notification");
                return;
            }

            // Send email notification to voter
            boolean emailSent = EmailNotificationService.sendVoteConfirmationEmail(
                    voterId,
                    candidate.getName(),
                    election.getTitle()
            );

            if (emailSent) {
                System.out.println("✅ Vote confirmation email sent to voter ID: " + voterId);
            } else {
                System.out.println("⚠️ Vote successful but email notification failed for voter ID: " + voterId);
            }

        } catch (Exception e) {
            System.err.println("❌ Vote confirmation notification failed: " + e.getMessage());
            // Don't return false here - vote was successful, just notification failed
        }
    }

    public static Candidate getCandidateById(int candidateId) {
        return CandidateService.getCandidateById(candidateId);
    }


    // In VoteService.java - Add this method
    public static Map<String, String> getVoteConfirmationDetails(int voterId, int electionId) {
        Map<String, String> confirmation = new HashMap<>();

        String sql = """
        SELECT 
            e.title as election_title,
            c.name as candidate_name,
            c.party as candidate_party,
            c.position as candidate_position,
            v.voted_at,
            u.full_name as voter_name,
            u.email as voter_email
        FROM votes v
        JOIN elections e ON v.election_id = e.id
        JOIN candidates c ON v.candidate_id = c.id
        JOIN users u ON v.voter_id = u.id
        WHERE v.voter_id = ? AND v.election_id = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voterId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                confirmation.put("electionTitle", rs.getString("election_title"));
                confirmation.put("candidateName", rs.getString("candidate_name"));
                confirmation.put("candidateParty", rs.getString("candidate_party"));
                confirmation.put("candidatePosition", rs.getString("candidate_position"));
                confirmation.put("votedAt", rs.getTimestamp("voted_at").toString());
                confirmation.put("voterName", rs.getString("voter_name"));
                confirmation.put("voterEmail", rs.getString("voter_email"));
                confirmation.put("status", "CONFIRMED");
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote confirmation fetch failed: " + e.getMessage());
        }

        return confirmation;
    }


// In VoteService.java - Add these methods

    // Get election winners by position
    public static Map<String, Candidate> getElectionWinners(int electionId) {
        Map<String, Candidate> winners = new HashMap<>();

        String sql = """
        SELECT c.*, c.position,
               RANK() OVER (PARTITION BY c.position ORDER BY c.vote_count DESC) as rank
        FROM candidates c 
        WHERE c.election_id = ? AND c.is_active = TRUE
        ORDER BY c.position, c.vote_count DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                if (rs.getInt("rank") == 1) { // Only get the top candidate for each position
                    String position = rs.getString("position");
                    Candidate winner = extractCandidateFromResultSet(rs);
                    winners.put(position, winner);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get election winners failed: " + e.getMessage());
        }

        return winners;
    }

    // Get detailed election results with winners
    public static Map<String, Object> getDetailedElectionResults(int electionId) {
        Map<String, Object> results = new HashMap<>();

        // Get winners
        Map<String, Candidate> winners = getElectionWinners(electionId);
        results.put("winners", winners);

        // Get total votes
        int totalVotes = getTotalVotesInElection(electionId);
        results.put("totalVotes", totalVotes);

        // Get results by position
        List<CandidateResult> allResults = getElectionResults(electionId);
        results.put("allResults", allResults);

        // Get election details
        Election election = ElectionService.getElectionById(electionId);
        results.put("election", election);

        return results;
    }

    // Helper method to extract candidate from ResultSet
    private static Candidate extractCandidateFromResultSet(ResultSet rs) throws SQLException {
        Candidate candidate = new Candidate();
        candidate.setId(rs.getInt("id"));
        candidate.setName(rs.getString("name"));
        candidate.setEmail(rs.getString("email"));
        candidate.setParty(rs.getString("party"));
        candidate.setPosition(rs.getString("position"));
        candidate.setElectionId(rs.getInt("election_id"));
        candidate.setVoteCount(rs.getInt("vote_count"));
        candidate.setManifesto(rs.getString("manifesto"));
        return candidate;
    }





    // 2️⃣ Update candidate vote count
    private static void updateCandidateVoteCount(int candidateId) {
        String sql = "UPDATE candidates SET vote_count = vote_count + 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, candidateId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote count update failed: " + e.getMessage());
        }
    }

    // 3️⃣ Check if user has already voted in an election
    public static boolean hasUserVoted(int voterId, int electionId) {
        String sql = "SELECT id FROM votes WHERE voter_id = ? AND election_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voterId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote check failed: " + e.getMessage());
            return false;
        }
    }

    // 4️⃣ Get vote history for a user
    public static List<Vote> getVoteHistory(int voterId) {
        List<Vote> voteHistory = new ArrayList<>();
        String sql = "SELECT v.*, c.name as candidate_name, e.title as election_title " +
                "FROM votes v " +
                "JOIN candidates c ON v.candidate_id = c.id " +
                "JOIN elections e ON v.election_id = e.id " +
                "WHERE v.voter_id = ? ORDER BY v.voted_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voterId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vote vote = new Vote();
                vote.setId(rs.getInt("id"));
                vote.setVoterId(rs.getInt("voter_id"));
                vote.setCandidateId(rs.getInt("candidate_id"));
                vote.setElectionId(rs.getInt("election_id"));
                vote.setVotedAt(rs.getTimestamp("voted_at"));
                vote.setCandidateName(rs.getString("candidate_name"));
                vote.setElectionTitle(rs.getString("election_title"));

                voteHistory.add(vote);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote history fetch failed: " + e.getMessage());
        }

        return voteHistory;
    }

    // 5️⃣ Get election results
    public static List<CandidateResult> getElectionResults(int electionId) {
        List<CandidateResult> results = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.party, c.position, c.vote_count, " +
                "ROUND((c.vote_count * 100.0 / NULLIF((SELECT SUM(vote_count) FROM candidates WHERE election_id = ?), 0)), 2) as percentage " +
                "FROM candidates c " +
                "WHERE c.election_id = ? AND c.is_active = TRUE " +
                "ORDER BY c.vote_count DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CandidateResult result = new CandidateResult();
                result.setCandidateId(rs.getInt("id"));
                result.setCandidateName(rs.getString("name"));
                result.setParty(rs.getString("party"));
                result.setPosition(rs.getString("position"));
                result.setVoteCount(rs.getInt("vote_count"));
                result.setPercentage(rs.getDouble("percentage"));

                results.add(result);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election results fetch failed: " + e.getMessage());
        }

        return results;
    }

    // 6️⃣ Get total votes cast in an election
    public static int getTotalVotesInElection(int electionId) {
        String sql = "SELECT COUNT(*) as total_votes FROM votes WHERE election_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_votes");
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Total votes count failed: " + e.getMessage());
        }

        return 0;
    }

    // 7️⃣ Get candidates for an election
    public static List<Candidate> getCandidatesForElection(int electionId) {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE election_id = ? AND is_active = TRUE ORDER BY position, name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setId(rs.getInt("id"));
                candidate.setName(rs.getString("name"));
                candidate.setEmail(rs.getString("email"));
                candidate.setParty(rs.getString("party"));
                candidate.setPosition(rs.getString("position"));
                candidate.setElectionId(rs.getInt("election_id"));
                candidate.setVoteCount(rs.getInt("vote_count"));
                candidate.setManifesto(rs.getString("manifesto"));

                candidates.add(candidate);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Candidates fetch failed: " + e.getMessage());
        }

        return candidates;
    }

    // 8️⃣ Cancel/Delete a vote (Admin function)
    public static boolean cancelVote(int voteId) {
        String sql = "DELETE FROM votes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voteId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Also decrease candidate vote count
                decreaseCandidateVoteCount(voteId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote cancellation failed: " + e.getMessage());
        }

        return false;
    }

    // 9️⃣ Decrease candidate vote count when vote is cancelled
    private static void decreaseCandidateVoteCount(int voteId) {
        String sql = "UPDATE candidates c " +
                "JOIN votes v ON c.id = v.candidate_id " +
                "SET c.vote_count = c.vote_count - 1 WHERE v.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voteId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote count decrease failed: " + e.getMessage());
        }
    }

    // 🔟 Get vote confirmation details
    public static Map<String, String> getVoteConfirmation(int voterId, int electionId) {
        Map<String, String> confirmation = new HashMap<>();

        String sql = """
            SELECT 
                v.voted_at,
                c.name as candidate_name,
                e.title as election_title
            FROM votes v
            JOIN candidates c ON v.candidate_id = c.id
            JOIN elections e ON v.election_id = e.id
            WHERE v.voter_id = ? AND v.election_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, voterId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                confirmation.put("electionTitle", rs.getString("election_title"));
                confirmation.put("candidateName", rs.getString("candidate_name"));
                confirmation.put("votedAt", rs.getTimestamp("voted_at").toString());
                confirmation.put("status", "CONFIRMED");
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Vote confirmation fetch failed: " + e.getMessage());
        }

        return confirmation;
    }

    // Inner class for election results
    public static class CandidateResult {
        private int candidateId;
        private String candidateName;
        private String party;
        private String position;
        private int voteCount;
        private double percentage;

        // Getters and Setters
        public int getCandidateId() { return candidateId; }
        public void setCandidateId(int candidateId) { this.candidateId = candidateId; }

        public String getCandidateName() { return candidateName; }
        public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

        public String getParty() { return party; }
        public void setParty(String party) { this.party = party; }

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }

        public int getVoteCount() { return voteCount; }
        public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }
}