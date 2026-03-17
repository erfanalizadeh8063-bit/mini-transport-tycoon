package tycoon.ui;
import tycoon.model.City;
import tycoon.model.Tile;
import tycoon.model.Industry;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tycoon.model.*;
import java.util.List;

public class GameRenderer {
    private static final int TILE_SIZE = 40;

    public void render(GraphicsContext gc, WorldMap map, List<Vehicle> vehicles) {
        // 1. Clear background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, map.getWidth() * TILE_SIZE, map.getHeight() * TILE_SIZE);

        // 2. Draw Map Tiles
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                drawTile(gc, map.getTile(x, y), x, y);
            }
        }

        // 3. Draw Vehicles
        for (Vehicle v : vehicles) {
            drawVehicle(gc, v);
        }
    }

    private void drawTile(GraphicsContext gc, Tile tile, int x, int y) {
        int px = x * TILE_SIZE;
        int py = y * TILE_SIZE;

        // Logic based on Tile type
        if (tile instanceof RoadTile) {
            gc.setFill(Color.GRAY);
        } else if (tile instanceof City) {
            gc.setFill(Color.web("#a1a1a1")); // City foundation color
        } else if (tile instanceof Industry) {
            gc.setFill(Color.web("#8b4513")); // Industry/Factory color
        } else {
            gc.setFill(Color.LIGHTGREEN); // Grass
        }

        gc.fillRect(px, py, TILE_SIZE, TILE_SIZE);
        
        // Draw grid lines
        gc.setStroke(Color.web("#000000", 0.1));
        gc.setLineWidth(1.0);
        gc.strokeRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void drawVehicle(GraphicsContext gc, Vehicle v) {
        // Use currentTile and targetTile to interpolate position
        RoadTile start = v.getCurrentTile();
        RoadTile end = v.getTargetTile(); // Ensure targetTile is public or has getter

        if (start == null) return;

        double startX = start.getPos().x() * TILE_SIZE;
        double startY = start.getPos().y() * TILE_SIZE;

        double drawX, drawY;

        if (end != null) {
            // Smooth interpolation between start and end tile
            double endX = end.getPos().x() * TILE_SIZE;
            double endY = end.getPos().y() * TILE_SIZE;
            double p = v.getProgress(); // Value from 0.0 to 1.0

            drawX = startX + (endX - startX) * p;
            drawY = startY + (endY - startY) * p;
        } else {
            drawX = startX;
            drawY = startY;
        }

        // Draw the vehicle (Blue circle with a border)
        gc.setFill(Color.BLUE);
        gc.fillOval(drawX + 8, drawY + 8, 24, 24);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(drawX + 8, drawY + 8, 24, 24);
    }
}