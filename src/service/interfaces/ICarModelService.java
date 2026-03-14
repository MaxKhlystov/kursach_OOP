package service.interfaces;

import model.CarModel;
import java.util.List;

public interface ICarModelService {
    boolean addModel(String brandName, String modelName, int createdBy);
    boolean addModel(int brandId, String modelName, int createdBy);
    CarModel getModelById(int id);  // ← Добавляем
    List<CarModel> getModelsByBrand(int brandId);
    List<CarModel> getModelsByBrandName(String brandName);
    List<CarModel> getAllModels();
    List<String> getModelNamesByBrand(String brandName);
    boolean updateModel(int id, String newName);
    boolean deleteModel(int id);
    boolean modelExists(String brandName, String modelName);
}