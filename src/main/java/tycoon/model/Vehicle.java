package tycoon.model;

/**
 * Abstract class for all road vehicles (Buses and Trucks).
 */
public abstract class Vehicle {
    protected String id;
    protected double speed;      // Units per second
    protected int capacity;      // Maximum cargo/passengers
    
    // Smooth movement logic (UML: progress: double 0..1)
    protected double progress;   
    protected RoadTile currentTile;
    protected RoadTile targetTile;
    
    protected Route route;       // The assigned circular route
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
        if (targetTile == null) return;

        // Check if the way is clear (integrates with colleague's Junction/TrafficLight)
        if (progress == 0) {
            if (!targetTile.canEnter(currentDirection)) {
                return; // Wait until the next tile is free or light is green
            }
            // Reserve immediately so no other vehicle can claim this tile
            targetTile.reserve(currentDirection, this);
        }

        // Advance progress based on speed and delta time
        progress += (speed * dt);

        // If progress >= 1.0, the vehicle has reached the center of the targetTile
        if (progress >= 1.0) {
            moveToNextTile();
        }
    }

    private void moveToNextTile() {
        // Release the old tile occupancy
        if (currentTile != null) {
            currentTile.release(currentDirection);
        }

        // Move into the target
        currentTile = targetTile;
        progress = 0.0;

        // targetTile was already reserved when movement started; nothing to do here

        // Logic to determine the NEXT targetTile based on Route would go here
        // targetTile = pathFinder.nextStep(currentTile, route.getCurrentTarget());
    }

    public void assignRoute(Route r) {
        this.route = r;
    }
    public RoadTile getCurrentTile() { return currentTile; }
    public RoadTile getTargetTile() { return targetTile; }
    public double getProgress() { return progress; }


}