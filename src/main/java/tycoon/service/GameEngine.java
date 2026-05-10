package tycoon.service;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import tycoon.model.*; 
/**
 * The core controller of the game simulation.
 * Manages the game economy (balance), the list of active vehicles, 
 * and the simulation clock (tick system).
 */
public class GameEngine implements Serializable {
    private WorldMap worldMap;
    private List<Vehicle> vehicles;
    private double simulationSpeed = 1.0; 
    private double balance = 10000.0;     
    

    private PathFinder pathFinder;

    public GameEngine(WorldMap map) {
        this.worldMap = map;
        this.vehicles = new ArrayList<>();
        this.pathFinder = new PathFinder(map);
    }


    public void tick(double dt) {
        if (simulationSpeed == 0) return;

        double effectiveDt = dt * simulationSpeed;

        for (int x = 0; x < worldMap.getWidth(); x++) {
            for (int y = 0; y < worldMap.getHeight(); y++) {
                worldMap.getTile(x, y).onTick(effectiveDt);
            }
        }

        double dailyMaintenance = 0;
        for (int i = vehicles.size() - 1; i >= 0; i--) {
            Vehicle v = vehicles.get(i);
            v.update(effectiveDt, pathFinder, this);

            if (v instanceof SmallTruck) {
                dailyMaintenance += 2.0 * effectiveDt;
            } else if (v instanceof HeavyTruck) {
                dailyMaintenance += 8.0 * effectiveDt;
            } else if (v instanceof CityBus) {
                dailyMaintenance += 4.0 * effectiveDt;
            } else if (v instanceof Coach) {
                dailyMaintenance += 10.0 * effectiveDt;
            } else {
                dailyMaintenance += 5.0 * effectiveDt; 
            }
        }
        
        this.balance -= dailyMaintenance;
    }

    public boolean isBankrupt() {
        return balance < 0;
    }


    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void spend(double amount) {
        this.balance -= amount;
    }

    public void earn(double amount) {
        this.balance += amount;
    }

   
    public boolean canAfford(double amount) {
        return this.balance >= amount;
    }

    public boolean spendMoney(double amount) {
        if (canAfford(amount)) {
            spend(amount);
            return true;
        }
        return false;
    }

    public void setSimulationSpeed(double speed) {
        this.simulationSpeed = speed;
    }

    public void setSpeed(double speed) { 
        this.simulationSpeed = speed; 
    }

    public double getSimulationSpeed() {
        return simulationSpeed;
    }

    public void addVehicle(Vehicle v) { 
        this.vehicles.add(v); 
    }

    public double getBalance() { 
        return balance; 
    }
}