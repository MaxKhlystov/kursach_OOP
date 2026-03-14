package view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.User;
import model.Car;
import model.CarListItem;
import controller.MechanicController;

public class AddRepairDialog extends JDialog {
    private final MechanicController controller;
    private JComboBox<User> clientComboBox;
    private JList<CarListItem> carList;
    private DefaultListModel<CarListItem> carListModel;
    private JTextField descriptionField;
    private JTextField costField;

    public AddRepairDialog(JFrame parent, MechanicController controller) {
        super(parent, "Добавить ремонт", true);
        this.controller = controller;
        initializeUI();
        loadClients();
    }

    private void initializeUI() {
        setSize(500, 450);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Выбор клиента"));
        topPanel.add(new JLabel("Клиент:"));
        clientComboBox = new JComboBox<>();
        clientComboBox.setPreferredSize(new Dimension(250, 30));
        clientComboBox.addActionListener(e -> updateCarList());
        topPanel.add(clientComboBox);

        JPanel carPanel = new JPanel(new BorderLayout(10, 10));
        carPanel.setBorder(BorderFactory.createTitledBorder("Автомобили клиента"));

        carListModel = new DefaultListModel<>();
        carList = new JList<>(carListModel);
        carList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carList.setVisibleRowCount(5);
        carList.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane carScrollPane = new JScrollPane(carList);
        carScrollPane.setPreferredSize(new Dimension(400, 120));
        carPanel.add(carScrollPane, BorderLayout.CENTER);

        JPanel repairPanel = new JPanel(new GridBagLayout());
        repairPanel.setBorder(BorderFactory.createTitledBorder("Данные ремонта"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        repairPanel.add(new JLabel("Описание:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        descriptionField = new JTextField(20);
        repairPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        repairPanel.add(new JLabel("Стоимость (руб):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        costField = new JTextField("0.0", 10);
        repairPanel.add(costField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Добавить ремонт");
        JButton cancelButton = new JButton("Отмена");

        addButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));

        addButton.addActionListener(e -> onAdd());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(carPanel, BorderLayout.CENTER);
        mainPanel.add(repairPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        carList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Нет доступных автомобилей");
                    setForeground(Color.GRAY);
                } else if (value instanceof CarListItem) {
                    setText(value.toString());
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });
    }

    private void loadClients() {
        List<User> clients = controller.getClients();
        DefaultComboBoxModel<User> model = new DefaultComboBoxModel<>();
        for (User client : clients) {
            model.addElement(client);
        }
        clientComboBox.setModel(model);
        clientComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    setText(((User) value).getFullName());
                }
                return this;
            }
        });

        if (clients.size() > 0) {
            clientComboBox.setSelectedIndex(0);
            updateCarList();
        }
    }

    private void updateCarList() {
        carListModel.clear();

        User selectedClient = (User) clientComboBox.getSelectedItem();
        if (selectedClient != null) {
            List<Car> clientCars = controller.getClientCars(selectedClient.getId());
            for (Car car : clientCars) {
                carListModel.addElement(new CarListItem(car));
            }
        }

        if (carListModel.isEmpty()) {
            carListModel.addElement(null);
        }
    }

    private void onAdd() {
        try {
            User selectedClient = (User) clientComboBox.getSelectedItem();
            CarListItem selectedCarItem = carList.getSelectedValue();

            if (selectedClient == null) {
                JOptionPane.showMessageDialog(this,
                        "Выберите клиента", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedCarItem == null || selectedCarItem.getCar() == null) {
                JOptionPane.showMessageDialog(this,
                        "Выберите автомобиль", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Введите описание ремонта", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double cost;
            try {
                cost = Double.parseDouble(costField.getText().trim());
                if (cost < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Стоимость не может быть отрицательной", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Стоимость должна быть числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            controller.handleAddRepair(selectedCarItem.getCar().getId(), description, cost);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}