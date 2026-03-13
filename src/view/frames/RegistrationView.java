package view.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegistrationView extends JFrame {
    private JTextField fullNameField;      // Было usernameField, теперь fullNameField
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton backButton;

    public RegistrationView() {
        initializeUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Выход при закрытии
    }

    private void initializeUI() {
        setTitle("Автосервис - Регистрация");
        setSize(450, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Регистрация", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 20));

        // ФИО теперь первое поле (было username)
        formPanel.add(new JLabel("ФИО:"));
        fullNameField = new JTextField();
        formPanel.add(fullNameField);

        formPanel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Подтвердите пароль:"));
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Телефон:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Роль:"));
        String[] roles = {"CLIENT", "MECHANIC"};
        roleComboBox = new JComboBox<>(roles);
        formPanel.add(roleComboBox);

        registerButton = new JButton("Зарегистрироваться");
        backButton = new JButton("Назад");
        formPanel.add(backButton);
        formPanel.add(registerButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    public void setRegisterListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public void setBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public String getFullName() { return fullNameField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getConfirmPassword() { return new String(confirmPasswordField.getPassword()); }
    public String getEmail() { return emailField.getText().trim(); }
    public String getPhone() { return phoneField.getText().trim(); }
    public String getRole() { return (String) roleComboBox.getSelectedItem(); }

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
        roleComboBox.setSelectedIndex(0);
    }
}