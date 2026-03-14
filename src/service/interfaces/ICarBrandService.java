package service.interfaces;

import model.CarBrand;
import java.util.List;

public interface ICarBrandService {
    boolean addBrand(String name, int createdBy);
    CarBrand getBrandById(int id);
    CarBrand getBrandByName(String name);
    List<CarBrand> getAllBrands();
    List<String> getAllBrandNames();
    boolean updateBrand(int id, String newName);
    boolean deleteBrand(int id);
    boolean brandExists(String name);
}