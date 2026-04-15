package tycoon.model;

public class Industry extends Facility {
    private CargoType produces;
    private double productionBuffer = 0.0; 

    public Industry(Vector2 pos, double height, WorldMap map, String name, CargoType produces) {
        super(pos, height, map, name);
        this.produces = produces;
    }

    @Override
    public int load(CargoType type, int amount) {
        if (type != produces) return 0;
        return super.load(type, amount); 
    }

    @Override
    public void onTick(double dt) {

        productionBuffer += 2.0 * dt; 
        
        if (productionBuffer >= 1.0) {
            int producedThisTick = (int) productionBuffer;
            produce(produces, producedThisTick);
            productionBuffer -= producedThisTick; 
        }
    }
}