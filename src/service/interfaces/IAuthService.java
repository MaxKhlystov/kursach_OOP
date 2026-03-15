package service.interfaces;

import model.User;

public interface IAuthService {
    User login(String username, String password);
    String validateRegistration(User user, String role);
    boolean register(User user, String role);
    boolean addRoleToExistingUser(User existingUser, String newRole);
}