package lunarlander.options;

import javax.swing.*;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import lunarlander.Settings;
import lunarlander.network.GameManager;
import lunarlander.network.NetworkManager;
import lunarlander.network.packet.PlayerPacket;
import lunarlander.player.*;
import lunarlander.thread.NetworkSimulationThread;

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class RendezvousOptions extends MessagedOptions implements GameManager {
    
    public RendezvousOptions(NetworkPlayer player, NetworkManager networkManager) {
        thisPlayer = player;
        this.networkManager = networkManager;
        players = new Vector<NetworkPlayer>();        

        players.add(player);

        chat = new ChatPanel(players, player, networkManager);
    }
    
    public abstract int getNetworkRole();
    
    public void updatePlayer() {
        updatePlayer(thisPlayer);
    }
        
    public void updatePlayer(NetworkPlayer player) {
        for (NetworkPlayer p : players) {
            synchronized(p) {
                if (p.getId() == player.getId()) {
                    p.setName(player.getName());
                    p.setColor(player.getColor());
                    p.setRole(player.getRole());
                    p.setTeam(player.getTeam());
                    chat.updatePlayersList();
                    return;
                }
            }
        }
        
        addPlayer(player);
    }    
    
    public PlayerPacket getSyncPlayerPacketToSend() {
        return playerOptions.getUpdatePacketToSend();
    }
    
    public PlayerPacket getPlayerPacketToSend() {
        return new PlayerPacket(thisPlayer);
    }
    
    public void receiveChatMessage(int fromPlayerId, String message) {
        chat.receiveMessage(fromPlayerId, message);
    }    
    
    public void receiveStatusMessage(String message) {
        chat.receiveStatusMessage(message);
    }
    
    public Player getPlayer(int playerId) {
        for (Player player : players) {
            if (player.getId() == playerId) {
                return player;
            }
        }
        
        return null;
    }
    
    public void addPlayer(NetworkPlayer player) {
        players.add(player);
        chat.updatePlayersList();
        chat.receiveStatusMessage(
                "<strong>" + player.getName() + "</strong> has joined the game");
    }    
    public void removePlayer(NetworkPlayer player) {
        players.remove(player);
        chat.updatePlayersList();
    }
    public void removePlayer(int id) {
        
        for (Iterator<NetworkPlayer> it = players.iterator(); it.hasNext();) {
            NetworkPlayer player = it.next();
            if (player.getId() == id) {
                it.remove();
                break;
            }
        }
        
        chat.updatePlayersList();
    }    
    
    protected void createSubOptions() {
        super.createSubOptions();
        
        playerOptions = new NetworkPlayerOptions(this, thisPlayer);
        subOptions.add(playerOptions);                
    }
    
    public void setPreferredSize(Dimension size) {
        super.setPreferredSize(size);
        chat.setPreferredSize(new Dimension(size.width, (int)(0.2 * size.height)));
    }

    /* (non-Javadoc)
     * @see Options#createMainPanel()
     */
    protected JComponent createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        panel.add(tabs, "Center");
        panel.add(chat, "South");
        return panel;
    }
    
    protected void createSubPanels() {
        tabs = new JTabbedPane();
        
        tabs.add("Player Setup", playerOptions);
        tabs.add("Previous Game Stats", new JPanel());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see Options#createEndButtonsPanel()
     */
    protected JPanel createEndButtonsPanel() {
        buttonsPanel = new JPanel();
        btDefault = new JButton("Reset Default Settings");
        btDefault.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                ((Options)tabs.getSelectedComponent()).fillDefaults();
            }
        });
        
        btDisconnect = new JButton("Disconnect From Server");
        btDisconnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    networkManager.disconnect();
                } catch (IOException e) {
                    System.err.println("COULD NOT DISCONNECT: " + e);
                    e.printStackTrace();
                }
                exitOptions();
            }
        });
    
        buttonsPanel.add(btDefault);
        buttonsPanel.add(btDisconnect);
        return buttonsPanel;
    }   
    
    public boolean isTeamGame() {
        return true;
    }
        
    protected void prepareForDisplay() {
        super.prepareForDisplay();
        thisPlayer.setName(Settings.getString(Settings.PLAYER_ONE+Settings.NAME));
        thisPlayer.setColor(Settings.getColor(Settings.PLAYER_ONE+Settings.COLOR)); 
        thisPlayer.setTeam(Team.values()[Settings.getInt(Settings.PLAYER_ONE+Settings.TEAM)]); 
        thisPlayer.setRole(PlayerRole.values()[Settings.getInt(Settings.PLAYER_ONE+Settings.ROLE)]);
    }
    
    protected JTabbedPane tabs;
    protected ChatPanel chat;
    protected NetworkPlayerOptions playerOptions;
    protected CommonNetworkGameOptions commonOptions;
    
    protected Vector<NetworkPlayer> players;
    protected NetworkPlayer thisPlayer;
    protected NetworkManager networkManager;
    
    protected JButton btDefault;
    protected JButton btDisconnect;
}
