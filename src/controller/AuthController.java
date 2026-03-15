package controller;

import model.User;
import service.interfaces.IAuthService;
import service.impl.AuthService;
import service.interfaces.IUserService;
import service.impl.UserService;
import view.frames.*;
import view.dialogs.ChooseRoleDialog;
import view.dialogs.EnterPasswordDialog;
import view.dialogs.LinkAccountDialog;
import view.dialogs.PasswordOnlyDialog;

import java.util.logging.Logger;

public class AuthController {
    private final IAuthService authService;
    private final IUserService userService;
    private LoginView loginView;
    private RegistrationView registrationView;
    private ClientView currentClientView;
    private MechanicView currentMechanicView;
    private AdminView currentAdminView;
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    public AuthController() {
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    public void setRegistrationView(RegistrationView registrationView) {
        this.registrationView = registrationView;
    }

    public User handleLogin(String emailOrPhone, String password) {
        User user = authService.login(emailOrPhone, password);
        if (user != null) {
            return user;
        } else {
            loginView.showError("Неверный email/телефон или пароль");
            return null;
        }
    }

    public void openDashboardWithRoleCheck(User user, String selectedRole) {
        if (user.hasRole(selectedRole)) {
            loginView.hideView();
            openDashboard(user, selectedRole);
        } else {
            loginView.showError("У вас нет роли " + selectedRole +
                    ". Доступные роли: " + String.join(", ", user.getRoles()));
        }
    }

    public void startRegistration() {
        ChooseRoleDialog roleDialog = new ChooseRoleDialog(loginView);
        roleDialog.setVisible(true);

        String selectedRole = roleDialog.getSelectedRole();
        if (selectedRole == null) return;

        if ("MECHANIC".equals(selectedRole) || "ADMIN".equals(selectedRole)) {
            EnterPasswordDialog passwordDialog = new EnterPasswordDialog(loginView, selectedRole);
            passwordDialog.setVisible(true);

            if (!passwordDialog.isSuccess()) {
                return;
            }
        }

        registrationView.setRole(selectedRole);
        registrationView.showView();
    }

    public void handleRegister(String fullName, String password, String confirmPassword,
                               String email, String phone, String role) {
        if (!password.equals(confirmPassword)) {
            registrationView.showError("Пароли не совпадают");
            return;
        }

        User user = new User(fullName, password, email, phone);

        String validationError = authService.validateRegistration(user, role);
        if (validationError != null) {
            registrationView.showError(validationError);
            return;
        }

        boolean success = authService.register(user, role);

        if (success) {
            registrationView.showSuccess("Регистрация прошла успешно!");
            registrationView.hideView();
            loginView.clearFields();
            loginView.showView();
        } else {
            registrationView.showError("Ошибка регистрации. Попробуйте еще раз.");
        }
    }

    public void handleLinkAccount() {
        String targetRole = registrationView.getRole();
        LinkAccountDialog linkDialog = new LinkAccountDialog(registrationView, targetRole);
        linkDialog.setVisible(true);

        if (linkDialog.isSuccess()) {
            User existingUser = linkDialog.getFoundUser();

            // Проверяем, есть ли уже такая роль
            if (existingUser.hasRole(targetRole)) {
                registrationView.showError("У этого пользователя уже есть роль " + targetRole);
                return;
            }

            // Запоминаем пользователя и показываем диалог для пароля
            pendingUser = existingUser;
            showPasswordForExistingUser(targetRole);
        }
    }

    private User pendingUser; // временно храним пользователя для добавления роли

    private void showPasswordForExistingUser(String newRole) {
        PasswordOnlyDialog passwordDialog = new PasswordOnlyDialog(registrationView);

        passwordDialog.setOkListener(e -> {
            String password = passwordDialog.getPassword();

            if (password.length() < 3) {
                passwordDialog.showError("Пароль должен содержать минимум 3 символа");
                return;
            }

            // Проверяем пароль существующего пользователя
            User authUser = authService.login(pendingUser.getEmail(), password);
            if (authUser == null) {
                passwordDialog.showError("Неверный пароль");
                return;
            }

            // Добавляем новую роль существующему пользователю
            boolean success = authService.addRoleToExistingUser(pendingUser, newRole);

            if (success) {
                passwordDialog.setSuccess(true);
                passwordDialog.dispose();
                registrationView.showSuccess("Роль " + newRole + " успешно добавлена!");
                registrationView.hideView();
                pendingUser = null;
                loginView.clearFields();
                loginView.showView();
            } else {
                passwordDialog.showError("Ошибка при добавлении роли");
            }
        });

        passwordDialog.setCancelListener(e -> {
            passwordDialog.dispose();
            pendingUser = null;
        });

        passwordDialog.setVisible(true);
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
        if (currentAdminView != null) {
            currentAdminView.hideView();
            currentAdminView = null;
        }
        loginView.clearFields();
        loginView.showView();
    }

    private void openDashboard(User user, String selectedRole) {
        if ("MECHANIC".equals(selectedRole)) {
            MechanicController mechanicController = new MechanicController(user, this);
            MechanicView mechanicView = new MechanicView(mechanicController, user);
            mechanicController.setView(mechanicView);
            currentMechanicView = mechanicView;
            mechanicView.showView();
        } else if ("ADMIN".equals(selectedRole)) {
            AdminController adminController = new AdminController(user, this);
            AdminView adminView = new AdminView(adminController, user);
            adminController.setView(adminView);
            currentAdminView = adminView;
            adminView.showView();
        } else {
            ClientController clientController = new ClientController(user, this);
            ClientView clientView = new ClientView(clientController, user);
            clientController.setView(clientView);
            currentClientView = clientView;
            clientView.showView();
        }
    }
}