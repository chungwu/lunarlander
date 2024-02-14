package lunarlander.network.packet;

import java.io.Serializable;

public class ReceivedMapPacket extends Packet implements Serializable {

    public ReceivedMapPacket(int playerId) {
        super();
        this.playerId = playerId;
    }
    
    public int playerId;

}
