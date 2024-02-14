package lunarlander.options;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;

import lunarlander.game.GameType;
import lunarlander.game.LunarLanderGame;
import lunarlander.network.packet.GameOptionsPacket;
import lunarlander.*;

/**
 * @author Chung
 * 
 * Options panel for starting a network game
 */
public class CommonNetworkGameOptions extends StartArmedGameOptions {
    
    public CommonNetworkGameOptions(RendezvousOptions gameOptions) {
        this.gameOptions = gameOptions;
        this.modified = false;
        this.changeListener = new ChangeListener();
    }

    public boolean isTeamedGame() {
        return getSelectedGameType() != GameType.DEATHMATCH;
    }

    public GameType getGameType() {
        if (coGameTypeChooser == null) {
            return GameType.NETWORK_GAMETYPES[Settings.getDefaultInt(Settings.SELECTED_NETWORK_GAME)];
        }
        else {
            return getSelectedGameType();
        }
    }
    
    public synchronized void updateGameOptions(GameOptionsPacket packet) {
        Settings.setInt(Settings.SELECTED_NETWORK_GAME, packet.gameType);
        
        GameType gameType = GameType.NETWORK_GAMETYPES[packet.gameType];
        String prefix = gameType.getPrefix();
        
        Settings.setDouble(prefix + Settings.TURBO, packet.turbo);
        Settings.setDouble(prefix + Settings.SAFE_VX, packet.safeVelocityX);
        Settings.setDouble(prefix + Settings.SAFE_VY, packet.safeVelocityY);
        Settings.setDouble(prefix + Settings.SAFE_ANGLE, packet.safeAngle);
        Settings.setInt(prefix + Settings.ROCKET_BUDGET, packet.rocketBudget);
        Settings.setBoolean(prefix + Settings.ENABLE_SMALL_ROCKETS, packet.enableSmallRockets);
        Settings.setBoolean(prefix + Settings.ENABLE_BIG_ROCKETS, packet.enableBigRockets);
        Settings.setBoolean(prefix + Settings.ENABLE_DRONES, packet.enableDrones);
        Settings.setBoolean(prefix + Settings.ROCKETS_DAMAGE, packet.rocketsDamage);
        
        Settings.setBoolean(prefix + Settings.INDICATE_ENEMY, packet.indicateEnemy);
        Settings.setDouble(prefix + Settings.TIME_LIMIT, packet.timeLimit);
        Settings.setInt(prefix + Settings.POINT_LIMIT, packet.pointLimit);
        Settings.setBoolean(prefix + Settings.INDICATE_TEAMMATE, packet.indicateTeam);
        Settings.setBoolean(prefix + Settings.FRIENDLY_FIRE, packet.friendlyFire);
        
        System.out.println("GOT GAME UPDATE!!!  prefix: " + prefix);
        
        fillSettings();
        
        this.repaint();
    }
    
    public GameType getSelectedGameType() {
        return (GameType) coGameTypeChooser.getSelectedItem();
    }
    
    public synchronized GameOptionsPacket getPacketToSend() {
        saveSettings();
        return new GameOptionsPacket(this);
    }
    
    public synchronized GameOptionsPacket getSyncPacketToSend() {
        if (modified || specificOptions.modified) {
            modified = false;
            specificOptions.modified = false;
            return getPacketToSend();
        } else {
            return null;
        }        
    }

    protected void createWidgets() {
        super.createWidgets();

        coGameTypeChooser = new JComboBox(GameType.NETWORK_GAMETYPES);
        coGameTypeChooser.setSelectedIndex(Settings.getDefaultInt(Settings.SELECTED_NETWORK_GAME));
        coGameTypeChooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                mapSelection.setGameType(getSelectedGameType());
                mapSelection.fillDefaults();
                specificOptions.switchGameType(getSelectedGameType());
                mapAndMisc.repaint();
                gameOptions.playerOptions.landerCanvas.getLander().setDrawTeamColor(isTeamedGame());
                gameOptions.chat.drawTeam = isTeamedGame();
                gameOptions.chat.updatePlayersList();
            }
        });
        
        coGameTypeChooser.addActionListener(changeListener);
        cbEnableSmallRockets.addActionListener(changeListener);
        cbEnableBigRockets.addActionListener(changeListener);
        cbEnableDrones.addActionListener(changeListener);
        cbRocketsDamage.addActionListener(changeListener);
        tfRocketBudget.addKeyListener(changeListener);
        tfSafeVelocityX.addKeyListener(changeListener);
        tfSafeVelocityY.addKeyListener(changeListener);
        tfSafeAngle.addKeyListener(changeListener);
        tfTurbo.addKeyListener(changeListener);
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        specificOptions = new GameSpecificOptions();
        subOptions.add(specificOptions);
    }
    
    protected MapSelectionPanel createMapSelectionOptions() {
        return new MapSelectionPanel(getSelectedGameType(), MapSelectionPanel.HORIZONTAL, 150);
    }
    
    protected void createSubPanels() {
        super.createSubPanels();
        mapAndMisc = new JPanel(new FlowLayout());
        mapAndMisc.add(createMapSelectionPanel());
        mapAndMisc.add(specificOptions);
        specificOptions.switchGameType(getGameType());
    }

    public void saveSettings() {
        super.saveSettings();
        Settings.setInt(Settings.SELECTED_NETWORK_GAME, coGameTypeChooser.getSelectedIndex());
    }
    
    public void fillSettings() {
        super.fillSettings();
        int selectedIndex = Settings.getInt(Settings.SELECTED_NETWORK_GAME);
        coGameTypeChooser.setSelectedIndex(selectedIndex);
    }

    /**
     * implements Options.createMainPanel()
     */
    protected JComponent createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(coGameTypeChooser);

        JPanel fieldsAndBoxes = new JPanel();
        fieldsAndBoxes.setLayout(new FlowLayout());
        fieldsAndBoxes.add(createTextFieldsPanel(5));
        fieldsAndBoxes.add(createCheckBoxesPanel());

        mainPanel.add(fieldsAndBoxes);
        mainPanel.add(mapAndMisc);

        panel.add(mainPanel, "Center");

        return panel;
    }

    public String getName() {
        return "Game Options";
    }
    
    protected class ChangeListener extends KeyAdapter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            modified = true;
        }
        
        public void keyTyped(KeyEvent e) {
            modified = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see MessagedOptions#getInstructions()
     */
    protected String getInstructions() {
        return "";
    }

    protected JPanel createEndButtonsPanel() {
        return new JPanel();
    }
    
    protected JComboBox coGameTypeChooser;
    protected JPanel mapAndMisc;

    protected RendezvousOptions gameOptions;
    protected GameSpecificOptions specificOptions;
    
    protected boolean modified;
    protected ChangeListener changeListener;
    
    private static final long serialVersionUID = 1L;
}