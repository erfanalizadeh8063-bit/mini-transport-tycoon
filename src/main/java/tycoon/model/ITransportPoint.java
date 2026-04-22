package tycoon.model;

/**
 * Interface for any point on the map that can handle cargo or passengers.
 * Both City and Industry implement this to allow universal vehicle interaction.
 */
public interface ITransportPoint {
    String getName();

    RoadTile getAccessTile();
    /**
     * Attempts to load a specific amount of cargo from this point onto a vehicle.
     * @param type The type of cargo (e.g., PASSENGERS, GOODS_A)[cite: 30].
     * @param amount The maximum amount the vehicle can take.
     * @return The actual amount successfully loaded.
     */
    int load(CargoType type, int amount);

    /**
     * Attempts to unload a specific amount of cargo from a vehicle to this point.
     * @param type The type of cargo being delivered[cite: 31].
     * @param amount The amount being unloaded.
     * @return The actual amount successfully received by the facility.
     */
    int unload(CargoType type, int amount);
}