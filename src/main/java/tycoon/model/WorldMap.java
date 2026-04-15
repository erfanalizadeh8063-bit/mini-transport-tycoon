package tycoon.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 * Manages the 2D grid of tiles and provides spatial queries.
 */
public class WorldMap implements Serializable {
    private int width;  // UML: width: int
    private int height; // UML: height: int
    private Tile[][] grid; // UML: WorldMap contains Tiles

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        initializeEmptyMap();
    }

    /**
     * Fills the map with EmptyTiles at height 0 by default.
     */
    private void initializeEmptyMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new EmptyTile(new Vector2(x, y), 0.0, this);
            }
        }
    }

    /**
     * Returns the tile at specific coordinates if within bounds.
     */
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[x][y];
        }
        return null;
    }

    /**
     * Updates a specific tile in the grid.
     */
    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[x][y] = tile;
        }
    }

    /**
     * Returns adjacent tiles for pathfinding or connectivity checks.
     */
    public List<Tile> neighbors(Tile tile) {
        List<Tile> result = new ArrayList<>();
        Vector2 pos = tile.getPos();
        
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] d : directions) {
            Tile neighbor = getTile(pos.x() + d[0], pos.y() + d[1]);
            if (neighbor != null) {
                result.add(neighbor);
            }
        }
        return result;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setFacility(int startX, int startY, int width, int height, Facility facility) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
               setTile(x, y, facility);
            }
        }
    }

    public RoadTile placeStop(int x, int y, double speedLimit) {
        RoadTile stopTile = new RoadTile(new Vector2(x, y), 0.0, this, speedLimit);
        

        setTile(x, y, stopTile);
        
        bindStopToAdjacentFacilities(stopTile);
        
        return stopTile;
    }

    private void bindStopToAdjacentFacilities(RoadTile stopTile) {
        List<Tile> adjacentTiles = neighbors(stopTile);
        
        for (Tile neighbor : adjacentTiles) {
            if (neighbor instanceof Facility facility) {
                facility.setAccessTile(stopTile);
                System.out.println("Success: Linked stop at (" + stopTile.getPos().x() + "," + stopTile.getPos().y() + ") to " + facility.getName());
                

            }
        }
    }
}