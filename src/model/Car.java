package model;

import java.time.LocalDate;

public class Car {
    private int id;
    private String brand;
    private String model;
    private int year;
    private String vin;
    private String licensePlate;
    private int ownerId;
    private LocalDate registrationDate;

    public Car() {}

    public Car(String brand, String model, int year, String vin, String licensePlate, int ownerId) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.ownerId = ownerId;
        this.registrationDate = LocalDate.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return String.format("%s %s (%d) - VIN: %s", brand, model, year, vin);
    }
}