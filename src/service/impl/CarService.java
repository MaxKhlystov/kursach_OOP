package service.impl;

import model.Car;
import dao.interfaces.ICarDAO;
import dao.impl.CarDAO;
import service.interfaces.ICarService;

import java.util.List;
import java.util.logging.Logger;

public class CarService implements ICarService {
    private static final Logger logger = Logger.getLogger(CarService.class.getName());
    private final ICarDAO carDAO;

    public CarService() {
        this.carDAO = new CarDAO();
    }

    public CarService(ICarDAO carDAO) {
        this.carDAO = carDAO;
    }

    @Override
    public boolean addCar(Car car) {
        if (car == null || car.getBrand() == null || car.getModel() == null) {
            logger.warning("Попытка добавить невалидный автомобиль");
            return false;
        }

        boolean success = carDAO.addCar(car);
        if (success) {
            logger.info("Автомобиль добавлен: " + car.getBrand() + " " + car.getModel());
        }
        return success;
    }

    @Override
    public boolean deleteCar(int carId) {
        boolean success = carDAO.deleteCar(carId);
        if (success) {
            logger.info("Автомобиль удален ID: " + carId);
        }
        return success;
    }

    @Override
    public List<Car> getClientCars(int clientId) {
        List<Car> cars = carDAO.getCarsByOwner(clientId);
        logger.info("Получены автомобили клиента " + clientId + ": " + cars.size() + " шт.");
        return cars;
    }

    @Override
    public List<Car> getAllCars() {
        List<Car> cars = carDAO.getAllCars();
        logger.info("Получены все автомобили: " + cars.size() + " шт.");
        return cars;
    }

    @Override
    public Car getCarById(int carId) {
        Car car = carDAO.getCarById(carId);
        if (car != null) {
            logger.info("Получен автомобиль ID: " + carId);
        }
        return car;
    }
}