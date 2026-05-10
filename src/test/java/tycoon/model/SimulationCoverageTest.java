
package tycoon.model;

import org.junit.jupiter.api.Test;
import tycoon.service.GameEngine;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class SimulationCoverageTest {

    @Test
    public void testFullSimulation_MegaCoverage() {
        WorldMap map = new WorldMap(5, 5);
        GameEngine engine = new GameEngine(map);

        City cityA = new City(new Vector2(0, 0), 0, map, "CityA", 100);
        City cityB = new City(new Vector2(3, 0), 0, map, "CityB", 100);

        RoadTile r0 = new RoadTile(new Vector2(0, 0), 0, map, 50);
        RoadTile r1 = new RoadTile(new Vector2(1, 0), 0, map, 50);
        RoadTile r2 = new RoadTile(new Vector2(2, 0), 0, map, 50);
        RoadTile r3 = new RoadTile(new Vector2(3, 0), 0, map, 50);

        r0.addConnection(Direction.E);
        r1.addConnection(Direction.W); r1.addConnection(Direction.E);
        r2.addConnection(Direction.W); r2.addConnection(Direction.E);
        r3.addConnection(Direction.W);

        map.setTile(0, 0, r0); map.setTile(1, 0, r1);
        map.setTile(2, 0, r2); map.setTile(3, 0, r3);

        cityA.setAccessTile(r0);
        cityB.setAccessTile(r3);

        r1.setJunction(new Junction());
        r1.getJunction().install(new TrafficLight());

        cityA.produce(CargoType.PASSENGER, 50);


        Vehicle bus = new CityBus("MEGA_BUS");
        bus.setRoute(new Route(Arrays.asList(cityA, cityB)));
        bus.setCurrentTile(r0);
        engine.addVehicle(bus);

        for (int i = 0; i < 100; i++) {
            engine.tick(0.1);
        }

        assertNotNull(bus.getCurrentTile(), "Bus should still be valid after driving.");
        assertTrue(engine.getBalance() > 0, "Economy should remain intact.");
    }
}