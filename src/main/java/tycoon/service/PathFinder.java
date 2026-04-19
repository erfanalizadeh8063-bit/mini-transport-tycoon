package tycoon.service;

import java.util.*;

import tycoon.model.Direction;
import tycoon.model.RoadTile;
import tycoon.model.Tile;
import tycoon.model.WorldMap;

public class PathFinder {
    private final WorldMap map;

    public PathFinder(WorldMap map) {
        this.map = map;
    }

    public List<RoadTile> findPath(RoadTile start, RoadTile goal) {
        if (start == null || goal == null) return null;
        if (start == goal) return List.of(start);

        Queue<RoadTile> queue = new LinkedList<>();
        Map<RoadTile, RoadTile> cameFrom = new HashMap<>();

        queue.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            RoadTile current = queue.poll();

            if (current == goal) {
                return reconstructPath(cameFrom, goal);
            }

            for (Direction dir : current.getConnections()) {
                int nx = current.getPos().x() + dir.dx();
                int ny = current.getPos().y() + dir.dy();

                Tile tile = map.getTile(nx, ny);
                if (!(tile instanceof RoadTile nextRoad)) {
                    continue;
                }

                if (!nextRoad.isConnected(dir.opposite())) {
                    continue;
                }

                if (!cameFrom.containsKey(nextRoad)) {
                    cameFrom.put(nextRoad, current);
                    queue.add(nextRoad);
                }
            }
        }

        return null;
    }

    private List<RoadTile> reconstructPath(Map<RoadTile, RoadTile> cameFrom, RoadTile goal) {
        List<RoadTile> path = new ArrayList<>();
        RoadTile current = goal;

        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }

        Collections.reverse(path);
        return path;
    }
}