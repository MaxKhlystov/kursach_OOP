package service.impl;

import dao.interfaces.ICarModelDAO;
import dao.interfaces.ICarBrandDAO;
import dao.impl.CarModelDAO;
import dao.impl.CarBrandDAO;
import model.CarModel;
import model.CarBrand;
import service.interfaces.ICarModelService;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CarModelService implements ICarModelService {
    private static final Logger logger = Logger.getLogger(CarModelService.class.getName());
    private final ICarModelDAO modelDAO;
    private final ICarBrandDAO brandDAO;

    public CarModelService() {
        this.modelDAO = new CarModelDAO();
        this.brandDAO = new CarBrandDAO();
    }

    @Override
    public boolean addModel(String brandName, String modelName, int createdBy) {
        if (brandName == null || brandName.trim().isEmpty() ||
                modelName == null || modelName.trim().isEmpty()) {
            logger.warning("Попытка добавить модель с пустыми данными");
            return false;
        }

        CarBrand brand = brandDAO.getBrandByName(brandName);
        if (brand == null) {
            brand = new CarBrand(brandName.trim(), createdBy);
            boolean brandCreated = brandDAO.addBrand(brand);
            if (!brandCreated) {
                logger.severe("Не удалось создать марку: " + brandName);
                return false;
            }
            brand = brandDAO.getBrandByName(brandName);
            if (brand == null) {
                return false;
            }
        }

        return addModel(brand.getId(), modelName.trim(), createdBy);
    }

    @Override
    public boolean addModel(int brandId, String modelName, int createdBy) {
        if (modelName == null || modelName.trim().isEmpty()) {
            logger.warning("Попытка добавить модель с пустым именем");
            return false;
        }

        CarBrand brand = brandDAO.getBrandById(brandId);
        if (brand == null) {
            logger.warning("Марка не найдена: " + brandId);
            return false;
        }

        if (modelExists(brand.getName(), modelName)) {
            logger.info("Модель уже существует: " + brand.getName() + " " + modelName);
            return false;
        }

        CarModel model = new CarModel(brandId, modelName.trim(), createdBy);
        boolean success = modelDAO.addModel(model);
        if (success) {
            logger.info("Модель добавлена: " + brand.getName() + " " + modelName);
        }
        return success;
    }

    @Override
    public List<CarModel> getModelsByBrand(int brandId) {
        return modelDAO.getModelsByBrand(brandId);
    }

    @Override
    public CarModel getModelById(int id) {
        return modelDAO.getModelById(id);
    }

    @Override
    public List<CarModel> getModelsByBrandName(String brandName) {
        return modelDAO.getModelsByBrandName(brandName);
    }

    @Override
    public List<CarModel> getAllModels() {
        return modelDAO.getAllModels();
    }

    @Override
    public List<String> getModelNamesByBrand(String brandName) {
        return modelDAO.getModelsByBrandName(brandName).stream()
                .map(CarModel::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateModel(int id, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }
        CarModel model = modelDAO.getModelById(id);
        if (model == null) {
            return false;
        }
        model.setName(newName.trim());
        return modelDAO.updateModel(model);
    }

    @Override
    public boolean deleteModel(int id) {
        return modelDAO.deleteModel(id);
    }

    @Override
    public boolean modelExists(String brandName, String modelName) {
        return modelDAO.modelExists(brandName, modelName);
    }
}