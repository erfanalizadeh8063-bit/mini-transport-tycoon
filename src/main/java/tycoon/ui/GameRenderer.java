package tycoon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import tycoon.model.*;
import java.util.List;
/**
 * Responsible for drawing the game world and its entities onto a JavaFX Canvas.
 * Manages sprite loading and renders tiles (Forest, Road, Buildings) and 
 * vehicles with smooth interpolation based on their current progress.
 */
public class GameRenderer {
    private static final int TILE_SIZE = 64;

    private static final double[][] TREE_POSITIONS = {
        {0.12, 0.12},
        {0.55, 0.10},
        {0.10, 0.55},
        {0.55, 0.55}
    };
    private static final double[] TREE_SIZES  = { 0.36, 0.30, 0.28, 0.26 };
    private static final Color[]  TREE_COLORS = {
        Color.web("#2d6a2d"),
        Color.web("#3a7a3a"),
        Color.web("#4a8a4a"),
        Color.web("#5a9a5a")
    };
    private static final Color[] TRUNK_COLORS = {
        Color.web("#6b3a2a"),
        Color.web("#7a4a3a"),
        Color.web("#8a5a4a"),
        Color.web("#9a6a5a")
    };

    private Image grassImg;
    private Image cityImg;
    private Image factoryImg;
    private Image treeImg;
    private Image roadImg;
    private Image smallTruckImg;
    private Image heavyTruckImg;
    private Image cityBusImg;
    private Image coachImg;

    public GameRenderer() {
        try {
            grassImg      = new Image(getClass().getResourceAsStream("/grass.png"));
            cityImg       = new Image(getClass().getResourceAsStream("/city.jpg"));
            factoryImg    = new Image(getClass().getResourceAsStream("/factory.png"));
            treeImg       = new Image(getClass().getResourceAsStream("/tree.png"));
            roadImg       = new Image(getClass().getResourceAsStream("/road.png"));
            smallTruckImg = new Image(getClass().getResourceAsStream("/small_truck.png"));
            heavyTruckImg = new Image(getClass().getResourceAsStream("/heavy_truck.png"));
            cityBusImg    = new Image(getClass().getResourceAsStream("/city_bus.png"));
            coachImg      = new Image(getClass().getResourceAsStream("/coach.png"));
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

        if (tile instanceof City || tile instanceof Industry) return;

        if (tile instanceof ForestTile forest) {
            drawForestTile(gc, px, py, forest);
        }

        if (tile instanceof RoadTile road) {
            drawRoadTile(gc, road, px, py);
        }

        gc.setStroke(Color.web("#000000", 0.05));
        gc.strokeRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void drawForestTile(GraphicsContext gc, int px, int py, ForestTile forest) {
        int count = forest.getTreeCount();

        Color bg = switch (count) {
            case 1  -> Color.web("#a8d5a2");
            case 2  -> Color.web("#7ec87a");
            case 3  -> Color.web("#5aad55");
            default -> Color.web("#3a8f35");
        };
        gc.setFill(bg);
        gc.fillRect(px, py, TILE_SIZE, TILE_SIZE);

        if (treeImg != null && !treeImg.isError()) {
            double size = TILE_SIZE * 0.5;
            if (count >= 1) gc.drawImage(treeImg, px,        py,        size, size);
            if (count >= 2) gc.drawImage(treeImg, px + size, py + size, size, size);
            if (count >= 3) gc.drawImage(treeImg, px + size, py,        size, size);
            if (count == 4) gc.drawImage(treeImg, px,        py + size, size, size);
        } else {
            drawFallbackTrees(gc, px, py, count);
        }

        if (forest.isFullyGrown()) {
            gc.setStroke(Color.web("#2d6a2d", 0.4));
            gc.setLineWidth(1.5);
            gc.strokeRect(px + 1, py + 1, TILE_SIZE - 2, TILE_SIZE - 2);
        }
    }

    private void drawFallbackTrees(GraphicsContext gc, int px, int py, int count) {
        for (int i = 0; i < count; i++) {
            double tx   = px + TREE_POSITIONS[i][0] * TILE_SIZE;
            double ty   = py + TREE_POSITIONS[i][1] * TILE_SIZE;
            double size = TREE_SIZES[i] * TILE_SIZE;
            double trunkW = size * 0.25;
            double trunkH = size * 0.3;

            gc.setFill(TRUNK_COLORS[i]);
            gc.fillRect(tx + size / 2 - trunkW / 2, ty + size - trunkH, trunkW, trunkH);

            gc.setFill(TREE_COLORS[i]);
            gc.fillOval(tx, ty, size, size * 0.85);
        }
    }

    private void drawRoadTile(GraphicsContext gc, RoadTile road, int px, int py) {
        if (roadImg != null && !roadImg.isError()) {
            gc.drawImage(roadImg, px, py, TILE_SIZE, TILE_SIZE);
            if (road.hasJunction() && road.getJunction().hasLight()) {
                drawTrafficLight(gc, road, px, py);
            }
            return;
        }

        double center    = TILE_SIZE / 2.0;
        double roadWidth = TILE_SIZE * 0.62;
        double halfRoad  = roadWidth / 2.0;
        double laneDash  = TILE_SIZE * 0.18;

        gc.setFill(Color.web("#7a7a7a"));

        if (road.getConnectionCount() == 0) {
            gc.fillRect(px + center - halfRoad, py + center - halfRoad, roadWidth, roadWidth);
            return;
        }

        gc.fillRect(px + center - halfRoad, py + center - halfRoad, roadWidth, roadWidth);

        if (road.isConnected(Direction.N)) gc.fillRect(px + center - halfRoad, py,          roadWidth, center);
        if (road.isConnected(Direction.S)) gc.fillRect(px + center - halfRoad, py + center, roadWidth, center);
        if (road.isConnected(Direction.W)) gc.fillRect(px,          py + center - halfRoad, center, roadWidth);
        if (road.isConnected(Direction.E)) gc.fillRect(px + center, py + center - halfRoad, center, roadWidth);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        if (road.isConnected(Direction.N)) gc.strokeLine(px + center, py,                     px + center,          py + center - halfRoad);
        if (road.isConnected(Direction.S)) gc.strokeLine(px + center, py + center + halfRoad, px + center,          py + TILE_SIZE);
        if (road.isConnected(Direction.W)) gc.strokeLine(px,          py + center,            px + center - halfRoad, py + center);
        if (road.isConnected(Direction.E)) gc.strokeLine(px + center + halfRoad, py + center, px + TILE_SIZE,        py + center);

        if (road.getConnectionCount() >= 2) {
            gc.strokeLine(px + center - laneDash / 2, py + center, px + center + laneDash / 2, py + center);
            gc.strokeLine(px + center, py + center - laneDash / 2, px + center, py + center + laneDash / 2);
        }

        if (road.hasJunction()) {
            gc.setStroke(Color.GOLD);
            gc.setLineWidth(2.5);
            gc.strokeRect(px + center - halfRoad + 4, py + center - halfRoad + 4, roadWidth - 8, roadWidth - 8);

            if (road.getJunction().hasLight()) {
                drawTrafficLight(gc, road, px, py);
            }
        }
    }

    private void drawTrafficLight(GraphicsContext gc, RoadTile road, int px, int py) {
        double center = TILE_SIZE / 2.0;
        TrafficLight light = road.getJunction().getTrafficLight();

        double boxW = 20, boxH = 14;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(px + center - boxW / 2, py + center - boxH / 2, boxW, boxH, 4, 4);

        Color nsColor = (light.getPhase() == SignalPhase.NS_GREEN) ? Color.LIMEGREEN : Color.RED;
        Color ewColor = (light.getPhase() == SignalPhase.EW_GREEN) ? Color.LIMEGREEN : Color.RED;

        gc.setFill(nsColor);
        gc.fillOval(px + center - 3, py + center - 6, 6, 6);

        gc.setFill(ewColor);
        gc.fillOval(px + center + 3, py + center, 6, 6);
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
        RoadTile end   = v.getTargetTile();
        if (start == null) return;

        double drawX = start.getPos().x() * TILE_SIZE;
        double drawY = start.getPos().y() * TILE_SIZE;

        if (end != null) {
            double p = v.getProgress();
            drawX += (end.getPos().x() * TILE_SIZE - drawX) * p;
            drawY += (end.getPos().y() * TILE_SIZE - drawY) * p;
        }

        double imgX    = drawX + TILE_SIZE * 0.15;
        double imgY    = drawY + TILE_SIZE * 0.15;
        double imgSize = TILE_SIZE * 0.7;
        double cx      = drawX + TILE_SIZE * 0.2;
        double cy      = drawY + TILE_SIZE * 0.2;
        double cs      = TILE_SIZE * 0.6;

        if (v instanceof SmallTruck) {
            if (smallTruckImg != null && !smallTruckImg.isError()) gc.drawImage(smallTruckImg, imgX, imgY, imgSize, imgSize);
            else drawFallbackCircle(gc, Color.web("#FFDAC1"), cx, cy, cs);
        } else if (v instanceof HeavyTruck) {
            if (heavyTruckImg != null && !heavyTruckImg.isError()) gc.drawImage(heavyTruckImg, imgX, imgY, imgSize, imgSize);
            else drawFallbackCircle(gc, Color.web("#FF9AA2"), cx, cy, cs);
        } else if (v instanceof CityBus) {
            if (cityBusImg != null && !cityBusImg.isError()) gc.drawImage(cityBusImg, imgX, imgY, imgSize, imgSize);
            else drawFallbackCircle(gc, Color.web("#A8D8EA"), cx, cy, cs);
        } else if (v instanceof Coach) {
            if (coachImg != null && !coachImg.isError()) gc.drawImage(coachImg, imgX, imgY, imgSize, imgSize);
            else drawFallbackCircle(gc, Color.web("#85C1E9"), cx, cy, cs);
        }
    }

    private void drawFallbackCircle(GraphicsContext gc, Color color, double x, double y, double size) {
        gc.setFill(color);
        gc.fillOval(x, y, size, size);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeOval(x, y, size, size);
    }
}
