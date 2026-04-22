package tycoon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoadTileTest {
    private RoadTile roadTile;
    private WorldMap dummyMap;
    private Vector2 dummyPos;

    @BeforeEach
    public void setUp() {
        dummyMap = new WorldMap(10, 10);
        dummyPos = new Vector2(5, 5);
        roadTile = new RoadTile(dummyPos, 0.0, dummyMap, 50.0);
        
        roadTile.addConnection(Direction.N);
        roadTile.addConnection(Direction.S);
        roadTile.addConnection(Direction.E);
        roadTile.addConnection(Direction.W);
        
        roadTile.setJunction(new Junction());
        roadTile.getJunction().install(new TrafficLight());
    }

    @Test
    public void testTrafficLight_CanEnter_Logic() {
        TrafficLight light = roadTile.getJunction().getTrafficLight();
        
        assertTrue(light.getPhase() == SignalPhase.NS_GREEN || light.getPhase() == SignalPhase.EW_GREEN, 
            "Traffic light must be initialized with a valid phase.");

        if (light.getPhase() == SignalPhase.NS_GREEN) {
            assertTrue(roadTile.canEnter(Direction.N), "Vehicles traveling North should be allowed during NS_GREEN.");
            assertFalse(roadTile.canEnter(Direction.E), "Vehicles traveling East should be blocked during NS_GREEN.");
        } else {
            assertTrue(roadTile.canEnter(Direction.E), "Vehicles traveling East should be allowed during EW_GREEN.");
            assertFalse(roadTile.canEnter(Direction.N), "Vehicles traveling North should be blocked during EW_GREEN.");
        }
    }

    @Test
    public void testVehicleCollision_Queueing() {
        Direction travelDir = Direction.N;
        
        Vehicle ghostCar = new SmallTruck("GHOST_01", CargoType.WOOD);
        roadTile.reserve(travelDir, ghostCar);

        boolean canEnter = roadTile.canEnter(travelDir);

        assertFalse(canEnter, "A vehicle should be blocked from entering a tile that is already occupied in the same direction, preventing clipping.");
    }
}