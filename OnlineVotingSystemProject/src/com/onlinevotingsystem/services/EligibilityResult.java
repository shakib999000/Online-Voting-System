package com.onlinevotingsystem.services;

public class EligibilityResult {
    private boolean eligible;
    private String message;

    public EligibilityResult(boolean eligible, String message) {
        this.eligible = eligible;
        this.message = message;
    }

    // Getters
    public boolean isEligible() {
        return eligible;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "EligibilityResult{" +
                "eligible=" + eligible +
                ", message='" + message + '\'' +
                '}';
    }
}