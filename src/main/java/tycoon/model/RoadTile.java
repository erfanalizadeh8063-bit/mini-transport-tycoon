package tycoon.model;

import java.util.HashMap;
import java.util.Map;

public class RoadTile extends Tile {
    private double speedLimit;
    private Map<Direction, Vehicle> occupancy;
    private Junction junction;


    public boolean hasJunction() {
        return junction != null;
    }

    public Junction getJunction() {
        return junction;
    }

    public RoadTile(Vector2 pos, double height, WorldMap map, double speedLimit) {
        super(pos, height, map);
        this.speedLimit = speedLimit;
        this.occupancy = new HashMap<>();
        this.junction = null;
    }

    public void setJunction(Junction junction) {
        this.junction = junction;
    }

    @Override
    public boolean isBuildable() {
        return false;
    }

    @Override
    public double getBuildCostModifier() {
        return 1.0;
    }

    public boolean canEnter(Direction dir) {
        if (occupancy.containsKey(dir)) {
            return false;
        }
        if (junction != null && junction.hasLight()) {
            return junction.getTrafficLight().canPass(dir);
        }
        return true;
    }

    public void reserve(Direction dir, Vehicle v) {
        occupancy.put(dir, v);
    }

    public void release(Direction dir) {
        occupancy.remove(dir);
    }

    @Override
    public void onTick(double dt) {
    }

    public double getSpeedLimit() {
        return speedLimit;
    }
}