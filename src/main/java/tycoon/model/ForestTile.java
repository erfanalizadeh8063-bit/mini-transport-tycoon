package tycoon.model;

public class ForestTile extends Tile {
    private int treeCount;
    private double growthTimer;

    private static final double GROWTH_INTERVAL = 30.0;
    private static final double SPREAD_INTERVAL = 60.0;
    private static final double BASE_CLEAR_COST = 50.0;

    public ForestTile(Vector2 pos, double height, WorldMap map, int treeCount) {
        super(pos, height, map);
        this.treeCount = Math.max(1, Math.min(4, treeCount));
        this.growthTimer = 0;
    }

    @Override
    public void onTick(double dt) {
        growthTimer += dt;

        if (treeCount < 4 && growthTimer >= GROWTH_INTERVAL) {
            treeCount++;
            growthTimer = 0;
        }

        if (treeCount == 4 && growthTimer >= SPREAD_INTERVAL) {
            growthTimer = 0;
            spreadToNeighbour();
        }
    }

    private void spreadToNeighbour() {
        for (Tile neighbour : map.neighbors(this)) {
            if (neighbour instanceof EmptyTile) {
                map.setTile(neighbour.getPos().x(), neighbour.getPos().y(),
                        new ForestTile(neighbour.getPos(), neighbour.getHeight(), map, 1));
                return;
            }
        }
    }

    @Override
    public boolean isBuildable() { return true; }

    @Override
    public double getBuildCostModifier() {
        return 1.0 + (treeCount * BASE_CLEAR_COST / 100.0);
    }

    public int getTreeCount() { return treeCount; }
}
