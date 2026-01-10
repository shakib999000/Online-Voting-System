package com.onlinevotingsystem.entities;

import java.sql.Timestamp;

public class Issue {
    private int id;
    private int userId;
    private int electionId;
    private String title;
    private String description;
    private String type; // TECHNICAL, FRAUD, ACCESS, OTHER
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private String status; // PENDING, IN_PROGRESS, RESOLVED, CLOSED
    private String resolutionNote;
    private int resolvedBy;
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    // Additional fields for display
    private String userName;
    private String userEmail;
    private String electionTitle;

    // ========== CONSTRUCTORS ==========

    public Issue() {}

    public Issue(int userId, String title, String description, String type, String priority) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = "PENDING";
    }

    // ========== GETTERS AND SETTERS ==========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getElectionId() { return electionId; }
    public void setElectionId(int electionId) { this.electionId = electionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }

    public int getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(int resolvedBy) { this.resolvedBy = resolvedBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getElectionTitle() { return electionTitle; }
    public void setElectionTitle(String electionTitle) { this.electionTitle = electionTitle; }

    // ========== HELPER METHODS ==========

    public String getPriorityColor() {
        switch (priority) {
            case "LOW": return "#28A745";
            case "MEDIUM": return "#FFC107";
            case "HIGH": return "#FD7E14";
            case "URGENT": return "#DC3545";
            default: return "#6C757D";
        }
    }

    public String getStatusColor() {
        switch (status) {
            case "PENDING": return "#6C757D";
            case "IN_PROGRESS": return "#007BFF";
            case "RESOLVED": return "#28A745";
            case "CLOSED": return "#6C757D";
            default: return "#6C757D";
        }
    }

    public boolean isResolved() {
        return "RESOLVED".equals(status) || "CLOSED".equals(status);
    }

    public String getTypeIcon() {
        switch (type) {
            case "TECHNICAL": return "🔧";
            case "FRAUD": return "🚨";
            case "ACCESS": return "🔐";
            case "OTHER": return "📝";
            default: return "❓";
        }
    }

    // ========== TO STRING ==========

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}