package tycoon.service;

import java.util.List;

import tycoon.model.Vehicle;
import tycoon.model.WorldMap;

import java.util.ArrayList;

/**
 * Orchestrates the simulation logic and time management.
 */
public class GameEngine {
    private WorldMap worldMap;
    private List<Vehicle> vehicles;
    private double simulationSpeed = 1.0; 
    private double balance = 10000.0;     

    public GameEngine(WorldMap map) {
        this.worldMap = map;
        this.vehicles = new ArrayList<>();
    }

    /**
     * The main loop call. dt is the real-world time elapsed.
     */
    public void tick(double dt) {
        if (simulationSpeed == 0) return; // Paused

        double effectiveDt = dt * simulationSpeed;

        // 1. Update all tiles (for industry production and city demand)
        for (int x = 0; x < worldMap.getWidth(); x++) {
            for (int y = 0; y < worldMap.getHeight(); y++) {
                worldMap.getTile(x, y).onTick(effectiveDt);
            }
        }

        // 2. Update all vehicles (movement and cargo handling)
        // 这样写最安全，即使在 update() 里面删除了车辆也不会崩溃
        for (int i = vehicles.size() - 1; i >= 0; i--) {
            vehicles.get(i).update(effectiveDt);
        }
        
        // 3. Check for bankruptcy
        if (balance < 0) {
            // In a real app, you might trigger a Game Over screen here
            // System.out.println("Game Over: Bankrupt!");
        }
    }

    // --- NEW METHODS FOR UI INTEGRATION ---

    /**
     * Required by GameWindow to render vehicles on the canvas.
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * Spend money (e.g., when building roads or buying vehicles).
     */
    public void spend(double amount) {
        this.balance -= amount;
    }

    /**
     * Earn money (e.g., when delivering cargo).
     */
    public void earn(double amount) {
        this.balance += amount;
    }

    /**
     * Check if the player can afford a specific cost.
     */
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

    // --- GETTERS & SETTERS ---
    /**
     * 对应 GameWindow 里的 engine.setSimulationSpeed(倍率)
     */
    public void setSimulationSpeed(double speed) {
        this.setSpeed(speed);
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