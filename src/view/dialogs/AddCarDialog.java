package view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import controller.ClientController;
import service.interfaces.ICarBrandService;
import service.interfaces.ICarModelService;
import service.impl.CarBrandService;
import service.impl.CarModelService;
import utils.InputValidator;

public class AddCarDialog extends JDialog {
    private final ClientController controller;
    private final ICarBrandService brandService;
    private final ICarModelService modelService;
    private JComboBox<String> brandComboBox;
    private JComboBox<String> modelComboBox;
    private JTextField yearField;
    private JTextField vinField;
    private JTextField licensePlateField;

    public AddCarDialog(JFrame parent, ClientController controller) {
        super(parent, "Добавить автомобиль", true);
        this.controller = controller;
        this.brandService = new CarBrandService();
        this.modelService = new CarModelService();
        initializeUI();
        loadBrands();
    }

    private void initializeUI() {
        setSize(450, 400);
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
        JLabel titleLabel = new JLabel("Добавление автомобиля", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Марка:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        brandComboBox = new JComboBox<>();
        brandComboBox.setEditable(true);
        brandComboBox.addActionListener(e -> onBrandSelected());
        mainPanel.add(brandComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Модель:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        modelComboBox = new JComboBox<>();
        modelComboBox.setEditable(true);
        mainPanel.add(modelComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Год выпуска:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        yearField = new JTextField(10);
        mainPanel.add(yearField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("VIN-номер:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        vinField = new JTextField(15);
        mainPanel.add(vinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Гос. номер:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        licensePlateField = new JTextField(10);
        mainPanel.add(licensePlateField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");

        addButton.addActionListener(e -> onAdd());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadBrands() {
        List<String> brands = brandService.getAllBrandNames();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("");
        for (String brand : brands) {
            model.addElement(brand);
        }
        brandComboBox.setModel(model);
    }

    private void onBrandSelected() {
        String selectedBrand = (String) brandComboBox.getSelectedItem();
        if (selectedBrand != null && !selectedBrand.trim().isEmpty()) {
            loadModels(selectedBrand.trim());
        } else {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("");
            modelComboBox.setModel(model);
        }
    }

    private void loadModels(String brandName) {
        List<String> models = modelService.getModelNamesByBrand(brandName);
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("");
        for (String m : models) {
            model.addElement(m);
        }
        modelComboBox.setModel(model);
    }

    private void onAdd() {
        try {
            String brand = brandComboBox.getSelectedItem() != null ?
                    brandComboBox.getSelectedItem().toString().trim() : "";
            String model = modelComboBox.getSelectedItem() != null ?
                    modelComboBox.getSelectedItem().toString().trim() : "";
            String yearStr = yearField.getText().trim();
            String vin = vinField.getText().trim().toUpperCase();
            String licensePlate = licensePlateField.getText().trim().toUpperCase();

            if (brand.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите марку автомобиля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (model.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите модель автомобиля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (yearStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите год выпуска", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (vin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите VIN-номер", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (licensePlate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите гос. номер", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!InputValidator.isValidVin(vin)) {
                JOptionPane.showMessageDialog(this,
                        "VIN-номер должен содержать 17 символов (буквы A-Z и цифры, без I, O, Q)",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!InputValidator.isValidLicensePlate(licensePlate)) {
                JOptionPane.showMessageDialog(this,
                        "Неверный формат гос. номера. Пример: А123ВС777",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int year;
            try {
                year = Integer.parseInt(yearStr);
                int currentYear = java.time.Year.now().getValue();
                if (year < 1900 || year > currentYear + 1) {
                    JOptionPane.showMessageDialog(this,
                            "Год должен быть между 1900 и " + (currentYear + 1),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Год должен быть числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            brandService.addBrand(brand, controller.getCurrentUser().getId());
            modelService.addModel(brand, model, controller.getCurrentUser().getId());

            controller.handleAddCar(brand, model, year, vin, licensePlate);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}