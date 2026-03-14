package model;

public class CarListItem {
    private final Car car;
    private final String displayText;

    public CarListItem(Car car) {
        this.car = car;
        this.displayText = String.format("%s %s (%s) - %s",
                car.getBrand(), car.getModel(), car.getYear(), car.getLicensePlate());
    }

    public Car getCar() {
        return car;
    }

    @Override
    public String toString() {
        return displayText;
    }
}