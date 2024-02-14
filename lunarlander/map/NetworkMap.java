package lunarlander.map;

import lunarlander.network.packet.MapPacket;


public class NetworkMap extends GameMap {

    public NetworkMap(MapPacket packet) {
        super();
        moon = new Moon(packet);
    }

    @Override
    public int getMapType() {
        return 0;
    }

    @Override
    public Moon getMoon() {
        return moon;
    }

    @Override
    public double getWidth() {
        return moon.getWorldWidth();
    }

    @Override
    public double getHeight() {
        return moon.getWorldHeight();
    }

    @Override
    public int getNumPads(int padType) {
        return moon.getNumOfPads(padType);
    }

    @Override
    public double getGravity() {
        return moon.getGravity();
    }

    @Override
    public String shortName() {
        return moon.getMapName();
    }

    @Override
    public String toString() {
        return moon.getMapName();
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
    
    private Moon moon;

}
