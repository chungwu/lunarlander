package lunarlander.network;

import lunarlander.network.packet.Packet;

public class UnexpectedPacketException extends Exception {

    public UnexpectedPacketException(Packet packet, String reason) {
        super();
        this.packet = packet;
        this.reason = reason;
    }
    
    public String toString() {
        return "Unexpected Packet: " + packet + "; " + reason;
    }

    private Packet packet;
    private String reason;
}
