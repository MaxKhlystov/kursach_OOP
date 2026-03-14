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
        logger.info("Запрос уведомлений для пользователя ID: " + userId);
        List<Notification> notifications = notificationDAO.getNotificationsByUser(userId);
        logger.info("Получено уведомлений: " + notifications.size());
        return notifications;
    }

    @Override
    public List<Notification> getAllNotifications() {
        logger.info("Запрос всех уведомлений");
        return notificationDAO.getAllNotifications();
    }

    @Override
    public boolean markNotificationAsRead(int notificationId) {
        logger.info("Пометка уведомления как прочитанного: " + notificationId);
        boolean success = notificationDAO.markAsRead(notificationId);
        if (success) {
            logger.info("Уведомление помечено как прочитанное: " + notificationId);
        } else {
            logger.warning("Не удалось пометить уведомление: " + notificationId);
        }
        return success;
    }

    @Override
    public int getUnreadCount(int userId) {
        int count = notificationDAO.getUnreadCount(userId);
        logger.info("Непрочитанных уведомлений для пользователя " + userId + ": " + count);
        return count;
    }

    @Override
    public void addNotification(int userId, String message) {
        logger.info("Добавление уведомления для пользователя: " + userId);
        Notification notification = new Notification(userId, message);
        boolean success = notificationDAO.addNotification(notification);
        if (success) {
            logger.info("Уведомление добавлено для пользователя: " + userId);
        } else {
            logger.warning("Ошибка добавления уведомления для пользователя: " + userId);
        }
    }

    @Override
    public boolean deleteNotification(int notificationId) {
        logger.info("Удаление уведомления ID: " + notificationId);
        boolean success = notificationDAO.deleteNotification(notificationId);
        if (success) {
            logger.info("Уведомление удалено ID: " + notificationId);
        } else {
            logger.warning("Не удалось удалить уведомление ID: " + notificationId);
        }
        return success;
    }
}