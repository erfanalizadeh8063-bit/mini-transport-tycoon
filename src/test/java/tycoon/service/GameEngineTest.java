package tycoon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tycoon.model.WorldMap;
import tycoon.model.Vehicle;
import tycoon.model.SmallTruck;
import tycoon.model.CargoType;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {
    private GameEngine engine;
    private WorldMap dummyMap;

    @BeforeEach
    public void setUp() {
        dummyMap = new WorldMap(10, 10);
        engine = new GameEngine(dummyMap); 
    }

    @Test
    public void testSpendMoney_HappyPath() {
        boolean success = engine.spendMoney(500.0);
        
        assertTrue(success, "Engine should allow spending 500 when having sufficient funds.");
        assertEquals(9500.0, engine.getBalance(), 0.001, "Balance should be deducted by 500, resulting in 9500.");
    }

    @Test
    public void testSpendMoney_ErrorPath_InsufficientFunds() {
        boolean success = engine.spendMoney(20000.0);
        
        assertFalse(success, "Transaction should be denied due to insufficient funds.");
        assertEquals(10000.0, engine.getBalance(), 0.001, "Balance should remain unchanged after a failed transaction.");
    }

    @Test
    public void testBankrupt_EdgeCase() {
        assertFalse(engine.isBankrupt(), "Engine should not be bankrupt in the initial state.");
        engine.spend(10001.0);
        assertTrue(engine.isBankrupt(), "Engine should report bankruptcy when balance falls below zero.");
    }

    @Test
    public void testEngineCoreLoop_TickAndVehicles() {
        Vehicle testTruck = new SmallTruck("TRUCK_TEST", CargoType.WOOD);
        engine.addVehicle(testTruck);
        
        assertFalse(engine.getVehicles().isEmpty(), "Engine should successfully store added vehicles.");
        assertEquals(1, engine.getVehicles().size(), "Engine vehicle list size should be 1.");

        engine.setSimulationSpeed(2.0);
        assertEquals(2.0, engine.getSimulationSpeed(), "Engine should correctly update simulation speed.");


        assertDoesNotThrow(() -> {
            engine.tick(0.5); 
        }, "Engine tick should execute smoothly without throwing exceptions.");
    }
}