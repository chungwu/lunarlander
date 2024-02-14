/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.*;


/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MapEditorTitleCanvas extends JPanel {
    /**
     * Draws "Lunar Lander Map Editor"
     * @param g     Graphics context
     */
    public void paint(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);

        setBackground(Color.black);

        g.setColor(Color.white);
        Font f = new Font("Courier", Font.BOLD, Math.min(36, getHeight()));
        g.setFont(f);

        int strWidth = g.getFontMetrics().stringWidth("Lunar Lander");
        g.drawString("Lunar Lander", (getWidth() - strWidth) / 2, (getHeight() + f.getSize()) / 2);
        
        strWidth = g.getFontMetrics().stringWidth("Map Editor");
        g.drawString("Map Editor", (getWidth() - strWidth) / 2, (getHeight() + f.getSize()) / 2 + f.getSize());
    }
    
    private static final long serialVersionUID = 1L;
}
