package tycoon.model;

import java.util.List;

public class Industry extends Facility {
    private CargoType produces;

    public Industry(String name, List<Vector2> tiles, WorldMap map, CargoType produces) {
        super(name, tiles, map);
        this.produces = produces;
    }


    @Override
    public int load(CargoType type, int amount) {
        if (type != produces) return 0;
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
    public void onTick(double dt) {
        produce(dt);
    }

    public void produce(double dt) {
        int currentAmount = storage.getOrDefault(produces, 0);
        int producedThisTick = (int)(1 * dt); 
        if (producedThisTick > 0) {
            storage.put(produces, currentAmount + producedThisTick);
        }
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