package view.frames;

import javax.swing.*;
import java.awt.*;
import model.User;
import model.Car;
import model.Repair;
import service.interfaces.IRepairService;
import controller.ClientController;
import view.dialogs.AddCarDialog;
import view.dialogs.ProfileDialog;

import java.util.List;

public class ClientView extends JFrame {
    private final User currentUser;
    private final ClientController controller;
    private JTextArea contentArea;
    private JPanel dynamicButtonsPanel;

    public ClientView(ClientController controller, User user) {
        this.controller = controller;
        this.currentUser = user;
        initializeUI();
        setupController();
    }

    private void initializeUI() {
        setTitle("Автосервис - Клиент: " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                controller.handleLogout();
            }
        });

        createMenuBar();
        createButtonPanel();
        createContentArea();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu systemMenu = new JMenu("Система");
        JMenuItem profileMenuItem = new JMenuItem("Личный кабинет");
        JMenuItem logoutMenuItem = new JMenuItem("Выйти из аккаунта");
        JMenuItem exitMenuItem = new JMenuItem("Выйти из приложения");

        systemMenu.add(profileMenuItem);
        systemMenu.addSeparator();
        systemMenu.add(logoutMenuItem);
        systemMenu.add(exitMenuItem);

        JMenu carMenu = new JMenu("Автомобили");
        JMenuItem addCarMenuItem = new JMenuItem("Добавить автомобиль");
        JMenuItem myCarsMenuItem = new JMenuItem("Мои автомобили");
        carMenu.add(addCarMenuItem);
        carMenu.add(myCarsMenuItem);

        JMenu repairMenu = new JMenu("Ремонты");
        JMenuItem myRepairsMenuItem = new JMenuItem("Мои ремонты");
        repairMenu.add(myRepairsMenuItem);

        JMenu helpMenu = new JMenu("Помощь");
        JMenuItem userGuideMenuItem = new JMenuItem("Руководство пользователя");
        JMenuItem aboutMenuItem = new JMenuItem("О программе");
        helpMenu.add(userGuideMenuItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutMenuItem);

        menuBar.add(systemMenu);
        menuBar.add(carMenu);
        menuBar.add(repairMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Быстрые действия"));

        JButton profileButton = new JButton("Личный кабинет");
        JButton logoutButton = new JButton("Выйти из аккаунта");
        JButton addCarButton = new JButton("Добавить авто");
        JButton myCarsButton = new JButton("Мои автомобили");
        JButton myRepairsButton = new JButton("Мои ремонты");

        buttonPanel.add(profileButton);
        buttonPanel.add(addCarButton);
        buttonPanel.add(myCarsButton);
        buttonPanel.add(myRepairsButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void createContentArea() {
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        contentArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Информация"));

        add(scrollPane, BorderLayout.CENTER);

        dynamicButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(dynamicButtonsPanel, BorderLayout.SOUTH);
    }

    private void setupController() {
        JMenuBar menuBar = getJMenuBar();
        JMenu systemMenu = menuBar.getMenu(0);
        JMenu carMenu = menuBar.getMenu(1);
        JMenu repairMenu = menuBar.getMenu(2);
        JMenu helpMenu = menuBar.getMenu(3);

        systemMenu.getItem(0).addActionListener(e -> showProfileDialog());
        systemMenu.getItem(2).addActionListener(e -> controller.handleLogout());
        systemMenu.getItem(3).addActionListener(e -> System.exit(0));

        carMenu.getItem(0).addActionListener(e -> {
            showAddCarDialog();
        });
        carMenu.getItem(1).addActionListener(e -> {
            controller.handleViewCars();
        });

        repairMenu.getItem(0).addActionListener(e -> {
            controller.handleViewRepairs();
        });

        helpMenu.getItem(0).addActionListener(e -> controller.handleShowUserGuide());
        helpMenu.getItem(2).addActionListener(e -> controller.handleShowAbout());

        JPanel buttonPanel = (JPanel) getContentPane().getComponent(0);


        if (buttonPanel.getComponentCount() >= 5) {
            ((JButton) buttonPanel.getComponent(0)).addActionListener(e -> {
                showProfileDialog();
            });
            ((JButton) buttonPanel.getComponent(1)).addActionListener(e -> {
                showAddCarDialog();
            });
            ((JButton) buttonPanel.getComponent(2)).addActionListener(e -> {
                controller.handleViewCars();
            });
            ((JButton) buttonPanel.getComponent(3)).addActionListener(e -> {
                controller.handleViewRepairs();
            });
            ((JButton) buttonPanel.getComponent(4)).addActionListener(e -> {
                controller.handleLogout();
            });
        }
    }

    public void displayWelcome() {
        contentArea.setText("Добро пожаловать, " + currentUser.getFullName() + "!\n\n" +
                "Вы вошли как клиент. Используйте меню или кнопки для работы с приложением.\n\n" +
                "Доступные функции:\n" +
                "• Личный кабинет (изменение данных)\n" +
                "• Добавление и управление автомобилями\n" +
                "• Просмотр истории ремонтов");
    }

    public void displayCars(List<Car> cars, ClientController controller) {
        StringBuilder sb = new StringBuilder();
        sb.append("МОИ АВТОМОБИЛИ\n\n");

        if (cars.isEmpty()) {
            sb.append("У вас нет зарегистрированных автомобилей.\n");
        } else {
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                sb.append((i + 1) + ". ").append(car.toString()).append("\n")
                        .append("   Гос. номер: ").append(car.getLicensePlate()).append("\n")
                        .append("   Дата регистрации: ").append(car.getRegistrationDate()).append("\n\n");
            }
        }

        contentArea.setText(sb.toString());

        dynamicButtonsPanel.removeAll();
        for (Car car : cars) {
            JButton deleteButton = new JButton("Удалить " + car.getBrand() + " " + car.getModel());
            final int carId = car.getId();
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Вы уверены, что хотите удалить этот автомобиль?\nВсе связанные ремонты также будут удалены.",
                        "Подтверждение удаления",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    controller.handleDeleteCar(carId);
                }
            });
            dynamicButtonsPanel.add(deleteButton);
        }

        dynamicButtonsPanel.revalidate();
        dynamicButtonsPanel.repaint();
    }

    public void displayRepairs(List<Car> cars, IRepairService repairService) {
        StringBuilder sb = new StringBuilder();
        sb.append("МОИ РЕМОНТЫ\n\n");

        if (cars.isEmpty()) {
            sb.append("У вас нет автомобилей для отображения ремонтов.\n");
        } else {
            for (Car car : cars) {
                List<Repair> repairs = repairService.getCarRepairs(car.getId());

                sb.append("Автомобиль: ").append(car.getBrand()).append(" ").append(car.getModel())
                        .append(" (").append(car.getLicensePlate()).append(")\n");

                if (repairs.isEmpty()) {
                    sb.append("   Ремонтов не найдено\n\n");
                } else {
                    for (Repair repair : repairs) {
                        sb.append("   📋 ").append(repair.getDescription()).append("\n")
                                .append("      Статус: ").append(repair.getStatusText()).append("\n")
                                .append("      Стоимость: ").append(String.format("%.2f", repair.getCost())).append(" руб.\n")
                                .append("      Дата начала: ").append(repair.getStartDate()).append("\n");

                        if (repair.getEndDate() != null) {
                            sb.append("      Дата окончания: ").append(repair.getEndDate()).append("\n");
                        }
                        sb.append("\n");
                    }
                }
            }
        }

        contentArea.setText(sb.toString());
        dynamicButtonsPanel.removeAll();
        dynamicButtonsPanel.revalidate();
        dynamicButtonsPanel.repaint();
    }

    private void showProfileDialog() {
        new ProfileDialog(this, currentUser, controller);
    }

    private void showAddCarDialog() {
        AddCarDialog dialog = new AddCarDialog(this, controller);
        dialog.setVisible(true);
    }

    public void showView() {
        setVisible(true);
    }

    public void hideView() {
        setVisible(false);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateProfileInfo(User user) {
        setTitle("Автосервис - Клиент: " + user.getFullName());
    }

    public void displayUserGuide(String guide) {
        JTextArea guideArea = new JTextArea(guide);
        guideArea.setEditable(false);
        guideArea.setFont(new Font("Arial", Font.PLAIN, 14));
        guideArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(guideArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Руководство пользователя", JOptionPane.INFORMATION_MESSAGE);
    }

    public void displayAbout(String about) {
        JOptionPane.showMessageDialog(this, about, "О программе", JOptionPane.INFORMATION_MESSAGE);
    }
}