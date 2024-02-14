package lunarlander.canvas;

import java.awt.*;

import lunarlander.gameobject.LunarLander;
import lunarlander.gameobject.PreviewLunarLander;


/*
 * Created on Jan 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IconCanvas extends Canvas {

    public IconCanvas(PreviewLunarLander lander) {
        super();
        llx = 0;
        lly = 0;
        urx = LunarLander.LANDER_LENGTH;
        ury = LunarLander.LANDER_LENGTH;
        this.lander = lander;
        this.setSize(new Dimension(VIEW_SIZE, VIEW_SIZE));
    }
    
    /* (non-Javadoc)
     * @see Canvas#getWorldWidth()
     */
    public double getWorldWidth() {
        // TODO Auto-generated method stub
        return WORLD_SIZE;
    }

    /* (non-Javadoc)
     * @see Canvas#getWorldHeight()
     */
    public double getWorldHeight() {
        // TODO Auto-generated method stub
        return WORLD_SIZE;
    }

    /* (non-Javadoc)
     * @see Canvas#paintScreen(java.awt.Graphics)
     */
    public void paintScreen(Graphics g) {
        // TODO Auto-generated method stub
        lander.draw(g, this);
    }   
    
    private PreviewLunarLander lander;
    public static final int WORLD_SIZE = 50;
    public static final int VIEW_SIZE = 20;
    
    private static final long serialVersionUID = 1L;

}
