package lunarlander.options;
import javax.swing.*;

import java.awt.event.*;

import lunarlander.Settings;
import lunarlander.game.GameType;

/*
 * Created on Jan 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GameSpecificOptions extends Options {

    public GameSpecificOptions() {
        modified = false;
        changeListener = new ChangeListener();
        uneditable = false;
    }
    
    public void switchGameType(GameType type) {
        
        tfTimeLimit.setEnabled(false);
        tfPointLimit.setEnabled(false);
        cbEnemyIndicator.setEnabled(false);
        cbTeamIndicator.setEnabled(false);
        cbFriendlyFire.setEnabled(false);
        
        if (uneditable) {
            return;
        }
        
        tfTimeLimit.setEnabled(true);
        tfPointLimit.setEnabled(true);
        cbEnemyIndicator.setEnabled(true);
        
        if (type == GameType.DEATHMATCH) {
            return;
        } 
        cbTeamIndicator.setEnabled(true);
        cbFriendlyFire.setEnabled(true);          
    }
    
    public void setUneditable(boolean uneditable) {
        this.uneditable = uneditable;
    }
    
    /* (non-Javadoc)
     * @see Options#createMainPanel()
     */
    protected JComponent createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel checkBoxes = createCheckBoxOptions();
        if (checkBoxes != null) {
            panel.add(checkBoxes);
        }
        
        JPanel endGame = createEndGameOptions();
        if (endGame != null) {
            panel.add(endGame);
        }
        
        return panel;
    }

    /* (non-Javadoc)
     * @see GameSpecificOptions#createCheckBoxOptions()
     */
    protected JPanel createCheckBoxOptions() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(cbEnemyIndicator);
        panel.add(cbTeamIndicator);
        panel.add(cbFriendlyFire);
        return panel;
    }

    /* (non-Javadoc)
     * @see GameSpecificOptions#createEndGameOptions()
     */
    protected JPanel createEndGameOptions() {
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("End Game Conditions"));
        
        JPanel time = new JPanel();
        time.add(new JLabel("Time Limit:"));
        time.add(tfTimeLimit);
        
        JPanel points = new JPanel();
        points.add(new JLabel("Point Limit:"));
        points.add(tfPointLimit);
        
        panel.add(time);
        panel.add(points);
        return panel;
    }

    /* (non-Javadoc)
     * @see Options#createWidgets()
     */
    protected void createWidgets() {
        tfTimeLimit = new JTextField(5);
        tfPointLimit = new JTextField(5);
        cbEnemyIndicator = new JCheckBox("Draw Enemy Off-Screen Indicator");        
        cbTeamIndicator = new JCheckBox("Draw Team Indicator");
        cbFriendlyFire = new JCheckBox("Friendly Fire");
        
        tfTimeLimit.addKeyListener(changeListener);
        tfPointLimit.addKeyListener(changeListener);
        cbEnemyIndicator.addActionListener(changeListener);
        cbTeamIndicator.addActionListener(changeListener);
        cbFriendlyFire.addActionListener(changeListener);
    }

    /* (non-Javadoc)
     * @see Options#fillDefaults()
     */
    protected void fillDefaults() {
        super.fillDefaults();
        tfTimeLimit.setText("" + Settings.getDefaultDouble(Settings.NETWORK+Settings.TIME_LIMIT));
        tfPointLimit.setText("" + Settings.getDefaultInt(Settings.NETWORK+Settings.POINT_LIMIT));
        cbEnemyIndicator.setSelected(Settings.getDefaultBoolean(Settings.NETWORK+Settings.INDICATE_ENEMY));
        cbTeamIndicator.setSelected(Settings.getDefaultBoolean(Settings.NETWORK+Settings.INDICATE_TEAMMATE));
        cbFriendlyFire.setSelected(Settings.getDefaultBoolean(Settings.NETWORK+Settings.FRIENDLY_FIRE));
    }

    /* (non-Javadoc)
     * @see Options#fillSettings()
     */
    protected void fillSettings() {
        super.fillSettings();
        System.out.println("FILLING game-specific settings!");
        tfTimeLimit.setText("" + Settings.getDouble(Settings.NETWORK+Settings.TIME_LIMIT));
        tfPointLimit.setText("" + Settings.getInt(Settings.NETWORK+Settings.POINT_LIMIT));
        cbEnemyIndicator.setSelected(Settings.getBoolean(Settings.NETWORK+Settings.INDICATE_ENEMY));

        cbTeamIndicator.setSelected(Settings.getBoolean(Settings.NETWORK+Settings.INDICATE_TEAMMATE));
        cbFriendlyFire.setSelected(Settings.getBoolean(Settings.NETWORK+Settings.FRIENDLY_FIRE));
        
        System.out.println("TEAM INDICATOR: " + Settings.getBoolean(Settings.NETWORK+Settings.INDICATE_TEAMMATE));
    }

    /* (non-Javadoc)
     * @see Options#saveSettings()
     */
    public void saveSettings() {
        super.saveSettings();
        Settings.setDouble(Settings.NETWORK+Settings.TIME_LIMIT, Double.parseDouble(tfTimeLimit.getText()));
        Settings.setInt(Settings.NETWORK+Settings.POINT_LIMIT, Integer.parseInt(tfPointLimit.getText()));
        Settings.setBoolean(Settings.NETWORK+Settings.INDICATE_ENEMY, cbEnemyIndicator.isSelected());
        Settings.setBoolean(Settings.NETWORK+Settings.INDICATE_TEAMMATE, cbTeamIndicator.isSelected());
        Settings.setBoolean(Settings.NETWORK+Settings.FRIENDLY_FIRE, cbFriendlyFire.isSelected());
    }
    
    public void disableAll() {
        tfTimeLimit.setEnabled(false);
        tfPointLimit.setEnabled(false);
        cbEnemyIndicator.setEnabled(false);
        cbTeamIndicator.setEnabled(false);
        cbFriendlyFire.setEnabled(false);
    }
    
    protected class ChangeListener extends KeyAdapter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            modified = true;
        }
        
        public void keyTyped(KeyEvent e) {
            modified = true;
        }
    }
    protected ChangeListener changeListener;
    protected JTextField tfTimeLimit;
    protected JTextField tfPointLimit;
    protected JCheckBox cbEnemyIndicator;
    protected JCheckBox cbTeamIndicator;
    protected JCheckBox cbFriendlyFire;
    
    protected boolean modified;
    protected boolean uneditable;
}
