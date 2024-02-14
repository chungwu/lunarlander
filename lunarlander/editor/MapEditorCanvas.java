/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

import lunarlander.canvas.Canvas;
import lunarlander.player.PreviewLanderPlayer;
import lunarlander.util.Vect2D;
import lunarlander.gameobject.PreviewLunarLander;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class MapEditorCanvas extends Canvas {

    public MapEditorCanvas(DraftMap map, MapEditorPane pane) {
        super();
        this.map = map;
        this.editorPane = pane;
        llx = 0;
        lly = 0;
        zoom = 1;

        PreviewLanderPlayer player = new PreviewLanderPlayer(null);
        player.setColor(Color.orange);
        lander = new PreviewLunarLander(new Vect2D(0, 0), new Vect2D(0, 0), 0, player);
        drawLander = false;

        scrollStartTime = -1;

        mouseTracker = new MouseTracker(this);
        keyboardTracker = new KeyboardTracker(this);

        redrawTimer = new Timer(33, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                repaint();
                redrawTimer.restart();
            }
        });
        redrawTimer.start();

        this.addMouseListener(mouseTracker);
        this.addMouseMotionListener(mouseTracker);
        this.addMouseWheelListener(mouseTracker);
        LunarLanderMapEditor.frame.addKeyListener(keyboardTracker);
    }

    public DraftMap getMap() {
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lunarlander.canvas.Canvas#getWorldWidth()
     */
    public double getWorldWidth() {
        return map.getWorldWidth();
    }

    /*
     * (non-Javadoc)
     * 
     * @see lunarlander.canvas.Canvas#getWorldHeight()
     */
    public double getWorldHeight() {
        return map.getWorldHeight();
    }

    public MouseTracker getMouseTracker() {
        return mouseTracker;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lunarlander.canvas.Canvas#paintScreen(java.awt.Graphics)
     */
    public void paintScreen(Graphics g) {

        updateViewWindow();

        setBackground(Color.black);
        map.draw(g, this);

        paintXCoordinates(g);
        paintYCoordinates(g);

        if (USE_BOUNDARIES) {
            paintMovementBoundaries(g);
        }

        paintSelectionBox(g);

        paintLander(g);
    }

    private void updateViewWindow() {

        updateScrolling();

        if (llx > getWorldWidth()) {
            llx -= getWorldWidth();
        }
        else if (llx < 0) {
            llx += getWorldWidth();
        }
        urx = llx + zoomedWidth();

        ury = lly + zoomedHeight();
    }

    private void updateScrolling() {

        if (scrollLeft || scrollRight || scrollUp || scrollDown) {
            if (scrollStartTime == -1) {
                scrollStartTime = System.currentTimeMillis();
            }
            else if (System.currentTimeMillis() - scrollStartTime > SCROLL_WAIT_INTERVAL) {
                if (scrollLeft) {
                    llx -= scrollXSpeed;
                }
                else if (scrollRight) {
                    llx += scrollXSpeed;
                }

                if (scrollUp) {
                    lly += scrollYSpeed;
                }
                else if (scrollDown) {
                    lly -= scrollYSpeed;
                }
            }
        }
        else {
            scrollStartTime = -1;
        }
    }

    private double zoomedWidth() {
        /*
         * switch (zoom) { case 1: return 1500; case 2: return 1000; case 3: return 750; case 4:
         * return 500; case 5: return 250; default: System.err.println("Uh-oh, invalid zoom " +
         * zoom); return -1; }
         */
        return getWorldWidth() / zoom * 4.0 / 3.0;
    }

    private double zoomedHeight() {
        return zoomedWidth() / 800 * 600;
    }

    private void paintXCoordinates(Graphics g) {
        double interval = zoomedWidth() / 10;
        double worldWidth = getWorldWidth();
        double canvasWidth = getWidth();

        Font f = new Font("Courier", Font.PLAIN, Math.min(12, getHeight()));
        g.setFont(f);

        g.setColor(Color.white);
        for (int i = 0; i < getWorldWidth(); i += interval) {

            int strWidth = g.getFontMetrics().stringWidth("" + i);
            for (int j = 0; j < urx / worldWidth; j++) {
                int x = worldToCanvasX(i) + j * worldToCanvasLength(worldWidth);

                this.drawString(g, new Vect2D(x, getHeight() - 20 + f.getSize() + 3), "" + i);
            }
        }
    }

    private void paintYCoordinates(Graphics g) {
        double interval;

        /*
         * switch (zoom) { case 1: interval = 50; break; case 2: interval = 50; break; case 3:
         * interval = 25; break; case 4: interval = 25; break; case 5: interval = 10; break;
         * default: System.err.println("Invalid zoom " + zoom); interval = -1; }
         */
        interval = zoomedHeight() / 10;

        int start = (int) (((int) (lly / interval)) * interval);

        for (int i = start; i < ury; i += interval) {
            int canvasHeight = worldToCanvasY(i);
            g.drawString("" + i, 3, canvasHeight);
        }
    }

    private void paintMovementBoundaries(Graphics g) {
        int blx = MOVEMENT_BOUNDARY;
        int bly = MOVEMENT_BOUNDARY;
        int bux = getWidth() - MOVEMENT_BOUNDARY;
        int buy = getHeight() - MOVEMENT_BOUNDARY;

        g.setColor(Color.yellow.darker().darker().darker());

        g.drawLine(0, bly, getWidth(), bly);
        g.drawLine(0, buy, getWidth(), buy);
        g.drawLine(blx, 0, blx, getHeight());
        g.drawLine(bux, 0, bux, getHeight());
    }

    private void paintSelectionBox(Graphics g) {
        if (mouseTracker.makingSelectionBox) {
            g.setColor(Color.green);
            int startY = getHeight() - selectionStartY;
            int endY = getHeight() - selectionEndY;
            g.drawRect(Math.min(selectionStartX, selectionEndX), Math.min(startY, endY), Math
                    .abs(selectionEndX - selectionStartX), Math.abs(endY - startY));
        }
    }

    public void scrollLeft(int speedDial) {
        scrollLeft = true;
        scrollXSpeed = speedDialToSpeed(speedDial);
    }

    public void scrollRight(int speedDial) {
        scrollRight = true;
        scrollXSpeed = speedDialToSpeed(speedDial);
    }

    public void scrollUp(int speedDial) {
        scrollUp = true;
        scrollYSpeed = speedDialToSpeed(speedDial);
    }

    public void scrollDown(int speedDial) {
        scrollDown = true;
        scrollYSpeed = speedDialToSpeed(speedDial);
    }

    public void scrollNoneX() {
        scrollLeft = false;
        scrollRight = false;
    }

    public void scrollNoneY() {
        scrollUp = scrollDown = false;
    }

    private double speedDialToSpeed(int speedDial) {
        switch (speedDial) {
            case 0:
                return 100;
            case 1:
                return 50;
            case 2:
                return 25;
            case 3:
                return 10;
            case 4:
                return 5;
            default:
                System.err.println("Invalid speed dial " + speedDial);
                return -1;
        }
    }

    public void scrollBy(int deltaX, int deltaY) {
        double length = canvasToWorldLength(deltaX);
        double height = canvasToWorldHeight(deltaY);

        llx -= length;
        urx -= length;
        lly -= height;
        ury -= height;
    }

    public void zoomIn() {
        if (zoom < ZOOM_LEVELS) {
            zoom++;
            repaint();
        }
    }

    public void zoomOut() {
        if (zoom > 1) {
            zoom--;
            repaint();
        }
    }

    public void selectClicked(int x, int y) {

        map.unselect();

        map.selectObject(canvasToWorldX(x), canvasToWorldY(y),
                canvasToWorldLength(SELECT_TOLERANCE));

        Object selected = map.getSelected();

        if (selected == null) {
            editorPane.showMapOptions();
        }
        else if (selected instanceof DraftPoint) {
            editorPane.showPointOptions((DraftPoint) selected);
        }
        else if (selected instanceof DraftTerrainSegment) {
            editorPane.showSegmentOptions((DraftTerrainSegment) selected);
        }

        if (selected != null) {
            System.out.println("SELECTED: " + selected);
        }
    }

    public void addPoint(int x, int y) {
        DraftPoint point = map.addPoint(canvasToWorldX(x), canvasToWorldY(y));
        map.setSelected(point);
        editorPane.showPointOptions(point);
    }

    public void movePoint(DraftPoint point, int x, int y) {
        map.movePoint(point, canvasToWorldX(x), canvasToWorldY(y));
    }

    public void moveSegment(DraftTerrainSegment segment, int x, int y) {
        map.moveSegment(segment, canvasToWorldX(x), canvasToWorldY(y));
    }

    public void deleteSelected() {
        System.out.println("DELETING!");
        Object selected = map.getSelected();
        if (selected != null) {
            if (selected instanceof DraftPoint) {
                map.deletePoint((DraftPoint) selected);
            }
            else if (selected instanceof DraftTerrainSegment) {
                map.deleteSegment((DraftTerrainSegment) selected);
            }
        }
        map.unselect();
    }

    public void startSelectionBox(int x, int y) {
        selectionStartX = x;
        selectionStartY = y;
        System.out.println("Starting Selection Box! " + new Vect2D(x, y));
    }

    public void updateSelectionBox(int x, int y) {
        selectionEndX = x;
        selectionEndY = y;
    }

    public void endSelectionBox() {
        System.out.println("Ending selection box! " + new Vect2D(selectionEndX, selectionEndY));

        double worldStartX = canvasToWorldX(selectionStartX);
        double worldStartY = canvasToWorldY(selectionStartY);
        double worldEndX = canvasToWorldX(selectionEndX);
        double worldEndY = canvasToWorldY(selectionEndY);

        double minWorldY = Math.min(worldStartY, worldEndY);
        double maxWorldY = Math.max(worldStartY, worldEndY);

        int minCanvasX = Math.min(selectionStartX, selectionEndX);
        int maxCanvasX = Math.max(selectionStartX, selectionEndX);
        double minWorldX = Math.min(worldStartX, worldEndX);
        double maxWorldX = Math.max(worldStartX, worldEndX);

        if (maxCanvasX - minCanvasX > worldToCanvasLength(map.getWorldWidth())) {
            System.out.println("Selecting everything");
            map.selectObjects(0, minWorldY, map.getWorldWidth(), maxWorldY);
        }
        else if (canvasToWorldX(minCanvasX) == minWorldX) {
            System.out.println("Everything within selection, from "
                    + new Vect2D(minWorldX, minWorldY) + " to " + new Vect2D(maxWorldX, maxWorldY));
            map.selectObjects(minWorldX, minWorldY, maxWorldX, maxWorldY);
        }
        else {
            System.out.println("Selecting in parts");
            // first select the first part
            map
                    .selectObjects(canvasToWorldX(minCanvasX), minWorldY, map.getWorldWidth(),
                            maxWorldY);

            // now select the second part
            map.selectObjects(0, minWorldY, canvasToWorldX(maxCanvasX), maxWorldY);
        }

        if (map.getSelection().size() <= 1) {
            Object selected = map.getSelected();

            if (selected == null) {
                editorPane.showMapOptions();
            }
            else if (selected instanceof DraftPoint) {
                editorPane.showPointOptions((DraftPoint) selected);
            }
            else if (selected instanceof DraftTerrainSegment) {
                editorPane.showSegmentOptions((DraftTerrainSegment) selected);
            }
        }
    }

    public void updateLanderPosition(int x, int y) {
        Vect2D prevPosition = lander.getPosition();
        Vect2D newPosition = new Vect2D(canvasToWorldX(x), canvasToWorldY(y));

        if (prevPosition.equals(newPosition)) {
            lander.setAngle(0);
        }
        else {

            double angle = newPosition.subtract(prevPosition).angle();

            System.out.println("prev: " + prevPosition + ", new: " + newPosition);

            System.out.println("Angle before adjustment: " + Math.toDegrees(angle));

            if (angle > 0) {
                if (newPosition.getX() > prevPosition.getX()) {
                    angle = angle - Math.PI / 2;
                }
                else if (newPosition.getX() == prevPosition.getX()) {
                    angle = 0;
                }
                else {
                    angle = Math.PI / 2 + angle;
                }
            }
            else {
                if (newPosition.getX() > prevPosition.getX()) {
                    angle = -Math.PI / 2 + angle;
                }
                else if (newPosition.getX() == prevPosition.getX()) {
                    angle = -Math.PI;
                }
                else {
                    angle = Math.PI / 2 + angle;
                }
            }

            System.out.println("Angle after adjustment: " + Math.toDegrees(angle));
            lander.setDesiredAngle(angle);
        }
        lander.setPosition(newPosition);
    }

    private void paintLander(Graphics g) {
        if (drawLander) {
            lander.step(LANDER_TIMESTEP);
            lander.draw(g, this);
        }
    }


    private static class MouseTracker implements MouseMotionListener, MouseListener,
            MouseWheelListener {

        public MouseTracker(MapEditorCanvas canvas) {
            this.canvas = canvas;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                canvas.selectClicked(e.getX(), canvas.getHeight() - e.getY());
            }
            else if (e.getButton() == MouseEvent.BUTTON3) {
                canvas.addPoint(e.getX(), canvas.getHeight() - e.getY());
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e) {
            pressedButton = e.getButton();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e) {
            dragging = false;
            draggingSpace = false;
            draggingSelection = false;
            pressedButton = MouseEvent.NOBUTTON;

            if (makingSelectionBox) {
                canvas.endSelectionBox();
                makingSelectionBox = false;
            }

            canvas.repaint();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent arg0) {
            canvas.scrollNoneX();
            canvas.scrollNoneY();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e) {

            if (!dragging) {
                dragging = true;

                if (pressedButton == MouseEvent.BUTTON1) {
                    canvas.selectClicked(e.getX(), canvas.getHeight() - e.getY());
                    if (canvas.getMap().hasSelection()) {
                        draggingSelection = true;
                    }
                }
                if (!draggingSelection) {
                    prevSpaceX = e.getX();
                    prevSpaceY = canvas.getHeight() - e.getY();

                    if (pressedButton == MouseEvent.BUTTON1) {
                        canvas.startSelectionBox(prevSpaceX, prevSpaceY);
                        makingSelectionBox = true;
                    }
                    else if (pressedButton == MouseEvent.BUTTON2) {
                        draggingSpace = true;
                    }
                }
            }

            if (draggingSelection) {
                Object selected = canvas.getMap().getSelected();
                if (selected instanceof DraftPoint) {
                    DraftPoint point = (DraftPoint) selected;
                    canvas.movePoint(point, e.getX(), canvas.getHeight() - e.getY());
                    canvas.editorPane.showPointOptions(point);
                }
                else if (selected instanceof DraftTerrainSegment) {
                    DraftTerrainSegment segment = (DraftTerrainSegment) selected;
                    canvas.moveSegment(segment, e.getX(), canvas.getHeight() - e.getY());

                    canvas.editorPane.showSegmentOptions(segment);
                }
            }
            else {

                int newSpaceX = e.getX();
                int newSpaceY = canvas.getHeight() - e.getY();

                if (makingSelectionBox) {
                    canvas.updateSelectionBox(newSpaceX, newSpaceY);
                }
                else if (draggingSpace) {
                    canvas.scrollBy(newSpaceX - prevSpaceX, newSpaceY - prevSpaceY);
                }
                prevSpaceX = newSpaceX;
                prevSpaceY = newSpaceY;
            }

            mouseMoved(e);
            canvas.repaint();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved(MouseEvent e) {

            if (canvas.drawLander) {
                canvas.updateLanderPosition(e.getX(), canvas.getHeight() - e.getY());
                canvas.repaint();
            }

            if (USE_BOUNDARIES) {
                int x = e.getX();
                int y = canvas.getHeight() - e.getY();

                if (x < 0 || x > canvas.getWidth() || y < 0 || y > canvas.getHeight()) {
                    return;
                }

                if (x < MOVEMENT_BOUNDARY) {
                    canvas.scrollLeft(x / MOVEMENT_ZONE_WIDTH);
                }
                else if (x > canvas.getWidth() - MOVEMENT_BOUNDARY) {
                    canvas.scrollRight((canvas.getWidth() - x) / MOVEMENT_ZONE_WIDTH);
                }
                else {
                    canvas.scrollNoneX();
                }

                if (y < MOVEMENT_BOUNDARY) {
                    canvas.scrollDown(y / MOVEMENT_ZONE_WIDTH);
                }
                else if (y > canvas.getHeight() - MOVEMENT_BOUNDARY) {
                    canvas.scrollUp((canvas.getHeight() - y) / MOVEMENT_ZONE_WIDTH);
                }
                else {
                    canvas.scrollNoneY();
                }
            }
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            int clicks = e.getWheelRotation();

            if (clicks < 0) {
                for (int i = 0; i < -clicks; i++) {
                    canvas.zoomIn();
                }
            }
            else {
                for (int i = 0; i < clicks; i++) {
                    canvas.zoomOut();
                }
            }
        }


        private MapEditorCanvas canvas;
        private boolean dragging;
        private boolean draggingSelection;
        private boolean draggingSpace;
        private boolean makingSelectionBox;
        private int prevSpaceX;
        private int prevSpaceY;
        private int pressedButton;
    }


    private static class KeyboardTracker implements KeyListener {

        public KeyboardTracker(MapEditorCanvas canvas) {
            this.canvas = canvas;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped(KeyEvent e) {
            switch (e.getKeyChar()) {
                case 'z':
                    canvas.zoomIn();
                    break;
                case 'a':
                    canvas.zoomOut();
                    break;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyChar()) {
                case 'p':
                case 'P':
                    canvas.drawLander = true;
                    break;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    canvas.deleteSelected();
                    break;

                case KeyEvent.VK_RIGHT:
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
                        canvas.scrollRight(FAST_KEYBOARD_SCROLL_DIAL);
                    }
                    else {
                        canvas.scrollRight(SLOW_KEYBOARD_SCROLL_DIAL);
                    }
                    canvas.scrollStartTime = 0;
                    break;

                case KeyEvent.VK_LEFT:
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
                        canvas.scrollLeft(FAST_KEYBOARD_SCROLL_DIAL);
                    }
                    else {
                        canvas.scrollLeft(SLOW_KEYBOARD_SCROLL_DIAL);
                    }
                    canvas.scrollStartTime = 0;
                    break;

                case KeyEvent.VK_UP:
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
                        canvas.scrollUp(FAST_KEYBOARD_SCROLL_DIAL);
                    }
                    else {
                        canvas.scrollUp(SLOW_KEYBOARD_SCROLL_DIAL);
                    }
                    canvas.scrollStartTime = 0;
                    break;

                case KeyEvent.VK_DOWN:
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
                        canvas.scrollDown(FAST_KEYBOARD_SCROLL_DIAL);
                    }
                    else {
                        canvas.scrollDown(SLOW_KEYBOARD_SCROLL_DIAL);
                    }
                    canvas.scrollStartTime = 0;
                    break;
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased(KeyEvent e) {

            switch (e.getKeyChar()) {
                case 'p':
                case 'P':
                    canvas.drawLander = false;
                    break;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
                    canvas.scrollNoneX();
                    break;

                case KeyEvent.VK_LEFT:
                    canvas.scrollNoneX();
                    break;

                case KeyEvent.VK_UP:
                    canvas.scrollNoneY();
                    break;

                case KeyEvent.VK_DOWN:
                    canvas.scrollNoneY();
                    break;
            }
        }


        private MapEditorCanvas canvas;
    }


    private DraftMap map;
    private MapEditorPane editorPane;
    private int zoom;
    private MouseTracker mouseTracker;
    private KeyboardTracker keyboardTracker;
    private boolean scrollLeft;
    private boolean scrollRight;
    private boolean scrollUp;
    private boolean scrollDown;
    private double scrollXSpeed;
    private double scrollYSpeed;
    private long scrollStartTime;
    private int selectionStartX;
    private int selectionStartY;
    private int selectionEndX;
    private int selectionEndY;
    private Timer redrawTimer;
    private PreviewLunarLander lander;
    private static final double LANDER_TIMESTEP = 0.3;
    private boolean drawLander;
    private static final int ZOOM_LEVELS = 10;
    private static final int MOVEMENT_BOUNDARY = 50;
    private static final int MOVEMENT_ZONES = 5;
    private static final int MOVEMENT_ZONE_WIDTH = MOVEMENT_BOUNDARY / MOVEMENT_ZONES;
    private static final int SELECT_TOLERANCE = 8;
    private static final long SCROLL_WAIT_INTERVAL = 200;
    private static final int SLOW_KEYBOARD_SCROLL_DIAL = 3;
    private static final int FAST_KEYBOARD_SCROLL_DIAL = 1;

    private static final long serialVersionUID = 1L;
    private static final boolean USE_BOUNDARIES = false;
}
