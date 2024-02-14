package lunarlander.options;

import javax.swing.*;

import lunarlander.*;
import lunarlander.game.GameType;
import lunarlander.game.LunarLanderDuo;

/**
 * @author Chung
 * 
 * StartSingleGameOptions contain Options for starting a single-player game
 */
public class StartSingleGameOptions extends StartGameOptions {
    
    /**
     * implements MessagedOptions.getInstructions()
     */
    protected String getInstructions() {
        return "You can start a single-player Lunar Lander game by configuring your desired settings here and clicking \"Start Game\".  If you wish to change these settings later, you will need to exit and restart a game.";
    }

    /**
     * overrides StartGameOptions.createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        playerOptions = new PlayerOptions(LunarLanderDuo.PLAYER_ONE);
        subOptions.add(playerOptions);
    }

    /**
     * implements Options.createMainPanel()
     */
    protected JComponent createMainPanel() {
        tabs = new JTabbedPane();
        JComponent main = super.createMainPanel();
        tabs.add("Start Game", main);
        tabs.add("Player Setup", playerOptions);
        return tabs;
    }

    
    /**
     * start a single game
     */
    protected void start() {
        super.start();
        playerOptions.saveSettings();
        LunarLanderLauncher.launchSingle();
    }
    
    /**
     * implements StartGameOptions.getGamePrefix()
     */
    protected GameType getGameType() {
        return GameType.SINGLE;
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
    
    protected JTabbedPane tabs;
    protected PlayerOptions playerOptions;
    
    private static final long serialVersionUID = 1L;
}