package lunarlander.network.packet;

import java.awt.Color;
import java.io.Serializable;

import lunarlander.player.*;

public class PlayerPacket extends Packet implements Serializable {

    public PlayerPacket(NetworkPlayer player) {
        this.id = player.getId();
        this.name = player.getName();
        this.color = player.getColor();
        this.team = player.getTeam();
        this.role = player.getRole();
    }
    
    public String toString() {
        return "PLAYER with id=" + id + ",name=" + name + ",color=" +
            color + ",team=" + team + ",role=" + role;
    }
    
    public int id;
    public String name;
    public Color color;
    public Team team;
    public PlayerRole role;
}
