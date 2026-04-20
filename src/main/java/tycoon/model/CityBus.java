package tycoon.model;

public class CityBus extends Vehicle {
    public CityBus(String id) {
        super(id, 6.0, 40, CargoType.PASSENGER);
    }
}