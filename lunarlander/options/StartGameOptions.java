package lunarlander.options;


import java.awt.*;
import javax.swing.*;

import lunarlander.*;
import lunarlander.game.GameType;

import java.awt.event.*;

/**
 * @author Chung
 * 
 * Base class for Options that start games
 */
public abstract class StartGameOptions extends MessagedOptions {

    /**
     * overrides Options.createWidgets(); does NOT add an action listener to btStart; the
     * implementing class should do that in its createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();
        tfSafeVelocityX = new JTextField(10);
        tfSafeVelocityY = new JTextField(10);
        tfSafeAngle = new JTextField(10);
        tfTurbo = new JTextField(10);
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        mapSelection = createMapSelectionOptions();
        subOptions.add(mapSelection);
    }
    
    protected MapSelectionPanel createMapSelectionOptions() {
        return new MapListSelectionPanel(getGameType());
    }
    
    /**
     * overrides Options.fillDefaults()
     */
    protected void fillDefaults() {
        super.fillDefaults();
        String prefix = getGameType().getPrefix();
        tfSafeVelocityX.setText("" + Settings.getDefaultDouble(prefix+Settings.SAFE_VX));
        tfSafeVelocityY.setText("" + Settings.getDefaultDouble(prefix+Settings.SAFE_VY));
        tfSafeAngle.setText("" + Settings.getDefaultDouble(prefix+Settings.SAFE_ANGLE));
        tfTurbo.setText("" + Settings.getDefaultDouble(prefix+Settings.TURBO));
    }

    /**
     * implements Options.fillSettings()
     */
    protected void fillSettings() {
        super.fillSettings();
        
        String prefix = getGameType().getPrefix();
        tfSafeVelocityX.setText("" + Settings.getDouble(prefix+Settings.SAFE_VX));
        tfSafeVelocityY.setText("" + Settings.getDouble(prefix+Settings.SAFE_VY));
        tfSafeAngle.setText("" + Settings.getDouble(prefix+Settings.SAFE_ANGLE));
        tfTurbo.setText("" + Settings.getDouble(prefix+Settings.TURBO));
    }

    /**
     * implements Options.saveSettings()
     */
    public void saveSettings() {
        super.saveSettings();
        
        String prefix = getGameType().getPrefix();
        Settings.setDouble(prefix+Settings.TURBO, Double.parseDouble(tfTurbo.getText()));
        Settings.setDouble(prefix+Settings.SAFE_VX, Double.parseDouble(tfSafeVelocityX.getText()));
        Settings.setDouble(prefix+Settings.SAFE_VY, Double.parseDouble(tfSafeVelocityY.getText()));
        Settings.setDouble(prefix+Settings.SAFE_ANGLE, Double.parseDouble(tfSafeAngle.getText()));
    }

    /**
     * implements Options.createMainPanel()
     */
    protected JComponent createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel fieldsAndBoxes = new JPanel();
        fieldsAndBoxes.setLayout(new FlowLayout());
        fieldsAndBoxes.add(createTextFieldsPanel(5));
        fieldsAndBoxes.add(createCheckBoxesPanel());

        mainPanel.add(fieldsAndBoxes);
        mainPanel.add(createMapSelectionPanel());

        panel.add(mainPanel, "Center");

        return panel;
    }

    /**
     * create a JPanel for text fields
     * 
     * @param rows number of text fields
     * @return the JPanel
     */
    protected JPanel createTextFieldsPanel(int rows) {
        JPanel panel = new JPanel(new GridLayout(rows, 2));

        panel.add(new JLabel("Turbo: "));
        panel.add(tfTurbo);

        panel.add(new JLabel("Maximum horizontal landing speed (m/s): "));
        panel.add(tfSafeVelocityX);

        panel.add(new JLabel("Maximum vertical landing speed (m/s): "));
        panel.add(tfSafeVelocityY);

        panel.add(new JLabel("Maximum landing angle (degrees): "));
        panel.add(tfSafeAngle);

        return panel;
    }

    /**
     * create a JPanel for check boxes.
     * 
     * @return the JPanel
     */
    protected JPanel createCheckBoxesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    /**
     * create a JPanel for map selection
     * 
     * @return such a JPanel
     */
    protected JPanel createMapSelectionPanel() {
        JPanel panel = new JPanel();
        panel.add(mapSelection);
        return panel;
    }

    /**
     * create a JPanel for the terminal buttons (OK, ResetToDefault, Cancel)
     * 
     * @return the JPanel
     */
    protected JPanel createEndButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        btStart = new JButton("Start Game");
        btStart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                start();
            }
        });

        btResetToDefault = new JButton("Reset to Default Settings");
        btResetToDefault.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                resetToDefaults();
            }
        });

        btCancel = new JButton("Cancel");
        btCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                cancel();
            }
        });
    
        panel.add(btStart);
        panel.add(btResetToDefault);
        panel.add(btCancel);
        return panel;
    }
    
    /**
     * starts the game; called when user hits "Start"
     */
    protected void start() {
        saveSettings();
        prepareForExit();
    }

    /**
     * resets field values to defaults; called when user hits "Reset To Default"
     */
    protected void resetToDefaults() {
        fillDefaults();
    }

    /**
     * called when user hits "Cancel"
     */
    protected void cancel() {
        exitOptions();
    }   
    
    protected abstract GameType getGameType();
    
    protected void prepareForDisplay() {
        super.prepareForDisplay();
        mapSelection.prepareForDisplay();
    }

    JTextField tfSafeVelocityX;
    JTextField tfSafeVelocityY;
    JTextField tfSafeAngle;
    JTextField tfTurbo;

    JButton btStart;
    JButton btResetToDefault;
    JButton btCancel;

    MapSelectionPanel mapSelection;
}