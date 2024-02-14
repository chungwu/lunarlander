package lunarlander.network.packet;

import java.io.Serializable;

import lunarlander.player.NetworkPlayer;
import lunarlander.gameobject.NetworkLunarLander;;

public class PlayerGameStatePacket extends Packet implements Serializable {

    public PlayerGameStatePacket(NetworkPlayer player) {
        super();
        landerState = new LanderState((NetworkLunarLander) player.getLander());
        playerId = player.getId();
    }

    public int playerId;
    public LanderState landerState;
}
