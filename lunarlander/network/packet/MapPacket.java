package lunarlander.network.packet;

import java.io.Serializable;
import java.util.List;

import lunarlander.map.GameMap;
import lunarlander.map.TerrainSegment;

public class MapPacket extends Packet implements Serializable {

    public MapPacket(GameMap map) {
        super();
        if (map.isGameType(GameMap.RANDOM)) {
            mapName = "Random";
        } else if (map.isGameType(GameMap.FLAT)) {
            mapName = "Flat";
        } else {
            mapName = map.getMoon().getMapName();
        }
        worldWidth = map.getMoon().getWorldWidth();
        worldHeight = map.getMoon().getWorldHeight();
        gravity = map.getMoon().getGravity();
        segments = map.getMoon().getTerrain().getSegments();
    }

    public String toString() {
        return "MAP: " + mapName + ", " + worldWidth + "x" + worldHeight + ", " + gravity;
    }
    
    public String mapName;
    public double worldWidth;
    public double worldHeight;
    public double gravity;
    public List<TerrainSegment> segments;
}
