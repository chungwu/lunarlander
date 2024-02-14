package lunarlander.options;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import lunarlander.*;
import lunarlander.canvas.MiniLanderCanvas;
import lunarlander.game.LunarLanderGame;
import lunarlander.game.LunarLanderDuo;
import lunarlander.player.Player;

/**
 * @author Chung
 * 
 * Options panel for changing player options like name, color, etc.
 */
public class PlayerOptions extends MessagedOptions {

    /**
     * Constructor; defaults to player one settings
     */
    public PlayerOptions() {
        this(LunarLanderDuo.PLAYER_ONE);
    }

    /**
     * Constructor
     * 
     * @param playerNumber PLAYER_ONE or PLAYER_TWO
     */
    public PlayerOptions(int playerNumber) {
        this.playerNumber = playerNumber;
        if (playerNumber == LunarLanderDuo.PLAYER_ONE) {
            playerPrefix = Settings.PLAYER_ONE;
        }
        else {
            playerPrefix = Settings.PLAYER_TWO;
        }
    }

    /**
     * apply the current settings onto the current game, if one exists
     */
    public void forceCurrentGame() {
        LunarLanderGame game = LunarLanderLauncher.game;
        if (game != null) {
            Player player = (Player) game.getPlayer(playerNumber);
            player.setName(Settings.getString(playerPrefix + Settings.NAME));
            player.setColor(Settings.getColor(playerPrefix + Settings.COLOR));
            player.getLander().setColor(player.getColor());
            player.getLander().setLandingAssist(Settings.getBoolean(playerPrefix + Settings.LANDING_ASSIST));
        }
    }

    /**
     * overrides MessagedOptions.getInstructions()
     */
    protected String getInstructions() {
        return "Customize your player information here.";
    }

    /**
     * overrides MessagedOptions.createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();
        tfPlayerName = new JTextField(20);

        String[] colorNames = new String[SELECTABLE_COLORS.length + 1];
        for (int i = 0; i < SELECTABLE_COLOR_NAMES.length; i++) {
            colorNames[i] = SELECTABLE_COLOR_NAMES[i].toString();
        }

        colorNames[colorNames.length - 1] = "Custom...";
        coColorChooser = new JComboBox(colorNames);
        coColorChooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updateSliders();
            }
        });

        ColorChangeListener listener = new ColorChangeListener();

        slColorRed = new JSlider(0, 255);
        slColorRed.setMajorTickSpacing(50);
        slColorRed.setMinorTickSpacing(1);
        slColorRed.setPaintTicks(true);
        slColorRed.setPaintLabels(true);
        slColorRed.addChangeListener(listener);

        slColorGreen = new JSlider(0, 255);
        slColorGreen.setMajorTickSpacing(50);
        slColorGreen.setMinorTickSpacing(1);
        slColorGreen.setPaintTicks(true);
        slColorGreen.setPaintLabels(true);
        slColorGreen.addChangeListener(listener);

        slColorBlue = new JSlider(0, 255);
        slColorBlue.setMajorTickSpacing(50);
        slColorBlue.setMinorTickSpacing(1);
        slColorBlue.setPaintTicks(true);
        slColorBlue.setPaintLabels(true);
        slColorBlue.addChangeListener(listener);

        landerCanvas = new MiniLanderCanvas();

        lbRed = new JLabel("Red:  ");
        lbRed.setHorizontalAlignment(SwingConstants.LEFT);
        lbGreen = new JLabel("Green:  ");
        lbGreen.setHorizontalAlignment(SwingConstants.LEFT);
        lbBlue = new JLabel("Blue:  ");
        lbBlue.setHorizontalAlignment(SwingConstants.LEFT);

        cbLandingAssist = new JCheckBox("Landing assist");
    }

    /**
     * overrides MessagedOptions.fillDefaults()
     */
    protected void fillDefaults() {
        super.fillDefaults();
        
        tfPlayerName.setText(Settings.getDefaultString(playerPrefix + Settings.NAME));

        Color color = Settings.getDefaultColor(playerPrefix + Settings.COLOR);

        coColorChooser.setSelectedIndex(SELECTABLE_COLORS.length);
        for (int i = 0; i < SELECTABLE_COLORS.length; i++) {
            if (color.equals(SELECTABLE_COLORS[i])) {
                coColorChooser.setSelectedIndex(i);
                break;
            }
        }
        cbLandingAssist.setSelected(Settings.getDefaultBoolean(playerPrefix + Settings.LANDING_ASSIST));
    }

    /**
     * overrides MessagedOptions.fillSettings()
     */
    protected void fillSettings() {
        super.fillSettings();
        
        tfPlayerName.setText((Settings.getString(playerPrefix + Settings.NAME)));
        
        Color color = Settings.getColor(playerPrefix + Settings.COLOR);
        coColorChooser.setSelectedIndex(SELECTABLE_COLORS.length);
        for (int i = 0; i < SELECTABLE_COLORS.length; i++) {
            if (color.equals(SELECTABLE_COLORS[i])) {
                coColorChooser.setSelectedIndex(i);
            }
        }
        updateSliders();

        cbLandingAssist.setSelected(Settings.getBoolean(playerPrefix + Settings.LANDING_ASSIST));
    }

    /**
     * overrides MessagedOptions.saveSettings()
     */
    public void saveSettings() {
        super.saveSettings();
        
        Settings.setString(playerPrefix+Settings.NAME, tfPlayerName.getText());
        Settings.setColor(playerPrefix+Settings.COLOR, getSelectedColor());
        Settings.setBoolean(playerPrefix+Settings.LANDING_ASSIST, cbLandingAssist.isSelected());
    }

    /**
     * overrides MessagedOptions.createMainPanel
     */
    protected JComponent createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createOthersPanel());
        panel.add(createColorPanel());

        return panel;
    }

    /**
     * @return panel for settings other than choosing color
     */
    protected JPanel createOthersPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(createTwoColumnPanel());
        panel.add(createCheckBoxPanel());
        return panel;
    }

    /**
     * @return panel of checkboxes
     */
    protected JPanel createCheckBoxPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(cbLandingAssist);
        return panel;
    }

    /**
     * overrides ButtonedPanel.createEndButtonsPanel(); returns an empty panel
     */
    protected JPanel createEndButtonsPanel() {
        return new JPanel();
    }

    /**
     * @return panel with two-column-settings
     */
    protected JPanel createTwoColumnPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        panel.add(new JLabel("Player Name: "));
        panel.add(tfPlayerName);

        /*
         * panel.add(new JLabel("Team: ")); panel.add(coTeamChooser);
         * 
         * panel.add(new JLabel("Role: ")); panel.add(coRoleChooser);
         */

        return panel;
    }

    /**
     * @return panel for choosing colors
     */
    protected JPanel createColorPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lander Appearance"));

        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));

        JPanel color = new JPanel(new FlowLayout());
        color.add(new JLabel("Color: "));
        color.add(coColorChooser);

        JPanel red = new JPanel(new FlowLayout());
        red.add(lbRed);
        red.add(slColorRed);

        JPanel green = new JPanel(new FlowLayout());
        green.add(lbGreen);
        green.add(slColorGreen);

        JPanel blue = new JPanel(new FlowLayout());
        blue.add(lbBlue);
        blue.add(slColorBlue);

        settings.add(color);
        settings.add(red);
        settings.add(green);
        settings.add(blue);

        panel.add(settings);
        panel.add(landerCanvas);

        wrapper.add(panel);

        return wrapper;
    }

    /**
     * updates the sliders to reflect the current color chosen by coColorChooser
     */
    protected void updateSliders() {
        if (coColorChooser.getSelectedIndex() == SELECTABLE_COLORS.length) {
            slColorRed.setEnabled(true);
            slColorBlue.setEnabled(true);
            slColorGreen.setEnabled(true);
            lbRed.setEnabled(true);
            lbGreen.setEnabled(true);
            lbBlue.setEnabled(true);
        }
        else {
            Color color = SELECTABLE_COLORS[coColorChooser.getSelectedIndex()];
            slColorRed.setEnabled(false);
            slColorBlue.setEnabled(false);
            slColorGreen.setEnabled(false);
            slColorRed.setValue(color.getRed());
            slColorBlue.setValue(color.getBlue());
            slColorGreen.setValue(color.getGreen());
            lbRed.setEnabled(false);
            lbGreen.setEnabled(false);
            lbBlue.setEnabled(false);
        }
        landerCanvas.setColor(getSelectedColor());
    }

    /**
     * @return Color based on slider values
     */
    protected Color getSelectedColor() {
        return new Color(slColorRed.getValue(), slColorGreen.getValue(), slColorBlue.getValue());
    }

    /**
     * overrides Options.prepareForDisplay(); starts lander canvas animation
     */
    protected void prepareForDisplay() {
        super.prepareForDisplay();
        landerCanvas.startAnimation();
    }

    /**
     * overrides Options.prepareForExit(); stops lander canvas animation
     */
    protected void prepareForExit() {
        super.prepareForExit();
        landerCanvas.stopAnimation();
    }


    /**
     * @author Chung
     * 
     * Private class that updates the canvas with the new RGB value when someone changes the sliders
     */
    class ColorChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent event) {
            updateSliders();
        }
    }


    protected JTextField tfPlayerName;
    protected JComboBox coColorChooser;
    protected JSlider slColorRed;
    protected JSlider slColorBlue;
    protected JSlider slColorGreen;
    protected JLabel lbRed;
    protected JLabel lbGreen;
    protected JLabel lbBlue;
    protected MiniLanderCanvas landerCanvas;

    JCheckBox cbLandingAssist;

    protected int playerNumber;
    protected String playerPrefix;

    public static final String[] SELECTABLE_COLOR_NAMES = { "Orange", "Green", "Blue", "Red",
            "Pink", "Yellow", "Magenta", "Cyan", "Light Gray", "White" };
    public static final Color[] SELECTABLE_COLORS = { Color.orange, Color.green,
            new Color(0, 150, 255), new Color(255, 50, 0), Color.pink, Color.yellow, Color.magenta,
            Color.cyan, Color.lightGray, Color.white };
    
    private static final long serialVersionUID = 1L;
}