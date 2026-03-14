package model;

import java.time.LocalDateTime;

public class CarModel {
    private int id;
    private int brandId;
    private String brandName;
    private String name;
    private int createdBy;
    private LocalDateTime createdAt;

    public CarModel() {}

    public CarModel(int brandId, String name, int createdBy) {
        this.brandId = brandId;
        this.name = name;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name;
    }
}