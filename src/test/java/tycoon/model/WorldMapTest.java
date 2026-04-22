package tycoon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WorldMapTest {

    private WorldMap map;

    @BeforeEach
    public void setUp() {
        map = new WorldMap(10, 10);
    }

    @Test
    public void testInitialTiles_AllEmpty() {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                assertInstanceOf(EmptyTile.class, map.getTile(x, y),
                    "All tiles should be EmptyTile on initialization.");
            }
        }
    }

    @Test
    public void testGetTile_OutOfBounds_ReturnsNull() {
        assertNull(map.getTile(-1, 0), "Out of bounds x should return null.");
        assertNull(map.getTile(0, -1), "Out of bounds y should return null.");
        assertNull(map.getTile(10, 0), "x equal to width should return null.");
        assertNull(map.getTile(0, 10), "y equal to height should return null.");
    }

    @Test
    public void testSetTile_OutOfBounds_DoesNotThrow() {
        assertDoesNotThrow(() -> map.setTile(-1, 0, new EmptyTile(new Vector2(0, 0), 0.0, map)),
            "setTile out of bounds should not throw.");
    }

    @Test
    public void testSetTile_ReplacesTile() {
        ForestTile forest = new ForestTile(new Vector2(3, 3), 0.0, map, 2);
        map.setTile(3, 3, forest);
        assertSame(forest, map.getTile(3, 3), "setTile should replace the tile at the given position.");
    }

    @Test
    public void testNeighbors_CenterTile_ReturnsFour() {
        Tile center = map.getTile(5, 5);
        List<Tile> neighbours = map.neighbors(center);
        assertEquals(4, neighbours.size(), "Center tile should have 4 neighbours.");
    }

    @Test
    public void testNeighbors_CornerTile_ReturnsTwo() {
        Tile corner = map.getTile(0, 0);
        List<Tile> neighbours = map.neighbors(corner);
        assertEquals(2, neighbours.size(), "Corner tile should have 2 neighbours.");
    }

    @Test
    public void testNeighbors_EdgeTile_ReturnsThree() {
        Tile edge = map.getTile(0, 5);
        List<Tile> neighbours = map.neighbors(edge);
        assertEquals(3, neighbours.size(), "Edge tile should have 3 neighbours.");
    }

    @Test
    public void testIsForest_ReturnsTrueForForestTile() {
        map.setTile(4, 4, new ForestTile(new Vector2(4, 4), 0.0, map, 2));
        assertTrue(map.isForest(4, 4), "isForest should return true for a ForestTile.");
    }

    @Test
    public void testIsForest_ReturnsFalseForEmptyTile() {
        assertFalse(map.isForest(4, 4), "isForest should return false for an EmptyTile.");
    }

    @Test
    public void testGetForestTiles_ReturnsAllForests() {
        map.setTile(1, 1, new ForestTile(new Vector2(1, 1), 0.0, map, 1));
        map.setTile(2, 2, new ForestTile(new Vector2(2, 2), 0.0, map, 3));
        map.setTile(3, 3, new ForestTile(new Vector2(3, 3), 0.0, map, 4));
        assertEquals(3, map.getForestTiles().size(),
            "getForestTiles should return all ForestTile instances on the map.");
    }

    @Test
    public void testGetForestTiles_EmptyMap_ReturnsEmptyList() {
        assertTrue(map.getForestTiles().isEmpty(),
            "getForestTiles should return empty list when no forests exist.");
    }

    @Test
    public void testPlaceRoad_ReplacesEmptyTile() {
        map.placeRoad(5, 5, 50.0);
        assertInstanceOf(RoadTile.class, map.getTile(5, 5),
            "placeRoad should replace the tile with a RoadTile.");
    }

    @Test
    public void testPlaceRoad_UpdatesNeighbourConnections() {
        map.placeRoad(5, 5, 50.0);
        map.placeRoad(5, 6, 50.0);
        RoadTile upper = (RoadTile) map.getTile(5, 5);
        assertTrue(upper.isConnected(Direction.S),
            "Road at (5,5) should connect south after placing road at (5,6).");
    }

    @Test
    public void testSetFacility_FillsArea() {
        City city = new City(new Vector2(2, 2), 0.0, map, "TestCity", 500);
        map.setFacility(2, 2, 3, 3, city);
        for (int x = 2; x < 5; x++) {
            for (int y = 2; y < 5; y++) {
                assertSame(city, map.getTile(x, y),
                    "setFacility should fill the entire area with the facility.");
            }
        }
    }
}
