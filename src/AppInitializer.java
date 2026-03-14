import controller.AuthController;
import view.frames.LoginView;
import view.frames.RegistrationView;
import model.DatabaseConnection;
import model.User;

import javax.swing.*;
import java.util.logging.Logger;

public class AppInitializer {
    private static final Logger logger = Logger.getLogger(AppInitializer.class.getName());

    public void initialize() {
        setupLookAndFeel();

        if (!initializeDatabase()) {
            System.exit(1);
        }

        startApplication();
        setupShutdownHook();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warning("Не удалось установить системный внешний вид: " + e.getMessage());
        }
    }

    private boolean initializeDatabase() {
        try {
            DatabaseConnection.getConnection();
            logger.info("База данных инициализирована");
            return true;
        } catch (Exception e) {
            logger.severe("Ошибка инициализации базы данных: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Ошибка подключения к базе данных: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void startApplication() {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            RegistrationView registrationView = new RegistrationView();

            AuthController authController = new AuthController();
            authController.setLoginView(loginView);
            authController.setRegistrationView(registrationView);

            setupLoginListeners(loginView, authController);
            setupRegistrationListeners(registrationView, authController);

            loginView.showView();
            logger.info("Приложение запущено");
        });
    }

    private void setupRegistrationListeners(RegistrationView registrationView, AuthController authController) {
        registrationView.setRegisterListener(e -> {
            String fullName = registrationView.getFullName();
            String password = registrationView.getPassword();
            String confirmPassword = registrationView.getConfirmPassword();
            String email = registrationView.getEmail();
            String phone = registrationView.getPhone();
            String role = registrationView.getRole();

            if (fullName.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                registrationView.showError("Заполните все поля");
                return;
            }

            authController.handleRegister(fullName, password, confirmPassword, email, phone, role);
        });

        registrationView.setBackListener(e -> authController.showLogin());

        registrationView.setHaveAccountListener(e -> authController.handleLinkAccount());
    }

    private void setupLoginListeners(LoginView loginView, AuthController authController) {
        loginView.setLoginListener(e -> {
            String emailOrPhone = loginView.getEmailOrPhone();
            String password = loginView.getPassword();
            String selectedRole = loginView.getSelectedRole();

            if (emailOrPhone.isEmpty() || password.isEmpty()) {
                loginView.showError("Заполните все поля");
                return;
            }

            User user = authController.handleLogin(emailOrPhone, password);
            if (user != null && !user.getRole().equals(selectedRole)) {
                loginView.showError("Вы вошли как " + user.getRole() +
                        ", но выбрали роль " + selectedRole + ". Пожалуйста, выберите правильную роль.");
                authController.handleLogout();
            }
        });

        loginView.setRegisterListener(e -> authController.startRegistration());

        loginView.setExitListener(e -> {
            DatabaseConnection.closeConnection();
            System.exit(0);
        });
    }

    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeConnection();
            logger.info("Приложение завершено");
        }));
    }
}