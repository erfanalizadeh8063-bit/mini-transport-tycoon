package tycoon.model;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class City extends Facility {
    private Map<CargoType, Double> demand;

    public City(String name, List<Vector2> tiles, WorldMap map) {
        super(name, tiles, map);
        this.demand = new HashMap<>();
        this.demand.put(CargoType.GOODS_A, 1.0);
    }

    public void updateDemand(double dt) {
        for (CargoType type : demand.keySet()) {
            double currentDemand = demand.get(type);
            demand.put(type, currentDemand + (0.01 * dt));
        }
    }

    @Override
    public void onTick(double dt) {
        updateDemand(dt);
        int currentPassengers = storage.getOrDefault(CargoType.PASSENGERS, 0);
        if (Math.random() < 0.1 * dt) { 
             storage.put(CargoType.PASSENGERS, currentPassengers + 1);
        }
    }

    @Override
    public int load(CargoType type, int amount) {
        if (type != CargoType.PASSENGERS) return 0;
        int available = storage.getOrDefault(type, 0);
        int actual = Math.min(available, amount);
        storage.put(type, available - actual);
        return actual;
    }

    @Override
    public int unload(CargoType type, int amount) {
        int current = storage.getOrDefault(type, 0);
        storage.put(type, current + amount);
        return amount;
    }

    @Override
    public boolean isBuildable() {
        return false; 
    }

    @Override
    public double getBuildCostModifier() {
        return 2.0; 
    }
}