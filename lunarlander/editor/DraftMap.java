/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import java.awt.Graphics;
import java.awt.Color;
import java.util.*;
import java.io.*;

import lunarlander.gameobject.Drawable;
import lunarlander.canvas.Canvas;
import lunarlander.util.Vect2D;
import lunarlander.map.*;
import lunarlander.game.LunarLanderGame;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class DraftMap implements Drawable {

    public DraftMap(NewMapOptions options) {
        worldWidth = options.getWorldWidth();
        worldHeight = options.getWorldHeight();

        isSingle = true;
        isDuo = false;
        isDeathmatch = false;
        isTeamDeathmatch = false;
        isCTF = false;
        name = options.getMapName();
        gravity = DEFAULT_GRAVITY;

        points = new LinkedList<DraftPoint>();
        selection = new ArrayList<Object>();

        points.add(new DraftPoint(0, 50, this));

        segments = new LinkedList<DraftTerrainSegment>();
        segments.add(new DraftTerrainSegment((DraftPoint) points.get(0),
                (DraftPoint) points.get(0), this));
    }

    public DraftMap(Moon moon) {
        name = moon.getMapName();
        gravity = moon.getGravity();
        worldWidth = moon.getWorldWidth();
        worldHeight = moon.getWorldHeight();

        isSingle = (moon.getGameType() & LunarLanderGame.SINGLE) != 0;
        isDuo = (moon.getGameType() & LunarLanderGame.DUO) != 0;
        isDeathmatch = (moon.getGameType() & LunarLanderGame.DEATHMATCH) != 0;
        isTeamDeathmatch = (moon.getGameType() & LunarLanderGame.TEAM_DEATHMATCH) != 0;
        isCTF = (moon.getGameType() & LunarLanderGame.CTF) != 0;

        List<TerrainSegment> terrainSegments = moon.getTerrain().getSegments();
        segments = new LinkedList<DraftTerrainSegment>();
        points = new LinkedList<DraftPoint>();
        selection = new ArrayList<Object>();

        DraftPoint prevPoint = new DraftPoint(terrainSegments.get(0).getLeftEndPoint(), this);
        points.addLast(prevPoint);

        for (TerrainSegment segment : terrainSegments) {
            DraftPoint newPoint = new DraftPoint(segment.getRightEndPoint(), this);
            points.addLast(newPoint);

            DraftTerrainSegment newSegment = new DraftTerrainSegment(prevPoint, newPoint, segment
                    .getMultiplier(), this);
            segments.addLast(newSegment);

            prevPoint = newPoint;
        }

        segments.getLast().setRightEndPoint(points.getFirst());
        System.out.println("Last segment: " + segments.getLast());

        System.out.println("Removing point " + points.removeLast());

    } /*
         * (non-Javadoc)
         * 
         * @see lunarlander.gameobject.Drawable#draw(java.awt.Graphics)
         */

    public void draw(Graphics g, Canvas canvas) {
        ListIterator<DraftTerrainSegment> segIter = segments.listIterator();
        g.setColor(Color.white);
        while (segIter.hasNext()) {
            DraftTerrainSegment segment = segIter.next();
            segment.draw(g, canvas, isSelected(segment));
        }

        ListIterator<DraftPoint> pointIter = points.listIterator();
        g.setColor(Color.cyan);
        while (pointIter.hasNext()) {
            DraftPoint point = pointIter.next();
            point.draw(g, canvas, isSelected(point));
        }

        g.setColor(Color.orange);
        int canvasWorldHeight = canvas.worldToCanvasY(getWorldHeight());
        g.drawLine(0, canvasWorldHeight, canvas.getWidth(), canvasWorldHeight);

        int canvasGroundHeight = canvas.worldToCanvasY(0);
        g.drawLine(0, canvasGroundHeight, canvas.getWidth(), canvasGroundHeight);

        int canvasWorldWidth = canvas.worldToCanvasLength(getWorldWidth());
        for (int j = 0; j < 1 + ((double) canvas.getWidth() / (double) canvasWorldWidth); j++) {
            int x = canvas.worldToCanvasX(0) + j * canvasWorldWidth;
            g.drawLine(x, 0, x, canvas.getHeight());
        }
    }

    public List<DraftTerrainSegment> getSegments() {
        return segments;
    }

    public boolean isSelected(Object o) {
        return selection.contains(o);
    }

    public boolean hasSelection() {
        return selection.size() > 0;
    }

    public List<Object> getSelection() {
        return selection;
    }

    public Object getSelected() {
        return (selection.size() > 0) ? selection.get(0) : null;
    }

    public void unselect() {
        selection.clear();
    }

    public void addSelected(Object o) {
        selection.add(o);
    }

    public void setSelected(Object o) {
        unselect();
        addSelected(o);
    }

    public void selectObject(double worldX, double worldY, double tolerance) {
        // System.out.println("Selected click at " + new Vect2D(worldX, worldY) + " with tolerance "
        // + tolerance);

        DraftTerrainSegment segment = getEncompassingSegment(worldX);
        Vect2D left = segment.getLeftEndPoint();
        Vect2D right = segment.getRightEndPoint();
        Vect2D clicked = new Vect2D(worldX, worldY);

        // first see if it's close enough to any of the points
        if (left.distance(clicked) < tolerance) {
            addSelected(left);
        }
        else if (right.distance(clicked) < tolerance
                || right.distance(new Vect2D(worldX - getWorldWidth(), worldY)) < tolerance) {
            addSelected(right);
        }
        else if (Math.abs(worldY - segment.getHeight(worldX)) < tolerance) {
            addSelected(segment);
        }
    }

    public void selectObjects(double startX, double startY, double endX, double endY) {

        System.out.println("Selecting objects from " + new Vect2D(startX, startY) + " to "
                + new Vect2D(endX, endY));

        for (DraftTerrainSegment segment : segments) {
            DraftPoint left = segment.getLeftEndPoint();
            DraftPoint right = segment.getRightEndPoint();

            if (startX <= left.getX() && left.getX() <= endX && startY <= left.getY()
                    && left.getY() <= endY) {

                if (!selection.contains(left)) {
                    selection.add(left);
                }

                if ((startX <= right.getX() && right.getX() <= endX && startY <= right.getY() && right
                        .getY() <= endY)
                        || (right.getX() == 0 && endX == getWorldWidth() && startY <= right.getY() && right
                                .getY() <= endY)) {
                    if (!selection.contains(segment)) {
                        selection.add(segment);
                    }
                }
            }
        }
    }

    public DraftTerrainSegment getEncompassingSegment(double x) {
        Iterator iter = segments.iterator();
        while (iter.hasNext()) {
            DraftTerrainSegment segment = (DraftTerrainSegment) iter.next();
            if (segment.contains(x)) {
                return segment;
            }
        }
        throw new IllegalArgumentException("Invalid coordinate " + x);
    }

    public double getWorldWidth() {
        return worldWidth;
    }

    public double getWorldHeight() {
        return worldHeight;
    }

    public void setWorldHeight(double h) {
        worldHeight = h;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double g) {
        gravity = g;
    }

    public boolean isCTF() {
        return isCTF;
    }

    public void setCTF(boolean isCTF) {
        this.isCTF = isCTF;
    }

    public boolean isDeathmatch() {
        return isDeathmatch;
    }

    public void setDeathmatch(boolean isDeathmatch) {
        this.isDeathmatch = isDeathmatch;
    }

    public boolean isDuo() {
        return isDuo;
    }

    public void setDuo(boolean isDuo) {
        this.isDuo = isDuo;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean isSingle) {
        this.isSingle = isSingle;
    }

    public boolean isTeamDeathmatch() {
        return isTeamDeathmatch;
    }

    public void setTeamDeathmatch(boolean isTeamDeathmatch) {
        this.isTeamDeathmatch = isTeamDeathmatch;
    }

    public DraftPoint addPoint(double x, double y) {
        ListIterator<DraftTerrainSegment> iter = segments.listIterator();
        while (iter.hasNext()) {
            DraftTerrainSegment segment = (DraftTerrainSegment) iter.next();
            if (segment.contains(x)) {

                DraftPoint newPoint;
                double minX = segment.getLeftEndPoint().getX() + POINT_NEIGHBOR_MIN_DISTANCE;
                double maxX = (segment.getRightEndPoint().getX() == 0) ? getWorldWidth()
                        - POINT_NEIGHBOR_MIN_DISTANCE : segment.getRightEndPoint().getX()
                        - POINT_NEIGHBOR_MIN_DISTANCE;
                if (x < minX) {
                    newPoint = new DraftPoint(minX, y, this);
                }
                else if (x > maxX) {
                    newPoint = new DraftPoint(maxX, y, this);
                }
                else {
                    newPoint = new DraftPoint(x, y, this);
                }

                iter.remove();
                iter.add(new DraftTerrainSegment(segment.getLeftEndPoint(), newPoint, this));
                iter.add(new DraftTerrainSegment(newPoint, segment.getRightEndPoint(), this));

                int leftIndex = points.indexOf(segment.getLeftEndPoint());
                points.add(leftIndex + 1, newPoint);

                return newPoint;
            }
        }

        throw new IllegalArgumentException("Invalid coordinate " + x);
    }

    public DraftTerrainSegment getSegmentStarting(DraftPoint point) {
        Iterator<DraftTerrainSegment> iter = segments.iterator();

        while (iter.hasNext()) {
            DraftTerrainSegment segment = iter.next();
            if (segment.getLeftEndPoint() == point) {
                return segment;
            }
        }

        return null;
    }

    public void movePoint(DraftPoint point, double x, double y) {

        int pointIndex = points.indexOf(point);
        DraftPoint leftPoint = (pointIndex == 0) ? points.getLast() : points.get(pointIndex - 1);
        DraftPoint rightPoint = (pointIndex == points.size() - 1) ? new DraftPoint(getWorldWidth(),
                0, this) : points.get(pointIndex + 1);

        if (point.getX() != 0) {
            if (x < leftPoint.getX() + POINT_NEIGHBOR_MIN_DISTANCE) {
                point.setX(leftPoint.getX() + POINT_NEIGHBOR_MIN_DISTANCE);
            }
            else if (x > rightPoint.getX() - POINT_NEIGHBOR_MIN_DISTANCE) {
                point.setX(rightPoint.getX() - POINT_NEIGHBOR_MIN_DISTANCE);
            }
            else {
                point.setX(x);
            }
        }

        point.setY(y);

        getSegmentStarting(leftPoint).refreshPadStatus();
        getSegmentStarting(point).refreshPadStatus();
    }

    public boolean canMovePoint(DraftPoint point, double x, double y) {
        if (point.getX() == 0 && x != 0) {
            return false;
        }

        int pointIndex = points.indexOf(point);
        Vect2D leftPoint = (Vect2D) points.get(pointIndex - 1);
        Vect2D rightPoint = (pointIndex == points.size() - 1) ? new Vect2D(getWorldWidth(), 0)
                : (Vect2D) points.get(pointIndex + 1);

        if (x < leftPoint.getX() + POINT_NEIGHBOR_MIN_DISTANCE
                || x > rightPoint.getX() - POINT_NEIGHBOR_MIN_DISTANCE) {
            return false;
        }

        return true;
    }

    public void moveSegment(DraftTerrainSegment segment, double x, double y) {

        DraftPoint head, tail;
        double deltaX = x - segment.getMidPoint().getX();
        double deltaY = y - segment.getMidPoint().getY();
        DraftPoint left = segment.getLeftEndPoint();
        DraftPoint right = segment.getRightEndPoint();

        if (left.getX() == 0 || right.getX() == 0) {
            movePoint(left, left.getX(), left.getY() + deltaY);
            movePoint(right, right.getX(), right.getY() + deltaY);
        }
        else {

            if (deltaX > 0) {
                head = right;
                tail = left;
            }
            else {
                head = left;
                tail = right;
            }

            int mult = segment.getMultiplier();

            double oldX = head.getX();
            movePoint(head, head.getX() + deltaX, head.getY() + deltaY);

            double moveAmount = head.getX() - oldX;
            movePoint(tail, tail.getX() + moveAmount, tail.getY() + deltaY);

            segment.setMultiplier(mult);
        }
    }

    public void deletePoint(DraftPoint point) {
        if (point.getX() != 0) {

            points.remove(point);

            ListIterator<DraftTerrainSegment> iter = segments.listIterator();
            while (iter.hasNext()) {
                DraftTerrainSegment leftSegment = (DraftTerrainSegment) iter.next();
                if (leftSegment.getRightEndPoint() == point) {
                    iter.remove();
                    DraftTerrainSegment rightSegment = (DraftTerrainSegment) iter.next();
                    iter.remove();

                    iter.add(new DraftTerrainSegment(leftSegment.getLeftEndPoint(), rightSegment
                            .getRightEndPoint(), this));
                    break;
                }
            }
        }
    }

    public void deleteSegment(DraftTerrainSegment segment) {
        if (segment.getRightEndPoint().getX() != 0) {
            deletePoint(segment.getRightEndPoint());
        }
        else if (segment.getLeftEndPoint().getX() != 0) {
            deletePoint(segment.getLeftEndPoint());
        }
    }

    public void save(File file) throws IOException {

        PrintWriter writer = new PrintWriter(file);

        try {
            writer.println("# Map Name");
            writer.println(name);
            writer.println();

            writer.println("# Map Type");

            int type = 0;
            if (isSingle) {
                type += LunarLanderGame.SINGLE;
            }
            if (isDuo) {
                type += LunarLanderGame.DUO;
            }
            if (isDeathmatch) {
                type += LunarLanderGame.DEATHMATCH;
            }
            if (isTeamDeathmatch) {
                type += LunarLanderGame.TEAM_DEATHMATCH;
            }
            if (isCTF) {
                type += LunarLanderGame.CTF;
            }
            writer.println(type);
            writer.println();

            writer.println("# Map Size");
            writer.println(worldWidth + "," + worldHeight);
            writer.println();

            writer.println("# Gravity");
            writer.println(gravity);

            writer.println("# Segments");

            for (DraftTerrainSegment segment : segments) {
                DraftPoint point = segment.getLeftEndPoint();
                writer.println(point.getX() + "," + point.getY() + "," + segment.getMultiplier());
            }

            writer.println("# Created " + new Date());

            writer.flush();
        }
        finally {
            writer.close();
        }
    }


    private double worldWidth;
    private double worldHeight;
    private LinkedList<DraftPoint> points;
    private LinkedList<DraftTerrainSegment> segments;
    private List<Object> selection;

    private String name;
    private boolean isSingle;
    private boolean isDuo;
    private boolean isDeathmatch;
    private boolean isTeamDeathmatch;
    private boolean isCTF;
    private double gravity;

    private static final double POINT_NEIGHBOR_MIN_DISTANCE = 1;

    public static final double DEFAULT_WORLD_WIDTH = 1000;
    public static final double DEFAULT_WORLD_HEIGHT = 1000;
    public static final double DEFAULT_GRAVITY = 2.0;
}
