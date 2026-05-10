package tycoon.model;


/**
 * Represents a forest tile on the map.
 * Trees grow over time (1 to 4) and spread to adjacent empty tiles.
 * Building roads on forest tiles costs more due to clearing.
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

    public ForestTile(Vector2 pos, double height, WorldMap map, int treeCount) {
        super(pos, height, map);
        this.treeCount = Math.max(MIN_TREES, Math.min(MAX_TREES, treeCount));
        this.growthTimer = 0.0;
        this.spreadTimer = 0.0;
    }

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
     * Clearing cost scales with tree density:
     * 1 tree = +$50, 2 trees = +$100, 3 trees = +$150, 4 trees = +$200
     */
    @Override
    public double getBuildCostModifier() {
        return (BASE_ROAD_COST + (treeCount * COST_PER_TREE)) / BASE_ROAD_COST;
    }

    /**
     * Returns the total road building cost including clearing cost.
     */
    public double getClearingCost() {
        return treeCount * COST_PER_TREE;
    }

    /**
     * Returns the total cost to build a road on this tile.
     */
    public double getTotalBuildCost() {
        return BASE_ROAD_COST + getClearingCost();
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    public int getTreeCount() {
        return treeCount;
    }

    public void setTreeCount(int count) {
        this.treeCount = Math.max(MIN_TREES, Math.min(MAX_TREES, count));
    }

    public boolean isFullyGrown() {
        return treeCount == MAX_TREES;
    }
}
