package service.interfaces;

import model.User;
import java.util.List;

public interface IUserService {
    String validateProfileUpdate(User user);
    boolean updateUser(User user);
    User getUserById(int userId);
    List<User> getClients();
}