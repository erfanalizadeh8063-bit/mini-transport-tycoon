package tycoon.model;

/**
 * Represents a City that occupies a 3x3 area on the map.
 * Cities generate passengers over time and grow as they receive goods.
 */
public class City extends Facility {
    private double population;
    private double timeSinceLastGeneration;
    private static final double GENERATION_INTERVAL = 5.0; // Generate passengers every 5 seconds

    public City(Vector2 pos, double height, WorldMap map, String name, double initialPopulation) {
        super(pos, height, map, name);
        this.population = initialPopulation;
        this.timeSinceLastGeneration = 0.0;
        
        this.maxCapacity = 1000; 
    }

    @Override
    public void onTick(double dt) {
        // 1. Natural population growth
        population += dt * 0.01; 

        // 2. Passenger generation timer
        timeSinceLastGeneration += dt;
        
        if (timeSinceLastGeneration >= GENERATION_INTERVAL) {
            int newPassengers = (int) (population * 0.1);
            
            produce(CargoType.PASSENGERS, newPassengers);
            
            timeSinceLastGeneration = 0.0; 
        }
    }

    /**
     * Helper method for the UI Detail Panel.
     * Returns the population as a clean integer.
     */
    public int getDisplayPopulation() {
        return (int) population;
    }
}