package service.impl;

import dao.interfaces.ICarBrandDAO;
import dao.impl.CarBrandDAO;
import model.CarBrand;
import service.interfaces.ICarBrandService;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CarBrandService implements ICarBrandService {
    private static final Logger logger = Logger.getLogger(CarBrandService.class.getName());
    private final ICarBrandDAO brandDAO;

    public CarBrandService() {
        this.brandDAO = new CarBrandDAO();
    }

    @Override
    public boolean addBrand(String name, int createdBy) {
        if (name == null || name.trim().isEmpty()) {
            logger.warning("Попытка добавить марку с пустым именем");
            return false;
        }

        if (brandDAO.brandExists(name)) {
            logger.info("Марка уже существует: " + name);
            return false;
        }

        CarBrand brand = new CarBrand(name.trim(), createdBy);
        boolean success = brandDAO.addBrand(brand);
        if (success) {
            logger.info("Марка добавлена: " + name);
        }
        return success;
    }

    @Override
    public CarBrand getBrandById(int id) {
        return brandDAO.getBrandById(id);
    }

    @Override
    public CarBrand getBrandByName(String name) {
        return brandDAO.getBrandByName(name);
    }

    @Override
    public List<CarBrand> getAllBrands() {
        return brandDAO.getAllBrands();
    }

    @Override
    public List<String> getAllBrandNames() {
        return brandDAO.getAllBrands().stream()
                .map(CarBrand::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateBrand(int id, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }
        CarBrand brand = brandDAO.getBrandById(id);
        if (brand == null) {
            return false;
        }
        brand.setName(newName.trim());
        return brandDAO.updateBrand(brand);
    }

    @Override
    public boolean deleteBrand(int id) {
        return brandDAO.deleteBrand(id);
    }

    @Override
    public boolean brandExists(String name) {
        return brandDAO.brandExists(name);
    }
}