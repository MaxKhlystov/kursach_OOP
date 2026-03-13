package service.impl;

import model.Notification;
import dao.interfaces.INotificationDAO;
import dao.impl.NotificationDAO;
import service.interfaces.INotificationService;

import java.util.List;
import java.util.logging.Logger;

public class NotificationService implements INotificationService {
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());
    private final INotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public NotificationService(INotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public List<Notification> getUserNotifications(int userId) {
        List<Notification> notifications = notificationDAO.getNotificationsByUser(userId);
        logger.info("Получены уведомления пользователя " + userId + ": " + notifications.size() + " шт.");
        return notifications;
    }

    @Override
    public boolean markNotificationAsRead(int notificationId) {
        boolean success = notificationDAO.markAsRead(notificationId);
        if (success) {
            logger.info("Уведомление помечено как прочитанное: " + notificationId);
        }
        return success;
    }

    @Override
    public int getUnreadCount(int userId) {
        int count = notificationDAO.getUnreadCount(userId);
        logger.info("Непрочитанных уведомлений у пользователя " + userId + ": " + count);
        return count;
    }

    @Override
    public void addNotification(int userId, String message) {
        Notification notification = new Notification(userId, message);
        boolean success = notificationDAO.addNotification(notification);
        if (success) {
            logger.info("Уведомление добавлено для пользователя: " + userId);
        }
    }
}