package dao.interfaces;

import model.User;
import java.util.List;

public interface IUserDAO {
    boolean createUser(User user);
    User authenticate(String username, String password);
    boolean emailExists(String email);
    boolean phoneExists(String phone);
    boolean fullNameExists(String fullName);
    boolean emailExistsForOtherUser(String email, int userId);
    boolean phoneExistsForOtherUser(String phone, int userId);
    boolean fullNameExistsForOtherUser(String fullName, int userId);
    List<User> getAllUsers();
    List<User> getClients();
    boolean updateUser(User user);
    User getUserById(int userId);
}
