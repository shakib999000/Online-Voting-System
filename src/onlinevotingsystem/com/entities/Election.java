package com.onlinevotingsystem.entities;

import java.sql.Timestamp;

public class Election {
    private int id;
    private String title;
    private String description;
    private Timestamp startDate;
    private Timestamp endDate;
    private String status; // UPCOMING, ONGOING, COMPLETED, CANCELLED
    private int createdBy;
    private int totalVotes;
    private Timestamp createdAt;

    // ========== CONSTRUCTORS ==========

    public Election() {}

    public Election(String title, String description, Timestamp startDate, Timestamp endDate, int createdBy) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
        this.status = "UPCOMING";
        this.totalVotes = 0;
    }

    // ========== GETTERS AND SETTERS ==========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getStartDate() { return startDate; }
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }

    public Timestamp getEndDate() { return endDate; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public int getTotalVotes() { return totalVotes; }
    public void setTotalVotes(int totalVotes) { this.totalVotes = totalVotes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // ========== HELPER METHODS ==========

    public boolean isUpcoming() {
        return "UPCOMING".equals(status);
    }

    public boolean isOngoing() {
        return "ONGOING".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    public String getStatusColor() {
        switch (status) {
            case "UPCOMING": return "#FFA500"; // Orange
            case "ONGOING": return "#28A745";  // Green
            case "COMPLETED": return "#007BFF"; // Blue
            case "CANCELLED": return "#DC3545"; // Red
            default: return "#6C757D"; // Gray
        }
    }

    public String getDuration() {
        if (startDate != null && endDate != null) {
            long diff = endDate.getTime() - startDate.getTime();
            long hours = diff / (60 * 60 * 1000);
            return hours + " hours";
        }
        return "N/A";
    }

    // ========== TO STRING ==========

    @Override
    public String toString() {
        return "Election{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}