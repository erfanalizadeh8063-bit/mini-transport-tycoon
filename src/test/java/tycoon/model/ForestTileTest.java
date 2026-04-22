package tycoon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ForestTileTest {

    private WorldMap map;

    @BeforeEach
    public void setUp() {
        map = new WorldMap(10, 10);
    }

    @Test
    public void testInitialTreeCountClamped_BelowMin() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, 0);
        assertEquals(ForestTile.MIN_TREES, tile.getTreeCount(),
            "Tree count below minimum should be clamped to MIN_TREES.");
    }

    @Test
    public void testInitialTreeCountClamped_AboveMax() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, 99);
        assertEquals(ForestTile.MAX_TREES, tile.getTreeCount(),
            "Tree count above maximum should be clamped to MAX_TREES.");
    }

    @Test
    public void testTreeGrowth_DoesNotGrowBeforeInterval() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, 1);
        map.setTile(1, 1, tile);
        tile.onTick(ForestTile.GROWTH_INTERVAL - 0.1);
        assertEquals(1, tile.getTreeCount(),
            "Trees should not grow before the growth interval elapses.");
    }

    @Test
    public void testTreeGrowth_GrowsAfterInterval() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, 1);
        map.setTile(1, 1, tile);
        tile.onTick(ForestTile.GROWTH_INTERVAL + 0.1);
        assertEquals(2, tile.getTreeCount(),
            "Trees should grow by 1 after the growth interval elapses.");
    }

    @Test
    public void testTreeGrowth_StopsAtMaxTrees() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, ForestTile.MAX_TREES);
        map.setTile(1, 1, tile);
        tile.onTick(ForestTile.GROWTH_INTERVAL * 10);
        assertEquals(ForestTile.MAX_TREES, tile.getTreeCount(),
            "Tree count must never exceed MAX_TREES.");
    }

    @Test
    public void testSpread_FullyGrownSpreadsToEmptyNeighbour() {
        ForestTile tile = new ForestTile(new Vector2(5, 5), 0.0, map, ForestTile.MAX_TREES);
        map.setTile(5, 5, tile);
        tile.onTick(ForestTile.SPREAD_INTERVAL + 0.1);

        boolean spread = false;
        for (Tile neighbour : map.neighbors(tile)) {
            if (neighbour instanceof ForestTile) {
                spread = true;
                break;
            }
        }
        assertTrue(spread, "A fully grown forest should spread to an adjacent empty tile.");
    }

    @Test
    public void testSpread_DoesNotSpreadBeforeInterval() {
        ForestTile tile = new ForestTile(new Vector2(5, 5), 0.0, map, ForestTile.MAX_TREES);
        map.setTile(5, 5, tile);
        tile.onTick(ForestTile.SPREAD_INTERVAL - 0.1);

        for (Tile neighbour : map.neighbors(tile)) {
            assertFalse(neighbour instanceof ForestTile,
                "Forest should not spread before the spread interval elapses.");
        }
    }

    @Test
    public void testSpread_DoesNotSpreadWhenNotFullyGrown() {
        ForestTile tile = new ForestTile(new Vector2(5, 5), 0.0, map, 2);
        map.setTile(5, 5, tile);
        tile.onTick(ForestTile.SPREAD_INTERVAL * 5);

        for (Tile neighbour : map.neighbors(tile)) {
            assertFalse(neighbour instanceof ForestTile,
                "Forest with fewer than MAX_TREES should not spread.");
        }
    }

    @Test
    public void testSpread_NoEmptyNeighbours_DoesNotCrash() {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = 5 + dx, ny = 5 + dy;
                if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10 && !(dx == 0 && dy == 0)) {
                    map.setTile(nx, ny, new RoadTile(new Vector2(nx, ny), 0.0, map, 50.0));
                }
            }
        }
        ForestTile tile = new ForestTile(new Vector2(5, 5), 0.0, map, ForestTile.MAX_TREES);
        map.setTile(5, 5, tile);
        assertDoesNotThrow(() -> tile.onTick(ForestTile.SPREAD_INTERVAL + 0.1),
            "Spread with no empty neighbours should not throw.");
    }

    @Test
    public void testBuildCostModifier_ScalesWithTreeCount() {
        for (int trees = ForestTile.MIN_TREES; trees <= ForestTile.MAX_TREES; trees++) {
            ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, trees);
            double expected = (ForestTile.BASE_ROAD_COST + trees * ForestTile.COST_PER_TREE)
                              / ForestTile.BASE_ROAD_COST;
            assertEquals(expected, tile.getBuildCostModifier(), 0.001,
                "Cost modifier should scale with tree count.");
        }
    }

    @Test
    public void testGetClearingCost_CorrectPerTree() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, 3);
        assertEquals(3 * ForestTile.COST_PER_TREE, tile.getClearingCost(), 0.001,
            "Clearing cost should be COST_PER_TREE multiplied by tree count.");
    }

    @Test
    public void testGetTotalBuildCost_IncludesBaseAndClearing() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, 4);
        double expected = ForestTile.BASE_ROAD_COST + 4 * ForestTile.COST_PER_TREE;
        assertEquals(expected, tile.getTotalBuildCost(), 0.001,
            "Total build cost should be base road cost plus clearing cost.");
    }

    @Test
    public void testIsBuildable_AlwaysTrue() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, 2);
        assertTrue(tile.isBuildable(), "Forest tiles must always be buildable.");
    }

    @Test
    public void testIsFullyGrown_TrueAtMaxTrees() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, ForestTile.MAX_TREES);
        assertTrue(tile.isFullyGrown(), "isFullyGrown should return true at MAX_TREES.");
    }

    @Test
    public void testIsFullyGrown_FalseBeforeMaxTrees() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, ForestTile.MAX_TREES - 1);
        assertFalse(tile.isFullyGrown(), "isFullyGrown should return false below MAX_TREES.");
    }

    @Test
    public void testSetTreeCount_ClampedWithinBounds() {
        ForestTile tile = new ForestTile(new Vector2(1, 1), 0.0, map, 2);
        tile.setTreeCount(-5);
        assertEquals(ForestTile.MIN_TREES, tile.getTreeCount(), "setTreeCount should clamp to MIN_TREES.");
        tile.setTreeCount(100);
        assertEquals(ForestTile.MAX_TREES, tile.getTreeCount(), "setTreeCount should clamp to MAX_TREES.");
    }
}
