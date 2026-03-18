package tycoon.model;

public class EmptyTile extends Tile {
    public EmptyTile(Vector2 pos, double height, WorldMap map) {
        super(pos, height, map);
    }

    @Override
    public boolean isBuildable() { return true; }

    @Override
    public double getBuildCostModifier() { return 1.0; }

    @Override
    public void onTick(double dt) {}
}