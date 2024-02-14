package lunarlander.options;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import javax.swing.*;

import lunarlander.canvas.MiniMapCanvas;
import lunarlander.game.GameType;
import lunarlander.game.LunarLanderGame;
import lunarlander.map.FlatMap;
import lunarlander.map.LoadedMap;
import lunarlander.map.GameMap;
import lunarlander.map.MoonFileException;
import lunarlander.map.RandomMap;
import lunarlander.map.Terrain;
import lunarlander.Settings;

/*
 * Created on Jan 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class MapSelectionPanel extends Options {
	
    /**
     * Constructor
     * 
     * @param gamePrefix prefix string of the game type
     * @param layout HORIZONTAL if random options are to right of map canvas, VERTICAL
     * 				 if under map canvas
     * @param size pixel width and height of the map panel
     */
    public MapSelectionPanel(GameType gameType, int layout, int size) {
        super();
        this.gameType = gameType;
        this.layout = layout;

        this.worldWidth = Settings.getDefaultDouble(gameType.getPrefix() + Settings.WORLD_WIDTH);
        this.worldHeight = Settings.getDefaultDouble(gameType.getPrefix() + Settings.WORLD_HEIGHT);
        this.gravity = Settings.getDefaultDouble(gameType.getPrefix() + Settings.GRAVITY);
        this.numLongPads = Settings.getDefaultInt(gameType.getPrefix() + Settings.LONG_PADS);
        this.numShortPads = Settings.getDefaultInt(gameType.getPrefix() + Settings.SHORT_PADS);
        filteredMaps = new ArrayList<GameMap>();
        this.mapCanvasSize = size;
    }
    
    /**
     * Constructor
     * 
     * @param gamePrefix prefix string of the game type
     * @param layout HORIZONTAL if random options are to right of map canvas, VERTICAL
     * 				 if under map canvas
     */
    public MapSelectionPanel(GameType gameType, int layout) {
        this(gameType, layout, MiniMapCanvas.DEFAULT_SIZE);
    }

    /**
     * @return array of Maps the user has selected
     */
    public GameMap[] getSelectedMaps() {
        return new GameMap[] { getSelectedMap() };
    }

    /**
     * implements Options.createWidgets()
     */
    protected void createWidgets() {

        mapCanvas = new MiniMapCanvas(mapCanvasSize);

        tfWorldWidth = new JTextField(5);
        tfNumOfLongPads = new JTextField(5);
        tfNumOfShortPads = new JTextField(5);

        coGameTypeChooser = new JComboBox(GameType.values());
        
        for (int i=0; i<GameType.values().length; i++) {
            if (GameType.values()[i] == gameType) {
                coGameTypeChooser.setSelectedIndex(i);
            }
        }
        
        coGameTypeChooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                updateMapChooser();
            }
        });

        coMapChooser = new JComboBox();
        coMapChooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                // if someone chose a different map from the drop-down, update the text field
                // properties
                mapCanvas.setMap((GameMap) filteredMaps.get(coMapChooser.getSelectedIndex()));
                updateRandomOptions();
            }
        });

        updateMapChooser();
    }

    /**
     * implements Options.fillDefaults(); clears selectedMaps and leaves only a random map
     */
    protected void fillDefaults() {
        super.fillDefaults();
        
        for(int i=0; i<GameType.values().length; i++) {
            if (gameType == GameType.values()[i]) {
                coGameTypeChooser.setSelectedIndex(i);
            }
        }
    }

    /**
     * implements Options.fillSettings(); drop-down selects the random map by default
     */
    protected void fillSettings() {
        super.fillSettings();
        
        updateMapChooser();
        coMapChooser.setSelectedIndex(0);
        mapCanvas.setMap(getSelectedMap());
        updateRandomOptions();
    }

    /**
     * implements Options.saveSettings(); makes a copy of the selectedMaps
     */
    public void saveSettings() {
        super.saveSettings();
        
        GameMap[] maps = {getSelectedMap()};
        Settings.set(gameType.getPrefix()+Settings.MAPS, maps);
    }

    /**
     * implements Options.createMainPanel(); contains the map selection/preview panel, add/remove
     * panel and the selected maps panel in a row
     */
    protected JComponent createMainPanel() {
        return createComboPreviewPanel();
    }

    /**
     * implements Options.createEndButtonsPanel(); contains nothing
     */
    protected JPanel createEndButtonsPanel() {
        JPanel panel = new JPanel();
        return panel;
    }

    /**
     * @return panel containing the preview window and the combo box
     */
    protected JPanel createComboPreviewPanel() {
        JPanel panel = new JPanel();

        if (layout == VERTICAL) {
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        }
        else {
            panel.setLayout(new FlowLayout());
        }
        panel.add(mapCanvas);

        JPanel selectPanel = new JPanel();

        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
        //selectPanel.add(coGameTypeChooser);
        selectPanel.add(coMapChooser);
        //selectPanel.add(createRandomOptionsPanel());
        panel.add(selectPanel);

        return panel;
    }

    /**
     * @return panel containing settable options for random terrain
     */
    protected JPanel createRandomOptionsPanel() {
        JPanel randomOptionsPanel = new JPanel(new GridLayout(3, 2));

        randomOptionsPanel.add(new JLabel("World width:"));
        randomOptionsPanel.add(tfWorldWidth);

        randomOptionsPanel.add(new JLabel("Num long pads:"));
        randomOptionsPanel.add(tfNumOfLongPads);

        randomOptionsPanel.add(new JLabel("Num short pads:"));
        randomOptionsPanel.add(tfNumOfShortPads);

        return randomOptionsPanel;
    }

    /**
     * updates filteredMaps and content of coMapChooser depending on the filter set by
     * coGameTypeChooser
     */
    protected void updateMapChooser() {

        filteredMaps.clear();

        filteredMaps
                .add(new RandomMap(worldWidth, worldHeight, gravity, numLongPads, numShortPads));
        filteredMaps.add(new FlatMap(worldWidth, worldHeight, gravity));

        int gameType = LunarLanderGame.GAME_TYPES[coGameTypeChooser.getSelectedIndex()];

        for (int i = 0; i < allMaps.size(); i++) {
            GameMap map = allMaps.get(i);
            if (map.canBePlayedAs(gameType)) {
                filteredMaps.add(map);
            }
        }

        coMapChooser.setModel(new DefaultComboBoxModel(getMapChoiceStrings(filteredMaps)));
        coMapChooser.setSelectedIndex(RANDOM_INDEX);
    }

    /**
     * @param maps an ArrayList of Maps
     * @return array of Strings to use for the JComboBox containing shortName of each map
     */
    protected String[] getMapChoiceStrings(ArrayList maps) {
        String[] names = new String[maps.size()];
        for (int i = 0; i < maps.size(); i++) {
            names[i] = ((GameMap) maps.get(i)).shortName();
        }
        return names;
    }

    /**
     * finds all files in the "maps" directory (under the current directory) that ends with ".moon".
     * 
     * @return array of Files
     */
    protected static File[] getAllMapFiles() {
        File currentDirectory = new File("./maps");
        File[] mapFiles = currentDirectory.listFiles(new FileFilter() {

            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".moon");
            }
        });

        if (mapFiles == null) {
            return new File[0];
        }
        else {
            return mapFiles;
        }
    }

    /**
     * tries to load Files found by getAllMapFiles() into Maps, disgarding those that throw
     * exceptions
     * 
     * @return ArrayList of valid maps
     */
    protected static ArrayList<GameMap> getAllMaps() {
        File[] allMapFiles = getAllMapFiles();
        ArrayList<GameMap> allMaps = new ArrayList<GameMap>(allMapFiles.length);

        for (int i = 0; i < allMapFiles.length; i++) {
            try {
                allMaps.add(new LoadedMap(allMapFiles[i]));
            }
            catch (MoonFileException e) {
                System.out.println("Error reading map file " + allMapFiles[i].getName() + "; " + e);
            }
        }
        return allMaps;
    }

    /**
     * @return Moon currently selected by the combo box
     */
    protected GameMap getSelectedMap() {
        return (GameMap) filteredMaps.get(coMapChooser.getSelectedIndex());
    }

    /**
     * whenever the user chooses a different combo box option, this method is called,
     * disabling/enabling text fields and filling in the proper values
     */
    protected void updateRandomOptions() {

        if (coMapChooser.getSelectedIndex() == RANDOM_INDEX) {
            tfWorldWidth.setEnabled(true);
            tfNumOfLongPads.setEnabled(true);
            tfNumOfShortPads.setEnabled(true);
            tfWorldWidth.setText("" + worldWidth);
            tfNumOfLongPads.setText("" + numLongPads);
            tfNumOfShortPads.setText("" + numShortPads);
        }
        else if (coMapChooser.getSelectedIndex() == FLAT_INDEX) {
            tfWorldWidth.setEnabled(true);
            tfNumOfLongPads.setEnabled(false);
            tfNumOfShortPads.setEnabled(false);
            tfWorldWidth.setText("" + worldWidth);
            tfNumOfLongPads.setText("" + 1);
            tfNumOfShortPads.setText("" + 0);
        }
        else {
            tfWorldWidth.setEnabled(false);
            tfNumOfLongPads.setEnabled(false);
            tfNumOfShortPads.setEnabled(false);

            GameMap map = getSelectedMap();
            tfWorldWidth.setText("" + map.getWidth());
            tfNumOfLongPads.setText("" + map.getNumPads(Terrain.LONG_LANDING_PAD));
            tfNumOfShortPads.setText("" + map.getNumPads(Terrain.SHORT_LANDING_PAD));
        }
    }
    
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }


    protected JComboBox coMapChooser;
    protected JComboBox coGameTypeChooser;
    protected JTextField tfWorldWidth;
    protected JTextField tfNumOfLongPads;
    protected JTextField tfNumOfShortPads;
    protected ArrayList<GameMap> filteredMaps;
    protected MiniMapCanvas mapCanvas;
    protected double worldWidth;
    protected double worldHeight;
    protected double gravity;
    protected int numLongPads;
    protected int numShortPads;

    protected GameType gameType;
    protected int layout;
    protected int mapCanvasSize;

    // all MapSelectionPanel uses the same list of Maps; this is to avoid reading map files for
    // every selection panel
    protected static ArrayList<GameMap> allMaps = getAllMaps();

    public static final int RANDOM_INDEX = 0;
    public static final int FLAT_INDEX = 1;

    // LAYOUT CONSTANTS
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    
    private static final long serialVersionUID = 1L;

}