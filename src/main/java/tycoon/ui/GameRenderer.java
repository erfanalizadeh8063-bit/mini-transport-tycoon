package tycoon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tycoon.model.*;
import java.util.List;

public class GameRenderer {
    private static final int TILE_SIZE = 64; // 与 Window 保持一致

    public void render(GraphicsContext gc, WorldMap map, List<Vehicle> vehicles) {
        gc.setFill(Color.LIGHTBLUE); //  배경
        gc.fillRect(0, 0, map.getWidth() * TILE_SIZE, map.getHeight() * TILE_SIZE);

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                drawTile(gc, map.getTile(x, y), x, y);
            }
        }

        for (Vehicle v : vehicles) {
            drawVehicle(gc, v);
        }
    }

    private void drawTile(GraphicsContext gc, Tile tile, int x, int y) {
        int px = x * TILE_SIZE;
        int py = y * TILE_SIZE;

        if (tile instanceof RoadTile) {
            gc.setFill(Color.GRAY);
        } else if (tile instanceof City) {
            gc.setFill(Color.DARKSLATEGRAY);
        } else if (tile instanceof Industry) {
            gc.setFill(Color.SADDLEBROWN);
        } else {
            gc.setFill(Color.web("#91cf60")); 
        }

        gc.fillRect(px, py, TILE_SIZE, TILE_SIZE);
        if (tile instanceof RoadTile roadTile && roadTile.hasJunction()) {
    if (roadTile.getJunction().hasLight()) {
        if (roadTile.getJunction().getTrafficLight().getState() == SignalPhase.NS_GREEN) {
            gc.setFill(Color.LIMEGREEN);
        } else {
            gc.setFill(Color.ORANGE);
        }
    } else {
        gc.setFill(Color.RED);
    }

    gc.fillOval(px + TILE_SIZE * 0.3, py + TILE_SIZE * 0.3, TILE_SIZE * 0.4, TILE_SIZE * 0.4);
    }
        
        
        gc.setStroke(Color.web("#000000", 0.05));
        gc.strokeRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void drawVehicle(GraphicsContext gc, Vehicle v) {
        RoadTile start = v.getCurrentTile();
        RoadTile end = v.getTargetTile();
        if (start == null) return;

        double drawX = start.getPos().x() * TILE_SIZE;
        double drawY = start.getPos().y() * TILE_SIZE;

        if (end != null) {
            double endX = end.getPos().x() * TILE_SIZE;
            double endY = end.getPos().y() * TILE_SIZE;
            double p = v.getProgress();
            drawX += (endX - drawX) * p;
            drawY += (endY - drawY) * p;
        }

        gc.setFill(Color.YELLOW);
        gc.fillOval(drawX + TILE_SIZE*0.2, drawY + TILE_SIZE*0.2, TILE_SIZE*0.6, TILE_SIZE*0.6);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(drawX + TILE_SIZE*0.2, drawY + TILE_SIZE*0.2, TILE_SIZE*0.6, TILE_SIZE*0.6);
    }
}