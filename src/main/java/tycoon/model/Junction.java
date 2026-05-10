package tycoon.model;
import java.io.Serializable;
/**
 * Represents a road intersection where multiple road segments meet.
 * Acts as a container for a TrafficLight, allowing infrastructure 
 * to regulate traffic flow at complex crossings.
 */
public class Junction  implements Serializable {
    private TrafficLight trafficLight;

    public Junction() {
        this.trafficLight = null;
    }

    public void install(TrafficLight light) {
        this.trafficLight = light;
    }

    public boolean hasLight() {
        return trafficLight != null;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }
}