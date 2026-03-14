package view.dialogs;

import javax.swing.*;
import java.awt.*;

public class EnterPasswordDialog extends JDialog {
    private boolean success = false;
    private JPasswordField passwordField;
    private String role;
    private static final String MECHANIC_PASSWORD = "MEHANIK";
    private static final String ADMIN_PASSWORD = "ADMIN";

    public EnterPasswordDialog(JFrame parent, String role) {
        super(parent, "Проверка доступа", true);
        this.role = role;
        initializeUI();
    }

    private void initializeUI() {
        setSize(350, 180);
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

        String message = role.equals("ADMIN") ?
                "Введите пароль для регистрации как администратор" :
                "Введите пароль для регистрации как механик";

        JLabel infoLabel = new JLabel(message);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(infoLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");

        okButton.addActionListener(e -> checkPassword());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void checkPassword() {
        String enteredPassword = new String(passwordField.getPassword());

        if (role.equals("ADMIN") && ADMIN_PASSWORD.equals(enteredPassword)) {
            success = true;
            dispose();
        } else if (role.equals("MECHANIC") && MECHANIC_PASSWORD.equals(enteredPassword)) {
            success = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Неверный пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    public boolean isSuccess() {
        return success;
    }
}