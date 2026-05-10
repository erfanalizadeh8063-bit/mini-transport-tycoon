package tycoon.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class ModelFinalPushTest {

    @Test
    public void testEnums_FreeCoverage() {
        assertNotNull(Direction.valueOf("N"), "Direction N should exist");
        assertTrue(Direction.values().length > 0, "Direction should have values");

        assertNotNull(CargoType.valueOf("WOOD"), "CargoType WOOD should exist");
        assertTrue(CargoType.values().length > 0, "CargoType should have values");

        assertNotNull(SignalPhase.valueOf("NS_GREEN"), "SignalPhase should exist");
        assertTrue(SignalPhase.values().length > 0, "SignalPhase should have values");
    }

    @Test
    public void testVector2_DataClass() {
        Vector2 v1 = new Vector2(10, 20);
        Vector2 v2 = new Vector2(10, 20);
        Vector2 v3 = new Vector2(5, 5);

        assertEquals(10, v1.x(), "X coordinate should be exactly 10");
        assertEquals(20, v1.y(), "Y coordinate should be exactly 20");

        Vector2 sum = v1.add(v3);
        assertEquals(15, sum.x(), "10 + 5 should be 15");
        assertEquals(25, sum.y(), "20 + 5 should be 25");

        assertEquals(v1, v2, "Vectors with identical coordinates should be equal");
        assertNotEquals(v1, v3, "Vectors with different coordinates should not be equal");
        assertEquals(v1.hashCode(), v2.hashCode(), "HashCodes should match for identical vectors");
        assertNotNull(v1.toString(), "toString should not return null");
    }
    
    @Test
    public void testRoute_EdgeCases() {
        WorldMap map = new WorldMap(5, 5);
        City cityA = new City(new Vector2(0,0), 0, map, "A", 100);
        Route route = new Route(Arrays.asList(cityA));
        
        assertEquals(1, route.getStopCount(), "Route should have exactly 1 stop");
        assertNotNull(route.getStops(), "Route stops list should not be null");
        
        assertDoesNotThrow(() -> {
            route.advance();
        }, "Advancing a single-stop route should not throw exceptions");
        
        assertEquals(cityA, route.getCurrentTarget(), "Target should remain the only stop");
    }
}