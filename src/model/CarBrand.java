package model;

import java.time.LocalDateTime;

public class CarBrand {
    private int id;
    private String name;
    private int createdBy;
    private LocalDateTime createdAt;

    public CarBrand() {}

    public CarBrand(String name, int createdBy) {
        this.name = name;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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