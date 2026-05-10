package tycoon.model;

import java.util.Random;

/**
 * Represents an empty, buildable tile on the map.
 * Over time, an empty tile may naturally grow into a ForestTile.
 * 
 */

public class EmptyTile extends Tile{

    private static final double FOREST_SPAWN_INTERVAL = 120.0;
    private static final double FOREST_SPAWN_CHANCE = 0.15;
    private static final Random rand = new Random();

    private double forestSpawnTimer = 0.0;

    public EmptyTile(Vector2 pos, double height, WorldMap map) {
        super(pos, height, map);
    }

    @Override
    public void onTick(double dt) {
        forestSpawnTimer += dt;
        if (forestSpawnTimer >= FOREST_SPAWN_INTERVAL) {
            forestSpawnTimer = 0.0;
            if (rand.nextDouble() < FOREST_SPAWN_CHANCE) {
                map.setTile(pos.x(), pos.y(),
                    new ForestTile(pos, height, map, ForestTile.MIN_TREES));
            }
        }
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public double getBuildCostModifier() {
        return 1.0;
    }
}
