package service.interfaces;

import model.Notification;
import java.util.List;

public interface INotificationService {
    List<Notification> getUserNotifications(int userId);
    boolean markNotificationAsRead(int notificationId);
    int getUnreadCount(int userId);
    void addNotification(int userId, String message);
}