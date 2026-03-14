package model;

public class User {
    private int id;
    private String fullName;
    private String password;
    private String role;
    private String email;
    private String phone;
    private int linkedUserId;

    public User() {}

    public User(String fullName, String password, String role, String email, String phone) {
        this.fullName = fullName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.linkedUserId = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getLinkedUserId() { return linkedUserId; }
    public void setLinkedUserId(int linkedUserId) { this.linkedUserId = linkedUserId; }

    @Override
    public String toString() {
        return fullName;
    }
}