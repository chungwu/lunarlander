/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import lunarlander.canvas.Canvas;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class PointOptions extends JPanel {

    public PointOptions(MapEditorPane p) {

        this.editorPane = p;

        tfX = new JTextField(5);
        tfY = new JTextField(5);

        btApply = new JButton("Apply");
        btApply.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editorPane.getMap().movePoint(point, Double.parseDouble(tfX.getText()),
                        Double.parseDouble(tfY.getText()));
                setPoint(point);
                LunarLanderMapEditor.setupFrame();
            }
        });

        btRestore = new JButton("Cancel");
        btRestore.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setPoint(point);
            }
        });

        btDelete = new JButton("Delete");
        btDelete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editorPane.getMap().deletePoint(point);
                editorPane.showMapOptions();
            }
        });

        this.add(new JLabel("X:"));
        this.add(tfX);
        this.add(new JLabel("Y:"));
        this.add(tfY);
        this.add(btApply);
        this.add(btRestore);
        this.add(btDelete);
    }

    public void setPoint(DraftPoint newp) {
        point = newp;
        tfX.setText("" + Canvas.round(newp.getX(), 2));
        tfY.setText("" + Canvas.round(newp.getY(), 2));

        if (point.getX() == 0) {
            tfX.setEnabled(false);
            btDelete.setEnabled(false);
        }
        else {
            tfX.setEnabled(true);
            btDelete.setEnabled(true);
        }
    }


    private MapEditorPane editorPane;
    private DraftPoint point;
    private JTextField tfX;
    private JTextField tfY;
    private JButton btApply;
    private JButton btRestore;
    private JButton btDelete;
    
    private static final long serialVersionUID = 1L;
}
