package com.onlinevotingsystem.services;

import java.sql.Timestamp;

public class ElectionInfo {
    private int id;
    private String title;
    private String status;
    private Timestamp startDate;
    private Timestamp endDate;

    public ElectionInfo(int id, String title, String status, Timestamp startDate, Timestamp endDate) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getStartDate() { return startDate; }
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }

    public Timestamp getEndDate() { return endDate; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }
}