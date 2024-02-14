package lunarlander.network.packet;

import java.io.Serializable;

public class RemovePlayerPacket extends Packet implements Serializable {

    public RemovePlayerPacket(int playerId) {
        super();
        this.playerId = playerId;
    }
    private int playerId;
}
