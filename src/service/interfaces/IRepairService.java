package service.interfaces;

import model.Repair;
import java.util.List;

public interface IRepairService {
    boolean addRepair(Repair repair);
    List<Repair> getCarRepairs(int carId);
    List<Repair> getAllRepairs();
    boolean updateRepairStatus(int repairId, String status);
    boolean moveToNextStatus(int repairId);
    boolean completeRepair(int repairId);
    Repair getRepairById(int repairId);
}