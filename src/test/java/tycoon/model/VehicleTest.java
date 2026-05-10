package tycoon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tycoon.service.GameEngine;
import tycoon.service.PathFinder;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {
    private WorldMap dummyMap;
    private GameEngine engine;
    private PathFinder pathFinder;

    @BeforeEach
    public void setUp() {
        dummyMap = new WorldMap(10, 10);
        engine = new GameEngine(dummyMap);
        pathFinder = new PathFinder(dummyMap);
    }

    @Test
    public void testVehicleSubclasses_Initialization() {
        Vehicle cityBus = new CityBus("BUS_01");
        Vehicle coach = new Coach("COACH_01");
        Vehicle heavyTruck = new HeavyTruck("HEAVY_01", CargoType.STEEL);
        Vehicle smallTruck = new SmallTruck("SMALL_01", CargoType.WOOD);

        assertEquals("BUS_01", cityBus.getId(), "CityBus ID should match.");
        assertEquals(CargoType.PASSENGER, coach.getCargoType(), "Coach should carry passengers.");
        assertEquals(CargoType.STEEL, heavyTruck.getCargoType(), "HeavyTruck should carry the assigned cargo type.");
        assertEquals(CargoType.WOOD, smallTruck.getCargoType(), "SmallTruck should carry the assigned cargo type.");
    }

    @Test
    public void testVehicleRoutingAndTick() {
        City cityA = new City(new Vector2(0, 0), 0, dummyMap, "City A", 100);
        City cityB = new City(new Vector2(2, 0), 0, dummyMap, "City B", 100);
        
        RoadTile roadA = new RoadTile(new Vector2(0, 0), 0, dummyMap, 50);
        RoadTile roadB = new RoadTile(new Vector2(2, 0), 0, dummyMap, 50);
        cityA.setAccessTile(roadA);
        cityB.setAccessTile(roadB);

        Vehicle bus = new CityBus("ROUTE_BUS");
        Route route = new Route(Arrays.asList(cityA, cityB));
        bus.setRoute(route);
        bus.setCurrentTile(roadA);

        assertNotNull(bus.getRoute(), "Vehicle should successfully store its route.");
        assertEquals(roadA, bus.getCurrentTile(), "Vehicle should be located at its starting tile.");

        bus.update(1.0, pathFinder, engine);
        
        assertTrue(bus.getSpeed() >= 0, "Vehicle speed should not be negative after an update.");
        Vehicle normalBus = new Bus("BUS_NORMAL");
        assertEquals("BUS_NORMAL", normalBus.getId(), "Normal Bus ID should match.");
    }
}