package view.dialogs;

import javax.swing.*;
import java.awt.*;
import controller.ClientController;

public class AddCarDialog extends JDialog {
    private final ClientController controller;

    public AddCarDialog(JFrame parent, ClientController controller) {
        super(parent, "Добавить автомобиль", true);
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        setLayout(new GridLayout(8, 2, 10, 10));

        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField vinField = new JTextField();
        JTextField licensePlateField = new JTextField();

        add(new JLabel("Марка:"));
        add(brandField);
        add(new JLabel("Модель:"));
        add(modelField);
        add(new JLabel("Год:"));
        add(yearField);
        add(new JLabel("VIN:"));
        add(vinField);
        add(new JLabel("Гос. номер:"));
        add(licensePlateField);

        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");

        addButton.addActionListener(e -> {
            try {
                String brand = brandField.getText().trim();
                String model = modelField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                String vin = vinField.getText().trim();
                String licensePlate = licensePlateField.getText().trim();

                if (brand.isEmpty() || model.isEmpty() || vin.isEmpty() || licensePlate.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                controller.handleAddCar(brand, model, year, vin, licensePlate);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Проверьте правильность числовых полей", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        add(addButton);
        add(cancelButton);
        setVisible(true);
    }
}