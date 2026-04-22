package tycoon.service;

import tycoon.model.WorldMap;
import java.io.*;

public class SaveManager {
    public static boolean saveGameData(WorldMap map, GameEngine engine, double time, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new Object[]{ map, engine, time });
            System.out.println("💾 Game saved successfully!");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving game:");
            e.printStackTrace();
            return false;
        }
    }

    public static Object[] loadGameData(String filename) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Object[]) ois.readObject();
        }
    }
}