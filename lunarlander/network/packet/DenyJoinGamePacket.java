package lunarlander.network.packet;

public class DenyJoinGamePacket extends Packet {

    public DenyJoinGamePacket(String reason) {
        super();
        this.reason = reason;
    }

    public String reason;
}
