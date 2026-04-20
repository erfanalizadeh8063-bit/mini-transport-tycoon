package tycoon.model;

import java.util.Random;

public class EmptyTile extends Tile {
    private int treeCount;
    private double growthTimer = 0;
    private static final Random rand = new Random();
    
    private WorldMap map; 

    public EmptyTile(Vector2 pos, double height, WorldMap map) {
        super(pos, height, map);
        this.treeCount = 0;
        this.map = map;
    }

    @Override
    public void onTick(double dt) {
        if (treeCount > 0) {
            growthTimer += dt;
            if (growthTimer >= 60.0) {
                growthTimer = 0;
                
                if (treeCount < 4 && rand.nextDouble() < 0.3) {
                    treeCount++;
                }
                
                if (treeCount >= 3 && rand.nextDouble() < 0.1) {
                    spreadTree();
                }
            }
        }
    }

    private void spreadTree() {
        var neighbors = this.map.neighbors(this); 
        if (neighbors != null) {
            for (Tile t : neighbors) {
                if (t instanceof EmptyTile et && et.getTreeCount() == 0) {
                    et.setTreeCount(1);
                    break;
                }
            }
        }
    }

    public int getTreeCount() { return treeCount; }
    public void setTreeCount(int count) { this.treeCount = count; }
    
    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public double getBuildCostModifier() {
        return (treeCount > 0) ? 2.0 : 1.0; 
    }
}