package tycoon.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for all road vehicles.
 * Path-based movement + traffic light check before entering next tile.
 */
public abstract class Vehicle {
    protected String id;
    protected double speed;
    protected int capacity;

    // movement on current segment
    protected double progress;
    protected RoadTile currentTile;
    protected RoadTile nextTile;

    // full path
    protected List<RoadTile> currentPath;
    protected int pathIndex;

    // simple shuttle between 2 stops
    protected RoadTile stopA;
    protected RoadTile stopB;
    protected boolean goingForward;

    // remember how we entered the current tile
    protected Direction currentDirection;

    public Vehicle(String id, double speed, int capacity) {
        this.id = id;
        this.speed = speed;
        this.capacity = capacity;
        this.progress = 0.0;
        this.currentPath = new ArrayList<>();
        this.pathIndex = 0;
        this.goingForward = true;
        this.currentDirection = null;
    }

    public void setShuttleStops(RoadTile stopA, RoadTile stopB) {
        this.stopA = stopA;
        this.stopB = stopB;
    }

    public void setPath(List<RoadTile> path) {
        this.currentPath = (path != null) ? new ArrayList<>(path) : new ArrayList<>();
        this.pathIndex = 0;
        this.progress = 0.0;
        this.currentDirection = null;

        if (currentPath.isEmpty()) {
            currentTile = null;
            nextTile = null;
            return;
        }

        currentTile = currentPath.get(0);

        if (currentPath.size() > 1) {
            nextTile = currentPath.get(1);
            pathIndex = 1;
        } else {
            nextTile = null;
        }
    }

    public void update(double dt) {
        if (currentTile == null || nextTile == null) {
            return;
        }

        Direction moveDir = getDirection(currentTile, nextTile);
        if (moveDir == null) {
            nextTile = null;
            return;
        }

        // If the next tile is blocked by a red light, wait steadily before it.
        if (!nextTile.canEnter(moveDir)) {
            if (progress > 0.90) {
                progress = 0.90;
            }
            return;
        }

        progress += speed * dt;

        while (progress >= 1.0 && nextTile != null) {
            moveDir = getDirection(currentTile, nextTile);
            if (moveDir == null) {
                nextTile = null;
                return;
            }

            if (!nextTile.canEnter(moveDir)) {
                progress = 0.90;
                return;
            }

            progress -= 1.0;
            advanceToNextTile(moveDir);
        }
    }

    private void advanceToNextTile(Direction moveDir) {
        currentTile = nextTile;
        currentDirection = moveDir;

        if (pathIndex >= currentPath.size() - 1) {
            handleReachedDestination();
            return;
        }

        pathIndex++;
        nextTile = currentPath.get(pathIndex);
    }

    private void handleReachedDestination() {
        progress = 0.0;
        nextTile = null;
    }

    private Direction getDirection(RoadTile from, RoadTile to) {
        if (from == null || to == null) {
            return null;
        }

        int dx = to.getPos().x() - from.getPos().x();
        int dy = to.getPos().y() - from.getPos().y();

        for (Direction dir : Direction.values()) {
            if (dir.dx() == dx && dir.dy() == dy) {
                return dir;
            }
        }

        return null;
    }

    public void reversePath(List<RoadTile> newPath) {
        goingForward = !goingForward;
        setPath(newPath);
    }

    public boolean hasReachedStop() {
        return currentTile != null && nextTile == null;
    }

    public RoadTile getCurrentTile() {
        return currentTile;
    }

    public RoadTile getTargetTile() {
        return nextTile;
    }

    public double getProgress() {
        return progress;
    }

    public String getId() {
        return id;
    }

    public boolean isGoingForward() {
        return goingForward;
    }

    public RoadTile getStopA() {
        return stopA;
    }

    public RoadTile getStopB() {
        return stopB;
    }
}