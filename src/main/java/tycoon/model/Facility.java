package tycoon.model;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Base class for Cities and Industrial facilities.
 * Implements ITransportPoint for cargo interaction.
 */
public abstract class Facility extends Tile implements ITransportPoint {
    protected String name;
    protected List<Vector2> occupiedTiles;
    protected Map<CargoType, Integer> storage;

    public Facility(String name, List<Vector2> occupiedTiles, WorldMap map) {
        super(occupiedTiles.get(0), 0, map); 
        
        this.name = name;
        this.occupiedTiles = occupiedTiles;
        this.storage = new HashMap<>();
    }

    public String getName() { return name; }
    public List<Vector2> getOccupiedTiles() { return occupiedTiles; }

    @Override
    public abstract int load(CargoType type, int amount);

    @Override
    public abstract int unload(CargoType type, int amount);
    
    @Override
    public void onTick(double dt) {
        //basic Logic
    }
}