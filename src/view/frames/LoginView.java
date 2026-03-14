package view.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField emailPhoneField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox; // Добавляем комбобокс
    private JButton loginButton;
    private JButton registerButton;
    private JButton exitButton;

    public LoginView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Автосервис - Вход");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Автосервис", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email/телефон
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Email или телефон:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailPhoneField = new JTextField(15);
        formPanel.add(emailPhoneField, gbc);

        // Пароль
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Роль
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Войти как:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] roles = {"Клиент", "Механик", "Администратор"};
        roleComboBox = new JComboBox<>(roles);
        formPanel.add(roleComboBox, gbc);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        loginButton = new JButton("Войти");
        registerButton = new JButton("Регистрация");
        exitButton = new JButton("Выход");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
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

    public String getSelectedRole() {
        String role = (String) roleComboBox.getSelectedItem();
        switch (role) {
            case "Клиент": return "CLIENT";
            case "Механик": return "MECHANIC";
            case "Администратор": return "ADMIN";
            default: return "CLIENT";
        }
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
        roleComboBox.setSelectedIndex(0);
    }
}