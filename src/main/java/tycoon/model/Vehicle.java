package tycoon.model;

import java.util.Set;

/**
 * Abstract class for all road vehicles (Buses and Trucks).
 */
public abstract class Vehicle {
    protected String id;
    protected double speed; // Units per second
    protected int capacity; // Maximum cargo/passengers

    // Smooth movement logic (UML: progress: double 0..1)
    protected double progress;
    protected RoadTile currentTile;
    protected RoadTile targetTile;

    protected Route route; // The assigned circular route
    protected Direction currentDirection;

    public Vehicle(String id, double speed, int capacity) {
        this.id = id;
        this.speed = speed;
        this.capacity = capacity;
        this.progress = 0.0;
    }

    /**
     * Core update loop for the vehicle.
     * Handles movement and tile transition.
     */
    public void update(double dt) {
        if (targetTile == null) {
            return;
        }

        // Advance progress based on speed and delta time
        progress += (speed * dt);

        // If progress >= 1.0, the vehicle has reached the target tile
        if (progress >= 1.0) {
            moveToNextTile();
        }
    }

    private void moveToNextTile() {
        if (currentTile != null && currentDirection != null) {
            currentTile.release(currentDirection);
        }

        RoadTile previousTile = currentTile;
        currentTile = targetTile;
        progress = 0.0;

        if (currentTile == null) {
            return;
        }

        if (currentDirection != null) {
            currentTile.reserve(currentDirection, this);
        }

        targetTile = findNextConnectedRoad(previousTile, currentTile);
    }

    private RoadTile findNextConnectedRoad(RoadTile previousTile, RoadTile current) {
        if (current == null) {
            return null;
        }

        Set<Direction> connections = current.getConnections();

        for (Direction dir : connections) {
            int nx = current.getPos().x() + dir.dx();
            int ny = current.getPos().y() + dir.dy();

            if (nx < 0 || ny < 0 || nx >= current.map.getWidth() || ny >= current.map.getHeight()) {
                continue;
            }

            Tile neighbor = current.map.getTile(nx, ny);
            if (!(neighbor instanceof RoadTile nextRoad)) {
                continue;
            }

            if (previousTile != null && nextRoad == previousTile) {
                continue;
            }

            currentDirection = dir;
            return nextRoad;
        }

        return null;
    }

    public void assignRoute(Route r) {
        this.route = r;
    }

    public RoadTile getCurrentTile() {
        return currentTile;
    }

    public RoadTile getTargetTile() {
        return targetTile;
    }

    public double getProgress() {
        return progress;
    }

    public void setCurrentTile(RoadTile tile) {
        this.currentTile = tile;
    }

    public void setTargetTile(RoadTile tile) {
        this.targetTile = tile;
    }
}