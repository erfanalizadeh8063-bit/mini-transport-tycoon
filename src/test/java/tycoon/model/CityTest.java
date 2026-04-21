package tycoon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CityTest {
    private City city;
    private WorldMap dummyMap;
    private Vector2 dummyPos;

    @BeforeEach
    public void setUp() {
        dummyMap = new WorldMap(10, 10);
        dummyPos = new Vector2(3, 3);
        city = new City(dummyPos, 0.0, dummyMap, "Test City", 1000.0);
    }

    @Test
    public void testPopulationGrowth_SlowIncrease() {
        assertEquals(1000, city.getDisplayPopulation(), "Initial population should be exactly 1000.");

        city.onTick(100.0);
        
        assertEquals(1001, city.getDisplayPopulation(), "Population should exhibit slow, continuous growth over time.");
    }

    @Test
    public void testPassengerGeneration_IntervalLogic() {
        city.onTick(4.9);
        
        assertEquals(0, city.getStockpile(CargoType.PASSENGER), "City should not generate passengers before the 5.0 second interval.");

        city.onTick(0.2);
        
        int generatedPassengers = city.getStockpile(CargoType.PASSENGER);
        assertTrue(generatedPassengers >= 100, "City should generate approximately 10% of its population as passengers once the interval is reached.");
    }
}