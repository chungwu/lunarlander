/*
 * Created on Mar 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import lunarlander.util.Vect2D;
import lunarlander.gameobject.Drawable;
import lunarlander.canvas.Canvas;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class DraftPoint extends Vect2D implements Drawable {

    public DraftPoint(double x, double y, DraftMap map) {
        super(x, y);
        this.map = map;
    }

    public DraftPoint(Vect2D point, DraftMap map) {
        this(point.getX(), point.getY(), map);
    }

    public void draw(Graphics g, Canvas canvas, boolean selected) {
        int canvasX = canvas.worldToCanvasX(getX()) - POINT_RADIUS;
        int canvasY = canvas.worldToCanvasY(getY()) - POINT_RADIUS;

        int canvasWorldWidth = canvas.worldToCanvasLength(canvas.getWorldWidth());

        if (selected) {
            g.setColor(Color.yellow);

            int sideLength = POINT_RADIUS * 2 + 2;

            for (int j = 0; j < canvas.getURX() / canvas.getWorldWidth(); j++) {
                g.drawRect(canvasX + POINT_RADIUS - sideLength / 2 + j * canvasWorldWidth, canvasY
                        + POINT_RADIUS - sideLength / 2, sideLength, sideLength);
            }

            g.setColor(Color.red.brighter());
        }
        else {
            g.setColor(Color.cyan);
        }
        for (int j = 0; j < canvas.getURX() / canvas.getWorldWidth(); j++) {
            canvas.fillOval(g, canvasX + j * canvasWorldWidth, canvasY, POINT_RADIUS * 2,
                    POINT_RADIUS * 2);
        }
    }

    public void draw(Graphics g, Canvas canvas) {
        draw(g, canvas, false);
    }
    
    /**
     * Checks vector equality
     */
    public boolean equals(Object o) {
        return this == o;
    }


    private DraftMap map;

    private static final int POINT_RADIUS = 3;
}
