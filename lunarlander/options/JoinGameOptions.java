package lunarlander.options;
import java.util.List;

import lunarlander.Settings;
import lunarlander.player.NetworkPlayer;
import lunarlander.player.Player;
import lunarlander.network.*;
import lunarlander.network.packet.GameOptionsPacket;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class JoinGameOptions extends RendezvousOptions {

    public JoinGameOptions(NetworkPlayer player, Client client) {
        super(player, client);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see MessagedOptions#getInstructions()
     */
    protected String getInstructions() {
        return "Please view game settings and configure your team preferences before the game starts.";
    }
    
    public int getNetworkRole() {
        return StartNetworkGameOptions.CLIENT;
    }
    
    public List<NetworkPlayer> getPlayers() {
        return this.players;
    }

    public Client getClient() {
        return (Client) networkManager;
    }
    
    public GameOptionsPacket getSyncGameOptionsPacketToSend() {
        return null;
    }
    
    public GameOptionsPacket getGameOptionsPacketToSend() {
        return null;
    }
    
    public void updateGameOptions(GameOptionsPacket packet) {
        commonOptions.updateGameOptions(packet);
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        commonOptions = new UneditableCommonNetworkGameOptions(this);
        subOptions.add(commonOptions);
    }
    
    protected void createSubPanels() {
        super.createSubPanels();
        tabs.add(commonOptions, 0);
    }
    
    private static final long serialVersionUID = 1L;
}