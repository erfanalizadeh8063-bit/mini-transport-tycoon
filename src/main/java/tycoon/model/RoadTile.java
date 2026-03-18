package tycoon.model;

import java.util.HashMap;
import java.util.Map;

public class RoadTile extends Tile {
    private double speedLimit;
    private Map<Direction, Vehicle> occupancy;

    public RoadTile(Vector2 pos, double height, WorldMap map, double speedLimit) {
        super(pos, height, map);
        this.speedLimit = speedLimit;
        this.occupancy = new HashMap<>();
    }

    @Override
    public boolean isBuildable() { return false; }

    @Override
    public double getBuildCostModifier() { return 1.0; }

    public boolean canEnter(Direction dir) {
        return !occupancy.containsKey(dir);
    }

    public void reserve(Direction dir, Vehicle v) { occupancy.put(dir, v); }
    public void release(Direction dir) { occupancy.remove(dir); }

    @Override
    public void onTick(double dt) {}
    public double getSpeedLimit() { return speedLimit; }
}