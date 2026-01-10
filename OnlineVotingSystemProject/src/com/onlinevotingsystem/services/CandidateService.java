package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.Candidate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateService {

    // 1️⃣ Add new candidate
    public static boolean addCandidate(Candidate candidate) {
        String sql = "INSERT INTO candidates (name, email, party, position, election_id, manifesto) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, candidate.getName());
            pstmt.setString(2, candidate.getEmail());
            pstmt.setString(3, candidate.getParty());
            pstmt.setString(4, candidate.getPosition());
            pstmt.setInt(5, candidate.getElectionId());
            pstmt.setString(6, candidate.getManifesto());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Add candidate failed: " + e.getMessage());
            return false;
        }
    }

    // 2️⃣ Get candidates for election
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
                candidate.setActive(rs.getBoolean("is_active"));

                candidates.add(candidate);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get candidates failed: " + e.getMessage());
        }

        return candidates;
    }

    // 3️⃣ Update candidate
    public static boolean updateCandidate(Candidate candidate) {
        String sql = "UPDATE candidates SET name = ?, email = ?, party = ?, position = ?, manifesto = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, candidate.getName());
            pstmt.setString(2, candidate.getEmail());
            pstmt.setString(3, candidate.getParty());
            pstmt.setString(4, candidate.getPosition());
            pstmt.setString(5, candidate.getManifesto());
            pstmt.setInt(6, candidate.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Update candidate failed: " + e.getMessage());
            return false;
        }
    }

    // 4️⃣ Delete candidate (soft delete)
    public static boolean deleteCandidate(int candidateId) {
        String sql = "UPDATE candidates SET is_active = FALSE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, candidateId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Delete candidate failed: " + e.getMessage());
            return false;
        }
    }

    // 5️⃣ Get candidate by ID
    public static Candidate getCandidateById(int candidateId) {
        String sql = "SELECT * FROM candidates WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, candidateId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setId(rs.getInt("id"));
                candidate.setName(rs.getString("name"));
                candidate.setEmail(rs.getString("email"));
                candidate.setParty(rs.getString("party"));
                candidate.setPosition(rs.getString("position"));
                candidate.setElectionId(rs.getInt("election_id"));
                candidate.setVoteCount(rs.getInt("vote_count"));
                candidate.setManifesto(rs.getString("manifesto"));
                candidate.setActive(rs.getBoolean("is_active"));
                return candidate;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get candidate failed: " + e.getMessage());
        }

        return null;
    }
}