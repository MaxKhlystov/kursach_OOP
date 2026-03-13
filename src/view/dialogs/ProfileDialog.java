package view.dialogs;

import javax.swing.*;
import java.awt.*;
import model.User;
import controller.ClientController;
import controller.MechanicController;

public class ProfileDialog extends JDialog {
    private final User user;
    private final Object controller; // ClientController или MechanicController

    public ProfileDialog(JFrame parent, User user, Object controller) {
        super(parent, "Личный кабинет", true);
        this.user = user;
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        setLayout(new GridLayout(7, 2, 10, 10));

        JTextField usernameField = new JTextField(user.getFullName());
        JTextField emailField = new JTextField(user.getEmail());
        JTextField phoneField = new JTextField(user.getPhone());
        JTextField fullNameField = new JTextField(user.getFullName());

        usernameField.setEditable(false);

        add(new JLabel("Логин:"));
        add(usernameField);
        add(new JLabel("ФИО:"));
        add(fullNameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Телефон:"));
        add(phoneField);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String fullName = fullNameField.getText().trim();

            if (email.isEmpty() || phone.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (controller instanceof ClientController) {
                ((ClientController) controller).handleUpdateProfile(email, phone, fullName);
            } else if (controller instanceof MechanicController) {
                ((MechanicController) controller).handleUpdateProfile(email, phone, fullName);
            }
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        add(saveButton);
        add(cancelButton);
        setVisible(true);
    }
}