package dao.interfaces;

import model.Repair;
import java.util.List;

public interface IRepairDAO {
    boolean addRepair(Repair repair);
    List<Repair> getRepairsByCar(int carId);
    List<Repair> getAllRepairs();
    boolean updateRepairStatus(int repairId, String status);
    boolean completeRepair(int repairId);
    Repair getRepairById(int repairId);
}
