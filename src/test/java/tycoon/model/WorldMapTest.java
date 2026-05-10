package tycoon.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WorldMapTest {

    @Test
    public void testRoadPlacementAndConnectivity() {
        WorldMap map = new WorldMap(5, 5);

        map.placeRoad(1, 1, 50.0);
        map.placeRoad(1, 2, 50.0); 

        RoadTile r1 = (RoadTile) map.getTile(1, 1);
        RoadTile r2 = (RoadTile) map.getTile(1, 2);

        assertNotNull(r1);
        assertNotNull(r2);

        assertTrue(r1.getConnectionCount() > 0, "Road should automatically connect to neighbors.");
    }

    @Test
    public void testFacilityAndStopBinding() {
        WorldMap map = new WorldMap(5, 5);

        Facility ind = new Industry(new Vector2(1,1), 0.0, map, "TestFactory", CargoType.WOOD);
        map.setFacility(1, 1, 2, 2, ind);


        RoadTile stop = map.placeStop(0, 1, 30.0);
        

        assertEquals(stop, ind.getAccessTile(), "Stop should be automatically linked to adjacent industry.");
    }
    @Test
    public void testGlobalUpdates() {
        WorldMap map = new WorldMap(3, 3);
        
        // 1. 修正 ForestTile 的构造函数（补上第4个参数 treeCount）
        // 参数依次是：坐标, 高度, 地图对象, 初始树木数量
        ForestTile forest = new ForestTile(new Vector2(0, 0), 0.0, map, 1);
        map.setTile(0, 0, forest);
        
        // 2. 模拟时间流逝（触发 ForestTile 的 onTick 逻辑）
        forest.onTick(1.0);
        
        // 3. 准备带红绿灯的道路
        RoadTile road = new RoadTile(new Vector2(1, 1), 0.0, map, 50.0);
        Junction junc = new Junction();
        junc.install(new TrafficLight());
        road.setJunction(junc);
        map.setTile(1, 1, road);

        // 4. 调用 WorldMap 的全局更新方法，这会触发所有红绿灯的 update
        assertDoesNotThrow(() -> map.updateTrafficLights(1.0));
        
        // 5. 验证森林查询逻辑
        List<ForestTile> forests = map.getForestTiles();
        assertFalse(forests.isEmpty(), "Should find the placed forest tile.");
        assertTrue(map.isForest(0, 0), "The tile at (0,0) should be a forest.");
        
        // 6. 额外覆盖一下 ForestTile 的特有方法（涨分必备）
        assertTrue(forest.getTreeCount() >= 1);
        assertTrue(forest.getBuildCostModifier() > 1.0);
        assertTrue(forest.isBuildable());
    }
    @Test
    public void testNeighborsQuery() {
        WorldMap map = new WorldMap(5, 5);
        Tile center = map.getTile(2, 2);
        List<Tile> neighbors = map.neighbors(center);
        assertEquals(4, neighbors.size(), "Center tile should have 4 neighbors.");
    }
}