package view.frames;

import javax.swing.*;
import java.awt.*;
import model.User;
import model.Car;
import model.Repair;
import service.interfaces.ICarService;
import service.interfaces.IUserService;
import controller.MechanicController;
import view.dialogs.AddRepairDialog;
import view.dialogs.ProfileDialog;

import java.util.List;

public class MechanicView extends JFrame {
    private final User currentUser;
    private final MechanicController controller; // СОХРАНЯЕМ ССЫЛКУ
    private JTextArea contentArea;
    private JPanel dynamicButtonsPanel;

    public MechanicView(MechanicController controller, User user) {
        this.controller = controller; // СОХРАНЯЕМ
        this.currentUser = user;
        initializeUI();
        setupController();
    }

    private void initializeUI() {
        setTitle("Автосервис - Механик: " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Обработчик закрытия окна - ТЕПЕРЬ ИСПОЛЬЗУЕМ СОХРАНЕННЫЙ КОНТРОЛЛЕР
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                controller.handleLogout(); // ПРЯМОЙ ДОСТУП К КОНТРОЛЛЕРУ
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
        JMenuItem viewCarsMenuItem = new JMenuItem("Просмотреть все автомобили");
        carMenu.add(viewCarsMenuItem);

        JMenu repairMenu = new JMenu("Ремонты");
        JMenuItem addRepairMenuItem = new JMenuItem("Добавить ремонт");
        JMenuItem viewRepairsMenuItem = new JMenuItem("Просмотреть все ремонты");
        repairMenu.add(addRepairMenuItem);
        repairMenu.add(viewRepairsMenuItem);

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
        JButton addRepairButton = new JButton("Добавить ремонт");
        JButton viewCarsButton = new JButton("Все автомобили");
        JButton viewRepairsButton = new JButton("Все ремонты");

        buttonPanel.add(profileButton);
        buttonPanel.add(addRepairButton);
        buttonPanel.add(viewCarsButton);
        buttonPanel.add(viewRepairsButton);
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
        carMenu.getItem(0).addActionListener(e -> controller.handleViewCars());
        repairMenu.getItem(0).addActionListener(e -> showAddRepairDialog());
        repairMenu.getItem(1).addActionListener(e -> controller.handleViewRepairs());
        helpMenu.getItem(0).addActionListener(e -> controller.handleShowUserGuide());
        helpMenu.getItem(2).addActionListener(e -> controller.handleShowAbout());

        JPanel buttonPanel = (JPanel) getContentPane().getComponent(0);
        ((JButton) buttonPanel.getComponent(0)).addActionListener(e -> showProfileDialog());
        ((JButton) buttonPanel.getComponent(1)).addActionListener(e -> showAddRepairDialog());
        ((JButton) buttonPanel.getComponent(2)).addActionListener(e -> controller.handleViewCars());
        ((JButton) buttonPanel.getComponent(3)).addActionListener(e -> controller.handleViewRepairs());
        ((JButton) buttonPanel.getComponent(4)).addActionListener(e -> controller.handleLogout());
    }

    public void displayWelcome() {
        contentArea.setText("Добро пожаловать, " + currentUser.getFullName() + "!\n\n" +
                "Вы вошли как механик. Используйте меню или кнопки для работы с приложением.\n\n" +
                "Доступные функции:\n" +
                "• Личный кабинет (изменение данных)\n" +
                "• Просмотр всех автомобилей клиентов\n" +
                "• Добавление новых ремонтов\n" +
                "• Управление статусами ремонтов\n" +
                "• Отслеживание выполнения работ");
    }

    public void displayAllCars(List<Car> cars, IUserService userService) {
        StringBuilder sb = new StringBuilder();
        sb.append("ВСЕ АВТОМОБИЛИ КЛИЕНТОВ\n\n");

        if (cars.isEmpty()) {
            sb.append("В системе нет автомобилей.\n");
        } else {
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                User owner = userService.getUserById(car.getOwnerId());
                String ownerName = (owner != null) ? owner.getFullName() : "Неизвестно";

                sb.append((i + 1) + ". ").append(car.toString()).append("\n")
                        .append("   Гос. номер: ").append(car.getLicensePlate()).append("\n")
                        .append("   Владелец: ").append(ownerName).append("\n")
                        .append("   Телефон: ").append(owner != null ? owner.getPhone() : "Неизвестно").append("\n")
                        .append("   Дата регистрации: ").append(car.getRegistrationDate()).append("\n\n");
            }
        }

        contentArea.setText(sb.toString());
        dynamicButtonsPanel.removeAll();
        dynamicButtonsPanel.revalidate();
        dynamicButtonsPanel.repaint();
    }

    public void displayAllRepairs(List<Repair> repairs, ICarService carService, IUserService userService, MechanicController controller) {
        StringBuilder sb = new StringBuilder();
        sb.append("ВСЕ РЕМОНТЫ\n\n");

        if (repairs.isEmpty()) {
            sb.append("В системе нет ремонтов.\n");
        } else {
            for (int i = 0; i < repairs.size(); i++) {
                Repair repair = repairs.get(i);
                Car car = carService.getCarById(repair.getCarId());
                User owner = (car != null) ? userService.getUserById(car.getOwnerId()) : null;
                String carInfo = (car != null) ? car.toString() : "Автомобиль не найден";
                String ownerInfo = (owner != null) ? owner.getFullName() : "Неизвестно";

                sb.append((i + 1) + ". Автомобиль: ").append(carInfo).append("\n")
                        .append("   Владелец: ").append(ownerInfo).append("\n")
                        .append("   Описание: ").append(repair.getDescription()).append("\n")
                        .append("   Статус: ").append(repair.getStatusText()).append("\n")
                        .append("   Стоимость: ").append(String.format("%.2f", repair.getCost())).append(" руб.\n")
                        .append("   Дата начала: ").append(repair.getStartDate()).append("\n");

                if (repair.getEndDate() != null) {
                    sb.append("   Дата окончания: ").append(repair.getEndDate()).append("\n");
                }

                sb.append("\n");
            }
        }

        contentArea.setText(sb.toString());

        dynamicButtonsPanel.removeAll();
        for (Repair repair : repairs) {
            if (!"COMPLETED".equals(repair.getStatus()) && !"CANCELLED".equals(repair.getStatus())) {
                JButton statusButton = new JButton("Изменить статус ремонта #" + repair.getId());
                final int repairId = repair.getId();
                final String currentStatus = repair.getStatus();
                statusButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Изменить статус ремонта на: " + getNextStatusText(currentStatus) + "?",
                            "Подтверждение изменения статуса",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.handleUpdateRepairStatus(repairId, currentStatus);
                    }
                });
                dynamicButtonsPanel.add(statusButton);
            }
        }

        dynamicButtonsPanel.revalidate();
        dynamicButtonsPanel.repaint();
    }

    private void showProfileDialog() {
        new ProfileDialog(this, currentUser, controller);
    }

    private void showAddRepairDialog() {
        new AddRepairDialog(this, controller);
    }

    private String getNextStatusText(String currentStatus) {
        switch (currentStatus) {
            case "DIAGNOSTICS": return "В ремонте";
            case "IN_REPAIR": return "Ремонт завершен";
            default: return "Неизвестно";
        }
    }

    public void showView() {
        displayWelcome();
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
        setTitle("Автосервис - Механик: " + user.getFullName());
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