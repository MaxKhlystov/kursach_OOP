package controller;

import model.User;
import service.interfaces.IAuthService;
import service.impl.AuthService;
import view.frames.LoginView;
import view.frames.RegistrationView;
import view.frames.ClientView;
import view.frames.MechanicView;
import java.util.logging.Logger;

public class AuthController {
    private final IAuthService authService;
    private LoginView loginView;
    private RegistrationView registrationView;
    private ClientView currentClientView;
    private MechanicView currentMechanicView;
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    public AuthController() {
        this.authService = new AuthService();
    }

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    public void setRegistrationView(RegistrationView registrationView) {
        this.registrationView = registrationView;
    }

    public void handleLogin(String username, String password) {
        User user = authService.login(username, password);
        if (user != null) {
            loginView.hideView();
            openDashboard(user);
        } else {
            loginView.showError("Неверное имя пользователя или пароль");
        }
    }

    public void handleRegister(String fullName, String password, String confirmPassword,
                               String email, String phone, String role) {
        if (!password.equals(confirmPassword)) {
            registrationView.showError("Пароли не совпадают");
            return;
        }

        User user = new User(fullName, password, role, email, phone);
        String validationError = authService.validateRegistration(user);
        if (validationError != null) {
            registrationView.showError(validationError);
            return;
        }

        boolean success = authService.register(user);

        if (success) {
            registrationView.showSuccess("Регистрация прошла успешно!");
            registrationView.hideView();
            loginView.showView();
        } else {
            registrationView.showError("Ошибка регистрации. Попробуйте еще раз.");
        }
    }

    public void showRegistration() {
        loginView.hideView();
        registrationView.showView();
    }

    public void showLogin() {
        registrationView.hideView();
        loginView.showView();
    }

    public void handleLogout() {
        if (currentClientView != null) {
            currentClientView.hideView();
            currentClientView = null;
        }
        if (currentMechanicView != null) {
            currentMechanicView.hideView();
            currentMechanicView = null;
        }
        loginView.clearFields();
        loginView.showView();
    }

    private void openDashboard(User user) {
        if ("MECHANIC".equals(user.getRole())) {
            MechanicController mechanicController = new MechanicController(user, this);
            MechanicView mechanicView = new MechanicView(mechanicController, user);
            mechanicController.setView(mechanicView);
            currentMechanicView = mechanicView;
            mechanicView.showView();
        } else {
            ClientController clientController = new ClientController(user, this);
            ClientView clientView = new ClientView(clientController, user);
            clientController.setView(clientView);
            currentClientView = clientView;
            clientView.showView();
        }
    }
}