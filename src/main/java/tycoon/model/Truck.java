package tycoon.model;

public class Truck extends Vehicle {
    public Truck(String id) {
        super(id, 1.0, 100, CargoType.GOODS_A); 
    }
}