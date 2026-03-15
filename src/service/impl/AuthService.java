package service.impl;

import model.User;
import dao.interfaces.IUserDAO;
import dao.impl.UserDAO;
import service.interfaces.IAuthService;

import java.util.logging.Logger;

public class AuthService implements IAuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final IUserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    @Override
    public User login(String emailOrPhone, String password) {
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            logger.warning("Попытка входа с пустыми данными");
            return null;
        }

        User user = userDAO.authenticate(emailOrPhone, password);
        if (user != null) {
            logger.info("Успешный вход: " + emailOrPhone);
        } else {
            logger.warning("Неудачный вход: " + emailOrPhone);
        }
        return user;
    }

    @Override
    public String validateRegistration(User user, String role) {
        if (user == null) {
            return "Невалидные данные пользователя";
        }
        if (user.getPassword() == null){
            return "Пароль не заполнен";
        }

        if (user.getFullName() == null){
            return "ФИО не заполнено";
        }

        if (userDAO.emailExists(user.getEmail())) {
            return "Email уже используется";
        }

        if (userDAO.phoneExists(user.getPhone())) {
            return "Номер телефона уже используется";
        }

        if (userDAO.fullNameExists(user.getFullName())) {
            return "ФИО уже используется";
        }

        if (user.getPassword().length() < 3) {
            return "Пароль должен содержать минимум 3 символа";
        }

        return null;
    }

    @Override
    public boolean register(User user, String role) {
        String validationError = validateRegistration(user, role);
        if (validationError != null) {
            logger.warning("Ошибка валидации регистрации: " + validationError);
            return false;
        }

        user.addRole(role); // Добавляем роль перед созданием
        boolean success = userDAO.createUser(user);
        if (success) {
            logger.info("Пользователь зарегистрирован: " + user.getFullName() + " с ролью " + role);
        } else {
            logger.severe("Ошибка регистрации: " + user.getFullName());
        }
        return success;
    }

    @Override
    public boolean addRoleToExistingUser(User existingUser, String newRole) {
        if (existingUser.hasRole(newRole)) {
            logger.warning("У пользователя уже есть роль " + newRole);
            return false;
        }

        boolean success = userDAO.addRoleToUser(existingUser.getId(), newRole);
        if (success) {
            existingUser.addRole(newRole);
            logger.info("Роль " + newRole + " добавлена пользователю " + existingUser.getFullName());
        }
        return success;
    }
}