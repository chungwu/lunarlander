package lunarlander.canvas;


import java.awt.*;
import java.util.Iterator;

import lunarlander.game.LunarLanderGame;
import lunarlander.*;
import lunarlander.gameobject.*;
import lunarlander.player.Player;


/**
 * This is the main canvas for the Lunar Lander game. It is responsible for drawing the world and
 * zooming/panning as necessary.
 * 
 * @author Michael Yu
 */
public abstract class GameCanvas extends Canvas {

    /**
     * Construct a new game canvas object
     */
    public GameCanvas(LunarLanderGame llg) {
        this.lunarLanderGame = llg;
    }

    /**
     * Reset the canvas
     */
    public void reset() {
        llx = 0.0;
        urx = lunarLanderGame.moon.getWorldWidth();
        lly = 0.0;
        ury = urx / Settings.getDefaultDouble(Settings.ASPECT_RATIO);;
    }

    /**
     * Paint the canvas, either the title screen or the game screen, depending on the "mode" flag
     * 
     * @param g is the graphics context
     */
    public void paintScreen(Graphics g) {
        if (Settings.getBoolean(Settings.ANTIALIAS)) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        setBackground(Color.black);
        paintInGame(g);
    }

    /**
     * Update viewing window in the world right before paintInGame
     */
    public abstract void updateViewingWindow();
    
    /**
     * Paint the screen when in game. Draws, in this order: - previous lander positions for each
     * lander, if trace flag on - the Moon terrain - any main display message - the landers - the
     * dashboard
     * 
     * CONSTRAINTS: draw moon after landers
     * 
     * @param g is the graphics context
     */
    public void paintInGame(Graphics g) {

        updateViewingWindow();
        
        drawBumpables(g);
        
        // Draw moon terrain
        lunarLanderGame.moon.draw(g, this);
        
        drawDashboard(g);
        
        // Draw any display messages
        if (displayMessage != null) {
            g.setColor(Color.white);
            Font f = new Font("Courier", Font.BOLD, Math.min(36, getHeight()));
            g.setFont(f);

            int strWidth = g.getFontMetrics().stringWidth(displayMessage);
            g.drawString(displayMessage, (getWidth() - strWidth) / 2,
                    (getHeight() + f.getSize()) / 2);
        }
    }

    /**
     * Draw all the bumpable objects
     * 
     * @param g the graphics context
     */
    public void drawBumpables(Graphics g) {
        synchronized (LunarLanderLauncher.game.bumpables) {
            for (Iterator it = lunarLanderGame.bumpables.iterator(); it.hasNext();) {
                Bumpable b = (Bumpable) it.next();
                b.draw(g);
            }
        }
    }

    /**
     * Draws the dashboard info for the particular lander at a particular x-offset
     * 
     * @param g Graphics context
     * @param xOffset offset from the left edge of the screen
     * @param player player to draw the dashboard for
     * @param nonScoreFontSize font size for non-score data
     * @param scoreFontSize font size for score
     */
    public void drawDashboard(Graphics g, int xOffset, Player p, int nonScoreFontSize,
            int scoreFontSize) {
        Graphics2D g2 = (Graphics2D) g;
        java.awt.font.FontRenderContext frc = g2.getFontRenderContext();
        Font f = new Font("Courier", Font.PLAIN, nonScoreFontSize);
        Font boldF = new Font("Courier", Font.BOLD, scoreFontSize);
        g.setFont(f);

        LunarLander lander = p.getLander();
        
        // TODO: REMOVE THIS STUPID HACK!!!
        if (lander == null) return;

        int canvasWidth = this.getWidth();
        int fontHeight = (int) f.getStringBounds("0", frc).getHeight();
        int fontWidth = (int) f.getStringBounds("w", frc).getWidth();
        int boldFontWidth = (int) boldF.getStringBounds(" ", frc).getWidth();
        int colWidth1 = 21 * fontWidth;
        int colWidth2 = 8 * fontWidth;
        int ulx = xOffset;
        int urx = ulx + colWidth1 + colWidth2;

        // Turn of anti-aliasing
        Object aaStatus = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setColor(p.getColor());
        //      Draw Score
        g.setFont(boldF);
        String score = "" + p.getScore();
        g.drawString("SCORE", ulx, DASHBOARD_TOP_MARGIN + fontHeight);
        g
                .drawString(score, urx - g.getFontMetrics().stringWidth(score), DASHBOARD_TOP_MARGIN
                        + fontHeight);
        g.setFont(f);

        // Draw Fuel
        String fuel = "" + round(lander.getFuel(), 1);
        g.drawString("FUEL", ulx, DASHBOARD_TOP_MARGIN + 2 * fontHeight);
        g.drawString(fuel, urx - g.getFontMetrics().stringWidth(fuel), DASHBOARD_TOP_MARGIN + 2 * fontHeight);

        // Draw Altitude
        String altitude = "" + round(lander.getPosition().getY(), 1);
        g.drawString("ALTITUDE", ulx, DASHBOARD_TOP_MARGIN + 3 * fontHeight);
        g.drawString(altitude, urx - g.getFontMetrics().stringWidth(altitude), DASHBOARD_TOP_MARGIN + 3
                * fontHeight);

        //      Draw Angle
        String angle = "" + round(Math.toDegrees(lander.getAngle()), 1);
        g.drawString("ANGLE", ulx, DASHBOARD_TOP_MARGIN + 4 * fontHeight);
        g
                .drawString(angle, urx - g.getFontMetrics().stringWidth(angle), DASHBOARD_TOP_MARGIN + 4
                        * fontHeight);

        // Draw Horizontal Velocity
        double roundedHV = round(lander.getVelocity().getX(), 1);
        String hv = Math.abs(roundedHV) + " ";
        if (lander.getVelocity().getX() > 0) {
            hv += "\u2192"; // Up arrow
        }
        else if (lander.getVelocity().getX() < 0) {
            hv += "\u2190"; // Down arrow
        }
        else {
            hv += " ";
        }
        g.drawString("HORIZONTAL VELOCITY", ulx, DASHBOARD_TOP_MARGIN + 5 * fontHeight);
        g.drawString(hv, urx - g.getFontMetrics().stringWidth(hv), DASHBOARD_TOP_MARGIN + 5
                * fontHeight);

        // Draw Vertical Velocity
        double roundedVV = round(lander.getVelocity().getY(), 1);
        String vv = Math.abs(roundedVV) + " ";
        if (lander.getVelocity().getY() > 0) {
            vv += "\u2191"; // Right arrow
        }
        else if (lander.getVelocity().getY() < 0) {
            vv += "\u2193"; // Left arrow
        }
        else {
            vv += " ";
        }
        
        g.drawString("VERTICAL VELOCITY", ulx, DASHBOARD_TOP_MARGIN + 6 * fontHeight);
        g.drawString(vv, urx - g.getFontMetrics().stringWidth(vv), DASHBOARD_TOP_MARGIN + 6
                * fontHeight);

        // Restore anti-aliasing settings
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aaStatus);

    }

    /**
     * Draws the dashboard for each lander
     * 
     * @param g is the graphics context
     */
    public void drawDashboard(Graphics g) {

        int dashboardWidth = getWidth() - DASHBOARD_SIDE_MARGIN;

        synchronized (LunarLanderLauncher.game.players) {
            int numDashboards = lunarLanderGame.players.size();

            for (int i = 0; i < numDashboards; i++) {
                this.drawDashboard(g, DASHBOARD_SIDE_MARGIN + i * dashboardWidth / numDashboards,
                        (Player) lunarLanderGame.players.get(numDashboards - i - 1),
                        NONSCORE_FONT_SIZE, SCORE_FONT_SIZE);
            }
        }
    }

    /**
     * Set the in-game display message
     * 
     * @param message is the message to be displayed in game
     */
    public void setDisplayMessage(String message) {
        displayMessage = message;
    }

    /**
     * implements Canvas.getWorldWidth()
     */
    public double getWorldWidth() {
        return LunarLanderLauncher.game.moon.getWorldWidth();
    }

    /**
     * implements Canvas.getWorldHeight()
     */
    public double getWorldHeight() {
        return LunarLanderLauncher.game.moon.getWorldHeight();
    }

    // dashboard constants
    public static final int DASHBOARD_SIDE_MARGIN = 50;
    public static final int DASHBOARD_TOP_MARGIN = 20;
    public static final int NONSCORE_FONT_SIZE = 13;
    public static final int SCORE_FONT_SIZE = 15;

    // Instance variables
    protected String displayMessage; // The message to display
    protected LunarLanderGame lunarLanderGame;
}