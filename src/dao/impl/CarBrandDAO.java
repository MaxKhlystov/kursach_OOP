package dao.impl;

import dao.interfaces.ICarBrandDAO;
import model.CarBrand;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CarBrandDAO implements ICarBrandDAO {
    private static final Logger logger = Logger.getLogger(CarBrandDAO.class.getName());

    @Override
    public boolean addBrand(CarBrand brand) {
        String sql = "INSERT INTO car_brands (name, created_by) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, brand.getName());
            pstmt.setInt(2, brand.getCreatedBy());

            int result = pstmt.executeUpdate();
            logger.info("Марка добавлена: " + brand.getName());
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка добавления марки: " + e.getMessage());
            return false;
        }
    }

    @Override
    public CarBrand getBrandById(int id) {
        String sql = "SELECT * FROM car_brands WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBrandFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения марки: " + e.getMessage());
        }
        return null;
    }

    @Override
    public CarBrand getBrandByName(String name) {
        String sql = "SELECT * FROM car_brands WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBrandFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения марки: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<CarBrand> getAllBrands() {
        List<CarBrand> brands = new ArrayList<>();
        String sql = "SELECT * FROM car_brands ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                brands.add(extractBrandFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения марок: " + e.getMessage());
        }
        return brands;
    }

    @Override
    public boolean updateBrand(CarBrand brand) {
        String sql = "UPDATE car_brands SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, brand.getName());
            pstmt.setInt(2, brand.getId());

            int result = pstmt.executeUpdate();
            logger.info("Марка обновлена: " + brand.getName());
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка обновления марки: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteBrand(int id) {
        String sql = "DELETE FROM car_brands WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            logger.info("Марка удалена ID: " + id);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка удаления марки: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean brandExists(String name) {
        String sql = "SELECT id FROM car_brands WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.severe("Ошибка проверки марки: " + e.getMessage());
            return false;
        }
    }

    private CarBrand extractBrandFromResultSet(ResultSet rs) throws SQLException {
        CarBrand brand = new CarBrand();
        brand.setId(rs.getInt("id"));
        brand.setName(rs.getString("name"));
        brand.setCreatedBy(rs.getInt("created_by"));
        brand.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return brand;
    }
}