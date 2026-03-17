package tycoon.model;

/**
 * Represents an empty tile where roads or facilities can be placed.
 */
public class EmptyTile extends Tile {
    public EmptyTile(Vector2 pos, int height, WorldMap map) {
        super(pos, height, map);
    }

    @Override
    public boolean isBuildable() {
        return true; // Empty land is buildable by default.
    }

    @Override
    public double getBuildCostModifier() {
        return 1.0; // Standard cost.
    }

    @Override
    public void onTick(double dt) {
        // No time-based logic for plain empty tiles.
    }
}