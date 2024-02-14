package lunarlander.options;
import javax.swing.*;
import lunarlander.*;


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
public class DeathmatchOptions {
    
}
//public class DeathmatchOptions extends GameSpecificOptions {
//    
//    public String getName() {
//        return "Deathmatch Options";
//    }
//    
//    /* (non-Javadoc)
//     * @see GameSpecificOptions#createTwoColumnOptions()
//     */
//    protected JPanel createTwoColumnOptions() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see GameSpecificOptions#createCheckBoxOptions()
//     */
//    protected JPanel createCheckBoxOptions() {
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.add(cbEnemyIndicator);
//        return panel;
//    }
//
//    /* (non-Javadoc)
//     * @see GameSpecificOptions#createEndGameOptions()
//     */
//    protected JPanel createEndGameOptions() {
//        
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.setBorder(BorderFactory.createTitledBorder("End Game Conditions"));
//        
//        JPanel time = new JPanel();
//        time.add(new JLabel("Time Limit:"));
//        time.add(tfTimeLimit);
//        
//        JPanel points = new JPanel();
//        points.add(new JLabel("Point Limit:"));
//        points.add(tfPointLimit);
//        
//        panel.add(time);
//        panel.add(points);
//        return panel;
//    }
//
//    /* (non-Javadoc)
//     * @see Options#createWidgets()
//     */
//    protected void createWidgets() {
//        tfTimeLimit = new JTextField(5);
//        tfPointLimit = new JTextField(5);
//        cbEnemyIndicator = new JCheckBox("Draw Enemy Off-Screen Indicator");
//        
//        tfTimeLimit.addActionListener(changeListener);
//        tfPointLimit.addActionListener(changeListener);
//        cbEnemyIndicator.addActionListener(changeListener);
//    }
//
//    /* (non-Javadoc)
//     * @see Options#fillDefaults()
//     */
//    protected void fillDefaults() {
//        super.fillDefaults();
//        tfTimeLimit.setText("" + Settings.getDefaultDouble(Settings.DEATHMATCH+Settings.TIME_LIMIT));
//        tfPointLimit.setText("" + Settings.getDefaultInt(Settings.DEATHMATCH+Settings.POINT_LIMIT));
//        cbEnemyIndicator.setSelected(Settings.getDefaultBoolean(Settings.DEATHMATCH+Settings.INDICATE_ENEMY));
//    }
//
//    /* (non-Javadoc)
//     * @see Options#fillSettings()
//     */
//    protected void fillSettings() {
//        super.fillSettings();
//        tfTimeLimit.setText("" + Settings.getDouble(Settings.DEATHMATCH+Settings.TIME_LIMIT));
//        tfPointLimit.setText("" + Settings.getInt(Settings.DEATHMATCH+Settings.POINT_LIMIT));
//        cbEnemyIndicator.setSelected(Settings.getBoolean(Settings.DEATHMATCH+Settings.INDICATE_ENEMY));
//    }
//
//    /* (non-Javadoc)
//     * @see Options#saveSettings()
//     */
//    protected void saveSettings() {
//        super.saveSettings();
//        Settings.setDouble(Settings.DEATHMATCH+Settings.TIME_LIMIT, Double.parseDouble(tfTimeLimit.getText()));
//        Settings.setInt(Settings.DEATHMATCH+Settings.POINT_LIMIT, Integer.parseInt(tfPointLimit.getText()));
//        Settings.setBoolean(Settings.DEATHMATCH+Settings.INDICATE_ENEMY, cbEnemyIndicator.isSelected());
//    }
//    
//    public void disable() {
//    	tfTimeLimit.setEnabled(false);
//    	tfPointLimit.setEnabled(false);
//    	cbEnemyIndicator.setEnabled(false);
//    }
//    
//    protected JTextField tfTimeLimit;
//    protected JTextField tfPointLimit;
//    protected JCheckBox cbEnemyIndicator;
//    
//    private static final long serialVersionUID = 1L;
//}
