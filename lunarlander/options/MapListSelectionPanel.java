package lunarlander.options;

import java.awt.event.*;
import javax.swing.*;

import lunarlander.game.GameType;
import lunarlander.map.FlatMap;
import lunarlander.map.GameMap;
import lunarlander.map.RandomMap;
import lunarlander.*;

import java.awt.*;
import java.util.Vector;

/**
 * @author Chung
 * 
 * A JPanel that allows the user to choose which map to use.
 */
public class MapListSelectionPanel extends MapSelectionPanel {

    /**
     * Constructor
     * 
     * @param gameType default game type this selection panel is for
     */
    public MapListSelectionPanel(GameType gameType) {
        super(gameType, MapSelectionPanel.VERTICAL);
        
    }

    /**
     * implements Options.createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();

        btAdd = new JButton("Add >>");
        btAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                GameMap selected = getSelectedMap();
                GameMap mapToAdd;

                // if the user wants to add a RANDOM map, then create a new RandomMap with the
                // specified width/etc
                if (selected.getMapType() == GameMap.RANDOM) {
                    double width = Double.parseDouble(tfWorldWidth.getText());
                    double height = worldHeight * width / worldWidth;
                    int numOfLongPads = Integer.parseInt(tfNumOfLongPads.getText());
                    int numOfShortPads = Integer.parseInt(tfNumOfShortPads.getText());
                    mapToAdd = new RandomMap(width, height, gravity, numOfLongPads, numOfShortPads);
                }
                // else if the user wants to add a FLAT map, then create a new FlatMap with the
                // specified width
                else if (selected.getMapType() == GameMap.FLAT) {
                    double width = Double.parseDouble(tfWorldWidth.getText());
                    double height = worldHeight * width / worldWidth;
                    mapToAdd = new FlatMap(width, height, gravity);
                }
                else {
                    mapToAdd = selected;
                }
                if (!selectedMaps.contains(mapToAdd)) {
                    selectedMaps.add(mapToAdd);
                    lsSelectedMaps.setListData(selectedMaps);
                }
            }
        });

        btRemove = new JButton("<< Remove");
        btRemove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                // remove every selected map from selectedMaps
                Object[] selected = lsSelectedMaps.getSelectedValues();
                for (int i = 0; i < selected.length; i++) {
                    selectedMaps.remove(selected[i]);
                }
                lsSelectedMaps.setListData(selectedMaps);
            }
        });

        selectedMaps = new Vector<GameMap>();
        selectedMaps.add(filteredMaps.get(MapSelectionPanel.RANDOM_INDEX));
        lsSelectedMaps = new JList(selectedMaps);
        
        Settings.set(gameType.getPrefix()+Settings.MAPS_VECTOR, selectedMaps.clone());
    }

    /**
     * implements Options.fillDefaults(); clears selectedMaps and leaves only a random map
     */
    protected void fillDefaults() {
        super.fillDefaults();
        selectedMaps.clear();
        selectedMaps.add(filteredMaps.get(MapSelectionPanel.RANDOM_INDEX));
        lsSelectedMaps.setListData(selectedMaps);
    }

    /**
     * implements Options.fillSettings(); drop-down selects the random map by default
     */
    protected void fillSettings() {
        super.fillSettings();
        selectedMaps = new Vector<GameMap>((Vector<GameMap>) Settings.get(gameType.getPrefix()+Settings.MAPS_VECTOR));
        lsSelectedMaps.setListData(selectedMaps);
    }

    /**
     * implements Options.saveSettings(); makes a copy of the selectedMaps
     */
    public void saveSettings() {
        super.saveSettings();
        
        Settings.set(gameType.getPrefix()+Settings.MAPS_VECTOR, selectedMaps.clone());
        
        GameMap[] maps = new GameMap[selectedMaps.size()];
        maps = (GameMap[]) selectedMaps.toArray(maps);
        Settings.set(gameType.getPrefix()+Settings.MAPS, maps);
    }

    /**
     * implements Options.createMainPanel(); contains the map selection/preview panel, add/remove
     * panel and the selected maps panel in a row
     */
    protected JComponent createMainPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(createComboPreviewPanel());
        panel.add(createAddRemovePanel());
        panel.add(createSelectedMapsPanel());
        return panel;
    }

    /**
     * implements Options.createEndButtonsPanel(); contains nothing
     */
    protected JPanel createEndButtonsPanel() {
        JPanel panel = new JPanel();
        return panel;
    }

    /**
     * @return panel containing "Add" and "Remove" buttons
     */
    protected JPanel createAddRemovePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(btAdd);
        panel.add(btRemove);

        return panel;
    }

    /**
     * @return panel containing JList with selected maps
     */
    protected JPanel createSelectedMapsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(lsSelectedMaps);

        panel.add(new JLabel("Selected Maps:"));
        panel.add(scrollPane);
        return panel;
    }


    protected JList lsSelectedMaps;
    protected Vector<GameMap> selectedMaps;
    protected JButton btAdd;
    protected JButton btRemove;
    
    private static final long serialVersionUID = 1L;
}