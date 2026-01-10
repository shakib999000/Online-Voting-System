package com.onlinevotingsystem.services;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.entities.Election;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ElectionStatusService {

    // 1️⃣ Update election status automatically based on dates
    public static void updateAllElectionStatuses() {
        String sql = """
            UPDATE elections 
            SET status = CASE 
                WHEN start_date > NOW() THEN 'UPCOMING'
                WHEN end_date < NOW() THEN 'COMPLETED' 
                ELSE 'ONGOING'
            END
            WHERE status != 'CANCELLED'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int updatedCount = stmt.executeUpdate(sql);
            System.out.println("✅ Updated status for " + updatedCount + " elections");

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election status update failed: " + e.getMessage());
        }
    }

    // 2️⃣ Manually set election status
    public static boolean setElectionStatus(int electionId, String newStatus, String reason) {
        String validStatus = validateStatus(newStatus);
        if (validStatus == null) {
            System.err.println("❌ Invalid election status: " + newStatus);
            return false;
        }

        String sql = "UPDATE elections SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, validStatus);
            pstmt.setInt(2, electionId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                logStatusChange(electionId, validStatus, reason);
                System.out.println("✅ Election " + electionId + " status changed to: " + validStatus);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Set election status failed: " + e.getMessage());
        }

        return false;
    }

    // 3️⃣ Get election status with details
    public static Map<String, Object> getElectionStatus(int electionId) {
        Map<String, Object> statusInfo = new HashMap<>();

        String sql = """
            SELECT 
                e.title,
                e.status,
                e.start_date,
                e.end_date,
                e.total_votes,
                COUNT(DISTINCT c.id) as candidate_count,
                COUNT(DISTINCT v.id) as vote_count,
                TIMESTAMPDIFF(HOUR, NOW(), e.start_date) as hours_until_start,
                TIMESTAMPDIFF(HOUR, NOW(), e.end_date) as hours_until_end
            FROM elections e
            LEFT JOIN candidates c ON e.id = c.election_id AND c.is_active = TRUE
            LEFT JOIN votes v ON e.id = v.election_id
            WHERE e.id = ?
            GROUP BY e.id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                statusInfo.put("title", rs.getString("title"));
                statusInfo.put("status", rs.getString("status"));
                statusInfo.put("startDate", rs.getTimestamp("start_date"));
                statusInfo.put("endDate", rs.getTimestamp("end_date"));
                statusInfo.put("totalVotes", rs.getInt("total_votes"));
                statusInfo.put("candidateCount", rs.getInt("candidate_count"));
                statusInfo.put("voteCount", rs.getInt("vote_count"));
                statusInfo.put("hoursUntilStart", rs.getInt("hours_until_start"));
                statusInfo.put("hoursUntilEnd", rs.getInt("hours_until_end"));

                // Calculate progress percentage
                String status = rs.getString("status");
                double progress = calculateProgress(status, rs.getInt("hours_until_start"), rs.getInt("hours_until_end"));
                statusInfo.put("progress", progress);

                // Get status description
                statusInfo.put("description", getStatusDescription(status, rs.getInt("hours_until_start"), rs.getInt("hours_until_end")));
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get election status failed: " + e.getMessage());
        }

        return statusInfo;
    }

    // 4️⃣ Get all elections with their current status
    public static List<Map<String, Object>> getAllElectionsWithStatus() {
        List<Map<String, Object>> elections = new ArrayList<>();

        String sql = """
            SELECT 
                e.id,
                e.title,
                e.status,
                e.start_date,
                e.end_date,
                e.total_votes,
                COUNT(DISTINCT c.id) as candidate_count,
                COUNT(DISTINCT v.id) as vote_count,
                CASE 
                    WHEN e.status = 'UPCOMING' THEN TIMESTAMPDIFF(HOUR, NOW(), e.start_date)
                    WHEN e.status = 'ONGOING' THEN TIMESTAMPDIFF(HOUR, NOW(), e.end_date)
                    ELSE 0
                END as hours_remaining
            FROM elections e
            LEFT JOIN candidates c ON e.id = c.election_id AND c.is_active = TRUE
            LEFT JOIN votes v ON e.id = v.election_id
            GROUP BY e.id
            ORDER BY e.start_date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> election = new HashMap<>();
                election.put("id", rs.getInt("id"));
                election.put("title", rs.getString("title"));
                election.put("status", rs.getString("status"));
                election.put("startDate", rs.getTimestamp("start_date"));
                election.put("endDate", rs.getTimestamp("end_date"));
                election.put("totalVotes", rs.getInt("total_votes"));
                election.put("candidateCount", rs.getInt("candidate_count"));
                election.put("voteCount", rs.getInt("vote_count"));
                election.put("hoursRemaining", rs.getInt("hours_remaining"));
                election.put("statusColor", getStatusColor(rs.getString("status")));
                election.put("statusIcon", getStatusIcon(rs.getString("status")));

                elections.add(election);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Get all elections with status failed: " + e.getMessage());
        }

        return elections;
    }

    // 5️⃣ Start election (change status to ONGOING)
    public static boolean startElection(int electionId) {
        // Check if election can be started
        if (!canStartElection(electionId)) {
            return false;
        }

        return setElectionStatus(electionId, "ONGOING", "Election started manually by admin");
    }

    // 6️⃣ End election (change status to COMPLETED)
    public static boolean endElection(int electionId) {
        return setElectionStatus(electionId, "COMPLETED", "Election ended manually by admin");
    }

    // 7️⃣ Cancel election
    public static boolean cancelElection(int electionId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            reason = "Election cancelled by admin";
        }
        return setElectionStatus(electionId, "CANCELLED", reason);
    }

    // 8️⃣ Extend election end time
    public static boolean extendElection(int electionId, Timestamp newEndDate, String reason) {
        String sql = "UPDATE elections SET end_date = ?, status = 'ONGOING' WHERE id = ? AND status IN ('UPCOMING', 'ONGOING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, newEndDate);
            pstmt.setInt(2, electionId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                logStatusChange(electionId, "EXTENDED", "Election extended: " + reason);
                System.out.println("✅ Election " + electionId + " extended to: " + newEndDate);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Extend election failed: " + e.getMessage());
        }

        return false;
    }

    // 9️⃣ Check if election can be started
    private static boolean canStartElection(int electionId) {
        String sql = "SELECT status, start_date, COUNT(id) as candidate_count FROM elections e " +
                "LEFT JOIN candidates c ON e.id = c.election_id AND c.is_active = TRUE " +
                "WHERE e.id = ? GROUP BY e.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                int candidateCount = rs.getInt("candidate_count");

                // Can only start if status is UPCOMING and has at least one candidate
                return "UPCOMING".equals(status) && candidateCount > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Can start election check failed: " + e.getMessage());
        }

        return false;
    }

    // 🔟 Get election status timeline
    public static List<Map<String, Object>> getElectionTimeline(int electionId) {
        List<Map<String, Object>> timeline = new ArrayList<>();

        String sql = """
            SELECT 'CREATED' as event, created_at as timestamp, 'Election created' as description 
            FROM elections WHERE id = ?
            UNION ALL
            SELECT 'STARTED' as event, start_date as timestamp, 'Election started' as description 
            FROM elections WHERE id = ? AND start_date <= NOW()
            UNION ALL
            SELECT 'ENDED' as event, end_date as timestamp, 'Election ended' as description 
            FROM elections WHERE id = ? AND end_date <= NOW()
            UNION ALL
            SELECT status as event, NOW() as timestamp, 
                   CONCAT('Status changed to ', status) as description 
            FROM elections WHERE id = ?
            ORDER BY timestamp
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            pstmt.setInt(2, electionId);
            pstmt.setInt(3, electionId);
            pstmt.setInt(4, electionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> event = new HashMap<>();
                event.put("event", rs.getString("event"));
                event.put("timestamp", rs.getTimestamp("timestamp"));
                event.put("description", rs.getString("description"));
                event.put("icon", getTimelineIcon(rs.getString("event")));
                timeline.add(event);
            }

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Election timeline failed: " + e.getMessage());
        }

        return timeline;
    }

    // ========== HELPER METHODS ==========

    private static String validateStatus(String status) {
        if (status == null) return null;

        switch (status.toUpperCase()) {
            case "UPCOMING":
            case "ONGOING":
            case "COMPLETED":
            case "CANCELLED":
                return status.toUpperCase();
            default:
                return null;
        }
    }

    private static void logStatusChange(int electionId, String newStatus, String reason) {
        String sql = "INSERT INTO election_status_log (election_id, old_status, new_status, reason, changed_by) " +
                "VALUES (?, (SELECT status FROM elections WHERE id = ?), ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            pstmt.setInt(2, electionId);
            pstmt.setString(3, newStatus);
            pstmt.setString(4, reason);
            pstmt.setInt(5, 1); // Assuming admin ID 1

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ [OVS] Status change logging failed: " + e.getMessage());
        }
    }

    private static double calculateProgress(String status, int hoursUntilStart, int hoursUntilEnd) {
        switch (status) {
            case "UPCOMING":
                return 0.0;
            case "ONGOING":
                if (hoursUntilEnd >= 0) {
                    // Calculate progress based on time elapsed
                    int totalHours = hoursUntilStart + Math.abs(hoursUntilEnd);
                    if (totalHours > 0) {
                        return ((double) Math.abs(hoursUntilStart) / totalHours) * 100;
                    }
                }
                return 50.0; // Default progress for ongoing elections
            case "COMPLETED":
            case "CANCELLED":
                return 100.0;
            default:
                return 0.0;
        }
    }

    private static String getStatusDescription(String status, int hoursUntilStart, int hoursUntilEnd) {
        switch (status) {
            case "UPCOMING":
                if (hoursUntilStart > 24) {
                    return "Starts in " + (hoursUntilStart / 24) + " days";
                } else if (hoursUntilStart > 0) {
                    return "Starts in " + hoursUntilStart + " hours";
                } else {
                    return "Starting soon";
                }
            case "ONGOING":
                if (hoursUntilEnd > 24) {
                    return "Ends in " + (hoursUntilEnd / 24) + " days";
                } else if (hoursUntilEnd > 0) {
                    return "Ends in " + hoursUntilEnd + " hours";
                } else {
                    return "Ending soon";
                }
            case "COMPLETED":
                return "Election completed";
            case "CANCELLED":
                return "Election cancelled";
            default:
                return "Unknown status";
        }
    }

    private static String getStatusColor(String status) {
        switch (status) {
            case "UPCOMING": return "#FFA500"; // Orange
            case "ONGOING": return "#28A745";  // Green
            case "COMPLETED": return "#007BFF"; // Blue
            case "CANCELLED": return "#DC3545"; // Red
            default: return "#6C757D"; // Gray
        }
    }

    private static String getStatusIcon(String status) {
        switch (status) {
            case "UPCOMING": return "⏰";
            case "ONGOING": return "✅";
            case "COMPLETED": return "🏁";
            case "CANCELLED": return "❌";
            default: return "❓";
        }
    }

    private static String getTimelineIcon(String event) {
        switch (event.toUpperCase()) {
            case "CREATED": return "📝";
            case "STARTED": return "🚀";
            case "ENDED": return "🏁";
            case "UPCOMING": return "⏰";
            case "ONGOING": return "✅";
            case "COMPLETED": return "🎯";
            case "CANCELLED": return "❌";
            default: return "📅";
        }
    }
}