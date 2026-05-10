package tycoon.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class RouteTest {

    @Test
    public void testRouteAdvancement_CircularLogic() {
        WorldMap map = new WorldMap(10, 10);
        
        // Create dummy transport points (Facilities implement ITransportPoint)
        City stopA = new City(new Vector2(0,0), 0, map, "Stop A", 100);
        Industry stopB = new Industry(new Vector2(5,5), 0, map, "Stop B", CargoType.WOOD);
        
        // Create a route: A -> B
        Route route = new Route(Arrays.asList(stopA, stopB));
        
        assertEquals(2, route.getStopCount(), "Route should contain exactly 2 stops.");
        
        // Initial target should be A
        assertEquals(stopA, route.getCurrentTarget(), "Initial target should be the first stop.");
        
        // Advance to B
        route.advance();
        assertEquals(stopB, route.getCurrentTarget(), "Target should advance to the second stop.");
        
        // Advance again, it should loop back to A
        route.advance();
        assertEquals(stopA, route.getCurrentTarget(), "Route should act as a circular loop and return to the first stop.");
    }
    
    @Test
    public void testRouteCreation_ErrorPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route(Arrays.asList());
        }, "Creating a route with empty stops should throw an exception.");
    }
}