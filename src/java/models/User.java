package models;
import java.sql.Date;

public class User {
    private int userId;
    private String username, email, passwordHash, fullName, phone, bio, avatar;
    private String githubLink, facebookLink, linkedinLink, websiteLink, address;
    private Date dob;
    private int roleId;

    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getLinkedinLink() {
        return linkedinLink;
    }

    public void setLinkedinLink(String linkedinLink) {
        this.linkedinLink = linkedinLink;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }
    
    public int getUserId() { return userId; } public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; } public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public int getRoleId() { return roleId; } public void setRoleId(int roleId) { this.roleId = roleId; }
    public String getFullName() { return fullName; } public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getBio() { return bio; } public void setBio(String bio) { this.bio = bio; }
    public String getAvatar() { return avatar; } public void setAvatar(String avatar) { this.avatar = avatar; }
    // ... thêm các getter/setter còn lại tương tự.
}