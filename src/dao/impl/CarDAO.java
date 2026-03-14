package dao.impl;

import dao.interfaces.ICarDAO;
import model.Car;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CarDAO implements ICarDAO {
    private static final Logger logger = Logger.getLogger(CarDAO.class.getName());

    @Override
    public boolean addCar(Car car) {
        String sql = "INSERT INTO cars (brand, model, year, vin, license_plate, owner_id, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        System.out.println("=== ОТЛАДКА: CarDAO.addCar ===");
        System.out.println("SQL: " + sql);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, car.getBrand());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setString(4, car.getVin());
            pstmt.setString(5, car.getLicensePlate());
            pstmt.setInt(6, car.getOwnerId());
            pstmt.setString(7, car.getRegistrationDate().toString());

            System.out.println("Parameters: " + car.getBrand() + ", " + car.getModel() + ", " +
                    car.getYear() + ", " + car.getVin() + ", " + car.getLicensePlate() +
                    ", " + car.getOwnerId() + ", " + car.getRegistrationDate());

            int result = pstmt.executeUpdate();
            System.out.println("executeUpdate result: " + result);

            if (result > 0) {
                // Получаем ID последней вставленной записи
                String idSql = "SELECT last_insert_rowid() as id";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(idSql)) {
                    if (rs.next()) {
                        car.setId(rs.getInt("id"));
                        System.out.println("Автомобиль добавлен с ID: " + car.getId());
                    }
                }
            }

            logger.info("Автомобиль добавлен: " + car.getBrand() + " " + car.getModel());
            return result > 0;

        } catch (SQLException e) {
            System.out.println("SQL EXCEPTION: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            logger.severe("Ошибка добавления автомобиля: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCar(int carId) {
        String sql = "DELETE FROM cars WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carId);
            int result = pstmt.executeUpdate();
            logger.info("Автомобиль удален ID: " + carId);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка удаления автомобиля: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Car> getCarsByOwner(int ownerId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE owner_id = ? ORDER BY brand, model";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                cars.add(extractCarFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения автомобилей: " + e.getMessage());
        }
        return cars;
    }

    @Override
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY brand, model";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cars.add(extractCarFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения всех автомобилей: " + e.getMessage());
        }
        return cars;
    }

    @Override
    public Car getCarById(int carId) {
        String sql = "SELECT * FROM cars WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCarFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения автомобиля: " + e.getMessage());
        }
        return null;
    }

    private Car extractCarFromResultSet(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getInt("id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setYear(rs.getInt("year"));
        car.setVin(rs.getString("vin"));
        car.setLicensePlate(rs.getString("license_plate"));
        car.setOwnerId(rs.getInt("owner_id"));
        car.setRegistrationDate(java.time.LocalDate.parse(rs.getString("registration_date")));
        return car;
    }
}