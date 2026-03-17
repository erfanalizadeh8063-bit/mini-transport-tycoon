
package tycoon.service;

import java.util.*;

import tycoon.model.RoadTile;
import tycoon.model.Tile;
import tycoon.model.WorldMap;

/**
 * Service to find a path between two road tiles using BFS.
 */
public class PathFinder {
    private WorldMap map;

    public PathFinder(WorldMap map) {
        this.map = map;
    }

    /**
     * Finds a list of RoadTiles connecting start and goal.
     */
    public List<RoadTile> findPath(RoadTile start, RoadTile goal) {
        Queue<List<RoadTile>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));
        Set<RoadTile> visited = new HashSet<>();
        visited.add(start);

        while (!queue.isEmpty()) {
            List<RoadTile> path = queue.poll();
            RoadTile last = path.get(path.size() - 1);

            if (last.equals(goal)) return path;

            for (Tile neighbor : map.neighbors(last)) {
                if (neighbor instanceof RoadTile nextRoad && !visited.contains(nextRoad)) {
                    visited.add(nextRoad);
                    List<RoadTile> newPath = new ArrayList<>(path);
                    newPath.add(nextRoad);
                    queue.add(newPath);
                }
            }
        }
        return null; // No path found
    }
}