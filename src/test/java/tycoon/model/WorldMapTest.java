package tycoon.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class WorldMapTest {

    @Test
    public void testMapInitializationAndBounds() {
        WorldMap map = new WorldMap(10, 15);
        
        assertEquals(10, map.getWidth(), "Map width should match the constructor parameter.");
        assertEquals(15, map.getHeight(), "Map height should match the constructor parameter.");
        
        // Every tile should initially be an EmptyTile
        Tile tile = map.getTile(0, 0);
        assertTrue(tile instanceof EmptyTile, "Default tiles should be initialized as EmptyTile.");
        
        // Test out-of-bounds safety (if your map handles it)
        assertNull(map.getTile(-1, -1), "Getting an out-of-bounds tile should safely return null.");
        assertNull(map.getTile(100, 100), "Getting an out-of-bounds tile should safely return null.");
    }

    @Test
    public void testTileReplacement() {
        WorldMap map = new WorldMap(5, 5);
        RoadTile road = new RoadTile(new Vector2(2, 2), 0.0, map, 50.0);
        
        map.setTile(2, 2, road);
        assertEquals(road, map.getTile(2, 2), "Map should allow replacing tiles at specific coordinates.");
    }
}