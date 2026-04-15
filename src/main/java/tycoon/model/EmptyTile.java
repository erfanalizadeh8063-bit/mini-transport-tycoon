package tycoon.model;
public class EmptyTile extends Tile{
    
    private int treeCount = 0; 

    public EmptyTile(Vector2 pos, double height, WorldMap map) {
        super(pos, height, map);
    }
 
    public int getTreeCount() {
        return this.treeCount;
    }

    public void setTreeCount(int count) {
        if (count < 0) this.treeCount = 0;
        else if (count > 4) this.treeCount = 4;
        else this.treeCount = count;
    }

    @Override
    public boolean isBuildable() { 
        return true; 
    }

    @Override
    public double getBuildCostModifier() { 
        return (treeCount > 0) ? 2.0 : 1.0; 
    }

    @Override
    public void onTick(double dt) {
    }
}