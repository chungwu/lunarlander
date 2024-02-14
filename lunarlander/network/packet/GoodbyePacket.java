package lunarlander.network.packet;

import java.io.Serializable;

public class GoodbyePacket extends Packet implements Serializable {

    public GoodbyePacket(int playerId) {
        super();
        this.playerId = playerId;
    }
    
    public int playerId;
}
