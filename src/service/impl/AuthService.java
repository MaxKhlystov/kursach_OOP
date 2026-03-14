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
    public String validateRegistration(User user) {
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
    public boolean register(User user) {
        String validationError = validateRegistration(user);
        if (validationError != null) {
            logger.warning("Ошибка валидации регистрации: " + validationError);
            return false;
        }

        boolean success = userDAO.createUser(user);
        if (success) {
            logger.info("Пользователь зарегистрирован: " + user.getFullName());
        } else {
            logger.severe("Ошибка регистрации: " + user.getFullName());
        }
        return success;
    }
}