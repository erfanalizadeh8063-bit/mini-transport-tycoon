package tycoon.model;

import tycoon.service.PathFinder;
import tycoon.service.GameEngine; 
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public abstract class Vehicle implements Serializable {
    protected String id;
    protected double speed;      
    protected int capacity;      
    
    protected double progress;   
    protected RoadTile currentTile;
    protected RoadTile targetTile;
    
    protected CargoType currentCargoType = null;
    protected int currentLoad = 0;
    protected CargoType allowedCargoType;
    
    protected List<RoadTile> currentSegmentPath = new ArrayList<>();
    protected Route route;       
    protected Direction currentDirection; 

    public Vehicle(String id, double speed, int capacity, CargoType allowedCargoType) {
        this.id = id;
        this.speed = speed;
        this.capacity = capacity;
        this.allowedCargoType = allowedCargoType;
        this.progress = 0.0;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void update(double dt, PathFinder pathFinder, GameEngine engine) {
        if (targetTile == null && route != null) {
            startNewSegment(pathFinder, engine);
        }

        if (targetTile == null) return;

        if (isPathBlocked()) {

            if (progress < 0.45) {
                progress += (speed * dt);
                if (progress > 0.45) progress = 0.45;
            }
            return;
        }

        progress += (speed * dt);

        while (progress >= 1.0) {
            moveToNextTile(pathFinder, engine);
        }
    }

    private boolean isPathBlocked() {
        if (targetTile == null || currentDirection == null) return false;
        return !targetTile.canEnter(currentDirection);
    }

    private void startNewSegment(PathFinder pathFinder, GameEngine engine) {
        ITransportPoint nextStopPoint = route.getCurrentTarget();
        if (nextStopPoint == null) return;

        RoadTile destination = nextStopPoint.getAccessTile(); 
        
        if (destination == null) return;

        List<RoadTile> path = pathFinder.findPath(this.currentTile, destination);
        
        if (path != null) {
            if (path.isEmpty()) {
                performStopActions(engine);
            } else {
                this.currentSegmentPath = path;
                this.targetTile = currentSegmentPath.remove(0);
                this.progress = 0.0;
                updateDirection();
                
                if (currentTile != null && currentDirection != null) {
                    currentTile.reserve(currentDirection, this);
                }
            }
        } else {
            System.out.println("Warning: " + id + " waiting for road connection.");
        }
    }

    private void performStopActions(GameEngine engine) {
        // 到站了，把座位腾出来
        if (currentTile != null && currentDirection != null) {
            currentTile.release(currentDirection);
        }
        targetTile = null; 
        ITransportPoint currentStop = route.getCurrentTarget();
        
        if (currentStop != null) {
            if (currentLoad > 0) {
                int unloaded = currentStop.unload(currentCargoType, currentLoad);
                if (unloaded > 0) {
                    currentLoad -= unloaded;
                    double earned = unloaded * 10.0; 
                    engine.earn(earned);
                }
                if (currentLoad == 0) currentCargoType = null;
            }

            if (currentLoad < capacity) {
                int amountToLoad = capacity - currentLoad;
                int loaded = currentStop.load(allowedCargoType, amountToLoad);
                if (loaded > 0) {
                    currentLoad += loaded;
                    currentCargoType = allowedCargoType;
                }
            }
        }
        
        if (route != null) {
            route.advance(); 
        }
    }

    private void moveToNextTile(PathFinder pathFinder, GameEngine engine) {
        if (currentTile != null && currentDirection != null) {
            currentTile.release(currentDirection);
        }

        currentTile = targetTile;
        progress -= 1.0; 

        if (!currentSegmentPath.isEmpty()) {
            targetTile = currentSegmentPath.remove(0);
            updateDirection();
            
            if (currentTile != null && currentDirection != null) {
                currentTile.reserve(currentDirection, this);
            }
        } else {
            targetTile = null;
            performStopActions(engine);
        }
    }

    private void updateDirection() {
        if (currentTile == null || targetTile == null) return;
        int dx = targetTile.getPos().x() - currentTile.getPos().x();
        int dy = targetTile.getPos().y() - currentTile.getPos().y();

        if (dx > 0) currentDirection = Direction.E;
        else if (dx < 0) currentDirection = Direction.W;
        else if (dy > 0) currentDirection = Direction.S;
        else if (dy < 0) currentDirection = Direction.N;
    }

    public RoadTile getCurrentTile() { return currentTile; }
    public RoadTile getTargetTile() { return targetTile; }
    public double getProgress() { return progress; }
    public void setCurrentTile(RoadTile tile) { this.currentTile = tile; }
}