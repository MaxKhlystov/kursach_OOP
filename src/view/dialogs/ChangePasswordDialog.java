package view.dialogs;

import javax.swing.*;
import java.awt.*;
import model.User;
import controller.ClientController;
import controller.MechanicController;

public class ChangePasswordDialog extends JDialog {
    private final User user;
    private final Object controller;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private boolean success = false;

    public ChangePasswordDialog(JFrame parent, User user, Object controller) {
        super(parent, "Смена пароля", true);
        this.user = user;
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 300);
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

        JLabel titleLabel = new JLabel("Смена пароля", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Текущий пароль:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        oldPasswordField = new JPasswordField(15);
        mainPanel.add(oldPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Новый пароль:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        newPasswordField = new JPasswordField(15);
        mainPanel.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Подтвердите пароль:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        confirmPasswordField = new JPasswordField(15);
        mainPanel.add(confirmPasswordField, gbc);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> onChangePassword());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
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

    private void onChangePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Проверка заполнения
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Проверка старого пароля
        if (!oldPassword.equals(user.getPassword())) {
            JOptionPane.showMessageDialog(this, "Неверный текущий пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Проверка, что новый пароль не совпадает со старым
        if (newPassword.equals(oldPassword)) {
            JOptionPane.showMessageDialog(this, "Новый пароль должен отличаться от текущего", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Проверка совпадения нового пароля и подтверждения
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Новый пароль и подтверждение не совпадают", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Проверка минимальной длины
        if (newPassword.length() < 3) {
            JOptionPane.showMessageDialog(this, "Пароль должен содержать минимум 3 символа", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Вызов соответствующего контроллера
        if (controller instanceof ClientController) {
            success = ((ClientController) controller).handleChangePassword(oldPassword, newPassword);
        } else if (controller instanceof MechanicController) {
            success = ((MechanicController) controller).handleChangePassword(oldPassword, newPassword);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Пароль успешно изменен", "Успех", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    public boolean isSuccess() {
        return success;
    }
}