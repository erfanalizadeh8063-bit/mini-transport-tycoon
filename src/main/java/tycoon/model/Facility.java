package tycoon.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a fixed facility (City, Industry) on the map.
 * Facilities take up space and manage an inventory of cargo.
 */
public abstract class Facility extends Tile {
    private String name;
    protected Map<CargoType, Integer> inventory;
    
    // Maximum capacity to prevent infinite cargo accumulation
    protected int maxCapacity = 500; 

    public Facility(Vector2 pos, double height, WorldMap map, String name) {
        super(pos, height, map); 
        this.name = name;
        this.inventory = new HashMap<>();
    }

    public int load(CargoType type, int requestedAmount) {
        int available = inventory.getOrDefault(type, 0);
        int amountToLoad = Math.min(available, requestedAmount);
        inventory.put(type, available - amountToLoad);
        return amountToLoad;
    }

    public void unload(CargoType type, int amount) {
        int current = inventory.getOrDefault(type, 0);
        inventory.put(type, Math.min(current + amount, maxCapacity));
    }

    protected void produce(CargoType type, int amount) {
        int current = inventory.getOrDefault(type, 0);
        if (current < maxCapacity) {
            inventory.put(type, Math.min(current + amount, maxCapacity));
        }
    }

    // --- Inherited from Tile ---
    @Override
    public boolean isBuildable() {
        return false; 
    }

    @Override
    public double getBuildCostModifier() {
        return 1.0;
    }

    // --- Getters ---
    public String getName() { return name; }
    public Map<CargoType, Integer> getInventory() { return inventory; }
    public int getStockpile(CargoType type) { return inventory.getOrDefault(type, 0); }
}