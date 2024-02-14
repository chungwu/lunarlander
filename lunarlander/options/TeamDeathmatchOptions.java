package lunarlander.options;
import javax.swing.*;

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TeamDeathmatchOptions {
    
}
//public class TeamDeathmatchOptions extends DeathmatchOptions {
//
//    public String getName() {
//        return "Team Deathmatch Options";
//    }
//    
//    protected JPanel createCheckBoxOptions() {
//        JPanel panel = super.createCheckBoxOptions();
//        panel.add(cbTeamIndicator);
//        panel.add(cbFriendlyFire);
//        return panel;
//    }
//    
//    protected void createWidgets() {
//        super.createWidgets();
//        cbTeamIndicator = new JCheckBox("Draw Team Indicator");
//        cbFriendlyFire = new JCheckBox("Friendly Fire");
//        cbTeamIndicator.addActionListener(changeListener);
//        cbFriendlyFire.addActionListener(changeListener);
//    }
//
//    /* (non-Javadoc)
//     * @see Options#fillDefaults()
//     */
//    protected void fillDefaults() {
//        super.fillDefaults();
//        cbTeamIndicator.setSelected(DEFAULT_TEAM_INDICATOR);
//        cbFriendlyFire.setSelected(DEFAULT_FRIENDLY_FIRE);
//    }
//
//    /* (non-Javadoc)
//     * @see Options#fillSettings()
//     */
//    protected void fillSettings() {
//        super.fillSettings();
//        cbTeamIndicator.setSelected(teamIndicator);
//        cbFriendlyFire.setSelected(friendlyFire);
//    }
//
//    /* (non-Javadoc)
//     * @see Options#saveSettings()
//     */
//    protected void saveSettings() {
//        super.saveSettings();
//        teamIndicator = cbTeamIndicator.isSelected();
//        friendlyFire = cbFriendlyFire.isSelected();
//    }
//    
//    public void disable() {
//    	super.disable();
//    	cbTeamIndicator.setEnabled(false);
//    	cbFriendlyFire.setEnabled(false);
//    }
//    
//    protected boolean teamIndicator;
//    protected boolean friendlyFire;
//    
//    protected JCheckBox cbTeamIndicator;
//    protected JCheckBox cbFriendlyFire;
//    
//    public static final boolean DEFAULT_TEAM_INDICATOR = true;
//    public static final boolean DEFAULT_FRIENDLY_FIRE = true;
//    
//    private static final long serialVersionUID = 1L;
//}
