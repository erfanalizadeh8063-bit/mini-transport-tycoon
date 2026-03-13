package tycoon.model;

public class TrafficLight {
    private SignalPhase state;

    public TrafficLight() {
        this.state = SignalPhase.NS_GREEN;
    }

    public SignalPhase getState() {
        return state;
    }

    public void setState(SignalPhase state) {
        this.state = state;
    }

    public void switchState() {
        if (state == SignalPhase.NS_GREEN) {
            state = SignalPhase.EW_GREEN;
        } else {
            state = SignalPhase.NS_GREEN;
        }
    }

    public boolean canPass(Direction direction) {
        if (state == SignalPhase.NS_GREEN) {
            return direction == Direction.N || direction == Direction.S;
        } else {
            return direction == Direction.E || direction == Direction.W;
        }
    }
}