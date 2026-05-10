package tycoon.service;

import org.junit.jupiter.api.Test;
import tycoon.model.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PathFinderTest {

    @Test
    public void testFindPath_SuccessfulConnection() {
        WorldMap map = new WorldMap(5, 5);
        
        // Create a simple horizontal road: (0,0) -> (1,0) -> (2,0)
        RoadTile start = new RoadTile(new Vector2(0,0), 0, map, 50.0);
        RoadTile mid = new RoadTile(new Vector2(1,0), 0, map, 50.0);
        RoadTile end = new RoadTile(new Vector2(2,0), 0, map, 50.0);
        
        // Connect them
        start.addConnection(Direction.E);
        mid.addConnection(Direction.W);
        mid.addConnection(Direction.E);
        end.addConnection(Direction.W);
        
        map.setTile(0, 0, start);
        map.setTile(1, 0, mid);
        map.setTile(2, 0, end);
        
        PathFinder pf = new PathFinder(map);
        List<RoadTile> path = pf.findPath(start, end);
        
        assertNotNull(path, "PathFinder should successfully find a route between connected roads.");
        assertFalse(path.isEmpty(), "Path should not be empty.");
        assertEquals(end, path.get(path.size() - 1), "The final tile in the path should be the destination.");
    }
}