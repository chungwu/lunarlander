package lunarlander.network;

import java.util.List;

import lunarlander.player.*;
import lunarlander.network.packet.*;

public interface GameManager {
    public void receiveChatMessage(int fromPlayerId, String message);
    public void receiveStatusMessage(String message);
    public void updatePlayer(NetworkPlayer playerToUpdate);
    public void addPlayer(NetworkPlayer playerToAdd);
    public void updateGameOptions(GameOptionsPacket packet);
    public void removePlayer(int playerId);
    
    public List<? extends Player> getPlayers();
    
    public PlayerPacket getPlayerPacketToSend();
    public GameOptionsPacket getGameOptionsPacketToSend();

    public PlayerPacket getSyncPlayerPacketToSend();      
    public GameOptionsPacket getSyncGameOptionsPacketToSend();
    
    public Player getPlayer(int playerId);
}
