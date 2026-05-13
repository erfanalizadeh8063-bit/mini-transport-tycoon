package tycoon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tycoon.model.*;
import java.util.List;
/**
 * Renders a simplified, top-down view of the WorldMap.
 * Provides a high-level overview of infrastructure, vehicle locations, 
 * and a viewport indicator that tracks the main scrollable game area.
 * 
 * @author Srinivas James Madoc
 * @version 1.0
 */
public class MinimapRenderer {
    private static final int CELL      = 4;
    private static final int TILE_SIZE = 64;

    /**
     * Renders the entire world map at minimap scale.
     * Each tile is drawn as a 4x4 pixel square with color-coded terrain.
     * 
     * @param gc graphics context to draw on
     * @param map the world map to render
     */
    public void render(GraphicsContext gc, WorldMap map) {
        gc.clearRect(0, 0, map.getWidth() * CELL, map.getHeight() * CELL);
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                gc.setFill(tileColor(map.getTile(x, y)));
                gc.fillRect(x * CELL, y * CELL, CELL, CELL);
            }
        }
    }

    /**
     * Renders all vehicles as small yellow dots on the minimap.
     * 
     * @param gc graphics context to draw on
     * @param vehicles list of vehicles to render
     */
    public void renderVehicles(GraphicsContext gc, List<Vehicle> vehicles) {
        gc.setFill(Color.YELLOW);
        for (Vehicle v : vehicles) {
            RoadTile tile = v.getCurrentTile();
            if (tile == null) continue;
            double x = tile.getPos().x() * CELL + CELL / 2.0 - 1;
            double y = tile.getPos().y() * CELL + CELL / 2.0 - 1;
            gc.fillOval(x, y, 3, 3);
        }
    }

    /**
     * Draws a white rectangle indicating the current viewport position.
     * Allows players to see which part of the map is currently visible.
     * 
     * @param gc graphics context to draw on
     * @param scrollH horizontal scroll position (0.0 to 1.0)
     * @param scrollV vertical scroll position (0.0 to 1.0)
     * @param viewW viewport width in pixels
     * @param viewH viewport height in pixels
     * @param mapW total map width in pixels
     * @param mapH total map height in pixels
     */
    public void drawViewport(GraphicsContext gc, double scrollH, double scrollV,
                             double viewW, double viewH, double mapW, double mapH) {
        double ratio = (double) CELL / TILE_SIZE;

        double totalMapPx  = mapW;
        double totalMapPy  = mapH;
        double mmW         = (mapW / TILE_SIZE) * CELL;
        double mmH         = (mapH / TILE_SIZE) * CELL;

        double vpW = viewW * ratio;
        double vpH = viewH * ratio;

        double maxScrollX = totalMapPx - viewW;
        double maxScrollY = totalMapPy - viewH;

        double vpX = (maxScrollX > 0) ? scrollH * maxScrollX * ratio : 0;
        double vpY = (maxScrollY > 0) ? scrollV * maxScrollY * ratio : 0;

        vpX = Math.max(0, Math.min(vpX, mmW - vpW));
        vpY = Math.max(0, Math.min(vpY, mmH - vpH));

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeRect(vpX, vpY, vpW, vpH);
    }

    /**
     * Draws a legend explaining minimap colors.
     * 
     * @param gc graphics context to draw on
     * @param offsetX x position for legend
     * @param offsetY y position for legend
     */
    public void drawLegend(GraphicsContext gc, double offsetX, double offsetY) {
        double boxSize = 8;
        double spacing = 14;
        double textX   = offsetX + boxSize + 4;

        Object[][] entries = {
            { Color.DARKSLATEGRAY, "City"    },
            { Color.SADDLEBROWN,   "Industry"},
            { Color.GRAY,          "Road"    },
            { Color.DARKGREEN,     "Forest"  },
            { Color.web("#91cf60"),"Empty"   },
            { Color.YELLOW,        "Vehicle" }
        };

        gc.setFill(Color.rgb(0, 0, 0, 0.55));
        gc.fillRoundRect(offsetX - 4, offsetY - 4,
            70, entries.length * spacing + 8, 4, 4);

        for (int i = 0; i < entries.length; i++) {
            double y = offsetY + i * spacing;
            gc.setFill((Color) entries[i][0]);
            gc.fillRect(offsetX, y, boxSize, boxSize);
            gc.setFill(Color.WHITE);
            gc.fillText((String) entries[i][1], textX, y + boxSize - 1);
        }
    }

    /**
     * Maps a tile type to its corresponding minimap color.
     * 
     * @param tile the tile to get color for
     * @return color representing this tile type
     */
    private Color tileColor(Tile tile) {
        if (tile instanceof RoadTile)   return Color.GRAY;
        if (tile instanceof City)       return Color.DARKSLATEGRAY;
        if (tile instanceof Industry)   return Color.SADDLEBROWN;
        if (tile instanceof ForestTile f) return f.isFullyGrown() ? Color.DARKGREEN : Color.GREEN;
        return Color.web("#91cf60");
    }

    /**
     * Returns the pixel size of each minimap cell.
     * 
     * @return cell size in pixels
     */
    public static int cellSize() { return CELL; }
}
