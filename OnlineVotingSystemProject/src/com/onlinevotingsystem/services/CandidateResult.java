package com.onlinevotingsystem.services;

public class CandidateResult {
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

    @Override
    public String toString() {
        return String.format("%s (%s) - %d votes (%.2f%%)", candidateName, party, voteCount, percentage);
    }
}