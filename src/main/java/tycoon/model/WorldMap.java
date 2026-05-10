package tycoon.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 * Manages the 2D grid of tiles and provides spatial queries.
 * This class handles tile layout, road connectivity logic, facility-stop binding, 
 * and global updates such as traffic light cycles and forest growth.
 * * @author Your Name
 * @version 1.0
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

    private void initializeEmptyMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new EmptyTile(new Vector2(x, y), 0.0, this);
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[x][y];
        }
        return null;
    }

    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[x][y] = tile;
        }
    }

    public void placeRoad(int x, int y, double speedLimit) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        RoadTile road = new RoadTile(new Vector2(x, y), 0.0, this, speedLimit);
        setTile(x, y, road);
        refreshRoadConnectivityAround(x, y);
    }

    public void refreshRoadConnectivityAround(int x, int y) {
        refreshRoadConnectivityAt(x, y);

        for (Direction dir : Direction.values()) {
            int nx = x + dir.dx();
            int ny = y + dir.dy();
            refreshRoadConnectivityAt(nx, ny);
        }
    }

    public void refreshRoadConnectivityAt(int x, int y) {
        Tile tile = getTile(x, y);
        if (!(tile instanceof RoadTile road)) {
            return;
        }

        road.clearConnections();

        for (Direction dir : Direction.values()) {
            int nx = x + dir.dx();
            int ny = y + dir.dy();
            Tile neighbor = getTile(nx, ny);

            if (neighbor instanceof RoadTile) {
                road.addConnection(dir);
            }
        }
    }

    public List<Tile> neighbors(Tile tile) {
        List<Tile> result = new ArrayList<>();
        Vector2 pos = tile.getPos();

        int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
        for (int[] d : directions) {
            Tile neighbor = getTile(pos.x() + d[0], pos.y() + d[1]);
            if (neighbor != null) {
                result.add(neighbor);
            }
        }
        return result;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setFacility(int startX, int startY, int width, int height, Facility facility) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                setTile(x, y, facility);
            }
        }
    }

    /**
     * Returns all ForestTiles currently on the map.
     */
    public List<ForestTile> getForestTiles() {
        List<ForestTile> forests = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] instanceof ForestTile f) forests.add(f);
            }
        }
        return forests;
    }

    /**
     * Returns true if the tile at (x,y) is a forest.
     */
    public boolean isForest(int x, int y) {
        return getTile(x, y) instanceof ForestTile;
    }

    public void updateTrafficLights(double dt) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = grid[x][y];
                if (tile instanceof RoadTile road && road.hasJunction()) {
                    Junction junction = road.getJunction();
                    if (junction.hasLight()) {
                        junction.getTrafficLight().update(dt);
                    }
                }
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