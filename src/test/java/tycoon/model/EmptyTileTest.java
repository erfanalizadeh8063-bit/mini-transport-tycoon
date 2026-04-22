package tycoon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmptyTileTest {

    private WorldMap map;
    private EmptyTile tile;

    @BeforeEach
    public void setUp() {
        map  = new WorldMap(10, 10);
        tile = new EmptyTile(new Vector2(5, 5), 0.0, map);
        map.setTile(5, 5, tile);
    }

    @Test
    public void testIsBuildable() {
        assertTrue(tile.isBuildable(), "Empty tile must be buildable.");
    }

    @Test
    public void testBuildCostModifier_AlwaysOne() {
        assertEquals(1.0, tile.getBuildCostModifier(), 0.001,
            "Empty tile cost modifier should always be 1.0.");
    }

    @Test
    public void testOnTick_DoesNotThrow() {
        assertDoesNotThrow(() -> tile.onTick(1.0),
            "onTick should not throw for any delta time.");
    }

    @Test
    public void testOnTick_NegativeDelta_DoesNotThrow() {
        assertDoesNotThrow(() -> tile.onTick(-1.0),
            "onTick should handle negative delta without throwing.");
    }

    @Test
    public void testNaturalForestSpawn_AfterLongTime() {
        for (int i = 0; i < 1000; i++) {
            tile.onTick(1.0);
            Tile current = map.getTile(5, 5);
            if (current instanceof ForestTile) return;
        }
    }
}
