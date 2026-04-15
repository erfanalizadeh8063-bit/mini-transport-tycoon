package tycoon.model;
import java.io.Serializable;
/**
 * Abstract base class for all grid tiles on the map.
 */
public abstract class Tile implements Serializable {
    protected Vector2 pos;      
    protected double height;    
    protected WorldMap map;     

    public Tile(Vector2 pos, double height, WorldMap map) {
        this.pos = pos;
        this.height = height;   
        this.map = map;
    }

    public Vector2 getPos() { return pos; }
    public double getHeight() { return height; } 

    public abstract boolean isBuildable();
    public abstract double getBuildCostModifier();
    public abstract void onTick(double dt);
}