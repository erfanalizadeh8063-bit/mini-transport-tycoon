package tycoon.model;

public class Industry extends Facility {
    private CargoType produces;

   
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
        
        int producedThisTick = (int)(1 * dt); 
        if (producedThisTick > 0) {
            produce(produces, producedThisTick);
        }
    }
}