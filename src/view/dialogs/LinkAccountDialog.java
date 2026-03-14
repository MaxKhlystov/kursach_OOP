package view.dialogs;

import javax.swing.*;
import java.awt.*;
import model.User;
import service.interfaces.IUserService;
import service.impl.UserService;

public class LinkAccountDialog extends JDialog {
    private User foundUser = null;
    private boolean success = false;
    private final IUserService userService;
    private JTextField searchField;

    public LinkAccountDialog(JFrame parent) {
        super(parent, "Поиск существующего аккаунта", true);
        this.userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 200);
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
        JLabel infoLabel = new JLabel("Введите email или телефон существующего аккаунта");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(infoLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Поиск:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        searchField = new JTextField(15);
        mainPanel.add(searchField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton searchButton = new JButton("Найти");
        JButton cancelButton = new JButton("Отмена");

        searchButton.addActionListener(e -> searchUser());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void searchUser() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите email или телефон", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        foundUser = userService.findByEmailOrPhone(searchTerm);

        if (foundUser != null) {
            String message = String.format("Найден пользователь:\n\nФИО: %s\nТелефон: %s\nEmail: %s\n\nСвязать с этим аккаунтом?",
                    foundUser.getFullName(), foundUser.getPhone(), foundUser.getEmail());

            int result = JOptionPane.showConfirmDialog(this, message, "Пользователь найден",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                success = true;
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Пользователь не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public User getFoundUser() {
        return foundUser;
    }

    public boolean isSuccess() {
        return success;
    }
}