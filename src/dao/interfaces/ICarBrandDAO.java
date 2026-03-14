package dao.interfaces;

import model.CarBrand;
import java.util.List;

public interface ICarBrandDAO {
    boolean addBrand(CarBrand brand);
    CarBrand getBrandById(int id);
    CarBrand getBrandByName(String name);
    List<CarBrand> getAllBrands();
    boolean updateBrand(CarBrand brand);
    boolean deleteBrand(int id);
    boolean brandExists(String name);
}