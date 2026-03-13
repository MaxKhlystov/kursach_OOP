package view.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField emailPhoneField;  // Переименовано с usernameField
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton exitButton;  // Новая кнопка

    public LoginView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Автосервис - Вход");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Автосервис", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Email или телефон:"));
        emailPhoneField = new JTextField();
        formPanel.add(emailPhoneField);

        formPanel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Панель для трех кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        loginButton = new JButton("Войти");
        registerButton = new JButton("Регистрация");
        exitButton = new JButton("Выход");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public void setLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
        passwordField.addActionListener(listener);
    }

    public void setRegisterListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public void setExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    public String getEmailOrPhone() {
        return emailPhoneField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void showView() {
        setVisible(true);
    }

    public void hideView() {
        setVisible(false);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void clearFields() {
        emailPhoneField.setText("");
        passwordField.setText("");
    }
}