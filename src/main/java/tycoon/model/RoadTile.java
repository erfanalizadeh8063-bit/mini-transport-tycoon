package tycoon.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a tile with a road that vehicles can travel on.
 */
public class RoadTile extends Tile {
    private double speedLimit;
    
    /**
     * UML Requirement: occupancy map to track which vehicle is in which direction.
     * Prevents more than one vehicle from occupying a tile in the same direction.
     */
    private Map<Direction, Vehicle> occupancy;

    public RoadTile(Vector2 pos, int height, WorldMap map, double speedLimit) {
        super(pos, height, map);
        this.speedLimit = speedLimit;
        this.occupancy = new HashMap<>();
    }

    @Override
    public boolean isBuildable() {
        return false; // Cannot build on top of an existing road.
    }

    @Override
    public double getBuildCostModifier() {
        return 1.0;
    }

    /**
     * Checks if a vehicle can enter this road tile from a specific direction.
     * @param dir The direction the vehicle is traveling.
     * @return true if the lane is empty.
     */
    public boolean canEnter(Direction dir) {
        return !occupancy.containsKey(dir);
    }

    /**
     * Reserves the tile for a vehicle in a specific direction.
     */
    public void reserve(Direction dir, Vehicle v) {
        occupancy.put(dir, v);
    }

    /**
     * Releases the tile when a vehicle moves out.
     */
    public void release(Direction dir) {
        occupancy.remove(dir);
    }

    @Override
    public void onTick(double dt) {
        // Future logic: road maintenance costs or wear-and-tear.
    }

    public double getSpeedLimit() {
        return speedLimit;
    }
}