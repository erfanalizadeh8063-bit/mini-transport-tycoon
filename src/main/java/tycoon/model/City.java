package tycoon.model;


public class City extends Facility {
    private double population;
    private double timeSinceLastGeneration;
    private static final double GENERATION_INTERVAL = 5.0;

    public City(Vector2 pos, double height, WorldMap map, String name, double initialPopulation) {
        super(pos, height, map, name);
        this.population = initialPopulation;
        this.timeSinceLastGeneration = 0.0;
        
        this.maxCapacity = 1000; 
    }

    @Override
    public void onTick(double dt) {

        population += dt * 0.01; 


        timeSinceLastGeneration += dt;
        
        if (timeSinceLastGeneration >= GENERATION_INTERVAL) {
            int newPassengers = (int) (population * 0.1);
            
            produce(CargoType.PASSENGER, newPassengers);
            
            timeSinceLastGeneration = 0.0; 
        }
    }


    public int getDisplayPopulation() {
        return (int) population;
    }
}