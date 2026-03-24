package tycoon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import tycoon.model.*;
import java.util.List;

public class GameRenderer {
    private static final int TILE_SIZE = 64; 

    private Image grassImg;
    private Image roadImg;
    private Image cityImg;
    private Image factoryImg;
    private Image treeImg; 
    private Image carImg; 

    public GameRenderer() {
        try {
            grassImg = new Image(getClass().getResourceAsStream("/grass.png"));
            roadImg = new Image(getClass().getResourceAsStream("/road.png"));
            cityImg = new Image(getClass().getResourceAsStream("/city.jpg"));
            factoryImg = new Image(getClass().getResourceAsStream("/factory.png"));
            treeImg = new Image(getClass().getResourceAsStream("/tree.png")); 
            carImg = new Image(getClass().getResourceAsStream("/car.png")); 
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

        if (tile instanceof RoadTile) {
            if (roadImg != null && !roadImg.isError()) {
                gc.drawImage(roadImg, px, py, TILE_SIZE, TILE_SIZE);
            }
        }
        

        gc.setStroke(Color.web("#000000", 0.05));
        gc.strokeRect(px, py, TILE_SIZE, TILE_SIZE);
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
        } 
        else if (tile instanceof Industry) {
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

        if (carImg != null && !carImg.isError()) {
            gc.drawImage(carImg, drawX + TILE_SIZE*0.15, drawY + TILE_SIZE*0.15, TILE_SIZE*0.7, TILE_SIZE*0.7);
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillOval(drawX + TILE_SIZE*0.2, drawY + TILE_SIZE*0.2, TILE_SIZE*0.6, TILE_SIZE*0.6);
        }
    }
}