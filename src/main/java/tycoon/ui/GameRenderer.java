package tycoon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import tycoon.model.*;
import java.util.List;

public class GameRenderer {
    private static final int TILE_SIZE = 64;

    private Image grassImg;
    private Image cityImg;
    private Image factoryImg;
    private Image treeImg; 
    
    private Image busImg; 
    private Image truckImg; 

    public GameRenderer() {
        try {
            grassImg = new Image(getClass().getResourceAsStream("/grass.png"));
            cityImg = new Image(getClass().getResourceAsStream("/city.jpg"));
            factoryImg = new Image(getClass().getResourceAsStream("/factory.png"));
            treeImg = new Image(getClass().getResourceAsStream("/tree.png")); 
            
            busImg = new Image(getClass().getResourceAsStream("/bus.png")); 
            truckImg = new Image(getClass().getResourceAsStream("/truck.png")); 
        } catch (Exception e) {
            System.err.println("Error loading sprites: " + e.getMessage());
        }
    }

    public void render(GraphicsContext gc, WorldMap map, List<Vehicle> vehicles) {
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, map.getWidth() * TILE_SIZE, map.getHeight() * TILE_SIZE);

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                drawBaseTile(gc, map.getTile(x, y), x, y);
            }
        }

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                drawBuildingTile(gc, map.getTile(x, y), x, y);
            }
        }

        for (Vehicle v : vehicles) {
            drawVehicle(gc, v);
        }
    }

    private void drawBaseTile(GraphicsContext gc, Tile tile, int x, int y) {
        int px = x * TILE_SIZE;
        int py = y * TILE_SIZE;

        if (grassImg != null && !grassImg.isError()) {
            gc.drawImage(grassImg, px, py, TILE_SIZE, TILE_SIZE);
        } else {
            gc.setFill(Color.web("#91cf60"));
            gc.fillRect(px, py, TILE_SIZE, TILE_SIZE);
        }

        if (tile instanceof City || tile instanceof Industry) {
            return;
        }

        if (tile instanceof EmptyTile) {
            int trees = ((EmptyTile) tile).getTreeCount();
            if (trees > 0 && treeImg != null && !treeImg.isError()) {
                gc.drawImage(treeImg, px + 10, py + 10, TILE_SIZE - 20, TILE_SIZE - 20);
            }
        }

        if (tile instanceof RoadTile road) {
            drawRoadTile(gc, road, px, py);
        }

        gc.setStroke(Color.web("#000000", 0.05));
        gc.strokeRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void drawRoadTile(GraphicsContext gc, RoadTile road, int px, int py) {
        double center = TILE_SIZE / 2.0;
        double roadWidth = TILE_SIZE * 0.62;
        double halfRoad = roadWidth / 2.0;
        double laneDash = TILE_SIZE * 0.18;
        double laneGap = TILE_SIZE * 0.10;

        gc.setFill(Color.web("#7a7a7a"));

        if (road.getConnectionCount() == 0) {
            gc.fillRect(px + center - halfRoad, py + center - halfRoad, roadWidth, roadWidth);
            return;
        }

        gc.fillRect(px + center - halfRoad, py + center - halfRoad, roadWidth, roadWidth);

        if (road.isConnected(Direction.N)) {
            gc.fillRect(px + center - halfRoad, py, roadWidth, center);
        }
        if (road.isConnected(Direction.S)) {
            gc.fillRect(px + center - halfRoad, py + center, roadWidth, center);
        }
        if (road.isConnected(Direction.W)) {
            gc.fillRect(px, py + center - halfRoad, center, roadWidth);
        }
        if (road.isConnected(Direction.E)) {
            gc.fillRect(px + center, py + center - halfRoad, center, roadWidth);
        }

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        if (road.isConnected(Direction.N)) {
            gc.strokeLine(px + center, py, px + center, py + center - halfRoad);
        }
        if (road.isConnected(Direction.S)) {
            gc.strokeLine(px + center, py + center + halfRoad, px + center, py + TILE_SIZE);
        }
        if (road.isConnected(Direction.W)) {
            gc.strokeLine(px, py + center, px + center - halfRoad, py + center);
        }
        if (road.isConnected(Direction.E)) {
            gc.strokeLine(px + center + halfRoad, py + center, px + TILE_SIZE, py + center);
        }

        if (road.getConnectionCount() >= 2) {
            gc.strokeLine(px + center - laneDash / 2, py + center, px + center + laneDash / 2, py + center);
            gc.strokeLine(px + center, py + center - laneDash / 2, px + center, py + center + laneDash / 2);
        }

        if (road.hasJunction()) {
            gc.setStroke(Color.GOLD);
            gc.setLineWidth(2.5);
            gc.strokeRect(px + center - halfRoad + 4, py + center - halfRoad + 4, roadWidth - 8, roadWidth - 8);

            if (road.getJunction().hasLight()) {
                TrafficLight light = road.getJunction().getTrafficLight();

                double boxW = 20;
                double boxH = 14;
                double boxX = px + center - boxW / 2;
                double boxY = py + center - boxH / 2;

                gc.setFill(Color.BLACK);
                gc.fillRoundRect(boxX, boxY, boxW, boxH, 4, 4);

                Color nsColor = (light.getPhase() == SignalPhase.NS_GREEN) ? Color.LIMEGREEN : Color.RED;
                Color ewColor = (light.getPhase() == SignalPhase.EW_GREEN) ? Color.LIMEGREEN : Color.RED;

                gc.setFill(nsColor);
                gc.fillOval(px + center - 3, py + center - 6, 6, 6);

                gc.setFill(ewColor);
                gc.fillOval(px + center + 3, py + center, 6, 6);
            }
        }
    }

    private void drawBuildingTile(GraphicsContext gc, Tile tile, int x, int y) {
        int px = x * TILE_SIZE;
        int py = y * TILE_SIZE;

        if (tile instanceof City) {
            Vector2 origin = tile.getPos();
            if (origin.x() == x && origin.y() == y) {
                if (cityImg != null && !cityImg.isError()) {
                    gc.drawImage(cityImg, px, py, TILE_SIZE * 3, TILE_SIZE * 3);
                } else {
                    gc.setFill(Color.DARKSLATEGRAY);
                    gc.fillRect(px, py, TILE_SIZE * 3, TILE_SIZE * 3);
                }
            }
        } else if (tile instanceof Industry) {
            Vector2 origin = tile.getPos();
            if (origin.x() == x && origin.y() == y) {
                if (factoryImg != null && !factoryImg.isError()) {
                    gc.drawImage(factoryImg, px, py, TILE_SIZE * 2, TILE_SIZE * 2);
                } else {
                    gc.setFill(Color.SADDLEBROWN);
                    gc.fillRect(px, py, TILE_SIZE * 2, TILE_SIZE * 2);
                }
            }
        }
    }

    private void drawVehicle(GraphicsContext gc, Vehicle v) {
        RoadTile start = v.getCurrentTile();
        RoadTile end = v.getTargetTile();
        if (start == null)
            return;

        double drawX = start.getPos().x() * TILE_SIZE;
        double drawY = start.getPos().y() * TILE_SIZE;

        if (end != null) {
            double endX = end.getPos().x() * TILE_SIZE;
            double endY = end.getPos().y() * TILE_SIZE;
            double p = v.getProgress();
            drawX += (endX - drawX) * p;
            drawY += (endY - drawY) * p;
        }

        if (v instanceof Truck) {
            if (truckImg != null && !truckImg.isError()) {
                gc.drawImage(truckImg, drawX + TILE_SIZE*0.15, drawY + TILE_SIZE*0.15, TILE_SIZE*0.7, TILE_SIZE*0.7);
            } else {
                gc.setFill(Color.web("#FF9AA2")); 
                gc.fillOval(drawX + TILE_SIZE * 0.2, drawY + TILE_SIZE * 0.2, TILE_SIZE * 0.6, TILE_SIZE * 0.6);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(3);
                gc.strokeOval(drawX + TILE_SIZE * 0.2, drawY + TILE_SIZE * 0.2, TILE_SIZE * 0.6, TILE_SIZE * 0.6);
            }
        } else if (v instanceof Bus) {
            if (busImg != null && !busImg.isError()) {
                gc.drawImage(busImg, drawX + TILE_SIZE*0.15, drawY + TILE_SIZE*0.15, TILE_SIZE*0.7, TILE_SIZE*0.7);
            } else {
                gc.setFill(Color.web("#A8D8EA")); 
                gc.fillOval(drawX + TILE_SIZE * 0.2, drawY + TILE_SIZE * 0.2, TILE_SIZE * 0.6, TILE_SIZE * 0.6);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(3);
                gc.strokeOval(drawX + TILE_SIZE * 0.2, drawY + TILE_SIZE * 0.2, TILE_SIZE * 0.6, TILE_SIZE * 0.6);
            }
        }
    }
}