package lunarlander.network.packet;

import java.nio.ByteBuffer;

import lunarlander.network.IncompletePacketException;
import lunarlander.network.InvalidPacketException;
import lunarlander.util.Vect2D;


/**
 * A UDP packet from the host or server that contains an update of the game state for all
 * connected players except for the recipient
 * 
 * @author mike
 */
public class ServerGameStatePacket {
    
    /**
     * Parse raw packet bytes and return a new ServerGameStatePacket object.  NOTE: This method
     * consumes bytes from the contents buffer!
     * 
     * @param contents raw bytes from the UDP packet.
     * @return a new ServerGameStatePacket object based on the raw bytes
     * @throws InvalidPacketException a malformed or corrupt packet was received
     * @throws IncompletePacketException the byte array does not contain a full packet
     */
    public static ServerGameStatePacket parsePacket(ByteBuffer contents) throws InvalidPacketException,
            IncompletePacketException {
        return null;
    }
    
    /**
     * @param playerIDs
     * @param time
     * @param positions
     * @param momentums
     * @param angles
     * @param rocketPositions
     * @param rocketMomentums
     * @param rocketTypes
     */
    public ServerGameStatePacket(int[] playerIDs, double[] time, Vect2D[] positions,
            Vect2D[] momentums, double[] angles, Vect2D[][] rocketPositions,
            Vect2D[][] rocketMomentums, int[][] rocketTypes) {
        super();
        this.playerIDs = playerIDs;
        this.time = time;
        this.positions = positions;
        this.momentums = momentums;
        this.angles = angles;
        this.rocketPositions = rocketPositions;
        this.rocketMomentums = rocketMomentums;
        this.rocketTypes = rocketTypes;
    }
    
    
    public int[] playerIDs;
    public double time[];
    public Vect2D positions[];
    public Vect2D momentums[];
    public double angles[];
    public Vect2D[][] rocketPositions;
    public Vect2D[][] rocketMomentums;
    public int[][] rocketTypes;    
}
