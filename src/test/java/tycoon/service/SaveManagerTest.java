package tycoon.service;

import org.junit.jupiter.api.Test;
import tycoon.model.WorldMap;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class SaveManagerTest {

    @Test
    public void testSaveAndLoadGame_HappyPath() {
        WorldMap map = new WorldMap(10, 10);
        GameEngine engine = new GameEngine(map);
        double simulatedTime = 42.5;
        
        // Use a temporary file name to avoid cluttering the real game directory
        String testFilename = "test_coverage_save.dat";

        // Test Saving
        boolean saveResult = SaveManager.saveGameData(map, engine, simulatedTime, testFilename);
        assertTrue(saveResult, "SaveManager should successfully save the game data to a file.");

        // Test Loading
        try {
            Object[] loadedData = SaveManager.loadGameData(testFilename);
            assertNotNull(loadedData, "Loaded data array should not be null.");
            assertEquals(3, loadedData.length, "Loaded data should contain exactly 3 elements (map, engine, time).");
            
            double loadedTime = (Double) loadedData[2];
            assertEquals(simulatedTime, loadedTime, 0.001, "The loaded time should match the saved time.");
        } catch (Exception e) {
            fail("Loading game data threw an unexpected exception: " + e.getMessage());
        } finally {
            // Clean up the test file so we don't leave trash behind
            File file = new File(testFilename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Test
    public void testSaveGame_ErrorPath() {
        WorldMap map = new WorldMap(10, 10);
        GameEngine engine = new GameEngine(map);
        
        // Attempt to save to an illegal path (simulating a crash/error)
        // Question marks are invalid filename characters in Windows
        boolean saveResult = SaveManager.saveGameData(map, engine, 0.0, "invalid/path/????.dat");
        
        assertFalse(saveResult, "SaveManager should catch the exception and return false for invalid paths.");
    }
}