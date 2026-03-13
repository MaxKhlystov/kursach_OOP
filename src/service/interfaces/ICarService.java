package service.interfaces;

import model.Car;
import java.util.List;

public interface ICarService {
    boolean addCar(Car car);
    boolean deleteCar(int carId);
    List<Car> getClientCars(int clientId);
    List<Car> getAllCars();
    Car getCarById(int carId);
}