package tycoon.model;

/**
 * Abstract base class for all grid tiles on the map.
 */
public abstract class Tile {
    protected Vector2 pos;      
    protected double height;    // 统一改为 double
    protected WorldMap map;     

    public Tile(Vector2 pos, double height, WorldMap map) {
        this.pos = pos;
        this.height = height;   // 修复：正确赋值
        this.map = map;
    }

    public Vector2 getPos() { return pos; }
    public double getHeight() { return height; } // 返回值改为 double

    public abstract boolean isBuildable();
    public abstract double getBuildCostModifier();
    public abstract void onTick(double dt);
}