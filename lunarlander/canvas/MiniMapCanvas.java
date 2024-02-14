
package lunarlander.canvas;

import java.awt.*;
import lunarlander.*;
import lunarlander.map.GameMap;
import lunarlander.map.TerrainSegment;


/**
 * @author Chung
 * 
 * MiniMapCanvas draws a preview map on the map selection screen
 */
public class MiniMapCanvas extends Canvas {

	/**
	 * default constructor; uses the default size
	 */
	public MiniMapCanvas() {
		this(DEFAULT_SIZE);
	}
	
	/**
	 * constructor
	 * @param size screen pixel size of map canvas; will be width and height
	 */
	public MiniMapCanvas(int size) {
		super();
		screenSize = size;
	}
	
    /**
     * paints a preview map depending on which map is selected
     * 
     * @param g Graphics context
     */
    public void paintScreen(Graphics g) {
        if (Settings.getBoolean(Settings.ANTIALIAS)) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        setBackground(Color.black);
        if (currentMap.getMapType() == GameMap.RANDOM) {
            paintRandom(g);
        }
        else {
            paintMoon(g);
        }

        g.setColor(Color.white);
        Font f = new Font("Courier", Font.PLAIN, 12);
        g.setFont(f);
        g.drawString("SIZE: " + (int) currentMap.getWidth() + "x"
                + (int) currentMap.getHeight(), 10, f.getSize());
        g.drawString("GRAVITY: " + round(currentMap.getGravity(), 1), 10, 2 * f.getSize());
    }

    /**
     * if selected map is just "random", then just write "Random"
     * 
     * @param g Graphics context
     */
    protected void paintRandom(Graphics g) {
        g.setColor(Color.white);
        Font f = new Font("Courier", Font.BOLD, Math.min(20, getHeight()));
        g.setFont(f);

        int strWidth = g.getFontMetrics().stringWidth("Random");
        g.drawString("Random", (getWidth() - strWidth) / 2, (getHeight() + f.getSize()) / 2);
    }

    /**
     * if selected map is real, paint the map as well as its world size and gravity
     * 
     * @param g Graphics context
     */
    protected void paintMoon(Graphics g) {
        boolean oldMultiplierDisplayed = TerrainSegment.isMultiplierDisplayed();
        TerrainSegment.setMultiplierDisplayed(true);
        currentMap.getMoon().draw(g, this);
        TerrainSegment.setMultiplierDisplayed(oldMultiplierDisplayed);
    }

    /**
     * sets a new Moon to paint; adjust the sizes and the llx/lly/urx/ury, etc.
     * 
     * @param m the new Moon to draw
     */
    public void setMap(GameMap map) {
        this.currentMap = map;
        if (map != null) {
            llx = 0;
            lly = 0;
            urx = currentMap.getWidth();
            ury = urx / Settings.getDefaultDouble(Settings.ASPECT_RATIO);
        }

        this.setPreferredSize(new Dimension(screenSize, screenSize));
        
        this.repaint();
    }

    /**
     * implements Canvas.getWorldWidth()
     */
    public double getWorldWidth() {
        return currentMap.getWidth();
    }

    /**
     * implements Canvas.getWorldHeight()
     */
    public double getWorldHeight() {
        return currentMap.getHeight();
    }


    protected GameMap currentMap;
    protected int screenSize;
    public static final int DEFAULT_SIZE = 200;
    
    private static final long serialVersionUID = 1L;
}