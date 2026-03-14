package view.dialogs;

import javax.swing.*;
import java.awt.*;
import model.User;
import controller.ClientController;
import controller.MechanicController;

public class ProfileDialog extends JDialog {
    private final User user;
    private final Object controller; // ClientController или MechanicController
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;

    public ProfileDialog(JFrame parent, User user, Object controller) {
        super(parent, "Личный кабинет", true);
        this.user = user;
        this.controller = controller;
        initializeUI();
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

        // Заголовок
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Личный кабинет", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, gbc);

        // ФИО
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("ФИО:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        fullNameField = new JTextField(user.getFullName(), 15);
        mainPanel.add(fullNameField, gbc);

        // Телефон
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Телефон:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phoneField = new JTextField(user.getPhone(), 15);
        mainPanel.add(phoneField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = new JTextField(user.getEmail(), 15);
        mainPanel.add(emailField, gbc);

        // Панель кнопок
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
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void onSave() {
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (controller instanceof ClientController) {
            ((ClientController) controller).handleUpdateProfile(email, phone, fullName);
        } else if (controller instanceof MechanicController) {
            ((MechanicController) controller).handleUpdateProfile(email, phone, fullName);
        }
        dispose();
    }

    private void onChangePassword() {
        new ChangePasswordDialog((JFrame) getParent(), user, controller);
    }
}