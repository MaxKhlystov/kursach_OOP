package service.interfaces;

import model.User;

public interface IAuthService {
    User login(String username, String password);
    String validateRegistration(User user);
    boolean register(User user);
}