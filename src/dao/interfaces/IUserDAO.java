package dao.interfaces;

import model.User;
import java.util.List;

public interface IUserDAO {
    boolean createUser(User user);
    User authenticate(String emailOrPhone, String password);
    User findByEmailOrPhone(String emailOrPhone);
    boolean emailExists(String email);
    boolean phoneExists(String phone);
    boolean fullNameExists(String fullName);
    boolean emailExistsForOtherUser(String email, int userId);
    boolean phoneExistsForOtherUser(String phone, int userId);
    boolean fullNameExistsForOtherUser(String fullName, int userId);
    List<User> getAllUsers();
    List<User> getUsersByRole(String role);  // Вместо getClients, getMechanics, getAdmins
    boolean addRoleToUser(int userId, String role);
    boolean removeRoleFromUser(int userId, String role);
    boolean updateUser(User user);
    boolean updatePassword(int userId, String newPassword);
    User getUserById(int userId);
    boolean deleteUser(int userId);
}