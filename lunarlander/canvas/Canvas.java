package lunarlander.canvas;

import java.awt.*;

import javax.swing.*;

import lunarlander.util.Vect2D;


/**
 * @author Chung
 * 
 * A "Canvas" is something that draws the world (Moon). Apparently. For some reason.
 * 
 * A Canvas has llx, lly, urx and ury, which draws the section of the world that the Canvas is
 * currently displaying. Common code for difference canvases are also pushed to here.
 */
public abstract class Canvas extends JPanel {

    /**
     * Get x coordinate of the lower left corner of the viewing window
     * 
     * @return the x coordinate of the lower left corner of the viewing window
     */
    public double getLLX() {
        return llx;
    }

    /**
     * Get y coordinate of the lower left corner of the viewing window
     * 
     * @return the y coordinate of the lower left corner of the viewing window
     */
    public double getLLY() {
        return lly;
    }

    /**
     * Get x coordinate of the upper right corner of the viewing window
     * 
     * @return the x coordinate of the upper right corner of the viewing window
     */
    public double getURX() {
        return urx;
    }

    /**
     * Get Y coordinate of the upper right corner of the viewing window
     * 
     * @return the y coordinate of the upper right corner of the viewing window
     */
    public double getURY() {
        return ury;
    }

    /**
     * @return the width of the world this Canvas displays
     */
    public abstract double getWorldWidth();

    /**
     * @return width of the world this Canvas displays
     */
    public abstract double getWorldHeight();

    /**
     * converts world x-coordinate to canvas x-coordinate
     * 
     * @param worldX x-coordinate in the world
     * @return x-coordinate in the canvas
     */
    public int worldToCanvasX(double worldX) {
        return (int)((double) getWidth() * ((worldX - llx) / (urx - llx)));
    }
    
    /**
     * converts world length to canvas length
     * 
     * @param worldLength length in world coordinates
     * @return length in canvas coordinates
     */
    public int worldToCanvasLength(double worldLength) {
        return (int) ((double) getWidth() * worldLength / (urx-llx));
    }

    /**
     * converts world y-coordinate to canvas y-coordinate
     * 
     * @param worldY y-coordinate in the world
     * @return y-coordinate in the canvas
     */
    public int worldToCanvasY(double worldY) {
        return (int) ((double) getHeight() * (1.0 - (worldY - lly) / (ury - lly)));
    }
    
    /**
     * converts world height to canvas height
     * 
     * @param worldHeight height in world coordinates
     * @return world height in canvas coordinates
     */
    public int worldToCanvasHeight(double worldHeight) {
        return (int) ((double)getHeight() * worldHeight / (ury-lly));
    }

    /**
     * converts world coordinate to canvas coordinate
     * 
     * @param worldPoint coordinate in the world
     * @return coordinate in the canvas
     */
    public Vect2D worldToCanvas(Vect2D worldPoint) {
        return new Vect2D(worldToCanvasX(worldPoint.getX()), worldToCanvasY(worldPoint.getY()));
    }

    /**
     * converts canvas x-coordinate to world x-coordinate
     * 
     * @param canvasX x-coordinate in the canvas
     * @return x-coordinate in the world
     */
    public double canvasToWorldX(int canvasX) {
        double x = ((double)canvasX / getWidth() * (urx-llx)) + llx;
        double worldWidth = getWorldWidth();
        while (x > worldWidth) {
            x -= worldWidth;
        }
        while(x < 0) {
            x += worldWidth;
        }
        return x;
    }
    
    /**
     * converts canvas length to world length
     * 
     * @param canvasLength length in canvas coordinates
     * @return length in world coordinates
     */
    public double canvasToWorldLength(int canvasLength) {
        return (double)canvasLength / getWidth() * (urx-llx);
    }

    /**
     * converts canvas y-coordinate to world y-coordinate
     * 
     * @param canvasY y-coordinate in the canvas
     * @return y-coordinate in the world
     */
    public double canvasToWorldY(int canvasY) {
        return ((double)canvasY / getHeight() * (ury-lly)) + lly;
    }
    
    /**
     * converts canvas height to world height
     * 
     * @param canvasHeight height in canvas coordinates
     * @return height in world coordinates
     */
    public double canvasToWorldHeight(int canvasHeight) {
        return (double)canvasHeight / getHeight() * (ury-lly);
    }

    /**
     * converts canvas coordinate to world coordinate
     * 
     * @param canvasPoint coordinate in the canvas
     * @return coordinate in the world
     */
    public Vect2D canvasToWorld(Vect2D canvasPoint) {
        return new Vect2D(canvasToWorldX((int)canvasPoint.getX()), canvasToWorldY((int)canvasPoint.getY()));
    }

    /**
     * does the real work of painting the canvas onto g
     * 
     * @param g Graphics context
     */
    public abstract void paintScreen(Graphics g);

    /**
     * overrides JComponent.paintComponent(); just calls paintScreen
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintScreen(g);
    }

    /**
     * Used to implement double-buffering; paints into an offscreen buffer and transfers it onto the
     * argument Graphics
     */
    public void updateOffscreenBuffer() {
        Graphics offScreenGraphics;

        // Check if dimensions have changed or buffer not created
        if (offScreenBuffer == null
                || (!(offScreenBuffer.getWidth(this) == this.getSize().width && offScreenBuffer
                        .getHeight(this) == this.getSize().height))) {
            offScreenBuffer = this.createImage(getSize().width, getSize().height);
        }

        offScreenGraphics = offScreenBuffer.getGraphics();

        // Paint onto offscreen buffer
        paint(offScreenGraphics);
    }

    /**
     * Transfer the offScreenBuffer to the active screen
     */
    public void updateActiveScreen() {

        updateOffscreenBuffer();

        Graphics g = this.getGraphics();
        if ((g != null) && offScreenBuffer != null) {
            g.drawImage(offScreenBuffer, 0, 0, this);
            g.dispose();
        }
    }

    /**
     * Round a floating point number to the given number of decimal places
     */
    public static double round(double val, int precision) {
        val = Math.floor(val * Math.pow(10, precision) + 0.5);
        return val / Math.pow(10, precision);
    }

    /**
     * Draw the polyline
     * 
     * @param g is the graphics context
     * @param lines is the array of polylines to draw
     */
    public void drawPolyline(Graphics g, Color color, Vect2D[] lines) {
        g.setColor(color);
        double worldWidth = getURX() - getLLX();
        double worldHeight = getURY() - getLLY();
        int canvasWidth = getWidth();
        int canvasHeight = getHeight();

        Vect2D translation = new Vect2D(-getLLX(), -getLLY());

        for (int i = 0; i < lines.length - 1; i++) {
            // Translate the lines so that the coordinates are offsets from the
            // points (LLX, LLY).
            Vect2D v1 = translation.add(lines[i]);
            Vect2D v2 = translation.add(lines[i + 1]);

            // Convert world coordinates to screen coordinates
            int x1 = (int) ((double) canvasWidth * v1.getX() / worldWidth);
            int y1 = (int) ((double) canvasHeight * (1.0 - (v1.getY() / worldHeight)));
            int x2 = (int) ((double) canvasWidth * v2.getX() / worldWidth);
            int y2 = (int) ((double) canvasHeight * (1.0 - (v2.getY() / worldHeight)));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Draw a 1 pixel dot
     * 
     * @param g is the graphics context
     * @param color is the color of the dot
     * @param position is the location in game coordinates
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

        //int screenX = (int) ((double) this.getWidth() * (posX - llx) / (urx - llx));
        //int screenY = (int) ((double) this.getHeight() * (1.0 - (posY - lly) / (ury - lly)));
        
        int screenX = worldToCanvasX(posX);
        int screenY = worldToCanvasY(posY);

        // draw on screen, using the color that it should
        g.setColor(color);
        g.drawOval(screenX, screenY, 1, 1);
    }
    
    /**
     * Draw a string
     * @param g Graphics context
     * @param position location of string in CANVAS coordinates
     * @param s string to draw
     */
    public void drawString(Graphics g, Vect2D position, String s) {       
        int strWidth = g.getFontMetrics().stringWidth(s);
        double worldWidth = getWorldWidth();
        g.drawString(s, (int)position.getX() -strWidth/2, (int)position.getY());
        /*
        for(int j=0; j<urx/worldWidth; j++) {
            int x = (int)position.getX() + j*worldToCanvasLength(worldWidth);
            g.drawString(s, x-strWidth/2, (int)position.getY());
        }
        */
    }
    
    /**
     * draw an oval on canvas
     * @param g Graphics context
     * @param x x-coord in WORLD
     * @param y y-coord in WORLD
     * @param r1 length in WORLD
     * @param r2 height in WORLD
     */
    public void drawOval(Graphics g, double x, double y, double r1, double r2) {
        int cx = worldToCanvasX(x);
        int cy = worldToCanvasY(y);
        int cr1 = worldToCanvasLength(r1);//(int)(this.getWidth() * r1 / (urx-llx));
        int cr2 = worldToCanvasHeight(r1);//(int)(this.getHeight() * r2 / (ury-lly));
        g.drawOval(cx, cy, cr1, cr2);
    }
    
    /**
     * fills an oval on canvas
     * @param g Graphics context
     * @param x x-coord in CANVAS
     * @param y y-coord in CANVAS
     * @param r1 length in CANVAS
     * @param r2 height in CANVAS
     */
    public void fillOval(Graphics g, int x, int y, int r1, int r2) {
        g.fillOval(x, y, r1, r2);
    }
    
    /**
     * @return aspect ratio (width/height) of this canvas
     */
    public double aspectRatio() {
        Dimension dim = this.getSize();
        return dim.getWidth() / dim.getHeight();
    }


    protected double llx; // Lower-left x coordinate of viewing window
    protected double lly; // Lower-left y coordinate of viewing window
    protected double urx; // Upper-right x coordinate of viewing window
    protected double ury; // Upper-right y coordinate of viewing window

    protected Image offScreenBuffer; // Used for double buffering
}