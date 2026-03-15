package service.interfaces;

import model.User;
import java.util.List;

public interface IUserService {
    String validateProfileUpdate(User user);
    boolean updateUser(User user);
    User getUserById(int userId);
    User findByEmailOrPhone(String emailOrPhone);
    List<User> getAllUsers();
    List<User> getUsersByRole(String role);
    boolean addRoleToUser(int userId, String role);
    boolean removeRoleFromUser(int userId, String role);
    boolean deleteUser(int userId);
}