package service.impl;

import model.Repair;
import dao.interfaces.IRepairDAO;
import dao.impl.RepairDAO;
import service.interfaces.IRepairService;

import java.util.List;
import java.util.logging.Logger;

public class RepairService implements IRepairService {
    private static final Logger logger = Logger.getLogger(RepairService.class.getName());
    private final IRepairDAO repairDAO;

    public RepairService() {
        this.repairDAO = new RepairDAO();
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

    @Override
    public boolean deleteRepair(int repairId) {
        boolean success = repairDAO.deleteRepair(repairId);
        if (success) {
            logger.info("Ремонт удален ID: " + repairId);
        }
        return success;
    }

    private String getNextStatus(String currentStatus) {
        switch (currentStatus) {
            case "DIAGNOSTICS": return "IN_REPAIR";
            case "IN_REPAIR": return "COMPLETED";
            default: return null;
        }
    }
}