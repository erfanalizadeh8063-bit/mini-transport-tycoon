package tycoon.service;

import java.util.*;

import tycoon.model.RoadTile;
import tycoon.model.Tile;
import tycoon.model.WorldMap;
import java.io.Serializable;
/**
 * Provides pathfinding capabilities for vehicles within the world map.
 * Implements a Breadth-First Search (BFS) algorithm to find the shortest 
 * route between two RoadTiles, ensuring vehicles navigate only on valid roads.
 */
public class PathFinder implements Serializable {
    private WorldMap map;

    public PathFinder(WorldMap map) {
        this.map = map;
    }

    public List<RoadTile> findPath(RoadTile start, RoadTile goal) {
        if (start == null || goal == null) return null;
        
        if (start.getPos().x() == goal.getPos().x() && start.getPos().y() == goal.getPos().y()) {
            return new ArrayList<>();
        }

        Queue<RoadTile> queue = new LinkedList<>();
        Map<RoadTile, RoadTile> cameFrom = new HashMap<>();

        queue.add(start);
        cameFrom.put(start, null); 

        RoadTile foundGoal = null;

        while (!queue.isEmpty()) {
            RoadTile current = queue.poll();

            if (current.getPos().x() == goal.getPos().x() && current.getPos().y() == goal.getPos().y()) {
                foundGoal = current;
                break; 
            }

            for (Tile neighbor : map.neighbors(current)) {
                if (neighbor instanceof RoadTile nextRoad && !cameFrom.containsKey(nextRoad)) {
                    cameFrom.put(nextRoad, current); 
                    queue.add(nextRoad);
                }
            }
        }

        if (foundGoal == null) return null;

        List<RoadTile> path = new ArrayList<>();
        RoadTile curr = foundGoal;
        while (curr != null) {
            path.add(curr);
            curr = cameFrom.get(curr);
        }
        
        Collections.reverse(path); 
        
        if (!path.isEmpty()) {
            path.remove(0);
        }
        
        return path;
    }
}