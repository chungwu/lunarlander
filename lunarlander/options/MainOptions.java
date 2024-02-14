package lunarlander.options;

import javax.swing.*;


import java.awt.*;
import java.awt.event.*;

import lunarlander.*;
import lunarlander.game.*;

/**
 * @author Chung
 * 
 * MainOptionsTab contains options fo game resolution, trace and anti-aliasing
 */
public class MainOptions extends ButtonedOptions {

    /**
     * overrides MessageOptions.createWidgets()
     */
    protected void createWidgets() {
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        gameOptions = new GameWideOptions();
        subOptions.add(gameOptions);
        
        player1Options = new PlayerOptions(LunarLanderDuo.PLAYER_ONE);
        subOptions.add(player1Options);
        
        player2Options = new PlayerOptions(LunarLanderDuo.PLAYER_TWO);
        subOptions.add(player2Options);
    }

    /**
     * implements Options.createMainPanel()
     */
    protected JComponent createMainPanel() {
        mainPanel = new JTabbedPane();
        mainPanel.add("Main", gameOptions);
        return mainPanel;
    }

    /**
     * implements Options.createEndButtonsPanel()
     */
    protected JPanel createEndButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        btOk = new JButton("OK");
        btOk.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                saveSettings();
                player1Options.forceCurrentGame();
                if (LunarLanderLauncher.game != null
                        && LunarLanderLauncher.game.getGameType() == GameType.DUO) {
                    player2Options.forceCurrentGame();
                }
                exitOptions();
            }
        });

        btDefault = new JButton("Restore Default Settings");
        btDefault.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                ((Options) ((JTabbedPane) mainPanel).getSelectedComponent()).fillDefaults();
            }
        });

        btCancel = new JButton("Cancel");
        btCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                exitOptions();
            }
        });

        panel.add(btOk);
        panel.add(btDefault);
        panel.add(btCancel);
        return panel;
    }

    /**
     * overrides Options.prepareForDisplay()
     * 
     * tells playerOptions to start animating its MiniLanderCanvas
     */
    protected void prepareForDisplay() {

        super.prepareForDisplay();

        if (LunarLanderLauncher.game != null
                && LunarLanderLauncher.game.getGameType() == GameType.DUO) {
            ((JTabbedPane) mainPanel).add("Player One Setup", player1Options);

            ((JTabbedPane) mainPanel).add("Player Two Setup", player2Options);
        }
        else {
            ((JTabbedPane) mainPanel).add("Player Setup", player1Options);
        }
        this.doLayout();
    }

    /**
     * overrides Options.prepareForExit()
     * 
     * tells playerOptions to stop animating its MiniLanderCanvas
     */
    protected void prepareForExit() {
        super.prepareForExit();
        ((JTabbedPane) mainPanel).remove(player1Options);
        ((JTabbedPane) mainPanel).remove(player2Options);
    }


    public GameWideOptions gameOptions;
    public PlayerOptions player1Options;
    public PlayerOptions player2Options;
    protected JButton btOk;
    protected JButton btDefault;
    protected JButton btCancel;
    
    private static final long serialVersionUID = 1L;
}