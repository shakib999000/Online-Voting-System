package com.onlinevotingsystem.entities;

public class User {
    private int id;
    private String name;        // maps to full_name
    private String email;
    private String password;
    private String role;
    private String phone;
    private String address;
    private String dateOfBirth;
    private String nidNumber;
    private boolean isVerified;
    private boolean isEligible;
    private String createdAt;   // NEW — because DB has created_at

    // Default constructor
    public User() {
        this.isVerified = false;
        this.isEligible = true;
    }

    // Constructor for registration
    public User(String name, String email, String password, String role,
                String phone, String address) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }

    // Full constructor
    public User(int id, String name, String email, String password, String role,
                String phone, String address, String dateOfBirth,
                String nidNumber, boolean isVerified, boolean isEligible,
                String createdAt) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.nidNumber = nidNumber;
        this.isVerified = isVerified;
        this.isEligible = isEligible;
        this.createdAt = createdAt;
    }

    private int age;

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    // ====== GETTERS/SETTERS ======

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getNidNumber() { return nidNumber; }
    public void setNidNumber(String nidNumber) { this.nidNumber = nidNumber; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isEligible() { return isEligible; }
    public void setEligible(boolean eligible) { isEligible = eligible; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // === Helper Methods ===
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isVoter() {
        return "VOTER".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
