package tycoon.service;

import tycoon.model.WorldMap;
import java.io.*;

public class SaveManager {
    public static boolean saveGame(WorldMap map, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(map);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static WorldMap loadGame(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (WorldMap) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}