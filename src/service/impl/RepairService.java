package service.impl;

import model.Repair;
import model.Car;
import dao.interfaces.IRepairDAO;
import dao.impl.RepairDAO;
import dao.interfaces.ICarDAO;
import dao.impl.CarDAO;
import service.interfaces.INotificationService;
import service.interfaces.IRepairService;

import java.util.List;
import java.util.logging.Logger;

public class RepairService implements IRepairService {
    private static final Logger logger = Logger.getLogger(RepairService.class.getName());
    private final IRepairDAO repairDAO;
    private final ICarDAO carDAO;
    private final INotificationService notificationService;

    public RepairService() {
        this.repairDAO = new RepairDAO();
        this.carDAO = new CarDAO();
        this.notificationService = new NotificationService();
    }

    public RepairService(IRepairDAO repairDAO, ICarDAO carDAO, INotificationService notificationService) {
        this.repairDAO = repairDAO;
        this.carDAO = carDAO;
        this.notificationService = notificationService;
    }

    @Override
    public boolean addRepair(Repair repair) {
        if (repair == null || repair.getDescription() == null) {
            logger.warning("Попытка добавить невалидный ремонт");
            return false;
        }

        boolean success = repairDAO.addRepair(repair);
        if (success) {
            logger.info("Ремонт добавлен для автомобиля ID: " + repair.getCarId());
            sendStatusChangeNotification(repair, "DIAGNOSTICS");
        }
        return success;
    }

    @Override
    public List<Repair> getCarRepairs(int carId) {
        List<Repair> repairs = repairDAO.getRepairsByCar(carId);
        logger.info("Получены ремонты автомобиля " + carId + ": " + repairs.size() + " шт.");
        return repairs;
    }

    @Override
    public List<Repair> getAllRepairs() {
        List<Repair> repairs = repairDAO.getAllRepairs();
        logger.info("Получены все ремонты: " + repairs.size() + " шт.");
        return repairs;
    }

    @Override
    public boolean updateRepairStatus(int repairId, String status) {
        boolean success = repairDAO.updateRepairStatus(repairId, status);
        if (success) {
            logger.info("Статус ремонта обновлен: " + repairId + " -> " + status);

            Repair repair = repairDAO.getRepairById(repairId);
            if (repair != null) {
                sendStatusChangeNotification(repair, status);
            }
        }
        return success;
    }

    @Override
    public boolean moveToNextStatus(int repairId) {
        Repair repair = repairDAO.getRepairById(repairId);
        if (repair == null) return false;

        String nextStatus = getNextStatus(repair.getStatus());
        if (nextStatus != null) {
            return updateRepairStatus(repairId, nextStatus);
        }
        return false;
    }

    @Override
    public boolean completeRepair(int repairId) {
        boolean success = repairDAO.completeRepair(repairId);
        if (success) {
            logger.info("Ремонт завершен: " + repairId);

            Repair repair = repairDAO.getRepairById(repairId);
            if (repair != null) {
                sendStatusChangeNotification(repair, "COMPLETED");
            }
        }
        return success;
    }

    @Override
    public Repair getRepairById(int repairId) {
        Repair repair = repairDAO.getRepairById(repairId);
        if (repair != null) {
            logger.info("Получен ремонт ID: " + repairId);
        }
        return repair;
    }

    private String getNextStatus(String currentStatus) {
        switch (currentStatus) {
            case "DIAGNOSTICS": return "IN_REPAIR";
            case "IN_REPAIR": return "COMPLETED";
            default: return null;
        }
    }

    private void sendStatusChangeNotification(Repair repair, String status) {
        try {
            Car car = carDAO.getCarById(repair.getCarId());
            if (car != null) {
                String carInfo = car.getBrand() + " " + car.getModel() + " (" + car.getLicensePlate() + ")";
                String message = formatStatusMessage(carInfo, status);
                notificationService.addNotification(car.getOwnerId(), message);
                logger.info("Уведомление отправлено клиенту " + car.getOwnerId() + " о смене статуса: " + status);
            }
        } catch (Exception e) {
            logger.severe("Ошибка отправки уведомления: " + e.getMessage());
        }
    }

    private String formatStatusMessage(String carInfo, String status) {
        String baseMessage = "Статус ремонта вашего автомобиля " + carInfo + " изменен: ";
        switch (status) {
            case "DIAGNOSTICS":
                return baseMessage + "Диагностика\nАвтомобиль проходит полную диагностику";
            case "IN_REPAIR":
                return baseMessage + "В ремонте\nНачались ремонтные работы";
            case "COMPLETED":
                return baseMessage + "Ремонт завершен\nАвтомобиль готов к выдаче!";
            case "CANCELLED":
                return baseMessage + "Отменен\nРемонт отменен";
            default:
                return baseMessage + getStatusText(status);
        }
    }

    private String getStatusText(String status) {
        switch (status) {
            case "DIAGNOSTICS": return "Диагностика";
            case "IN_REPAIR": return "В ремонте";
            case "COMPLETED": return "Ремонт завершен";
            case "CANCELLED": return "Отменен";
            default: return status;
        }
    }
}