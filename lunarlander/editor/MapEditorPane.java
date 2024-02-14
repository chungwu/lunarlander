/*
 * Created on Mar 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.*;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class MapEditorPane extends JPanel {

    public MapEditorPane(DraftMap map) {
        this.map = map;
        miniOptions = new JPanel();
        canvas = new MapEditorCanvas(map, this);

        // miniOptions.setPreferredSize(new Dimension(LunarLanderMapEditor.frame.getWidth()-100,
        // 50));

        canvas.setPreferredSize(new Dimension(LunarLanderMapEditor.frame.getWidth(),
                LunarLanderMapEditor.canvas.getHeight() - 60));

        mapOptions = new MapOptions(map);
        pointOptions = new PointOptions(this);
        segmentOptions = new SegmentOptions(this);

        this.setLayout(new BorderLayout());

        this.add(canvas, "Center");

        scrollPane = new JScrollPane(miniOptions, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setPreferredSize(new Dimension(LunarLanderMapEditor.frame.getWidth(), 60));

        System.out.println("Map options size: " + mapOptions.getMinimumSize());

        this.add(scrollPane, "South");

        switchMiniOptions(mapOptions);
    }

    public DraftMap getMap() {
        return map;
    }

    public void showMapOptions() {
        switchMiniOptions(mapOptions);
        map.unselect();
    }

    public void showPointOptions(DraftPoint point) {
        pointOptions.setPoint(point);
        switchMiniOptions(pointOptions);
    }

    public void showSegmentOptions(DraftTerrainSegment segment) {
        segmentOptions.setSegment(segment);
        switchMiniOptions(segmentOptions);
    }

    private void switchMiniOptions(JPanel newOptions) {
        miniOptions.removeAll();
        miniOptions.add(newOptions);
        LunarLanderMapEditor.setupFrame();
        scrollPane.revalidate();
        miniOptions.repaint();
    }


    private DraftMap map;
    private JPanel miniOptions;
    private JScrollPane scrollPane;
    private MapEditorCanvas canvas;
    private MapOptions mapOptions;
    private PointOptions pointOptions;
    private SegmentOptions segmentOptions;

    private static final long serialVersionUID = 1L;
}
