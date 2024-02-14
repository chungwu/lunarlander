package lunarlander.network.packet;

import java.io.Serializable;


public class AcceptJoinGamePacket extends Packet implements Serializable {

    public AcceptJoinGamePacket(int id) {
        this.id = id;
    }

    public String toString() {
        return "ACCEPT with id " + id;
    }
    
    public int id;
}
