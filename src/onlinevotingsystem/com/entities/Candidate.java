package com.onlinevotingsystem.entities;

public class Candidate {
    private int id;
    private String name;
    private String email;
    private String party;
    private String position;
    private int electionId;
    private int voteCount;
    private String manifesto;
    private String photoUrl;
    private boolean isActive;

    // Constructors
    public Candidate() {}

    public Candidate(String name, String email, String party, String position, int electionId) {
        this.name = name;
        this.email = email;
        this.party = party;
        this.position = position;
        this.electionId = electionId;
        this.isActive = true;
        this.voteCount = 0;
    }

    public Candidate(String name, String email, String party, String position, int electionId, String manifesto) {
        this(name, email, party, position, electionId);
        this.manifesto = manifesto;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getManifesto() {
        return manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Helper methods
    public void incrementVoteCount() {
        this.voteCount++;
    }

    public String getDisplayInfo() {
        return name + " (" + party + ") - " + position;
    }

    public boolean hasPhoto() {
        return photoUrl != null && !photoUrl.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", party='" + party + '\'' +
                ", position='" + position + '\'' +
                ", electionId=" + electionId +
                ", voteCount=" + voteCount +
                ", isActive=" + isActive +
                '}';
    }

    // Equality check based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return id == candidate.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}