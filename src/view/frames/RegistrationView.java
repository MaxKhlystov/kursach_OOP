package view.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import utils.InputValidator;

public class RegistrationView extends JFrame {
    private JTextField fullNameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton backButton;
    private JButton haveAccountButton;

    private String role;

    public RegistrationView() {
        initializeUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setRole(String role) {
        this.role = role;
        setTitle("Автосервис - Регистрация " + (role.equals("CLIENT") ? "клиента" : "механика"));
    }

    private void initializeUI() {
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Регистрация", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("ФИО:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        fullNameField = new JTextField(15);
        formPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Подтвердите пароль:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        confirmPasswordField = new JPasswordField(15);
        formPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Телефон:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel mainButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        registerButton = new JButton("Зарегистрироваться");
        backButton = new JButton("Назад");
        mainButtonPanel.add(registerButton);
        mainButtonPanel.add(backButton);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        haveAccountButton = new JButton("У меня уже есть аккаунт");
        haveAccountButton.setFont(new Font("Arial", Font.PLAIN, 12));
        haveAccountButton.setForeground(Color.BLUE);
        haveAccountButton.setBorderPainted(false);
        haveAccountButton.setContentAreaFilled(false);
        haveAccountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkPanel.add(haveAccountButton);

        buttonPanel.add(mainButtonPanel);
        buttonPanel.add(linkPanel);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    public void setRegisterListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public void setBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public void setHaveAccountListener(ActionListener listener) {
        haveAccountButton.addActionListener(listener);
    }

    public String getFullName() {
        return fullNameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    public String getEmail() {
        return emailField.getText().trim().toLowerCase();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public String getRole() {
        return role;
    }

    public String validateInputs() {
        String fullName = getFullName();
        String phone = getPhone();
        String email = getEmail();

        String nameError = InputValidator.validateName(fullName);
        if (nameError != null) return nameError;

        String phoneError = InputValidator.validatePhone(phone);
        if (phoneError != null) return phoneError;

        String emailError = InputValidator.validateEmail(email);
        if (emailError != null) return emailError;

        return null;
    }

    public void showView() {
        setVisible(true);
    }

    public void hideView() {
        setVisible(false);
        clearFields();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        fullNameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }
}