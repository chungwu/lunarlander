package lunarlander.options;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import lunarlander.*;
import lunarlander.game.GameType;
import lunarlander.game.LunarLanderDuo;

/**
 * @author Chung
 * 
 * StartMultiGameOptions contain Options for starting a local two-player game
 */
public class StartDuoGameOptions extends StartArmedGameOptions {

    /**
     * implements MessagedOptions.getInstructions()
     */
    protected String getInstructions() {
        return "You can start a local two-player Lunar Lander game by configuring your desired settings here and clicking \"Start Game\".  If you wish to change these settings later, you will need to exit and restart a game.";
    }

    /**
     * start a two-player game
     */
    protected void start() {
        super.start();
        player1Options.saveSettings();
        player2Options.saveSettings();
        LunarLanderLauncher.launchMulti();
    }

    /**
     * implements StartGameOptions.getGameType()
     */
    protected GameType getGameType() {
        return GameType.DUO;
    }

    /**
     * implements Options.createMainPanel()
     */
    protected JComponent createMainPanel() {
        tabs = new JTabbedPane();
        JComponent main = super.createMainPanel();
        tabs.add("Start Game", main);
        tabs.add("Player One Setup", player1Options);
        tabs.add("Player Two Setup", player2Options);
        return tabs;
    }
    
    /**
     * overrides Options.createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();
        cbMustLandToWin = new JCheckBox("Must land to win");
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        
        player1Options = new PlayerOptions(LunarLanderDuo.PLAYER_ONE);
        subOptions.add(player1Options);
        
        player2Options = new PlayerOptions(LunarLanderDuo.PLAYER_TWO);
        subOptions.add(player2Options);
    }
    
    /**
     * overrides StartGameOptions.fillDefaults()
     */
    protected void fillDefaults() {
        super.fillDefaults();
        String prefix = getGameType().getPrefix();
        cbMustLandToWin.setSelected(Settings.getDefaultBoolean(prefix+Settings.MUST_LAND_TO_WIN));
    }
    
    /**
     * overrides Options.fillCurrentSettings()
     */
    protected void fillSettings() {
        super.fillSettings();
        
        String prefix = getGameType().getPrefix();

        cbMustLandToWin.setSelected(Settings.getBoolean(prefix+Settings.MUST_LAND_TO_WIN));
    }
    
    /**
     * overrides StartArmedGameOptions.saveSettings()
     */
    public void saveSettings() {
        super.saveSettings();
        
        String prefix = getGameType().getPrefix();
        Settings.setBoolean(prefix+Settings.MUST_LAND_TO_WIN, cbMustLandToWin.isSelected());
    }
    
    /**
     * overrides StartArmedGameOptions.createCheckBoxesPanel()
     */
    protected JPanel createCheckBoxesPanel() {
        JPanel panel = super.createCheckBoxesPanel();
        panel.add(cbMustLandToWin);
        return panel;
    }

    /**
     * resets field values to defaults; called when user hits "Reset To Default"
     */
    protected void resetToDefaults() {
        if (tabs.getSelectedIndex() == 0) {
            fillDefaults();
        }
        else {
            ((Options) tabs.getSelectedComponent()).fillDefaults();
        }
    }
    protected PlayerOptions player1Options;
    protected PlayerOptions player2Options;

    protected JCheckBox cbMustLandToWin;
    protected JTabbedPane tabs;
    
    private static final long serialVersionUID = 1L;
}