package tycoon.model;
import java.io.Serializable;
public class Junction  implements Serializable {
    private TrafficLight trafficLight;

    public Junction() {
        this.trafficLight = null;
    }

    public void installTrafficLight(TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
    }

    public boolean hasLight() {
        return trafficLight != null;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }
}