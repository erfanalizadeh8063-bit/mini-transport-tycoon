package tycoon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmptyTileTest {
    private EmptyTile emptyTile;
    private WorldMap dummyMap;
    private Vector2 dummyPos;

    @BeforeEach
    public void setUp() {
        dummyMap = new WorldMap(10, 10);
        dummyPos = new Vector2(2, 2);
        emptyTile = new EmptyTile(dummyPos, 0.0, dummyMap);
    }

    @Test
    public void testBuildCostModifier_DynamicCost() {
        assertEquals(0, emptyTile.getTreeCount(), "Initial tree count should be 0.");
        assertEquals(1.0, emptyTile.getBuildCostModifier(), "Cost modifier should be 1.0 when no trees are present.");

        emptyTile.setTreeCount(1);
        assertEquals(2.0, emptyTile.getBuildCostModifier(), "Cost modifier should double to 2.0 when trees are present (requires clearing).");
    }

    @Test
    public void testTreeGrowth_TimeInterval() {
        emptyTile.setTreeCount(1);

        emptyTile.onTick(59.9);

        assertEquals(1, emptyTile.getTreeCount(), "Trees should not grow or spread before the 60-second interval.");

        emptyTile.onTick(0.2); 
        
        assertTrue(emptyTile.getTreeCount() >= 1 && emptyTile.getTreeCount() <= 4, 
            "After growth interval, tree count should remain within valid bounds (1 to 4).");
    }
}