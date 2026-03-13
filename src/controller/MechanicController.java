package controller;

import model.User;
import model.Car;
import model.Repair;
import service.interfaces.ICarService;
import service.impl.CarService;
import service.interfaces.IRepairService;
import service.impl.RepairService;
import service.interfaces.IUserService;
import service.impl.UserService;
import service.interfaces.INotificationService;
import service.impl.NotificationService;
import view.frames.MechanicView;
import java.util.List;

public class MechanicController {
    private final User currentUser;
    private MechanicView view;
    private final ICarService carService;
    private final IRepairService repairService;
    private final IUserService userService;
    private final INotificationService notificationService;
    private final AuthController authController;

    public MechanicController(User user, AuthController authController) {
        this.currentUser = user;
        this.authController = authController;
        this.carService = new CarService();
        this.repairService = new RepairService();
        this.userService = new UserService();
        this.notificationService = new NotificationService();
    }

    // Для тестирования
    public MechanicController(User user, AuthController authController,
                              ICarService carService, IRepairService repairService,
                              IUserService userService, INotificationService notificationService) {
        this.currentUser = user;
        this.authController = authController;
        this.carService = carService;
        this.repairService = repairService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public void setView(MechanicView view) {
        this.view = view;
    }

    public void handleViewCars() {
        List<Car> cars = carService.getAllCars();
        view.displayAllCars(cars, userService);
    }

    public void handleViewRepairs() {
        List<Repair> repairs = repairService.getAllRepairs();
        view.displayAllRepairs(repairs, carService, userService, this);
    }

    public void handleAddRepair(int carId, String description, double cost) {
        Repair repair = new Repair(carId, description, currentUser.getId());
        repair.setCost(cost);
        boolean success = repairService.addRepair(repair);

        if (success) {
            view.showSuccess("Ремонт добавлен успешно!");
            handleViewRepairs();
        } else {
            view.showError("Ошибка при добавлении ремонта");
        }
    }

    public void handleUpdateRepairStatus(int repairId, String currentStatus) {
        String newStatus = getNextStatus(currentStatus);
        if (newStatus == null) {
            view.showInfo("Ремонт уже завершен");
            return;
        }

        boolean success = repairService.updateRepairStatus(repairId, newStatus);
        if (success) {
            view.showSuccess("Статус ремонта успешно изменен!");
            handleViewRepairs();
        } else {
            view.showError("Ошибка при изменении статуса ремонта");
        }
    }

    public void handleUpdateProfile(String email, String phone, String fullName) {
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setFullName(fullName);

        boolean success = userService.updateUser(currentUser);
        if (success) {
            view.showSuccess("Данные успешно обновлены!");
            view.updateProfileInfo(currentUser);
        } else {
            view.showError("Ошибка обновления данных. Возможно, данные уже используются.");
        }
    }

    public void handleShowUserGuide() {
        String guide = """
            РУКОВОДСТВО ПОЛЬЗОВАТЕЛЯ - МЕХАНИК

            1. ЛИЧНЫЙ КАБИНЕТ
               • Просмотр и редактирование личных данных
               • Изменение контактной информации

            2. АВТОМОБИЛИ КЛИЕНТОВ
               • Просмотр всех автомобилей в системе
               • Информация о владельцах автомобилей
               • Контактные данные клиентов

            3. РЕМОНТЫ
               • Добавление новых ремонтов
               • Просмотр всех текущих ремонтов
               • Изменение статусов ремонтов
               • Отслеживание выполнения работ

            4. УПРАВЛЕНИЕ СТАТУСАМИ РЕМОНТА:
               • Диагностика → В ремонте → Ремонт завершен
               • Автоматические уведомления клиентам при смене статуса

            СТАТУСЫ РЕМОНТА:
            • Диагностика - автомобиль находится на диагностике
            • В ремонте - ремонт выполняется
            • Ремонт завершен - работа завершена, автомобиль готов к выдаче
            """;
        view.displayUserGuide(guide);
    }

    public void handleShowAbout() {
        String about = "Автосервис v2.0\n\nПриложение для механиков автосервиса.\nТекущий пользователь: " + currentUser.getFullName() + "\nРоль: Механик";
        view.displayAbout(about);
    }

    public void handleLogout() {
        authController.handleLogout();
    }

    private String getNextStatus(String currentStatus) {
        switch (currentStatus) {
            case "DIAGNOSTICS": return "IN_REPAIR";
            case "IN_REPAIR": return "COMPLETED";
            default: return null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getClients() {
        return userService.getClients();
    }

    public List<Car> getClientCars(int clientId) {
        return carService.getClientCars(clientId);
    }

    public List<Car> getAllCars() {
        return carService.getAllCars();
    }
}