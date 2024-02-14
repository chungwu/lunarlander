package lunarlander.options;

import javax.swing.*;
import lunarlander.*;

/**
 * @author Chung
 * 
 * Options panel for game-wide settings like trace, anti-aliasing and resolution
 */
public class GameWideOptions extends MessagedOptions {

    /**
     * overrides MessageOptions.createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();
        String[] resolutions = { "Small (800 x 600)", "Medium (1024 x 768)", "Large (1280 x 960)" };
        coCanvasResolution = new JComboBox(resolutions);
        coCanvasResolution.setSelectedIndex(0);

        cbEnableTrace = new JCheckBox("Enable trace");
        cbAntiAlias = new JCheckBox("Anti-aliased graphics");
        cbEnableSafetyDisplay = new JCheckBox("Display safe landing velocities and angles");
    }

    /**
     * implements MessagedOptions.getInstructions()
     */
    protected String getInstructions() {
        return "You can configure game-wide settings here; they will take effect immediately.";
    }

    /**
     * implements Options.fillDefaults()
     */
    protected void fillDefaults() {
        super.fillDefaults();
        
        coCanvasResolution.setSelectedIndex(Settings.getDefaultInt(Settings.RESOLUTION));
        cbEnableTrace.setSelected(Settings.getDefaultBoolean(Settings.TRACE));
        cbAntiAlias.setSelected(Settings.getDefaultBoolean(Settings.ANTIALIAS));
        cbEnableSafetyDisplay.setSelected(Settings.getDefaultBoolean(Settings.DISPLAY_SAFETY));
    }

    /**
     * implements Options.fillSettings()
     */
    protected void fillSettings() {
        super.fillSettings();
        
        coCanvasResolution.setSelectedIndex(Settings.getInt(Settings.RESOLUTION));
        cbEnableTrace.setSelected(Settings.getBoolean(Settings.TRACE));
        cbAntiAlias.setSelected(Settings.getBoolean(Settings.ANTIALIAS));
        cbEnableSafetyDisplay.setSelected(Settings.getBoolean(Settings.DISPLAY_SAFETY));
    }

    /**
     * implements Options.saveSettings()
     */
    public void saveSettings() {
        super.saveSettings();
        
        Settings.setBoolean(Settings.TRACE, cbEnableTrace.isSelected());
        Settings.setBoolean(Settings.ANTIALIAS, cbAntiAlias.isSelected());
        Settings.setBoolean(Settings.DISPLAY_SAFETY, cbEnableSafetyDisplay.isSelected());

        int newCanvasResolution = coCanvasResolution.getSelectedIndex();
        if (Settings.getInt(Settings.RESOLUTION) != newCanvasResolution) {
            Settings.setInt(Settings.RESOLUTION, newCanvasResolution);
            LunarLanderLauncher.canvas.setPreferredSize(LunarLanderLauncher
                    .preferredCanvasDimension());
            LunarLanderLauncher.mainOptions.prepareForDisplay();
            LunarLanderLauncher.frame.pack();
        }
    }

    /**
     * implements Options.createMainPanel()
     */
    protected JComponent createMainPanel() {
        JPanel panel = new JPanel();
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.add(coCanvasResolution);
        main.add(cbAntiAlias);
        main.add(cbEnableTrace);
        main.add(cbEnableSafetyDisplay);
        panel.add(main);
        return panel;
    }

    /**
     * implements Options.createEndButtonsPanel()
     */
    protected JPanel createEndButtonsPanel() {
        return new JPanel();
    }


    protected JComboBox coCanvasResolution;
    protected JCheckBox cbAntiAlias;
    protected JCheckBox cbEnableTrace;
    protected JCheckBox cbEnableSafetyDisplay;
    
    private static final long serialVersionUID = 1L;
}