package controller;

import model.User;
import model.Car;
import model.Repair;
import model.CarBrand;
import model.CarModel;
import service.interfaces.*;
import service.impl.*;
import view.frames.AdminView;

import javax.swing.*;
import java.util.List;
import java.util.logging.Logger;

public class AdminController {
    private final User currentUser;
    private AdminView view;
    private final IUserService userService;
    private final ICarService carService;
    private final IRepairService repairService;
    private final ICarBrandService brandService;
    private final ICarModelService modelService;
    private final AuthController authController;

    private static final Logger logger = Logger.getLogger(AdminController.class.getName());

    public AdminController(User user, AuthController authController) {
        this.currentUser = user;
        this.authController = authController;
        this.userService = new UserService();
        this.carService = new CarService();
        this.repairService = new RepairService();
        this.brandService = new CarBrandService();
        this.modelService = new CarModelService();
    }

    public void setView(AdminView view) {
        this.view = view;
    }

    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        logger.info("Админ " + currentUser.getFullName() + " запросил список пользователей. Найдено: " + users.size());
        return users;
    }

    public boolean deleteUser(int userId) {
        if (userId == currentUser.getId()) {
            view.showError("Нельзя удалить собственную учетную запись");
            return false;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Вы уверены, что хотите удалить пользователя ID " + userId + "?\n" +
                        "Это также удалит все связанные автомобили и ремонты!",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userService.deleteUser(userId);
            if (success) {
                view.showSuccess("Пользователь успешно удален");
            } else {
                view.showError("Ошибка при удалении пользователя");
            }
            return success;
        }
        return false;
    }

    public List<Car> getAllCars() {
        List<Car> cars = carService.getAllCars();
        logger.info("Админ " + currentUser.getFullName() + " запросил список автомобилей. Найдено: " + cars.size());
        return cars;
    }

    public boolean deleteCar(int carId) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Вы уверены, что хотите удалить автомобиль ID " + carId + "?\n" +
                        "Это также удалит все связанные ремонты!",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = carService.deleteCar(carId);
            if (success) {
                view.showSuccess("Автомобиль успешно удален");
            } else {
                view.showError("Ошибка при удалении автомобиля");
            }
            return success;
        }
        return false;
    }

    public List<Repair> getAllRepairs() {
        List<Repair> repairs = repairService.getAllRepairs();
        logger.info("Админ " + currentUser.getFullName() + " запросил список ремонтов. Найдено: " + repairs.size());
        return repairs;
    }

    public boolean deleteRepair(int repairId) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Вы уверены, что хотите удалить ремонт ID " + repairId + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = repairService.deleteRepair(repairId);
            if (success) {
                view.showSuccess("Ремонт успешно удален");
            } else {
                view.showError("Ошибка при удалении ремонта");
            }
            return success;
        }
        return false;
    }

    public List<CarBrand> getAllBrands() {
        List<CarBrand> brands = brandService.getAllBrands();
        logger.info("Админ " + currentUser.getFullName() + " запросил список марок. Найдено: " + brands.size());
        return brands;
    }

    public boolean deleteBrand(int id) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Вы уверены, что хотите удалить марку ID " + id + "?\n" +
                        "Это также удалит все связанные модели!",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = brandService.deleteBrand(id);
            if (success) {
                view.showSuccess("Марка успешно удалена");
            } else {
                view.showError("Ошибка при удалении марки");
            }
            return success;
        }
        return false;
    }

    public List<CarModel> getAllModels() {
        List<CarModel> models = modelService.getAllModels();
        logger.info("Админ " + currentUser.getFullName() + " запросил список моделей. Найдено: " + models.size());
        return models;
    }

    public boolean deleteModel(int id) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Вы уверены, что хотите удалить модель ID " + id + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = modelService.deleteModel(id);
            if (success) {
                view.showSuccess("Модель успешно удалена");
            } else {
                view.showError("Ошибка при удалении модели");
            }
            return success;
        }
        return false;
    }
    public int getClientCarsCount(int userId) {
        return carService.getClientCars(userId).size();
    }

    public int getMechanicRepairsCount(int userId) {
        int count = 0;
        List<Repair> allRepairs = repairService.getAllRepairs();
        for (Repair r : allRepairs) {
            if (r.getMechanicId() == userId) {
                count++;
            }
        }
        return count;
    }

    public User getUserById(int userId) {
        return userService.getUserById(userId);
    }

    public Car getCarById(int carId) {
        return carService.getCarById(carId);
    }

    public int getModelsCountByBrand(int brandId) {
        return modelService.getModelsByBrand(brandId).size();
    }

    public List<User> getClients() {
        return userService.getUsersByRole("CLIENT");
    }

    public List<User> getMechanics() {
        return userService.getUsersByRole("MECHANIC");
    }

    public List<User> getAdmins() {
        return userService.getUsersByRole("ADMIN");
    }

    public void handleLogout() {
        authController.handleLogout();
    }

    public User getCurrentUser() {
        return currentUser;
    }
}