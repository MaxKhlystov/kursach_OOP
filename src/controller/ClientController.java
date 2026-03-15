package controller;

import model.User;
import model.Car;
import service.interfaces.ICarService;
import service.impl.CarService;
import service.interfaces.IRepairService;
import service.impl.RepairService;
import service.interfaces.IUserService;
import service.impl.UserService;
import view.frames.ClientView;
import java.util.List;

public class ClientController {
    private final User currentUser;
    private ClientView view;
    private final ICarService carService;
    private final IRepairService repairService;
    private final IUserService userService;
    private final AuthController authController;

    public ClientController(User user, AuthController authController) {
        this.currentUser = user;
        this.authController = authController;
        this.carService = new CarService();
        this.repairService = new RepairService();
        this.userService = new UserService();
    }

    public void setView(ClientView view) {
        this.view = view;
        view.displayWelcome();
    }

    public void handleViewCars() {
        List<Car> cars = carService.getClientCars(currentUser.getId());
        view.displayCars(cars, this);
    }

    public void handleViewRepairs() {
        List<Car> cars = carService.getClientCars(currentUser.getId());
        view.displayRepairs(cars, repairService);
    }

    public void handleAddCar(String brand, String model, int year, String vin, String licensePlate) {
        Car car = new Car(brand, model, year, vin, licensePlate, currentUser.getId());
        boolean success = carService.addCar(car);

        if (success) {
            view.showSuccess("Автомобиль добавлен успешно!");
            handleViewCars();
        } else {
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

    public boolean handleUpdateProfile(String email, String phone, String fullName) {
        String oldEmail = currentUser.getEmail();
        String oldPhone = currentUser.getPhone();
        String oldFullName = currentUser.getFullName();

        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setFullName(fullName);

        boolean success = userService.updateUser(currentUser);

        if (success) {
            view.showSuccess("Данные успешно обновлены!");
            view.updateProfileInfo(currentUser);
        } else {
            currentUser.setEmail(oldEmail);
            currentUser.setPhone(oldPhone);
            currentUser.setFullName(oldFullName);
            view.showError("Ошибка обновления данных. Возможно, данные уже используются.");
        }
        return success;
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

    public void handleLogout() {
        authController.handleLogout();
    }

    public User getCurrentUser() {
        return currentUser;
    }
}