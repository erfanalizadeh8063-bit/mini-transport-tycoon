package tycoon.model;

import java.util.List;




/**
 * Manages a circular list of transport points for a vehicle to follow.
 */
public class Route {
    private List<ITransportPoint> stops; // UML: stops: List
    private int currentIndex;            // UML: index: int

    public Route(List<ITransportPoint> stops) {
        this.stops = stops;
        this.currentIndex = 0;
    }

    /**
     * Returns the current target destination.
     */
    public ITransportPoint getCurrentTarget() {
        if (stops.isEmpty()) return null;
        return stops.get(currentIndex);
    }

    /**
     * Advances the route to the next stop in a circle.
     */
    public void advance() {
        if (stops.isEmpty()) return;
        currentIndex = (currentIndex + 1) % stops.size();
    }
}