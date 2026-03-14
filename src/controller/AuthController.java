package controller;

import model.User;
import service.interfaces.IAuthService;
import service.impl.AuthService;
import service.interfaces.IUserService;
import service.impl.UserService;
import view.frames.LoginView;
import view.frames.RegistrationView;
import view.frames.ClientView;
import view.frames.MechanicView;
import view.dialogs.RoleSelectionDialog;
import view.dialogs.MechanicPasswordDialog;
import view.dialogs.LinkAccountDialog;

import java.util.logging.Logger;

public class AuthController {
    private final IAuthService authService;
    private final IUserService userService;
    private LoginView loginView;
    private RegistrationView registrationView;
    private ClientView currentClientView;
    private MechanicView currentMechanicView;
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private User pendingUser; // для связывания аккаунтов

    public AuthController() {
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public AuthController(IAuthService authService, IUserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    public void setRegistrationView(RegistrationView registrationView) {
        this.registrationView = registrationView;
    }

    public void handleLogin(String emailOrPhone, String password) {
        User user = authService.login(emailOrPhone, password);
        if (user != null) {
            loginView.hideView();
            openDashboard(user);
        } else {
            loginView.showError("Неверный email/телефон или пароль");
        }
    }

    public void startRegistration() {
        RoleSelectionDialog roleDialog = new RoleSelectionDialog(loginView);
        roleDialog.setVisible(true);

        String selectedRole = roleDialog.getSelectedRole();
        if (selectedRole == null) return; // Пользователь закрыл диалог

        if ("MECHANIC".equals(selectedRole)) {
            // Проверка пароля для механиков
            MechanicPasswordDialog passwordDialog = new MechanicPasswordDialog(loginView);
            passwordDialog.setVisible(true);

            if (!passwordDialog.isSuccess()) {
                return; // Неверный пароль или отмена
            }
        }

        // Показываем окно регистрации с выбранной ролью
        registrationView.setRole(selectedRole);
        registrationView.showView();
    }

    public void handleRegister(String fullName, String password, String confirmPassword,
                               String email, String phone, String role) {
        if (!password.equals(confirmPassword)) {
            registrationView.showError("Пароли не совпадают");
            return;
        }

        User user = new User(fullName, password, role, email, phone);

        // Если есть ожидающий пользователь для связывания
        if (pendingUser != null) {
            user.setLinkedUserId(pendingUser.getId());
        }

        String validationError = authService.validateRegistration(user);
        if (validationError != null) {
            registrationView.showError(validationError);
            return;
        }

        boolean success = authService.register(user);

        if (success) {
            registrationView.showSuccess("Регистрация прошла успешно!");
            registrationView.hideView();
            pendingUser = null;
            loginView.clearFields();
            loginView.showView();
        } else {
            registrationView.showError("Ошибка регистрации. Попробуйте еще раз.");
        }
    }

    public void handleLinkAccount() {
        LinkAccountDialog linkDialog = new LinkAccountDialog(registrationView);
        linkDialog.setVisible(true);

        if (linkDialog.isSuccess()) {
            pendingUser = linkDialog.getFoundUser();
            registrationView.showSuccess("Аккаунт будет связан с пользователем " + pendingUser.getFullName());
        }
    }

    public void showRegistration() {
        startRegistration();
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
        } else if ("ADMIN".equals(user.getRole())) {
            // TODO: Создать AdminController и AdminView
            // Пока временно открываем как клиента
            ClientController clientController = new ClientController(user, this);
            ClientView clientView = new ClientView(clientController, user);
            clientController.setView(clientView);
            currentClientView = clientView;
            clientView.showView();
        } else {
            ClientController clientController = new ClientController(user, this);
            ClientView clientView = new ClientView(clientController, user);
            clientController.setView(clientView);
            currentClientView = clientView;
            clientView.showView();
        }
    }
}