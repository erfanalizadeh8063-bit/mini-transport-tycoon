package tycoon.model;
import java.io.Serializable;
/**
 * Represents a 2D coordinate on the map grid.
 * Using Java record for automatic boilerplate (getters, equals, hashCode).
 */
public record Vector2(int x, int y) implements Serializable {
    /**
     * Adds another vector to this one.
     * @param other The vector to add.
     * @return A new Vector2 representing the sum.
     */
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }
}