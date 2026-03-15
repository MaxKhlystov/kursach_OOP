package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String fullName;
    private String password;
    private String email;
    private String phone;
    private List<String> roles = new ArrayList<>(); // Теперь список ролей

    public User() {}

    public User(String fullName, String password, String email, String phone) {
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public void addRole(String role) { this.roles.add(role); }
    public boolean hasRole(String role) { return roles.contains(role); }

    @Override
    public String toString() {
        return fullName;
    }
}