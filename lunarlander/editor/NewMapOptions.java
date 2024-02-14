/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import java.awt.GridLayout;

import javax.swing.*;


/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewMapOptions extends JPanel {
    public NewMapOptions() {
        this.add(createSizePanel());
    }
    
    private JPanel createSizePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        
        panel.add(new JLabel("Map Name:"));
        tfName = new JTextField("Awesome Map #" + (int)(1000*Math.random()), 15);
        panel.add(tfName);
        
        panel.add(new JLabel("World Width:"));
        tfWidth = new JTextField(15);
        tfWidth.setText("" + DraftMap.DEFAULT_WORLD_WIDTH);
        panel.add(tfWidth);
        
        panel.add(new JLabel("World Height:"));
        tfHeight = new JTextField(15);
        tfHeight.setText("" + DraftMap.DEFAULT_WORLD_HEIGHT);
        panel.add(tfHeight);
        
        return panel;
    }
    
    public String getMapName() {
        return tfName.getText();
    }
    
    public double getWorldWidth() {
        return Double.parseDouble(tfWidth.getText());
    }
    
    public double getWorldHeight() {
        return Double.parseDouble(tfHeight.getText());
    }
    
    private JTextField tfWidth;
    private JTextField tfHeight;
    private JTextField tfName;
    
    private static final long serialVersionUID = 1L;
}
