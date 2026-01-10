package com.onlinevotingsystem.entities;

import java.sql.Timestamp;

public class Vote {
    private int id;
    private int voterId;
    private int candidateId;
    private int electionId;
    private Timestamp votedAt;
    private String candidateName;
    private String electionTitle;

    // Constructors
    public Vote() {}

    public Vote(int voterId, int candidateId, int electionId) {
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.electionId = electionId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVoterId() { return voterId; }
    public void setVoterId(int voterId) { this.voterId = voterId; }

    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }

    public int getElectionId() { return electionId; }
    public void setElectionId(int electionId) { this.electionId = electionId; }

    public Timestamp getVotedAt() { return votedAt; }
    public void setVotedAt(Timestamp votedAt) { this.votedAt = votedAt; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getElectionTitle() { return electionTitle; }
    public void setElectionTitle(String electionTitle) { this.electionTitle = electionTitle; }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", voterId=" + voterId +
                ", candidateId=" + candidateId +
                ", electionId=" + electionId +
                ", votedAt=" + votedAt +
                '}';
    }
}