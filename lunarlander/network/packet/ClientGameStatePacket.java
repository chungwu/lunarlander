package lunarlander.network.packet;

import java.nio.ByteBuffer;

import lunarlander.network.IncompletePacketException;
import lunarlander.network.InvalidPacketException;
import lunarlander.util.Vect2D;


/**
 * A UDP packet from a connected client to a server or host that contains an update of the game 
 * state for the client
 * 
 * @author mike
 */
public class ClientGameStatePacket extends Packet {
    
    /**
     * Parse raw packet bytes and return a new ClientGameStatePacket object.  NOTE: This method
     * consumes bytes from the contents buffer!
     * 
     * @param contents raw bytes from the UDP packet.
     * @return a new ClientGameStatePacket object based on the raw bytes
     * @throws InvalidPacketException a malformed or corrupt packet was received
     * @throws IncompletePacketException the byte array does not contain a full packet
     */
    public static ClientGameStatePacket parsePacket(ByteBuffer contents) throws InvalidPacketException,
            IncompletePacketException {
        return null;
    }    
    
    /**
     * @param time
     * @param position
     * @param momentum
     * @param angle
     * @param rocketPositions
     * @param rocketMomentums
     * @param rocketTypes
     */
    public ClientGameStatePacket(double time, Vect2D position, Vect2D momentum, double angle,
            Vect2D[] rocketPositions, Vect2D[] rocketMomentums, int[] rocketTypes) {
        super();
        this.time = time;
        this.position = position;
        this.momentum = momentum;
        this.angle = angle;
        this.rocketPositions = rocketPositions;
        this.rocketMomentums = rocketMomentums;
        this.rocketTypes = rocketTypes;
    }
    
    
    public double time;
    public Vect2D position;
    public Vect2D momentum;
    public double angle;
    public Vect2D[] rocketPositions;
    public Vect2D[] rocketMomentums;
    public int[] rocketTypes;
}
