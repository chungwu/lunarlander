package lunarlander.network.packet;

import java.awt.Color;
import java.io.Serializable;

import lunarlander.player.*;

public class JoinGamePacket extends Packet implements Serializable {

    public JoinGamePacket(NetworkPlayer player) {
        name = player.getName();
        color = player.getColor();
        role = player.getRole();
        team = player.getTeam();
    }
    
    public String toString() {
        return "JOIN name=" + name + ", color=" + color + ", role=" + role + ", team=" + team;
    }
    
    public String name;
    public Color color;
    public PlayerRole role;
    public Team team;
}
