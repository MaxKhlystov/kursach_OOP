package view.dialogs;

import javax.swing.*;
import java.awt.*;
import model.User;
import model.Car;
import controller.MechanicController;
import java.util.List;

public class AddRepairDialog extends JDialog {
    private final MechanicController controller;

    public AddRepairDialog(JFrame parent, MechanicController controller) {
        super(parent, "Добавить ремонт", true);
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель выбора клиента
        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel clientLabel = new JLabel("Выберите клиента:");
        JComboBox<User> clientComboBox = new JComboBox<>();
        JCheckBox filterCheckBox = new JCheckBox("Показать только автомобили выбранного клиента");

        List<User> clients = controller.getClients();
        for (User client : clients) {
            clientComboBox.addItem(client);
        }

        clientPanel.add(clientLabel);
        clientPanel.add(clientComboBox);
        clientPanel.add(filterCheckBox);

        // Панель выбора автомобиля
        JPanel carPanel = new JPanel(new BorderLayout());
        JLabel carLabel = new JLabel("Выберите автомобиль:");
        JComboBox<Car> carComboBox = new JComboBox<>();

        List<Car> allCars = controller.getAllCars();
        for (Car car : allCars) {
            carComboBox.addItem(car);
        }

        carPanel.add(carLabel, BorderLayout.NORTH);
        carPanel.add(carComboBox, BorderLayout.CENTER);

        // Панель данных ремонта
        JPanel repairPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField descriptionField = new JTextField();
        JTextField costField = new JTextField("0.0");

        repairPanel.add(new JLabel("Описание ремонта:"));
        repairPanel.add(descriptionField);
        repairPanel.add(new JLabel("Стоимость:"));
        repairPanel.add(costField);
        repairPanel.add(new JLabel("Статус:"));
        repairPanel.add(new JLabel("Диагностика (автоматически)"));

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Добавить ремонт");
        JButton cancelButton = new JButton("Отмена");

        // Логика фильтрации
        clientComboBox.addActionListener(e -> {
            if (filterCheckBox.isSelected()) {
                User selectedClient = (User) clientComboBox.getSelectedItem();
                if (selectedClient != null) {
                    carComboBox.removeAllItems();
                    List<Car> clientCars = controller.getClientCars(selectedClient.getId());
                    for (Car car : clientCars) {
                        carComboBox.addItem(car);
                    }
                }
            }
        });

        filterCheckBox.addActionListener(e -> {
            if (filterCheckBox.isSelected()) {
                User selectedClient = (User) clientComboBox.getSelectedItem();
                if (selectedClient != null) {
                    carComboBox.removeAllItems();
                    List<Car> clientCars = controller.getClientCars(selectedClient.getId());
                    for (Car car : clientCars) {
                        carComboBox.addItem(car);
                    }
                }
            } else {
                carComboBox.removeAllItems();
                List<Car> allCarsList = controller.getAllCars();
                for (Car car : allCarsList) {
                    carComboBox.addItem(car);
                }
            }
        });

        addButton.addActionListener(e -> {
            try {
                Car selectedCar = (Car) carComboBox.getSelectedItem();
                String description = descriptionField.getText().trim();
                double cost = Double.parseDouble(costField.getText().trim());

                if (selectedCar == null) {
                    JOptionPane.showMessageDialog(this, "Выберите автомобиль", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Введите описание ремонта", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                controller.handleAddRepair(selectedCar.getId(), description, cost);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Проверьте правильность числовых полей", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(clientPanel, BorderLayout.NORTH);
        mainPanel.add(carPanel, BorderLayout.CENTER);
        mainPanel.add(repairPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}