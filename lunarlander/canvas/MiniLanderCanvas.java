package lunarlander.canvas;


import java.awt.*;

import javax.swing.*;


import java.awt.event.*;

import lunarlander.Settings;
import lunarlander.game.LunarLanderGame;
import lunarlander.gameobject.PreviewLunarLander;
import lunarlander.player.PreviewLanderPlayer;
import lunarlander.util.Vect2D;

/**
 * @author Chung
 * 
 * A Canvas that displays a preview lander for the PlayerOptions screen
 */
public class MiniLanderCanvas extends Canvas {

    /**
     * Constructor
     */
    public MiniLanderCanvas() {
        player = new PreviewLanderPlayer(null);
        player.setColor(Color.orange);
        lander = new PreviewLunarLander(new Vect2D(WORLD_WIDTH / 2, WORLD_HEIGHT / 2), new Vect2D(
                0, 0), 0, player);
        player.setLander(lander);
        player.updateRandomly();

        // every time the timer goes off, step a few steps, depending on the TURBO
        timer = new Timer((int) (LunarLanderGame.DRAW_DT * 1000), new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                for (int i = 0; i < TURBO * LunarLanderGame.DRAW_DT / LunarLanderGame.DT; i++) {
                    step(LunarLanderGame.DT);
                }
            }
        });

        this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // center the lander in the canvas
        llx = lander.getPosition().getX() - MARGIN;
        lly = lander.getPosition().getY() - MARGIN;
        urx = lander.getPosition().getX() + MARGIN;
        ury = lander.getPosition().getY() + MARGIN;
    }

    /**
     * implements Canvas.getWorldWidth()
     */
    public double getWorldWidth() {
        return WORLD_WIDTH;
    }

    /**
     * implements Canvas.getWorldHeight()
     */
    public double getWorldHeight() {
        return WORLD_HEIGHT;
    }

    /**
     * stops the timer, tells lander to step, tells player to play, repaints the canvas, and
     * restarts the timer
     * 
     * @param dt timestep size
     */
    protected void step(double dt) {
        timer.stop();
        lander.step(dt);
        player.play();
        this.repaint();
        timer.restart();
    }

    /**
     * implements Canvas.paintScreen; updates the viewing window to always include the lander, and
     * paints the lander
     */
    public void paintScreen(Graphics g) {
        updateViewingWindow();

        if (Settings.getBoolean(Settings.ANTIALIAS)) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        setBackground(Color.black);
        lander.draw(g, this);
    }

    /**
     * similar to SingleGameCanvas.updateViewingWindow() except no zooming, and wraps in the y
     * direction as well
     */
    public void updateViewingWindow() {
        double posX = lander.getPosition().getX();
        double posY = lander.getPosition().getY();

        // Calculate margin
        double windowWidth = urx - llx;
        double windowHeight = ury - lly;
        double margin = .15 * Math.min(windowWidth, windowHeight);

        // Wrap window location if lander location was wrapped
        if (posX < llx) {
            llx -= WORLD_WIDTH;
            urx -= WORLD_WIDTH;
        }
        if (posX > urx) {
            llx += WORLD_WIDTH;
            urx += WORLD_WIDTH;
        }
        if (posY < lly) {
            lly -= WORLD_HEIGHT;
            ury -= WORLD_HEIGHT;
        }
        if (posY > ury) {
            lly += WORLD_HEIGHT;
            ury += WORLD_HEIGHT;
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
     * changes the color of the player and the lander
     * @param c new Color
     */
    public void setColor(Color c) {
        player.setColor(c);
        lander.setColor(c);
    }

    /**
     * restarts the timer
     */
    public void startAnimation() {
        timer.restart();
    }

    /**
     * stops the timer
     */
    public void stopAnimation() {
        timer.stop();
    }

    /**
     * overrides Canvas.drawDot(); also wraps in the y direction
     */
    public void drawDot(Graphics g, Color color, Vect2D position) {

        double posX = position.getX();
        double posY = position.getY();

        // Wrap positions
        if (posX < llx) {
            posX += getWorldWidth();
        }
        else if (posX > urx) {
            posX -= getWorldWidth();
        }

        if (posY < lly) {
            posY += getWorldHeight();
        }
        else if (posY > ury) {
            posY -= getWorldHeight();
        }

        int screenX = (int) ((double) this.getWidth() * (posX - llx) / (urx - llx));
        int screenY = (int) ((double) this.getHeight() * (1.0 - (posY - lly) / (ury - lly)));

        // draw on screen, using the color that it should
        g.setColor(color);
        g.drawOval(screenX, screenY, 1, 1);
    }
    
    /**
     * @return the preview lander being drawn
     */
    public PreviewLunarLander getLander() {
        return lander;
    }


    protected PreviewLanderPlayer player;
    protected PreviewLunarLander lander;
    protected Timer timer;

    public static final int CANVAS_WIDTH = 200;
    public static final int CANVAS_HEIGHT = 200;
    public static final double WORLD_WIDTH = 1000;
    public static final double WORLD_HEIGHT = 1000;
    public static final double MARGIN = 50;
    public static final double TURBO = 4;
    
    private static final long serialVersionUID = 1L;
}