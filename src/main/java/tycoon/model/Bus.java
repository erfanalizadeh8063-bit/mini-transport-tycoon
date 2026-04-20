package tycoon.model;

public class Bus extends Vehicle {
    public Bus(String id) {
        super(id, 2.0, 40, CargoType.PASSENGER); 
    }
}