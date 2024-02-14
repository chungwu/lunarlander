package lunarlander.player;

import java.awt.Color;
import java.nio.channels.SocketChannel;

import lunarlander.gameobject.LunarLander;
import lunarlander.network.packet.JoinGamePacket;
import lunarlander.network.packet.PlayerPacket;


/*
 * Created on Jan 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NetworkPlayer extends HumanPlayer {

    public NetworkPlayer(int id, SocketChannel channel, String name, Color color, LunarLander lander, Team team, PlayerRole role) {
        super(name, color, lander, HumanPlayer.LAYOUT_ONE);
        this.id = id;
        this.team = team;
        this.role = role;
        this.channel = channel;
    }
    
    public NetworkPlayer(int id, String name, Color color, Team team, PlayerRole role) {
        this(id, null, name, color, null, team, role);
    }
    
    public NetworkPlayer(PlayerPacket packet) {
        this(packet.id, packet.name, packet.color, packet.team, packet.role);
    }
    
    public NetworkPlayer(int id, SocketChannel channel, JoinGamePacket packet) {
        this(id, channel, packet.name, packet.color, null, packet.team, packet.role);
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public void setRole(PlayerRole role) {
        this.role = role;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public PlayerRole getRole() {
        return role;
    }
    
    public int getId() {
        return id;
    }
    
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public SocketChannel getChannel() {
        return channel;
    }
    
    public boolean equals(Object o) {
        if (o instanceof NetworkPlayer) {
            return id == ((NetworkPlayer)o).getId();
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see Player#destruct()
     */
    public void destruct() {
        // TODO Auto-generated method stub

    }

    protected int id;
    protected Team team;
    protected PlayerRole role;
    protected SocketChannel channel;
    protected State state;
    
    public static enum State {
        NONE, RENDEZVOUS, RECEIVED_MAP, IN_GAME
    }
}
