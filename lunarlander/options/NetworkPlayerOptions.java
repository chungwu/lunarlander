package lunarlander.options;



import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import lunarlander.network.packet.PlayerPacket;
import lunarlander.player.NetworkPlayer;
import lunarlander.player.PlayerRole;
import lunarlander.player.Team;
import lunarlander.*;


/*
 * Created on Jan 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class NetworkPlayerOptions extends PlayerOptions {

    public NetworkPlayerOptions(RendezvousOptions gameOptions, NetworkPlayer player) {
        super();
        this.gameOptions = gameOptions;
        this.player = player;
        this.modified = false;
    }
    
    public PlayerPacket getUpdatePacketToSend() {
        synchronized(player) {
            if (modified) {
                PlayerPacket packet = new PlayerPacket(player);
                modified = false;
                return packet;
            } else {
                return null;
            }
        }
    }

    /**
     * overrides PlayerOptions.createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();

        coTeamChooser = new JComboBox(Team.values());
        coTeamChooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                synchronized(player) {
                    landerCanvas.getLander().setTeam((Team)coTeamChooser.getSelectedItem());
                    player.setTeam((Team)coTeamChooser.getSelectedItem());
                    gameOptions.chat.updatePlayersList();
                    modified = true;
                }
            }
        });

        coRoleChooser = new JComboBox(PlayerRole.values());
        coRoleChooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                synchronized(player) {
                    player.setRole((PlayerRole)coRoleChooser.getSelectedItem());
                    landerCanvas.getLander().setRole((PlayerRole) coRoleChooser.getSelectedItem());
                    gameOptions.chat.updatePlayersList();
                    modified = true;
                }
            }
        });
        
        this.tfPlayerName.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent event) {
                
                synchronized(player) {
                    if (event.getKeyChar() == '\b') {
                        player.setName(tfPlayerName.getText());
                    } else {
                        player.setName(tfPlayerName.getText()+event.getKeyChar());
                    }
                    gameOptions.chat.updatePlayersList();
                    modified = true;
                }
            }
        });
        
        this.coColorChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                synchronized(player) {
                    updateSliders();
                }
            }
        });
    }
    
    protected void updateSliders() {
        super.updateSliders();
        player.setColor(getSelectedColor());
        gameOptions.chat.updatePlayersList();
        modified = true;
    }
    
    /**
     * overrides MessagedOptions.fillDefaults()
     */
    protected void fillDefaults() {
        super.fillDefaults();
        coTeamChooser.setSelectedIndex(Settings.getDefaultInt(Settings.PLAYER_ONE+Settings.TEAM));
        coRoleChooser.setSelectedIndex(Settings.getDefaultInt(Settings.PLAYER_ONE+Settings.ROLE));
    }

    /**
     * overrides MessagedOptions.fillSettings()
     */
    protected void fillSettings() {
        super.fillSettings();
        coTeamChooser.setSelectedIndex(Settings.getInt(Settings.PLAYER_ONE+Settings.TEAM));
        coRoleChooser.setSelectedIndex(Settings.getInt(Settings.PLAYER_ONE+Settings.ROLE));
    }

    /**
     * overrides MessagedOptions.saveSettings()
     */
    public void saveSettings() {
        super.saveSettings();
        Settings.setInt(Settings.PLAYER_ONE+Settings.TEAM, coTeamChooser.getSelectedIndex());
        Settings.setInt(Settings.PLAYER_ONE+Settings.ROLE, coRoleChooser.getSelectedIndex());
    }

    /**
     * @return panel with two-column-settings
     */
    protected JPanel createTwoColumnPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Player Name: "));
        panel.add(tfPlayerName);

        panel.add(new JLabel("Team: "));
        panel.add(coTeamChooser);

        panel.add(new JLabel("Role: "));
        panel.add(coRoleChooser);

        return panel;
    }


    private JComboBox coTeamChooser;
    private JComboBox coRoleChooser;

    private RendezvousOptions gameOptions;
    private NetworkPlayer player;
    
    private boolean modified;
    
    private static final long serialVersionUID = 1L;
}