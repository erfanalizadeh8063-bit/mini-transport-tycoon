package tycoon.model;

public class Bus extends Vehicle {
    // 公交车：速度快(2.0)，容量中等(40)，只运乘客
    public Bus(String id) {
        super(id, 2.0, 40, CargoType.PASSENGERS); 
    }
}