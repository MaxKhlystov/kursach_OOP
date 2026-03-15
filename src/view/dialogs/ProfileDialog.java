package view.dialogs;

import javax.swing.*;
import java.awt.*;
import model.User;
import controller.ClientController;
import controller.MechanicController;
import utils.InputValidator;

public class ProfileDialog extends JDialog {
    private final User user;
    private final Object controller;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;

    public ProfileDialog(JFrame parent, User user, Object controller) {
        super(parent, "Личный кабинет", true);
        this.user = user;
        this.controller = controller;
        initializeUI();
        reloadData();
        setVisible(true);
    }

    private void initializeUI() {
        setSize(400, 350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Личный кабинет", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("ФИО:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        fullNameField = new JTextField(15);
        mainPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Телефон:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phoneField = new JTextField(15);
        mainPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = new JTextField(15);
        mainPanel.add(emailField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Сохранить");
        JButton changePasswordButton = new JButton("Сменить пароль");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> onSave());
        changePasswordButton.addActionListener(e -> onChangePassword());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void reloadData() {
        fullNameField.setText(user.getFullName());
        phoneField.setText(user.getPhone());
        emailField.setText(user.getEmail());
    }

    private void onSave() {
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();

        // Проверка на те же данные
        if (fullName.equals(user.getFullName()) &&
                phone.equals(user.getPhone()) &&
                email.equals(user.getEmail())) {
            JOptionPane.showMessageDialog(this,
                    "Нет изменений для сохранения",
                    "Информация",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nameError = InputValidator.validateName(fullName);
        if (nameError != null) {
            JOptionPane.showMessageDialog(this, nameError, "Ошибка", JOptionPane.ERROR_MESSAGE);
            reloadData();
            return;
        }

        String phoneError = InputValidator.validatePhone(phone);
        if (phoneError != null) {
            JOptionPane.showMessageDialog(this, phoneError, "Ошибка", JOptionPane.ERROR_MESSAGE);
            reloadData();
            return;
        }

        String emailError = InputValidator.validateEmail(email);
        if (emailError != null) {
            JOptionPane.showMessageDialog(this, emailError, "Ошибка", JOptionPane.ERROR_MESSAGE);
            reloadData();
            return;
        }

        boolean success = false;
        if (controller instanceof ClientController) {
            success = ((ClientController) controller).handleUpdateProfile(email, phone, fullName);
        } else if (controller instanceof MechanicController) {
            success = ((MechanicController) controller).handleUpdateProfile(email, phone, fullName);
        }

        if (success) {
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setEmail(email);
            dispose();
        } else {
            reloadData();
        }
    }

    private void onChangePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog((JFrame) getParent(), user, controller);
        dialog.setVisible(true);
    }
}