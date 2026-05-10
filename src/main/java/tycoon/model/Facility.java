package tycoon.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for all functional buildings (Industries, Cities) 
 * that act as transport points.
 * Manages an internal inventory of cargo and provides standardized 
 * methods for loading and unloading goods.
 */
public abstract class Facility extends Tile implements ITransportPoint {
    private String name;
    protected Map<CargoType, Integer> inventory;
    
    protected int maxCapacity = 500; 

    protected RoadTile accessTile;

    public Facility(Vector2 pos, double height, WorldMap map, String name) {
        super(pos, height, map); 
        this.name = name;
        this.inventory = new HashMap<>();
    }

  
    public void setAccessTile(RoadTile tile) {
        this.accessTile = tile;
    }

    @Override
    public RoadTile getAccessTile() {
        return accessTile;
    }

    @Override
    public int load(CargoType type, int requestedAmount) {
        int available = inventory.getOrDefault(type, 0);
        int amountToLoad = Math.min(available, requestedAmount);
        inventory.put(type, available - amountToLoad);
        return amountToLoad;
    }


    @Override
    public int unload(CargoType type, int amount) {
        int current = inventory.getOrDefault(type, 0);
        int spaceLeft = maxCapacity - current; 
        int actualUnloaded = Math.min(amount, spaceLeft); 
        
        inventory.put(type, current + actualUnloaded);
        return actualUnloaded;
    }

    protected void produce(CargoType type, int amount) {
        int current = inventory.getOrDefault(type, 0);
        if (current < maxCapacity) {
            inventory.put(type, Math.min(current + amount, maxCapacity));
        }
    }

   
    @Override
    public boolean isBuildable() {
        return false; 
    }

    @Override
    public double getBuildCostModifier() {
        return 1.0;
    }

    // --- Getters ---
    @Override
    public String getName() { return name; }
    
    public Map<CargoType, Integer> getInventory() { return inventory; }
    public int getStockpile(CargoType type) { return inventory.getOrDefault(type, 0); }
}