package lunarlander.network;

import java.io.IOException;

import lunarlander.player.NetworkPlayer;
import lunarlander.map.GameMap;
import lunarlander.network.packet.*;

public abstract class NetworkManager {
    
    public static final boolean DEBUG = false;
    
    public NetworkManager(NetworkPlayer thisPlayer, NetworkRole role) {
        this.thisPlayer = thisPlayer;
        this.role = role;
        this.packetManager = new PacketManager();
        this.started = false;
        this.framesSinceLastPlayerUpdate = 0;
        this.framesSinceLastGameOptionsUpdate = 0;
    }      
    
    public void setGameManager(GameManager manager) {
        this.gameManager = manager;
    }
    
    public void debugPrint(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }
    
    public NetworkPlayer getThisPlayer() {
        return thisPlayer;
    }
    
    // Interface to NetworkSimulationThread
    public abstract void processPackets();
    public abstract void sendUpdates() throws IOException;
    public abstract void disconnect() throws IOException;
    
    // Interface to GUI
    public abstract void sendChatMessage(int toPlayerId, String message) throws IOException;
    
    public void sendChatMessage(String message) throws IOException {
        sendChatMessage(ChatMessagePacket.EVERYONE, message);
    }
    
    public abstract void startGame(GameMap map) throws IOException;
    
    protected static final int FRAMES_TO_PLAYER_UPDATE = 30;
    protected int framesSinceLastPlayerUpdate;
    
    protected static final int FRAMES_TO_GAME_OPTIONS_UPDATE = 30;
    protected int framesSinceLastGameOptionsUpdate;
    
    protected NetworkPlayer thisPlayer;
    protected NetworkRole role;
    protected PacketManager packetManager;
    protected GameManager gameManager;
    protected boolean started;
}
