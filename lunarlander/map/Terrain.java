
package lunarlander.map;


import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.swing.Timer;

import lunarlander.LunarLanderLauncher;
import lunarlander.canvas.Canvas;
import lunarlander.gameobject.Drawable;
import lunarlander.util.Vect2D;



/**
 * A wrapping terrain / landscape.
 * 
 * @author Michael Yu
 */
public class Terrain implements Drawable {

    /**
     * Constructs a new Terrain object, initializing the segment heights to the heights given in the
     * height map.
     * 
     * @param heightMap is an array of heights specifying the heights of the terrain segments
     * @param worldWidth is the width of the entire terrain
     */
    Terrain(double[] heightMap, double worldWidth) {
        this.worldWidth = worldWidth;
        this.segments = new ArrayList<TerrainSegment>(heightMap.length);
        double segmentWidth = worldWidth / heightMap.length;

        // Create the terrain segments
        for (int i = 0; i < heightMap.length; i++) {
            int iNext = (i + 1) % heightMap.length;
            Vect2D left = new Vect2D(i * segmentWidth, heightMap[i]);
            Vect2D right = new Vect2D((i + 1) * segmentWidth, heightMap[iNext]);
            segments.add(i, new TerrainSegment(left, right));
        }

        // Setup redrawTimer to blink the score multipliers
        timer = new Timer(900, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                timer.stop();
                TerrainSegment.setMultiplierDisplayed(!TerrainSegment.isMultiplierDisplayed());
                timer.start();
            }
        });
        timer.start();
    }

    /**
     * Constructor; creates from a list of segments
     * 
     * @param segments list of TerrainSegments
     * @param worldWidth width of world
     */
    Terrain(List<TerrainSegment> segments, double worldWidth) {
        this.worldWidth = worldWidth;
        this.segments = new ArrayList<TerrainSegment>(segments);
        
        // Setup redrawTimer to blink the score multipliers
        timer = new Timer(900, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                timer.stop();
                TerrainSegment.setMultiplierDisplayed(!TerrainSegment.isMultiplierDisplayed());
                timer.start();
            }
        });
        timer.start();
    }
    /**
     * Find the index to the segment that covers the given x position
     * 
     * @param x is the x position
     * @return the index to the segment that encompasses positionX
     */
    public int getEncompassingSegmentIndex(double x) {
        while (x < 0) {
            x += worldWidth;
        }

        while (x >= worldWidth) {
            x -= worldWidth;
        }

        for (int i = 0; i < segments.size(); i++) {
            TerrainSegment segment = (TerrainSegment) segments.get(i);
            if (segment.getLeftEndPoint().getX() <= x && x < segment.getRightEndPoint().getX()) {
                return i;
            }
        }

        // We should never get here. This should really throw an exception,
        // but the students haven't learned exceptions yet.
        System.err.println("OOPS! Tried getting segment index for position " + x);
        return -1;
    }

    /**
     * Find the segment that covers the given x position
     * 
     * @param x is the x position
     * @return the terrain segment that encompasses positions getX
     */
    public TerrainSegment getEncompassingSegment(double x) {
        return segments.get(getEncompassingSegmentIndex(x));
    }

    /**
     * Return the height of the terrain
     * 
     * @param x is the x position
     * @return the height of the terrain at positions getX
     */
    public double getHeight(double x) {
        while (x < 0) {
            x += worldWidth;
        }

        while (x >= worldWidth) {
            x -= worldWidth;
        }

        TerrainSegment segment = getEncompassingSegment(x);
        Vect2D left = segment.getLeftEndPoint();
        Vect2D right = segment.getRightEndPoint();
        double rise = right.getY() - left.getY();
        double run = right.getX() - left.getX();
        return left.getY() + (x - left.getX()) * rise / run;
    }

    /**
     * @param position a Vect2D in space
     * @return closest landing pad to that point
     */
    public TerrainSegment getClosestLandingPad(Vect2D position) {
        TerrainSegment closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        for(int i=0; i<segments.size(); i++) {
            TerrainSegment segment = segments.get(i);
            if (segment.isLandingPad()) {
                double dist = segment.getMidPoint().distance(position);
                if (dist < closestDistance) {
                    closestDistance = dist;
                    closest = segment;
                }
            }
        }
        return closest;
    }
    
    /**
     * Is there a landing pad directly below?
     * 
     * @param x is the x position
     * @return true if there is a landing pad at the given x position, false otherwise
     */
    public boolean isOverLandingPad(double x) {
        return getEncompassingSegment(x).isLandingPad();
    }

    /**
     * implements Drawable.draw()
     */
    public void draw(Graphics g) {
        draw(g, LunarLanderLauncher.game.canvas);
    }

    /**
     * Draw the terrain
     * 
     * @param g is the graphics context
     * @param canvas canvas to draw in
     */
    public void draw(Graphics g, Canvas canvas) {
        for (Iterator it = segments.iterator(); it.hasNext();) {
            TerrainSegment segment = (TerrainSegment) it.next();
            segment.draw(g, canvas);
        }
    }

    /**
     * Get all the terrain segments
     * 
     * @return a list of all the terrain segments
     */
    public List<TerrainSegment> getSegments() {
        return segments;
    }

    /**
     * @return width of terrain
     */
    public double getWorldWidth() {
        return worldWidth;
    }
    
    /**
     * @param padType type of pad
     * @return length of this padType
     */
    public static double getPadLength(int padType) {
        return PAD_LENGTHS[padType];
    }
    
    /**
     * @param padType type of pad
     * @return multiplier for this padType
     */
    public static int getPadMultiplier(int padType) {
        return PAD_MULTIPLIERS[padType];
    }
    
    /**    
     * @param multiplier multiplier
     * @return pad type corresponding to this multiplier
     */
    public static int getPadType(int multiplier) {
        for(int i=0; i<PAD_MULTIPLIERS.length; i++) {
            if (multiplier == PAD_MULTIPLIERS[i]) {
                return i;
            }
        }
        return -1;
    }


    private double worldWidth; // the width of the world
    private List<TerrainSegment> segments; // all the terrain segments
    private Timer timer; // redrawTimer used to blink the score multipliers

    public static final int SHORT_LANDING_PAD = 0;
    public static final int LONG_LANDING_PAD = 1;
    public static final double[] PAD_LENGTHS = {15, 30};
    public static final int[] PAD_MULTIPLIERS = {2, 1};
}