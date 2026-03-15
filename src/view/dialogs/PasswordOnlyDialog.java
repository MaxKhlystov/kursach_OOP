package view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PasswordOnlyDialog extends JDialog {
    private JPasswordField passwordField;
    private JButton okButton;
    private JButton cancelButton;
    private boolean success = false;

    public PasswordOnlyDialog(JFrame parent) {
        super(parent, "Подтверждение", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(350, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Введите пароль для подтверждения");
        panel.add(infoLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        okButton = new JButton("Подтвердить");
        cancelButton = new JButton("Отмена");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);
    }

    public void setOkListener(ActionListener listener) {
        okButton.addActionListener(listener);
    }

    public void setCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void clearField() {
        passwordField.setText("");
    }
}