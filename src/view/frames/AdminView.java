package view.frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.User;
import model.Car;
import model.Repair;
import model.CarBrand;
import model.CarModel;
import model.Notification;
import controller.AdminController;

public class AdminView extends JFrame {
    private final User currentUser;
    private final AdminController controller;
    private JTabbedPane tabbedPane;
    private JTable usersTable;
    private JTable carsTable;
    private JTable repairsTable;
    private JTable brandsTable;
    private JTable modelsTable;
    private JTable notificationsTable;

    public AdminView(AdminController controller, User user) {
        this.controller = controller;
        this.currentUser = user;
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("Автосервис - Администратор: " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        createMenuBar();

        tabbedPane = new JTabbedPane();

        // Вкладка с пользователями
        usersTable = new JTable();
        tabbedPane.addTab("Пользователи", new JScrollPane(usersTable));

        // Вкладка с автомобилями
        carsTable = new JTable();
        tabbedPane.addTab("Автомобили", new JScrollPane(carsTable));

        // Вкладка с ремонтами
        repairsTable = new JTable();
        tabbedPane.addTab("Ремонты", new JScrollPane(repairsTable));

        // Вкладка с марками
        brandsTable = new JTable();
        tabbedPane.addTab("Марки авто", new JScrollPane(brandsTable));

        // Вкладка с моделями
        modelsTable = new JTable();
        tabbedPane.addTab("Модели авто", new JScrollPane(modelsTable));

        // Вкладка с уведомлениями
        notificationsTable = new JTable();
        tabbedPane.addTab("Уведомления", new JScrollPane(notificationsTable));

        add(tabbedPane, BorderLayout.CENTER);

        createButtonPanel();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu systemMenu = new JMenu("Система");
        JMenuItem refreshMenuItem = new JMenuItem("Обновить данные");
        JMenuItem logoutMenuItem = new JMenuItem("Выйти из аккаунта");
        JMenuItem exitMenuItem = new JMenuItem("Выйти из приложения");

        refreshMenuItem.addActionListener(e -> loadData());
        logoutMenuItem.addActionListener(e -> controller.handleLogout());
        exitMenuItem.addActionListener(e -> System.exit(0));

        systemMenu.add(refreshMenuItem);
        systemMenu.addSeparator();
        systemMenu.add(logoutMenuItem);
        systemMenu.add(exitMenuItem);

        JMenu helpMenu = new JMenu("Помощь");
        JMenuItem aboutMenuItem = new JMenuItem("О программе");
        aboutMenuItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutMenuItem);

        menuBar.add(systemMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Управление"));

        JButton refreshButton = new JButton("Обновить");
        JButton deleteButton = new JButton("Удалить выбранное");
        JButton logoutButton = new JButton("Выйти");

        refreshButton.addActionListener(e -> loadData());
        deleteButton.addActionListener(e -> onDelete());
        logoutButton.addActionListener(e -> controller.handleLogout());

        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void loadData() {
        loadUsers();
        loadCars();
        loadRepairs();
        loadBrands();
        loadModels();
        loadNotifications();
    }

    private void loadUsers() {
        List<User> users = controller.getAllUsers();
        String[] columns = {"ID", "ФИО", "Роль", "Email", "Телефон", "Связан с ID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getFullName(),
                    user.getRole(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getLinkedUserId() != 0 ? user.getLinkedUserId() : "-"
            });
        }
        usersTable.setModel(model);
    }

    private void loadCars() {
        List<Car> cars = controller.getAllCars();
        String[] columns = {"ID", "Марка", "Модель", "Год", "VIN", "Гос. номер", "Владелец ID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Car car : cars) {
            model.addRow(new Object[]{
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getYear(),
                    car.getVin(),
                    car.getLicensePlate(),
                    car.getOwnerId()
            });
        }
        carsTable.setModel(model);
    }

    private void loadRepairs() {
        List<Repair> repairs = controller.getAllRepairs();
        String[] columns = {"ID", "Автомобиль ID", "Описание", "Статус", "Стоимость", "Механик ID", "Дата начала", "Дата конца"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Repair repair : repairs) {
            model.addRow(new Object[]{
                    repair.getId(),
                    repair.getCarId(),
                    repair.getDescription(),
                    repair.getStatusText(),
                    repair.getCost(),
                    repair.getMechanicId(),
                    repair.getStartDate(),
                    repair.getEndDate() != null ? repair.getEndDate() : "-"
            });
        }
        repairsTable.setModel(model);
    }

    private void loadBrands() {
        List<CarBrand> brands = controller.getAllBrands();
        String[] columns = {"ID", "Название", "Кем создан", "Дата создания"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (CarBrand brand : brands) {
            model.addRow(new Object[]{
                    brand.getId(),
                    brand.getName(),
                    brand.getCreatedBy(),
                    brand.getCreatedAt()
            });
        }
        brandsTable.setModel(model);
    }

    private void loadModels() {
        List<CarModel> models = controller.getAllModels();
        String[] columns = {"ID", "Марка ID", "Марка", "Название", "Кем создан", "Дата создания"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (CarModel carModel : models) {
            model.addRow(new Object[]{
                    carModel.getId(),
                    carModel.getBrandId(),
                    carModel.getBrandName(),
                    carModel.getName(),
                    carModel.getCreatedBy(),
                    carModel.getCreatedAt()
            });
        }
        modelsTable.setModel(model);
    }

    private void loadNotifications() {
        List<Notification> notifications = controller.getAllNotifications();
        String[] columns = {"ID", "Пользователь ID", "Сообщение", "Прочитано", "Дата"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Notification notification : notifications) {
            model.addRow(new Object[]{
                    notification.getId(),
                    notification.getUserId(),
                    notification.getMessage(),
                    notification.isRead() ? "Да" : "Нет",
                    notification.getCreatedAt()
            });
        }
        notificationsTable.setModel(model);
    }

    private void onDelete() {
        int selectedTab = tabbedPane.getSelectedIndex();
        String tabTitle = tabbedPane.getTitleAt(selectedTab);

        int selectedRow = -1;
        int id = -1;

        switch (tabTitle) {
            case "Пользователи":
                selectedRow = usersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    id = (int) usersTable.getValueAt(selectedRow, 0);
                    deleteUser(id);
                }
                break;
            case "Автомобили":
                selectedRow = carsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    id = (int) carsTable.getValueAt(selectedRow, 0);
                    deleteCar(id);
                }
                break;
            case "Ремонты":
                selectedRow = repairsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    id = (int) repairsTable.getValueAt(selectedRow, 0);
                    deleteRepair(id);
                }
                break;
            case "Марки авто":
                selectedRow = brandsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    id = (int) brandsTable.getValueAt(selectedRow, 0);
                    deleteBrand(id);
                }
                break;
            case "Модели авто":
                selectedRow = modelsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    id = (int) modelsTable.getValueAt(selectedRow, 0);
                    deleteModel(id);
                }
                break;
            case "Уведомления":
                selectedRow = notificationsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    id = (int) notificationsTable.getValueAt(selectedRow, 0);
                    deleteNotification(id);
                }
                break;
        }

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Выберите запись для удаления во вкладке \"" + tabTitle + "\"",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteUser(int id) {
        String itemName = "пользователя";
        confirmAndDelete(itemName, id, new Runnable() {
            @Override
            public void run() {
                controller.deleteUser(id);
            }
        });
    }

    private void deleteCar(int id) {
        String itemName = "автомобиль";
        confirmAndDelete(itemName, id, new Runnable() {
            @Override
            public void run() {
                controller.deleteCar(id);
            }
        });
    }

    private void deleteRepair(int id) {
        String itemName = "ремонт";
        confirmAndDelete(itemName, id, new Runnable() {
            @Override
            public void run() {
                controller.deleteRepair(id);
            }
        });
    }

    private void deleteBrand(int id) {
        String itemName = "марку";
        confirmAndDelete(itemName, id, new Runnable() {
            @Override
            public void run() {
                controller.deleteBrand(id);
            }
        });
    }

    private void deleteModel(int id) {
        String itemName = "модель";
        confirmAndDelete(itemName, id, new Runnable() {
            @Override
            public void run() {
                controller.deleteModel(id);
            }
        });
    }

    private void deleteNotification(int id) {
        String itemName = "уведомление";
        confirmAndDelete(itemName, id, new Runnable() {
            @Override
            public void run() {
                controller.deleteNotification(id);
            }
        });
    }

    private void confirmAndDelete(String itemName, int id, Runnable deleteAction) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить " + itemName + " с ID " + id + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            deleteAction.run();
            loadData(); // Обновляем таблицу после удаления
        }
    }

    private void showAbout() {
        String about = "Автосервис v3.0\n\n" +
                "Панель администратора\n" +
                "Текущий пользователь: " + currentUser.getFullName() + "\n" +
                "Роль: Администратор\n\n" +
                "Доступные операции:\n" +
                "• Просмотр всех таблиц\n" +
                "• Удаление записей";
        JOptionPane.showMessageDialog(this, about, "О программе", JOptionPane.INFORMATION_MESSAGE);
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
}