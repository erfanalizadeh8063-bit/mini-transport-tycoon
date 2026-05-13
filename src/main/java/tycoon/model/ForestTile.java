package tycoon.model;


/**
 * Represents a forest tile on the map.
 * Trees grow over time (1 to 4) and spread to adjacent empty tiles.
 * Building roads on forest tiles costs more due to clearing.
 * 
 * @author Srinivas James Madoc
 * @version 1.0
 */
public class ForestTile extends Tile {

    public static final int MIN_TREES = 1;
    public static final int MAX_TREES = 4;
    public static final double GROWTH_INTERVAL = 30.0;
    public static final double SPREAD_INTERVAL = 60.0;
    public static final double BASE_ROAD_COST = 100.0;
    public static final double COST_PER_TREE = 50.0;

    private int treeCount;
    private double growthTimer;
    private double spreadTimer;

    /**
     * Creates a new forest tile with the specified tree count.
     * Tree count is automatically clamped between MIN_TREES and MAX_TREES.
     * 
     * @param pos the position of this tile on the map
     * @param height the elevation of this tile
     * @param map the world map this tile belongs to
     * @param treeCount initial number of trees (will be clamped to 1-4)
     */
    public ForestTile(Vector2 pos, double height, WorldMap map, int treeCount) {
        super(pos, height, map);
        this.treeCount = Math.max(MIN_TREES, Math.min(MAX_TREES, treeCount));
        this.growthTimer = 0.0;
        this.spreadTimer = 0.0;
    }

    /**
     * Updates the forest tile state each game tick.
     * Handles tree growth and spreading to adjacent empty tiles.
     * 
     * @param dt time elapsed since last tick in seconds
     */
    @Override
    public void onTick(double dt) {
        growthTimer += dt;
        spreadTimer += dt;

        if (treeCount < MAX_TREES && growthTimer >= GROWTH_INTERVAL) {
            treeCount++;
            growthTimer = 0.0;
        }

        if (treeCount == MAX_TREES && spreadTimer >= SPREAD_INTERVAL) {
            spreadTimer = 0.0;
            spreadToNeighbour();
        }
    }

    /**
     * Attempts to spread this forest to one adjacent empty tile.
     * Only called when the forest is fully grown (4 trees).
     */
    private void spreadToNeighbour() {
        for (Tile neighbour : map.neighbors(this)) {
            if (neighbour instanceof EmptyTile) {
                map.setTile(
                    neighbour.getPos().x(),
                    neighbour.getPos().y(),
                    new ForestTile(neighbour.getPos(), neighbour.getHeight(), map, MIN_TREES)
                );
                return;
            }
        }
    }

    /**
     * Returns the cost modifier for building roads on this tile.
     * Clearing cost scales with tree density:
     * 1 tree = +$50, 2 trees = +$100, 3 trees = +$150, 4 trees = +$200
     * 
     * @return multiplier applied to base road construction cost
     */
    @Override
    public double getBuildCostModifier() {
        return (BASE_ROAD_COST + (treeCount * COST_PER_TREE)) / BASE_ROAD_COST;
    }

    /**
     * Returns the cost to clear all trees from this tile.
     * 
     * @return clearing cost in dollars
     */
    public double getClearingCost() {
        return treeCount * COST_PER_TREE;
    }

    /**
     * Returns the total cost to build a road on this tile.
     * Includes base road cost plus tree clearing cost.
     * 
     * @return total build cost in dollars
     */
    public double getTotalBuildCost() {
        return BASE_ROAD_COST + getClearingCost();
    }

    /**
     * Forest tiles can always be built on after clearing trees.
     * 
     * @return always true
     */
    @Override
    public boolean isBuildable() {
        return true;
    }

    /**
     * Returns the current number of trees on this tile.
     * 
     * @return tree count (1-4)
     */
    public int getTreeCount() {
        return treeCount;
    }

    /**
     * Sets the number of trees on this tile.
     * Value is automatically clamped between MIN_TREES and MAX_TREES.
     * 
     * @param count desired tree count
     */
    public void setTreeCount(int count) {
        this.treeCount = Math.max(MIN_TREES, Math.min(MAX_TREES, count));
    }

    /**
     * Checks if this forest has reached maximum tree density.
     * 
     * @return true if tree count equals MAX_TREES (4)
     */
    public boolean isFullyGrown() {
        return treeCount == MAX_TREES;
    }
}
