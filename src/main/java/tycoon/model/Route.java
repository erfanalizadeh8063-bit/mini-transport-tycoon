package tycoon.model;

import java.util.List;
import java.io.Serializable;
/**
 * Defines an ordered sequence of transport points for a vehicle to follow.
 * Provides logic to track the current target and advance to the next 
 * stop in a circular loop once a destination is reached.
 */
public class Route implements Serializable {
    private List<ITransportPoint> stops; 
    private int currentIndex;            

    public Route(List<ITransportPoint> stops) {

        if (stops == null || stops.isEmpty()) {
            throw new IllegalArgumentException("Route must contain at least one stop.");
        }
        this.stops = stops;
        this.currentIndex = 0;
    }

    public ITransportPoint getCurrentTarget() {
        if (stops.isEmpty()) return null;
        return stops.get(currentIndex);
    }


    public RoadTile getCurrentTargetTile() {
        ITransportPoint target = getCurrentTarget();
        return (target != null) ? target.getAccessTile() : null;
    }

    
    public void advance() {
        if (stops.isEmpty()) return;
        currentIndex = (currentIndex + 1) % stops.size();
    }

    public int getStopCount() {
        return stops.size();
    }
    
    public List<ITransportPoint> getStops() {
        return stops;
    }
}