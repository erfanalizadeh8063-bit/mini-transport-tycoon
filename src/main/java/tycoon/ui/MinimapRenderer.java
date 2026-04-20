package tycoon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tycoon.model.*;

public class MinimapRenderer {
    private static final int CELL = 4;
    private static final int TILE_SIZE = 64;

    public void render(GraphicsContext gc, WorldMap map) {
        gc.clearRect(0, 0, map.getWidth() * CELL, map.getHeight() * CELL);
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                gc.setFill(tileColor(map.getTile(x, y)));
                gc.fillRect(x * CELL, y * CELL, CELL, CELL);
            }
        }
    }


    public void drawViewport(GraphicsContext gc, double scrollH, double scrollV,
                              double viewW, double viewH, double mapW, double mapH) {
        double ratio = (double) CELL / TILE_SIZE;
        
        double x = scrollH * (mapW - viewW) * ratio;
        double y = scrollV * (mapH - viewH) * ratio;
        
        double w = viewW * ratio;
        double h = viewH * ratio;
        
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5); 
        gc.strokeRect(x, y, w, h);
    }

    private Color tileColor(Tile tile) {
        if (tile instanceof RoadTile)    return Color.GRAY;
        if (tile instanceof City)        return Color.DARKSLATEGRAY;
        if (tile instanceof Industry)    return Color.SADDLEBROWN;
        

        if (tile instanceof EmptyTile) {
            if (((EmptyTile) tile).getTreeCount() > 0) {
                return Color.DARKGREEN; 
            }
        }
        
        return Color.web("#91cf60"); 
    }

    public static int cellSize() { return CELL; }
}