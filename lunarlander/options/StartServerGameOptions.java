package lunarlander.options;

import lunarlander.LunarLanderLauncher;
import lunarlander.Settings;
import lunarlander.player.NetworkPlayer;
import lunarlander.map.GameMap;
import lunarlander.network.Host;
import lunarlander.network.packet.GameOptionsPacket;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StartServerGameOptions extends RendezvousOptions {
    
    public StartServerGameOptions(NetworkPlayer player, Host host) {
        super(player, host);
        player.setId(Host.SERVER_PLAYER_ID);
    }
    
    public Host getHost() {
        return (Host) networkManager;
    }
    
    public GameOptionsPacket getSyncGameOptionsPacketToSend() {
        return commonOptions.getSyncPacketToSend();
    }
    
    public GameOptionsPacket getGameOptionsPacketToSend() {
        return commonOptions.getPacketToSend();
    }
    
    public void updateGameOptions(GameOptionsPacket packet) {
        throw new RuntimeException("Unexpected packet!");
    }
    
    protected String getInstructions() {
        return "Please customize settings for a new network game.  Click \"Start Game\" to start.";
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        commonOptions = new CommonNetworkGameOptions(this);
        subOptions.add(commonOptions);
    }
    
    protected void createSubPanels() {
        super.createSubPanels();
        tabs.add(commonOptions, 0);
    }
    
    protected JPanel createEndButtonsPanel() {
        JPanel endPanel = super.createEndButtonsPanel();
        JButton btStart = new JButton("Start Game");
        btStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    GameMap map = commonOptions.mapSelection.getSelectedMap();
                    networkManager.startGame(map);
                } catch (java.io.IOException exc) {
                    System.err.println("ERROR SENDING MAP: " + exc);
                    exc.printStackTrace();
                }
            }
        });
        endPanel.add(btStart, 0);
        return endPanel;
    }
    
    public int getNetworkRole() {
        return StartNetworkGameOptions.SERVER;
    }
    
    public List<NetworkPlayer> getPlayers() {
        return this.players;
    }
    
    private static final long serialVersionUID = 1L;
}
