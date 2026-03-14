package controller;

import model.User;
import model.Car;
import model.Notification;
import service.interfaces.ICarService;
import service.impl.CarService;
import service.interfaces.IRepairService;
import service.impl.RepairService;
import service.interfaces.INotificationService;
import service.impl.NotificationService;
import service.interfaces.IUserService;
import service.impl.UserService;
import view.frames.ClientView;
import java.util.List;

public class ClientController {
    private final User currentUser;
    private ClientView view;
    private final ICarService carService;
    private final IRepairService repairService;
    private final INotificationService notificationService;
    private final IUserService userService;
    private final AuthController authController;

    public ClientController(User user, AuthController authController) {
        this.currentUser = user;
        this.authController = authController;
        this.carService = new CarService();
        this.repairService = new RepairService();
        this.notificationService = new NotificationService();
        this.userService = new UserService();
    }

    // Для тестирования
    public ClientController(User user, AuthController authController,
                            ICarService carService, IRepairService repairService,
                            INotificationService notificationService, IUserService userService) {
        this.currentUser = user;
        this.authController = authController;
        this.carService = carService;
        this.repairService = repairService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    public void setView(ClientView view) {
        this.view = view;
        view.displayWelcome(getUnreadNotificationsCount());
    }

    public void handleViewCars() {
        List<Car> cars = carService.getClientCars(currentUser.getId());
        view.displayCars(cars, this);
    }

    public void handleViewRepairs() {
        List<Car> cars = carService.getClientCars(currentUser.getId());
        view.displayRepairs(cars, repairService);
    }

    public void handleViewNotifications() {
        List<Notification> notifications = notificationService.getUserNotifications(currentUser.getId());
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notificationService.markNotificationAsRead(notification.getId());
            }
        }
        view.displayNotifications(notifications);
        view.displayWelcome(getUnreadNotificationsCount());
    }

    public void handleAddCar(String brand, String model, int year, String vin, String licensePlate) {
        System.out.println("=== ОТЛАДКА: handleAddCar ===");
        System.out.println("brand: " + brand);
        System.out.println("model: " + model);
        System.out.println("year: " + year);
        System.out.println("vin: " + vin);
        System.out.println("licensePlate: " + licensePlate);
        System.out.println("ownerId: " + currentUser.getId());

        Car car = new Car(brand, model, year, vin, licensePlate, currentUser.getId());
        boolean success = carService.addCar(car);

        if (success) {
            System.out.println("Успех! Автомобиль добавлен с ID: " + car.getId());
            view.showSuccess("Автомобиль добавлен успешно!");
            handleViewCars();
        } else {
            System.out.println("ОШИБКА! Автомобиль не добавлен");
            view.showError("Ошибка при добавлении автомобиля");
        }
    }

    public void handleDeleteCar(int carId) {
        boolean success = carService.deleteCar(carId);
        if (success) {
            view.showSuccess("Автомобиль удален успешно!");
            handleViewCars();
        } else {
            view.showError("Ошибка при удалении автомобиля");
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
            РУКОВОДСТВО ПОЛЬЗОВАТЕЛЯ - КЛИЕНТ

            1. ЛИЧНЫЙ КАБИНЕТ
               • Просмотр и редактирование личных данных
               • Изменение контактной информации

            2. АВТОМОБИЛИ
               • Добавление новых автомобилей
               • Просмотр списка своих автомобилей
               • Удаление автомобилей

            3. РЕМОНТЫ
               • Просмотр истории ремонтов по всем автомобилям
               • Отслеживание текущего статуса ремонта
               • Просмотр стоимости ремонтов

            4. УВЕДОМЛЕНИЯ
               • Получение уведомлений о смене статуса ремонта
               • Просмотр истории уведомлений

            СТАТУСЫ РЕМОНТА:
            • Диагностика - автомобиль находится на диагностике
            • В ремонте - ремонт выполняется
            • Ремонт завершен - работа завершена, автомобиль готов к выдаче
            """;
        view.displayUserGuide(guide);
    }

    public void handleShowAbout() {
        String about = "Автосервис v2.0\n\nКлиентское приложение для управления автомобилями и ремонтами.\nТекущий пользователь: " + currentUser.getFullName() + "\nРоль: Клиент";
        view.displayAbout(about);
    }

    public boolean handleChangePassword(String oldPassword, String newPassword) {
        if (!currentUser.getPassword().equals(oldPassword)) {
            view.showError("Неверный текущий пароль");
            return false;
        }

        currentUser.setPassword(newPassword);
        boolean success = userService.updateUser(currentUser);

        if (success) {
            view.showSuccess("Пароль успешно изменен");
        } else {
            view.showError("Ошибка при смене пароля");
        }
        return success;
    }

    public void handleLogout() {
        authController.handleLogout();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public int getUnreadNotificationsCount() {
        return notificationService.getUnreadCount(currentUser.getId());
    }
}