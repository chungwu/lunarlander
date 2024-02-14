/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import lunarlander.gameobject.Drawable;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.util.List;

import lunarlander.canvas.Canvas;
import lunarlander.util.Vect2D;
import lunarlander.map.Terrain;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class DraftTerrainSegment implements Drawable {

    public DraftTerrainSegment(DraftPoint left, DraftPoint right, DraftMap map) {
        this(left, right, 0, map);
    }

    public DraftTerrainSegment(DraftPoint left, DraftPoint right, int mult, DraftMap map) {
        leftEndPoint = left;
        rightEndPoint = right;
        multiplier = mult;
        this.map = map;
    }

    public DraftPoint getLeftEndPoint() {
        return leftEndPoint;
    }
    
    public void setLeftEndPoint(DraftPoint p) {
        leftEndPoint = p;
    }

    public DraftPoint getRightEndPoint() {
        return rightEndPoint;
    }
    
    public void setRightEndPoint(DraftPoint p) {
        rightEndPoint = p;
    }

    public Vect2D getMidPoint() {
        if (rightEndPoint.getX() == 0) {
            return leftEndPoint.add(new Vect2D(map.getWorldWidth(), rightEndPoint.getY())).scale(
                    0.5);
        }
        else {
            return leftEndPoint.add(rightEndPoint).scale(0.5);
        }
    }

    public int getMultiplier() {
        return multiplier;
    }
    
    public void setMultiplier(int m) {
        multiplier = m;
    }

    public void draw(Graphics g, Canvas canvas) {
        draw(g, canvas, false);
    }

    public void makePad(int padType) throws CannotMakePadException {
        double padLength = Terrain.getPadLength(padType);

        List<DraftTerrainSegment> segments = map.getSegments();

        if (segments.size() == 1) {
            multiplier = Terrain.getPadMultiplier(padType);
            return;
        }

        int segmentIndex = segments.indexOf(this);
        DraftTerrainSegment leftSegment = (segmentIndex == 0) ? null : segments
                .get(segmentIndex - 1);
        DraftTerrainSegment rightSegment = (segmentIndex == segments.size() - 1) ? null : segments
                .get(segmentIndex + 1);

        DraftPoint pointToMove, pointToHold;
        double newX;

        if (rightEndPoint.getX() == 0) {
            pointToMove = leftEndPoint;
            pointToHold = rightEndPoint;
            newX = map.getWorldWidth() - padLength;
        }
        else if (rightSegment.isLandingPad() && rightEndPoint.getX() != padLength) {
            pointToMove = leftEndPoint;
            pointToHold = rightEndPoint;
            newX = rightEndPoint.getX() - padLength;
        }
        else {

            pointToMove = rightEndPoint;
            pointToHold = leftEndPoint;
            newX = leftEndPoint.getX() + padLength;
        }

        if (map.canMovePoint(pointToMove, newX, pointToHold.getY())) {
            map.movePoint(pointToMove, newX, pointToHold.getY());
            multiplier = Terrain.getPadMultiplier(padType);
            
            if (pointToMove == rightEndPoint) {
                rightSegment.setMultiplier(0);
            } else {
                leftSegment.setMultiplier(0);
            }
        }
        else if (rightEndPoint.getX() != 0
                && map
                        .canMovePoint(pointToHold, pointToMove.getX() - padLength, pointToMove
                                .getY())) {
            System.out.println("REVERSE MOVE!");
            map.movePoint(pointToHold, pointToMove.getX() - padLength, pointToMove.getY());
            multiplier = Terrain.getPadMultiplier(padType);
            
            if (pointToMove == rightEndPoint) {
                rightSegment.setMultiplier(0);
            } else {
                leftSegment.setMultiplier(0);
            }
        } else {
            throw new CannotMakePadException("Not enough room to make a landing pad here!");
        }
    }

    public void draw(Graphics g, Canvas canvas, boolean selected) {
        int x1 = canvas.worldToCanvasX(leftEndPoint.getX());
        int x2 = canvas.worldToCanvasX(rightEndPoint.getX());
        int y1 = canvas.worldToCanvasY(leftEndPoint.getY());
        int y2 = canvas.worldToCanvasY(rightEndPoint.getY());
        int canvasWorldWidth = canvas.worldToCanvasLength(canvas.getWorldWidth());

        if (rightEndPoint.getX() == 0) {
            x2 = canvas.worldToCanvasX(canvas.getWorldWidth());
        }

        for (int j = 0; j < canvas.getURX() / canvas.getWorldWidth(); j++) {

            if (selected) {
                g.setColor(Color.red.brighter().brighter());
            }
            else if (multiplier != 0) {
                g.setColor(Color.yellow);
            }
            else {
                g.setColor(Color.white);
            }
            g.drawLine(x1 + j * canvasWorldWidth, y1, x2 + j * canvasWorldWidth, y2);

            if (selected) {
                g.setColor(Color.yellow);
                g.drawLine(x1 + j * canvasWorldWidth, y1 + SELECTED_BAND_RADIUS, x2 + j
                        * canvasWorldWidth, y2 + SELECTED_BAND_RADIUS);
                g.drawLine(x1 + j * canvasWorldWidth, y1 - SELECTED_BAND_RADIUS, x2 + j
                        * canvasWorldWidth, y2 - SELECTED_BAND_RADIUS);
            }

            if (multiplier != 0) {
                g.setColor(Color.white);
                Font f = new Font("Courier", Font.PLAIN, Math.min(12, canvas.getHeight()));
                g.setFont(f);
                Vect2D midp = getMidPoint();
                Vect2D stringLoc = new Vect2D(canvas.worldToCanvasX(midp.getX()) + j
                        * canvasWorldWidth, canvas.worldToCanvasY(midp.getY()) + f.getSize() + 3);
                canvas.drawString(g, stringLoc, multiplier + "X");
            }
        }
    }

    public boolean isLandingPad() {
        return multiplier != 0;
    }
    
    public void refreshPadStatus() {
        if (multiplier != 0) {
            int padType = Terrain.getPadType(multiplier);
            if (leftEndPoint.getY() != rightEndPoint.getY()) {
                multiplier = 0;
            }
        }
    }

    public boolean contains(double x) {
        if (rightEndPoint.getX() == 0) {
            return leftEndPoint.getX() <= x;
        }
        else {
            return leftEndPoint.getX() <= x && rightEndPoint.getX() >= x;
        }
    }

    public double slope() {
        if (rightEndPoint.getX() == 0) {
            return (rightEndPoint.getY() - leftEndPoint.getY())
                    / (map.getWorldWidth() - leftEndPoint.getX());
        }
        else {
            return (rightEndPoint.getY() - leftEndPoint.getY())
                    / (rightEndPoint.getX() - leftEndPoint.getX());
        }
    }

    public double length() {
        if (rightEndPoint.getX() == 0) {
            return leftEndPoint.distance(new Vect2D(map.getWorldWidth(), rightEndPoint.getY()));
        }
        else {
            return leftEndPoint.distance(rightEndPoint);
        }
    }

    public double getHeight(double x) {
        if (!contains(x)) {
            throw new IllegalArgumentException("x coordinate " + x + " is not in this segment");
        }

        return leftEndPoint.getY() + slope() * (x - leftEndPoint.getX());
    }

    public String toString() {
        return "[" + leftEndPoint + "; " + rightEndPoint + "]";
    }


    private DraftPoint leftEndPoint;
    private DraftPoint rightEndPoint;
    private int multiplier;
    private DraftMap map;
    private static final int SELECTED_BAND_RADIUS = 3;
}
