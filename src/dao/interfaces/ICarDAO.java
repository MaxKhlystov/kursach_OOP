package dao.interfaces;

import model.Car;
import java.util.List;

public interface ICarDAO {
    boolean addCar(Car car);
    boolean deleteCar(int carId);
    List<Car> getCarsByOwner(int ownerId);
    List<Car> getAllCars();
    Car getCarById(int carId);
}