package dao.impl;

import dao.interfaces.ICarModelDAO;
import model.CarModel;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CarModelDAO implements ICarModelDAO {
    private static final Logger logger = Logger.getLogger(CarModelDAO.class.getName());

    @Override
    public boolean addModel(CarModel model) {
        String sql = "INSERT INTO car_models (brand_id, name, created_by) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, model.getBrandId());
            pstmt.setString(2, model.getName());
            pstmt.setInt(3, model.getCreatedBy());

            int result = pstmt.executeUpdate();
            logger.info("Модель добавлена: " + model.getName());
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка добавления модели: " + e.getMessage());
            return false;
        }
    }

    @Override
    public CarModel getModelById(int id) {
        String sql = "SELECT m.*, b.name as brand_name FROM car_models m " +
                "JOIN car_brands b ON m.brand_id = b.id WHERE m.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractModelFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения модели: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<CarModel> getModelsByBrand(int brandId) {
        List<CarModel> models = new ArrayList<>();
        String sql = "SELECT m.*, b.name as brand_name FROM car_models m " +
                "JOIN car_brands b ON m.brand_id = b.id WHERE m.brand_id = ? ORDER BY m.name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, brandId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                models.add(extractModelFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения моделей: " + e.getMessage());
        }
        return models;
    }

    @Override
    public List<CarModel> getModelsByBrandName(String brandName) {
        List<CarModel> models = new ArrayList<>();
        String sql = "SELECT m.*, b.name as brand_name FROM car_models m " +
                "JOIN car_brands b ON m.brand_id = b.id WHERE b.name = ? ORDER BY m.name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, brandName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                models.add(extractModelFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения моделей: " + e.getMessage());
        }
        return models;
    }

    @Override
    public List<CarModel> getAllModels() {
        List<CarModel> models = new ArrayList<>();
        String sql = "SELECT m.*, b.name as brand_name FROM car_models m " +
                "JOIN car_brands b ON m.brand_id = b.id ORDER BY b.name, m.name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                models.add(extractModelFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения моделей: " + e.getMessage());
        }
        return models;
    }

    @Override
    public boolean updateModel(CarModel model) {
        String sql = "UPDATE car_models SET brand_id = ?, name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, model.getBrandId());
            pstmt.setString(2, model.getName());
            pstmt.setInt(3, model.getId());

            int result = pstmt.executeUpdate();
            logger.info("Модель обновлена: " + model.getName());
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка обновления модели: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteModel(int id) {
        String sql = "DELETE FROM car_models WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            logger.info("Модель удалена ID: " + id);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка удаления модели: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean modelExists(String brandName, String modelName) {
        String sql = "SELECT m.id FROM car_models m " +
                "JOIN car_brands b ON m.brand_id = b.id " +
                "WHERE b.name = ? AND m.name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, brandName);
            pstmt.setString(2, modelName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.severe("Ошибка проверки модели: " + e.getMessage());
            return false;
        }
    }

    private CarModel extractModelFromResultSet(ResultSet rs) throws SQLException {
        CarModel model = new CarModel();
        model.setId(rs.getInt("id"));
        model.setBrandId(rs.getInt("brand_id"));
        model.setBrandName(rs.getString("brand_name"));
        model.setName(rs.getString("name"));
        model.setCreatedBy(rs.getInt("created_by"));
        model.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return model;
    }
}