package dao.interfaces;

import model.Notification;
import java.util.List;

public interface INotificationDAO {
    boolean addNotification(Notification notification);
    List<Notification> getNotificationsByUser(int userId);
    boolean markAsRead(int notificationId);
    int getUnreadCount(int userId);
}
