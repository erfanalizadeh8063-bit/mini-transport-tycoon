package tycoon.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * A specialized tile representing a road segment.
 * Handles vehicle occupancy, connectivity with adjacent roads, and 
 * manages junction logic, including traffic light integration for 
 * regulating vehicle entry.
 */
public class RoadTile extends Tile {
    private double speedLimit;
    private Map<Direction, Vehicle> occupancy;
    private Junction junction;
    private EnumSet<Direction> connections;

    public RoadTile(Vector2 pos, double height, WorldMap map, double speedLimit) {
        super(pos, height, map);
        this.speedLimit = speedLimit;
        this.occupancy = new HashMap<>();
        this.junction = null;
        this.connections = EnumSet.noneOf(Direction.class);
    }

    public boolean hasJunction() {
        return junction != null;
    }

    public Junction getJunction() {
        return junction;
    }

    public void setJunction(Junction junction) {
        this.junction = junction;
    }

    public void addConnection(Direction dir) {
        connections.add(dir);
        updateJunctionState();
    }

    public void removeConnection(Direction dir) {
        connections.remove(dir);
        updateJunctionState();
    }

    public void clearConnections() {
        connections.clear();
        updateJunctionState();
    }

    public boolean isConnected(Direction dir) {
        return connections.contains(dir);
    }

    public int getConnectionCount() {
        return connections.size();
    }

    public Set<Direction> getConnections() {
        return EnumSet.copyOf(connections);
    }

    private void updateJunctionState() {
        if (connections.size() >= 3) {
            if (junction == null) {
                junction = new Junction();
            }
        } else {
            junction = null;
        }
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
        if (dir == null) {
            return false;
        }

        if (occupancy.containsKey(dir)) {
            return false;
        }

        if (junction != null && junction.hasLight()) {
            return junction.getTrafficLight().canPass(dir);
        }

        return true; 
    }

    public void reserve(Direction dir, Vehicle v) {
        if (dir != null) {
            occupancy.put(dir, v);
        }
    }

    public void release(Direction dir) {
        if (dir != null) {
            occupancy.remove(dir);
        }
    }

    @Override
    public void onTick(double dt) {
        if (junction != null && junction.hasLight()) {
            junction.getTrafficLight().update(dt); 
        }
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

}