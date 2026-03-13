package dao.impl;

import dao.interfaces.INotificationDAO;
import model.Notification;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NotificationDAO implements INotificationDAO {
    private static final Logger logger = Logger.getLogger(NotificationDAO.class.getName());

    @Override
    public boolean addNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getMessage());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        notification.setId(rs.getInt(1));
                    }
                }
            }

            logger.info("Уведомление добавлено для пользователя ID: " + notification.getUserId());
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка добавления уведомления: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Notification> getNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения уведомлений: " + e.getMessage());
        }
        return notifications;
    }

    @Override
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            int result = pstmt.executeUpdate();
            logger.info("Уведомление помечено как прочитанное: " + notificationId);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка обновления уведомления: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения количества непрочитанных уведомлений: " + e.getMessage());
        }
        return 0;
    }

    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return notification;
    }
}