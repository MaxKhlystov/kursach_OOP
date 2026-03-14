package dao.interfaces;

import model.CarModel;
import java.util.List;

public interface ICarModelDAO {
    boolean addModel(CarModel model);
    CarModel getModelById(int id);
    List<CarModel> getModelsByBrand(int brandId);
    List<CarModel> getModelsByBrandName(String brandName);
    List<CarModel> getAllModels();
    boolean updateModel(CarModel model);
    boolean deleteModel(int id);
    boolean modelExists(String brandName, String modelName);
}