/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import lunarlander.canvas.Canvas;
import lunarlander.util.Vect2D;
import lunarlander.map.Terrain;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class SegmentOptions extends JPanel {

    public SegmentOptions(MapEditorPane pane) {

        this.editorPane = pane;

        tfX = new JTextField(5);
        tfY = new JTextField(5);
        tfMult = new JTextField(5);
        tfMult.setEnabled(false);

        coLandingPad = new JComboBox(new String[] { "Plain", "Short Pad", "Long Pad" });

        btApply = new JButton("Apply");
        btApply.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editorPane.getMap().moveSegment(segment, Double.parseDouble(tfX.getText()),
                        Double.parseDouble(tfY.getText()));

                switch (coLandingPad.getSelectedIndex()) {
                    case 0:
                        segment.setMultiplier(0);
                        break;
                    case 1:
                        try {
                            segment.makePad(Terrain.SHORT_LANDING_PAD);
                        }
                        catch (CannotMakePadException cme) {
                            JOptionPane.showMessageDialog(LunarLanderMapEditor.frame, cme
                                    .getMessage(), "Cannot Make Landing Pad!",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        break;

                    case 2:
                        try {
                            segment.makePad(Terrain.LONG_LANDING_PAD);
                        }
                        catch (CannotMakePadException cme) {
                            JOptionPane.showMessageDialog(LunarLanderMapEditor.frame, cme
                                    .getMessage(), "Cannot Make Landing Pad!",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                }
                
                setSegment(segment);
                LunarLanderMapEditor.setupFrame();
            }
        });

        btRestore = new JButton("Cancel");
        btRestore.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setSegment(segment);
            }
        });

        btDelete = new JButton("Delete");
        btDelete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editorPane.getMap().deleteSegment(segment);
                editorPane.showMapOptions();
            }
        });

        this.add(new JLabel("X:"));
        this.add(tfX);
        this.add(new JLabel("Y:"));
        this.add(tfY);
        this.add(coLandingPad);
        this.add(btApply);
        this.add(btRestore);
        this.add(btDelete);
    }

    public void setSegment(DraftTerrainSegment newseg) {
        segment = newseg;
        Vect2D midp = segment.getMidPoint();
        tfX.setText("" + Canvas.round(midp.getX(), 2));
        tfY.setText("" + Canvas.round(midp.getY(), 2));
        tfMult.setText("" + segment.getMultiplier());
        
        int mult = segment.getMultiplier();
        
        if (mult == 0) {
            coLandingPad.setSelectedIndex(0);
        }
        else {
            int padType = Terrain.getPadType(mult);
            if (padType == Terrain.SHORT_LANDING_PAD) {
                coLandingPad.setSelectedIndex(1);
            } else {
                coLandingPad.setSelectedIndex(2);
            }
        }
    }


    private MapEditorPane editorPane;
    private DraftTerrainSegment segment;
    private JTextField tfX;
    private JTextField tfY;
    private JTextField tfMult;
    private JComboBox coLandingPad;
    private JButton btApply;
    private JButton btRestore;
    private JButton btDelete;

    private static final long serialVersionUID = 1L;
}
