package tycoon.service;

import java.util.*;

import tycoon.model.RoadTile;
import tycoon.model.Tile;
import tycoon.model.WorldMap;

public class PathFinder {
    private WorldMap map;

    public PathFinder(WorldMap map) {
        this.map = map;
    }

    public List<RoadTile> findPath(RoadTile start, RoadTile goal) {

        if (start == null || goal == null) return null;
        if (start.equals(goal)) return Collections.singletonList(start);


        Queue<RoadTile> queue = new LinkedList<>();
 
        Map<RoadTile, RoadTile> cameFrom = new HashMap<>();

        queue.add(start);
        cameFrom.put(start, null); 

        RoadTile foundGoal = null;

        while (!queue.isEmpty()) {
            RoadTile current = queue.poll();


            if (current.equals(goal)) {
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
        return path;
    }
}