package tycoon.model;

public enum Direction {
    N(0, -1),
    E(1, 0),
    S(0, 1),
    W(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public Direction opposite() {
        return switch (this) {
            case N -> S;
            case E -> W;
            case S -> N;
            case W -> E;
        };
    }
}