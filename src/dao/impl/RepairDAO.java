package dao.impl;

import dao.interfaces.IRepairDAO;
import model.Repair;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RepairDAO implements IRepairDAO {
    private static final Logger logger = Logger.getLogger(RepairDAO.class.getName());

    @Override
    public boolean addRepair(Repair repair) {
        String sql = "INSERT INTO repairs (car_id, description, status, cost, start_date, mechanic_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, repair.getCarId());
            pstmt.setString(2, repair.getDescription());
            pstmt.setString(3, repair.getStatus());
            pstmt.setDouble(4, repair.getCost());
            pstmt.setString(5, repair.getStartDate().toString());
            pstmt.setInt(6, repair.getMechanicId());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                String idSql = "SELECT last_insert_rowid() as id";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(idSql)) {
                    if (rs.next()) {
                        repair.setId(rs.getInt("id"));
                        logger.info("Ремонт добавлен с ID: " + repair.getId());
                    }
                }
            }

            logger.info("Ремонт добавлен для автомобиля ID: " + repair.getCarId());
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка добавления ремонта: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Repair> getRepairsByCar(int carId) {
        List<Repair> repairs = new ArrayList<>();
        String sql = "SELECT * FROM repairs WHERE car_id = ? ORDER BY start_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                repairs.add(extractRepairFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения ремонтов: " + e.getMessage());
        }
        return repairs;
    }

    @Override
    public List<Repair> getAllRepairs() {
        List<Repair> repairs = new ArrayList<>();
        String sql = "SELECT * FROM repairs ORDER BY start_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                repairs.add(extractRepairFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения всех ремонтов: " + e.getMessage());
        }
        return repairs;
    }

    @Override
    public boolean updateRepairStatus(int repairId, String status) {
        String sql = "UPDATE repairs SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, repairId);

            int result = pstmt.executeUpdate();
            logger.info("Статус ремонта обновлен: " + repairId + " -> " + status);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка обновления статуса ремонта: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean completeRepair(int repairId) {
        String sql = "UPDATE repairs SET status = 'COMPLETED', end_date = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, java.time.LocalDate.now().toString());
            pstmt.setInt(2, repairId);

            int result = pstmt.executeUpdate();
            logger.info("Ремонт завершен: " + repairId);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка завершения ремонта: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Repair getRepairById(int repairId) {
        String sql = "SELECT * FROM repairs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, repairId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRepairFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения ремонта: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean deleteRepair(int repairId) {
        String sql = "DELETE FROM repairs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, repairId);
            int result = pstmt.executeUpdate();
            logger.info("Ремонт удален ID: " + repairId + ", результат: " + result);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка удаления ремонта: " + e.getMessage());
            return false;
        }
    }

    private Repair extractRepairFromResultSet(ResultSet rs) throws SQLException {
        Repair repair = new Repair();
        repair.setId(rs.getInt("id"));
        repair.setCarId(rs.getInt("car_id"));
        repair.setDescription(rs.getString("description"));
        repair.setStatus(rs.getString("status"));
        repair.setCost(rs.getDouble("cost"));
        repair.setStartDate(java.time.LocalDate.parse(rs.getString("start_date")));

        String endDate = rs.getString("end_date");
        if (endDate != null && !endDate.isEmpty()) {
            repair.setEndDate(java.time.LocalDate.parse(endDate));
        }

        repair.setMechanicId(rs.getInt("mechanic_id"));
        return repair;
    }
}