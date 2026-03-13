package service.impl;

import model.User;
import dao.interfaces.IUserDAO;
import dao.impl.UserDAO;
import service.interfaces.IUserService;

import java.util.List;
import java.util.logging.Logger;

public class UserService implements IUserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final IUserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public String validateProfileUpdate(User user) {
        if (user == null || user.getId() == 0) {
            return "Невалидные данные пользователя";
        }

        if (userDAO.emailExistsForOtherUser(user.getEmail(), user.getId())) {
            return "Email уже используется другим пользователем";
        }

        if (userDAO.phoneExistsForOtherUser(user.getPhone(), user.getId())) {
            return "Номер телефона уже используется другим пользователем";
        }

        if (userDAO.fullNameExistsForOtherUser(user.getFullName(), user.getId())) {
            return "ФИО уже используется другим пользователем";
        }

        return null;
    }

    @Override
    public boolean updateUser(User user) {
        String validationError = validateProfileUpdate(user);
        if (validationError != null) {
            logger.warning("Ошибка валидации обновления: " + validationError);
            return false;
        }

        boolean success = userDAO.updateUser(user);
        if (success) {
            logger.info("Пользователь обновлен: " + user.getFullName());
        } else {
            logger.severe("Ошибка обновления пользователя: " + user.getFullName());
        }
        return success;
    }

    @Override
    public User getUserById(int userId) {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            logger.info("Получен пользователь ID: " + userId);
        } else {
            logger.warning("Пользователь не найден ID: " + userId);
        }
        return user;
    }

    @Override
    public List<User> getClients() {
        return userDAO.getClients();
    }
}