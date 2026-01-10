package com.onlinevotingsystem.services;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private int userId;
    private String title;
    private String message;
    private String type; // SYSTEM, ELECTION, VOTE, ISSUE
    private boolean isRead;
    private Timestamp createdAt;

    // Constructors
    public Notification() {}

    public Notification(int userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public String getIcon() {
        switch (type) {
            case "VOTE": return "✅";
            case "ELECTION": return "🗳️";
            case "SYSTEM": return "🔔";
            case "ISSUE": return "⚠️";
            default: return "📢";
        }
    }

    public String getTimeAgo() {
        if (createdAt == null) return "";

        long diff = System.currentTimeMillis() - createdAt.getTime();
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m ago";
        if (hours < 24) return hours + "h ago";
        return days + "d ago";
    }

    @Override
    public String toString() {
        return getIcon() + " " + title + " - " + message + " (" + getTimeAgo() + ")";
    }
}