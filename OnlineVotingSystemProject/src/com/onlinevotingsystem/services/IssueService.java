package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.Issue;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueService {

    // 1️⃣ Create new issue
    public static boolean createIssue(Issue issue) {
        String sql = "INSERT INTO issues (user_id, election_id, title, description, type, priority, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, issue.getUserId());
            pstmt.setObject(2, issue.getElectionId() > 0 ? issue.getElectionId() : null);
            pstmt.setString(3, issue.getTitle());
            pstmt.setString(4, issue.getDescription());
            pstmt.setString(5, issue.getType());
            pstmt.setString(6, issue.getPriority());
            pstmt.setString(7, issue.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Create issue failed: " + e.getMessage());
            return false;
        }
    }

    // 2️⃣ Report issue (convenience method)
    public static boolean reportIssue(Issue issue) {
        // Ensure status is PENDING when reporting
        if (issue.getStatus() == null) {
            issue.setStatus("PENDING");
        }
        return createIssue(issue);
    }

    // 3️⃣ Get all issues (for admin)
    public static List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();

        // ✅ FIXED: Check if resolved_by column exists
        String sql;
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "issues", "resolved_by");

            if (columns.next()) {
                // Column exists
                sql = "SELECT i.*, u.full_name as user_name, u.email as user_email, e.title as election_title " +
                        "FROM issues i " +
                        "LEFT JOIN users u ON i.user_id = u.id " +
                        "LEFT JOIN elections e ON i.election_id = e.id " +
                        "ORDER BY i.created_at DESC";
            } else {
                // Column doesn't exist
                sql = "SELECT i.id, i.user_id, i.election_id, i.title, i.description, i.type, i.priority, " +
                        "i.status, i.resolution_note, i.created_at, i.resolved_at, " +
                        "u.full_name as user_name, u.email as user_email, e.title as election_title " +
                        "FROM issues i " +
                        "LEFT JOIN users u ON i.user_id = u.id " +
                        "LEFT JOIN elections e ON i.election_id = e.id " +
                        "ORDER BY i.created_at DESC";
            }
        } catch (SQLException e) {
            sql = "SELECT i.id, i.user_id, i.election_id, i.title, i.description, i.type, i.priority, " +
                    "i.status, i.resolution_note, i.created_at, i.resolved_at, " +
                    "u.full_name as user_name, u.email as user_email, e.title as election_title " +
                    "FROM issues i " +
                    "LEFT JOIN users u ON i.user_id = u.id " +
                    "LEFT JOIN elections e ON i.election_id = e.id " +
                    "ORDER BY i.created_at DESC";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Issue issue = extractIssueFromResultSet(rs);
                issues.add(issue);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get all issues failed: " + e.getMessage());
        }
        return issues;
    }

    // 4️⃣ Get issues by user
    public static List<Issue> getIssuesByUser(int userId) {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.id, i.user_id, i.election_id, i.title, i.description, i.type, i.priority, " +
                "i.status, i.resolution_note, i.created_at, i.resolved_at, " +
                "u.full_name as user_name, u.email as user_email, e.title as election_title " +
                "FROM issues i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "LEFT JOIN elections e ON i.election_id = e.id " +
                "WHERE i.user_id = ? " +
                "ORDER BY i.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Issue issue = extractIssueFromResultSet(rs);
                issues.add(issue);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get user issues failed: " + e.getMessage());
        }
        return issues;
    }

    // 5️⃣ Update issue status
    public static boolean updateIssueStatus(int issueId, String status, String resolutionNote, int resolvedBy) {
        // ✅ FIXED: Check if resolved_by column exists
        String sql;
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "issues", "resolved_by");

            if (columns.next()) {
                // Column exists
                sql = "UPDATE issues SET status = ?, resolution_note = ?, resolved_by = ?, resolved_at = NOW() WHERE id = ?";
            } else {
                // Column doesn't exist
                sql = "UPDATE issues SET status = ?, resolution_note = ?, resolved_at = NOW() WHERE id = ?";
            }
        } catch (SQLException e) {
            sql = "UPDATE issues SET status = ?, resolution_note = ?, resolved_at = NOW() WHERE id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (sql.contains("resolved_by")) {
                pstmt.setString(1, status);
                pstmt.setString(2, resolutionNote);
                pstmt.setInt(3, resolvedBy);
                pstmt.setInt(4, issueId);
            } else {
                pstmt.setString(1, status);
                pstmt.setString(2, resolutionNote);
                pstmt.setInt(3, issueId);
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Update issue status failed: " + e.getMessage());
            return false;
        }
    }

    // 6️⃣ Get issue by ID
    public static Issue getIssueById(int issueId) {
        String sql = "SELECT i.id, i.user_id, i.election_id, i.title, i.description, i.type, i.priority, " +
                "i.status, i.resolution_note, i.created_at, i.resolved_at, " +
                "u.full_name as user_name, u.email as user_email, e.title as election_title " +
                "FROM issues i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "LEFT JOIN elections e ON i.election_id = e.id " +
                "WHERE i.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, issueId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractIssueFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get issue by ID failed: " + e.getMessage());
        }
        return null;
    }

    // 7️⃣ Get issues by status
    public static List<Issue> getIssuesByStatus(String status) {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.id, i.user_id, i.election_id, i.title, i.description, i.type, i.priority, " +
                "i.status, i.resolution_note, i.created_at, i.resolved_at, " +
                "u.full_name as user_name, u.email as user_email, e.title as election_title " +
                "FROM issues i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "LEFT JOIN elections e ON i.election_id = e.id " +
                "WHERE i.status = ? " +
                "ORDER BY i.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Issue issue = extractIssueFromResultSet(rs);
                issues.add(issue);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get issues by status failed: " + e.getMessage());
        }
        return issues;
    }

    // 8️⃣ Get issue statistics
    public static Map<String, Integer> getIssueStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM issues GROUP BY status";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get issue statistics failed: " + e.getMessage());
        }
        return stats;
    }

    // 9️⃣ Delete issue (admin only)
    public static boolean deleteIssue(int issueId) {
        String sql = "DELETE FROM issues WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, issueId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Delete issue failed: " + e.getMessage());
            return false;
        }
    }

    // 🔟 Search issues by title or description
    public static List<Issue> searchIssues(String searchTerm) {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.id, i.user_id, i.election_id, i.title, i.description, i.type, i.priority, " +
                "i.status, i.resolution_note, i.created_at, i.resolved_at, " +
                "u.full_name as user_name, u.email as user_email, e.title as election_title " +
                "FROM issues i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "LEFT JOIN elections e ON i.election_id = e.id " +
                "WHERE i.title LIKE ? OR i.description LIKE ? " +
                "ORDER BY i.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Issue issue = extractIssueFromResultSet(rs);
                issues.add(issue);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Search issues failed: " + e.getMessage());
        }
        return issues;
    }

    // 1️⃣1️⃣ Get issues count by user
    public static int getIssueCountByUser(int userId) {
        String sql = "SELECT COUNT(*) as count FROM issues WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get issue count by user failed: " + e.getMessage());
        }
        return 0;
    }

    // 1️⃣2️⃣ Get recent issues (for dashboard)
    public static List<Issue> getRecentIssues(int limit) {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.id, i.user_id, i.election_id, i.title, i.description, i.type, i.priority, " +
                "i.status, i.resolution_note, i.created_at, i.resolved_at, " +
                "u.full_name as user_name, u.email as user_email, e.title as election_title " +
                "FROM issues i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "LEFT JOIN elections e ON i.election_id = e.id " +
                "ORDER BY i.created_at DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Issue issue = extractIssueFromResultSet(rs);
                issues.add(issue);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get recent issues failed: " + e.getMessage());
        }
        return issues;
    }

    // Helper method to extract issue from ResultSet
    private static Issue extractIssueFromResultSet(ResultSet rs) throws SQLException {
        Issue issue = new Issue();
        issue.setId(rs.getInt("id"));
        issue.setUserId(rs.getInt("user_id"));
        issue.setElectionId(rs.getInt("election_id"));
        issue.setTitle(rs.getString("title"));
        issue.setDescription(rs.getString("description"));
        issue.setType(rs.getString("type"));
        issue.setPriority(rs.getString("priority"));
        issue.setStatus(rs.getString("status"));
        issue.setResolutionNote(rs.getString("resolution_note"));

        // ✅ FIXED: Safe column access for resolved_by
        try {
            issue.setResolvedBy(rs.getInt("resolved_by"));
        } catch (SQLException e) {
            issue.setResolvedBy(0); // Default value if column doesn't exist
        }

        issue.setCreatedAt(rs.getTimestamp("created_at"));
        issue.setResolvedAt(rs.getTimestamp("resolved_at"));

        // Additional info from joins
        issue.setUserName(rs.getString("user_name"));
        issue.setUserEmail(rs.getString("user_email"));
        issue.setElectionTitle(rs.getString("election_title"));

        return issue;
    }
}