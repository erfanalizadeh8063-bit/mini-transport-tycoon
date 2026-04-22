package tycoon.model;

public class Coach extends Vehicle {
    public Coach(String id) {
        super(id, 10.0, 80, CargoType.PASSENGER);
    }
}