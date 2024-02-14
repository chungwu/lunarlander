package lunarlander.options;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import lunarlander.*;

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class StartArmedGameOptions extends StartGameOptions {

    /**
     * overrides Options.createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();
        cbEnableSmallRockets = new JCheckBox("Enable small rockets");
        cbEnableBigRockets = new JCheckBox("Enable big rockets");
        cbEnableDrones = new JCheckBox("Enable drones");
        cbRocketsDamage = new JCheckBox("Rockets do damage");
        tfRocketBudget = new JTextField(10);
    }

    /**
     * overrides StartGameOptions.fillDefaults()
     */
    protected void fillDefaults() {
        super.fillDefaults();
        String prefix = getGameType().getPrefix();
        cbEnableSmallRockets.setSelected(Settings.getDefaultBoolean(prefix+Settings.ENABLE_SMALL_ROCKETS));
        cbEnableBigRockets.setSelected(Settings.getDefaultBoolean(prefix+Settings.ENABLE_BIG_ROCKETS));
        cbRocketsDamage.setSelected(Settings.getDefaultBoolean(prefix+Settings.ROCKETS_DAMAGE));
        cbEnableDrones.setSelected(Settings.getDefaultBoolean(prefix+Settings.ENABLE_DRONES));
        tfRocketBudget.setText("" + Settings.getDefaultInt(prefix+Settings.ROCKET_BUDGET));
    }

    /**
     * overrides Options.fillCurrentSettings()
     */
    protected void fillSettings() {
        super.fillSettings();
        
        String prefix = getGameType().getPrefix();

        cbEnableSmallRockets.setSelected(Settings.getBoolean(prefix+Settings.ENABLE_SMALL_ROCKETS));
        cbEnableBigRockets.setSelected(Settings.getBoolean(prefix+Settings.ENABLE_BIG_ROCKETS));
        cbEnableDrones.setSelected(Settings.getBoolean(prefix+Settings.ENABLE_DRONES));
        cbRocketsDamage.setSelected(Settings.getBoolean(prefix+Settings.ROCKETS_DAMAGE));
        tfRocketBudget.setText("" + Settings.getInt(prefix+Settings.ROCKET_BUDGET));
    }

    /**
     * overrides Options.saveSettings()
     */
    public void saveSettings() {
        super.saveSettings();
        
        String prefix = getGameType().getPrefix();
        
        Settings.setBoolean(prefix+Settings.ENABLE_SMALL_ROCKETS, cbEnableSmallRockets.isSelected());
        Settings.setBoolean(prefix+Settings.ENABLE_BIG_ROCKETS, cbEnableBigRockets.isSelected());
        Settings.setBoolean(prefix+Settings.ENABLE_DRONES, cbEnableDrones.isSelected());
        Settings.setBoolean(prefix+Settings.ROCKETS_DAMAGE, cbRocketsDamage.isSelected());
        Settings.setInt(prefix+Settings.ROCKET_BUDGET, Integer.parseInt(tfRocketBudget.getText()));
    }

    /**
     * overrides StartGameOptions.createTextFieldsPanel()
     */
    protected JPanel createTextFieldsPanel(int rows) {
        JPanel panel = super.createTextFieldsPanel(rows + 1);

        panel.add(new JLabel("Total rocket budget: "));
        panel.add(tfRocketBudget);

        return panel;
    }

    /**
     * overrides StartGameOptions.createCheckBoxesPanel()
     */
    protected JPanel createCheckBoxesPanel() {
        JPanel panel = super.createCheckBoxesPanel();
        panel.add(cbEnableSmallRockets);
        panel.add(cbEnableBigRockets);
        panel.add(cbEnableDrones);
        panel.add(cbRocketsDamage);
        return panel;
    }
    protected JCheckBox cbEnableSmallRockets;
    protected JCheckBox cbEnableBigRockets;
    protected JCheckBox cbEnableDrones;
    protected JCheckBox cbRocketsDamage;
    protected JTextField tfRocketBudget;
}
