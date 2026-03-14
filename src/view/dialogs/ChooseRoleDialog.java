package view.dialogs;
import javax.swing.*;
import java.awt.*;

public class ChooseRoleDialog extends JDialog {
    private String selectedRole = null;

    public ChooseRoleDialog(JFrame parent) {
        super(parent, "Выбор роли", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Как вы хотите зарегистрироваться?", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton clientButton = new JButton("Клиент");
        clientButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clientButton.addActionListener(e -> {
            selectedRole = "CLIENT";
            dispose();
        });

        JButton mechanicButton = new JButton("Механик");
        mechanicButton.setFont(new Font("Arial", Font.PLAIN, 14));
        mechanicButton.addActionListener(e -> {
            selectedRole = "MECHANIC";
            dispose();
        });

        JButton adminButton = new JButton("Администратор");
        adminButton.setFont(new Font("Arial", Font.PLAIN, 14));
        adminButton.addActionListener(e -> {
            selectedRole = "ADMIN";
            dispose();
        });

        buttonPanel.add(clientButton);
        buttonPanel.add(mechanicButton);
        buttonPanel.add(adminButton);

        gbc.gridy = 1;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    public String getSelectedRole() {
        return selectedRole;
    }
}