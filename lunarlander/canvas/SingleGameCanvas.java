package lunarlander.canvas;


import java.awt.*;

import lunarlander.*;
import lunarlander.game.LunarLanderGame;
import lunarlander.gameobject.LunarLander;
import lunarlander.map.*;
import lunarlander.player.Player;

/**
 * This is the main canvas for the Lunar Lander game. It is responsible for drawing the world and
 * zooming/panning as necessary.
 * 
 * @author Michael Yu
 */
public class SingleGameCanvas extends GameCanvas {

    /**
     * Construct a new game canvas object
     * 
     * @param llg the game instance that is associated with this canvas
     */
    public SingleGameCanvas(LunarLanderGame llg) {
        super(llg);
        zoomed = false;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return (Player) lunarLanderGame.players.get(0);
    }

    /**
     * @return the lander
     */
    public LunarLander getLander() {
        return getPlayer().getLander();
    }

    /**
     * @return the score
     */
    public int getScore() {
        return getPlayer().getScore();
    }

    /**
     * Set viewing window to the 'zoomed out' state
     */
    public void zoomOut() {
        double posX = getLander().getPosition().getX();
        llx = posX - .5 * lunarLanderGame.moon.getWorldWidth();
        lly = 0;
        urx = posX + .5 * lunarLanderGame.moon.getWorldWidth();
        ury = lly + (urx-llx) / Settings.getDefaultDouble(Settings.ASPECT_RATIO);
        zoomed = false;
    }

    /**
     * Set viewing window to the 'zoomed in' state
     */
    public void zoomIn() {
        double width = (urx-llx)/3;
        double height = (ury-lly)/3;
        llx = getLander().getPosition().getX() - width / 2;
        urx = llx + width;
        lly = getLander().getPosition().getY() - 2 * height / 3;
        ury = lly + height;
        zoomed = true;
    }

    /**
     * Update the viewing window; zoom in or out or scroll if necessary
     */
    public void updateViewingWindow() {
        LunarLander lander = getLander();

        double posX = lander.getPosition().getX();
        double posY = lander.getPosition().getY();

        Moon moon = lunarLanderGame.moon;
        TerrainSegment segment = moon.getEncompassingTerrainSegment(posX);

        // Zoom in or out if necessary

        if (!zoomed && posY - segment.getLeftEndPoint().getY() < 170) {
            zoomIn();
        }
        else if (zoomed && posY - segment.getLeftEndPoint().getY() > 250) {
            zoomOut();
        }

        // Calculate margin
        double windowWidth = urx - llx;
        double windowHeight = ury - lly;
        double margin = .3 * Math.min(windowWidth, windowHeight);

        
        // Wrap window location if lander location was wrapped
        if (posX < llx) {
            llx -= lunarLanderGame.moon.getWorldWidth();
            urx -= lunarLanderGame.moon.getWorldWidth();
        }

        // Wrap window location if lander location was wrapped
        if (posX > urx) {
            llx += lunarLanderGame.moon.getWorldWidth();
            urx += lunarLanderGame.moon.getWorldWidth();
        }
        

        // Pan right
        if (posX - llx > windowWidth - margin) {
            llx = posX - windowWidth + margin;
            urx = llx + windowWidth;
        }

        // Pan left
        if (posX - llx < margin) {
            llx = posX - margin;
            urx = llx + windowWidth;
        }

        // Pan up
        if (posY - lly > windowHeight - margin) {
            lly = posY - windowHeight + margin;
            ury = lly + windowHeight;
        }

        // Pan down
        if (posY - lly < margin) {
            lly = posY - margin;
            ury = lly + windowHeight;
        }        
    }

    /**
     * overrides GameCanvas.paintInGame
     * 
     * also draws coordinates on screen
     * 
     * @param g is the graphics context
     */
    public void paintInGame(Graphics g) {
        updateViewingWindow();
        super.paintInGame(g);

        // draw coordinates
        drawCoordinates(g);
    }

    /**
     * Draw the lunar lander
     * 
     * @param g the graphics context
     */
    /**
     * overrides GameCanvas.drawLanders
     * 
     * Draws the single lander, but without rocket info or offscreen indicators
     */
    public void drawLanders(Graphics g) {
        getLander().draw(g);
    }

    /**
     * Draws the X coordinates of the world on the viewing window
     * 
     * @param g the graphics context
     */
    protected void drawCoordinates(Graphics g) {
        int interval = zoomed ? 20 : 50;
        double worldWidth = lunarLanderGame.moon.getWorldWidth();
        double canvasWidth = getWidth();

        Font f = new Font("Courier", Font.PLAIN, Math.min(12, getHeight()));
        g.setFont(f);

        for (int i = 0; i < (int) LunarLanderLauncher.game.map.getWidth(); i += interval) {

            int x1 = (int) ((double) canvasWidth * ((i - llx) / (urx - llx)));
            int x2 = (int) ((double) canvasWidth * ((i - worldWidth - llx) / (urx - llx)));
            int x3 = (int) ((double) canvasWidth * ((i + worldWidth - llx) / (urx - llx)));

            int strWidth = g.getFontMetrics().stringWidth("" + i);

            g.setColor(Color.white);
            g.drawString("" + i, x1 - strWidth / 2, getHeight() - 20 + f.getSize() + 3);
            g.drawString("" + i, x2 - strWidth / 2, getHeight() - 20 + f.getSize() + 3);
            g.drawString("" + i, x3 - strWidth / 2, getHeight() - 20 + f.getSize() + 3);
        }
    }
    
    private boolean zoomed; // true if the viewing window is currently "zoomed in"
    
    private static final long serialVersionUID = 1L;
}