package tycoon.model;

/**
 * Abstract base class for all grid tiles on the map.
 */
public abstract class Tile {
    protected Vector2 pos;      // Position on the grid (UML: pos: Vector2)
    protected int height;       // Terrain height (UML: height: int)
    protected WorldMap map;     // Reference to the world map container

    public Tile(Vector2 pos, int height, WorldMap map) {
        this.pos = pos;
        this.height = height;
        this.map = map;
    }

    public Vector2 getPos() { return pos; }
    public int getHeight() { return height; }

    /**
     * Checks if a player can build infrastructure on this tile.
     * @return true if buildable, false otherwise.
     */
    public abstract boolean isBuildable();

    /**
     * Returns a multiplier for building costs (e.g., forests are more expensive).
     * @return cost modifier as double.
     */
    public abstract double getBuildCostModifier();

    /**
     * Called every simulation tick to handle time-based updates.
     * @param dt Delta time since the last tick.
     */
    public abstract void onTick(double dt);
}