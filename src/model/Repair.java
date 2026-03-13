package model;

import java.time.LocalDate;

public class Repair {
    private int id;
    private int carId;
    private String description;
    private String status;
    private double cost;
    private LocalDate startDate;
    private LocalDate endDate;
    private int mechanicId;

    public Repair() {}

    public Repair(int carId, String description, int mechanicId) {
        this.carId = carId;
        this.description = description;
        this.status = "DIAGNOSTICS";
        this.startDate = LocalDate.now();
        this.mechanicId = mechanicId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getMechanicId() { return mechanicId; }
    public void setMechanicId(int mechanicId) { this.mechanicId = mechanicId; }

    public String getStatusText() {
        switch (status) {
            case "DIAGNOSTICS": return "Диагностика";
            case "IN_REPAIR": return "В ремонте";
            case "COMPLETED": return "Ремонт завершен";
            case "CANCELLED": return "Отменен";
            default: return status;
        }
    }
}