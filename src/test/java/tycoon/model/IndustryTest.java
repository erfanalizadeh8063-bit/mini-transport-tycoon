package tycoon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IndustryTest {
    private Industry industry;
    private WorldMap dummyMap;
    private Vector2 dummyPos;

    @BeforeEach
    public void setUp() {
        dummyMap = new WorldMap(10, 10);
        dummyPos = new Vector2(0, 0);
        industry = new Industry(dummyPos, 0.0, dummyMap, "Test Lumber Mill", CargoType.WOOD);
    }

    @Test
    public void testProductionBuffer_NonTrivial() {
        // Initial state
        assertEquals(0, industry.getStockpile(CargoType.WOOD), "Initial stockpile should be 0.");


        industry.onTick(0.2);
        assertEquals(0, industry.getStockpile(CargoType.WOOD), "Should not produce cargo if production buffer is below 1.0.");

        industry.onTick(0.4);
        assertEquals(1, industry.getStockpile(CargoType.WOOD), "Should produce exactly 1 cargo item once buffer exceeds 1.0.");
    }

    @Test
    public void testLoadCargo_ErrorPath_WrongType() {
        industry.produce(CargoType.WOOD, 10);

        int loadedAmount = industry.load(CargoType.IRON_ORE, 5);

        assertEquals(0, loadedAmount, "Should refuse to load a cargo type that the facility does not provide.");
        assertEquals(10, industry.getStockpile(CargoType.WOOD), "Stockpile should remain untouched after a denied load operation.");
    }
}