package tycoon.model;

import java.io.Serializable;
/**
 * Represents a traffic light system at a junction.
 * Manages signal phases (North-South vs East-West) and automatically
 * switches phases based on configurable green-light durations.
 */
public class TrafficLight implements Serializable {
    private SignalPhase phase;
    private double greenNS;
    private double greenEW;
    private double timer;

    public TrafficLight() {
        this.phase = SignalPhase.NS_GREEN;
        this.greenNS = 5.0;
        this.greenEW = 5.0;
        this.timer = 0.0;
    }

    public void switchPhase() {
        phase = (phase == SignalPhase.NS_GREEN)
                ? SignalPhase.EW_GREEN
                : SignalPhase.NS_GREEN;
        timer = 0.0;
    }

    public boolean canPass(Direction dir) {
        if (dir == null) {
            return false;
        }

        return switch (phase) {
            case NS_GREEN -> dir == Direction.N || dir == Direction.S;
            case EW_GREEN -> dir == Direction.E || dir == Direction.W;
        };
    }

    public void setTimings(double ns, double ew) {
        this.greenNS = ns;
        this.greenEW = ew;
    }

    public void update(double dt) {
        timer += dt;

        double limit = (phase == SignalPhase.NS_GREEN) ? greenNS : greenEW;
        if (timer >= limit) {
            switchPhase();
        }
    }

    public SignalPhase getPhase() {
        return phase;
    }

    public double getGreenNS() {
        return greenNS;
    }

    public double getGreenEW() {
        return greenEW;
    }
}