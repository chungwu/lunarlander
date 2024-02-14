
package lunarlander.gameobject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Vector;

import lunarlander.LunarLanderLauncher;
import lunarlander.canvas.Canvas;
import lunarlander.util.Vect2D;



public class Drone extends Rocket implements Controllable {

    /**
     * Static method for creating a drone of a certain type; use this in the same way as
     * Rocket.createRocket.
     * 
     * This is the recommended method of creating a Drone.
     * 
     * @param landerPosition firing lander's position vector
     * @param landerVelocity firing lander's velocity vector
     * @param landerAngle firing lander's angle
     * @param color the color of the drone
     * @return instance of Drone fired from the lander
     */
    public static Drone createDrone(Vect2D landerPosition, Vect2D landerVelocity,
            double landerAngle, Color color) {
        Drone drone = (Drone) createRocket(Rocket.DRONE_ROCKET, landerPosition, landerVelocity,
                landerAngle, color);
        drone.addToLists();
        return drone;
    }

    /**
     * constructor; also initializes fuel amount
     * 
     * @param position drone position vector
     * @param momentum drone momentum vector
     * @param color the color of the drone
     */
    public Drone(Vect2D position, Vect2D momentum, Color color) {
        super(Rocket.DRONE_ROCKET, position, momentum, color);
        fuelAmount = DRONE_FUEL;
    }

    // SIMULATION METHODS
    /**
     * overrides Rocket.step()
     * 
     * Only calls Rocket.step() if the drone is not yet dead. If it's dead for the first time, then
     * set the isDead flag and deadStartTime to now.
     */
    public void step(double dt) {

        if (!isDead && isTouchingGround()) {
            // dead for the first time!
            isDead = true;
            deadStartTime = System.currentTimeMillis();
            upPressed = downPressed = leftPressed = rightPressed = false;
        }

        // if still alive, simulate
        if (!isDead) {
            handleBoosters(dt);
            super.step(dt);
        }
    }

    /**
     * Calculate the impact of the directional boosters on the drone and update the momentum/fuel.
     * 
     * @param dt the timestep
     */
    protected void handleBoosters(double dt) {
        if (upPressed) {
            if (!isDead && fuelAmount > 0) {
                double thrustPercent = Math.min(1.0, fuelAmount / (DRONE_FUEL_BURN_RATE * dt));
                fuelAmount -= thrustPercent * DRONE_FUEL_BURN_RATE * dt;
                momentum = momentum.add(new Vect2D(0, thrustPercent * THRUST_STRENGTH));
            }
        }

        if (downPressed) {
            if (!isDead && fuelAmount > 0) {
                double thrustPercent = Math.min(1.0, fuelAmount / (DRONE_FUEL_BURN_RATE * dt));
                fuelAmount -= thrustPercent * DRONE_FUEL_BURN_RATE * dt;
                momentum = momentum.add(new Vect2D(0, thrustPercent * -THRUST_STRENGTH));
            }
        }

        if (leftPressed) {
            if (!isDead && fuelAmount > 0) {
                double thrustPercent = Math.min(1.0, fuelAmount / (DRONE_FUEL_BURN_RATE * dt));
                fuelAmount -= thrustPercent * DRONE_FUEL_BURN_RATE * dt;
                momentum = momentum.add(new Vect2D(thrustPercent * -THRUST_STRENGTH, 0));
            }
        }

        if (rightPressed) {
            if (!isDead && fuelAmount > 0) {
                double thrustPercent = Math.min(1.0, fuelAmount / (DRONE_FUEL_BURN_RATE * dt));
                fuelAmount -= thrustPercent * DRONE_FUEL_BURN_RATE * dt;
                momentum = momentum.add(new Vect2D(thrustPercent * THRUST_STRENGTH, 0));
            }
        }
    }

    /**
     * overrides Rocket.shouldDelete()
     * 
     * A Drone should be deleted if it's dead and it's been dead for longer than DEAD_TIME_SECONDS
     */
    public boolean shouldDelete() {
        return isDead && (System.currentTimeMillis() - deadStartTime > DEAD_TIME_SECONDS * 1000);
    }

    // CONTROL METHODS

    /**
     * implements Controllable.handleUp()
     */
    public void handleUp(boolean pressed) {
        upPressed = pressed && !isDead && fuelAmount > 0;
    }

    /**
     * implements Controllable.handleDown()
     */
    public void handleDown(boolean pressed) {
        downPressed = pressed && !isDead && fuelAmount > 0;
    }

    /**
     * implements Controllable.handleLeft()
     */
    public void handleLeft(boolean pressed) {
        leftPressed = pressed && !isDead && fuelAmount > 0;
    }

    /**
     * implements Controllable.handleRightPress()
     */
    public void handleRight(boolean pressed) {
        rightPressed = pressed && !isDead && fuelAmount > 0;
    }

    /**
     * implements Controllable.handleButton1Press()
     * 
     * Button 1 does nothing
     */
    public void handleButton1Tap() {
    }

    /**
     * implements Controllable.handleButton2Press()
     * 
     * Button 2 does nothing
     */
    public void handleButton2Tap() {
    }

    /**
     * implements Controllable.handleButton3Press()
     * 
     * Button 3 does nothing
     */
    public void handleButton3Tap() {
    }
    
    /**
     * implements Controllabe.isDead()
     */
    public boolean isDead() {
        return isDead;
    }

    // DRAWING METHODS

    /**
     * implements Bumpable.draw()
     * 
     * draws in different colors depending on isDead or not.
     */
    public void draw(Graphics g, Canvas canvas) {
        // Draw main flame
        Color c = isDead ? color.darker().darker().darker() : color;
        Vect2D[][] polylines = getDronePolylines();
        for (int i = 0; i < polylines.length; i++) {
            LunarLanderLauncher.game.canvas.drawPolyline(g, c, polylines[i]);
        }
        drawFuelAmount(g, canvas);
    }

    /**
     * @return polylines of the drone in world's coordinate system
     */
    private Vect2D[][] getDronePolylines() {

        Vect2D[][] polylines = getVanillaDronePolylines();

        // Translation vector
        Vect2D translation = new Vect2D(position.getX(), position.getY());

        for (int i = 0; i < polylines.length; i++) {
            for (int j = 0; j < polylines[i].length; j++) {
                polylines[i][j] = translation.add(polylines[i][j]);
            }
        }

        return polylines;
    }

    /**
     * @return polylines of the drone in the drone's coordinate system
     */
    private Vect2D[][] getVanillaDronePolylines() {
        Vector<Vect2D[]> polylines = new Vector<Vect2D[]>();
        Vect2D[] outerLines = { new Vect2D(getSize() / 2, 0), new Vect2D(0, getSize() / 2),
                new Vect2D(-getSize() / 2, 0), new Vect2D(0, -getSize() / 2),
                new Vect2D(getSize() / 2, 0) };
        Vect2D[] innerLines = { new Vect2D(getSize() / 4, 0), new Vect2D(0, getSize() / 4),
                new Vect2D(-getSize() / 4, 0), new Vect2D(0, -getSize() / 4),
                new Vect2D(getSize() / 4, 0) };

        polylines.add(outerLines);
        polylines.add(innerLines);

        if (downPressed) {
            polylines.add(new Vect2D[] { new Vect2D(FLAME_WIDTH / 2, getSize() / 2),
                    new Vect2D(FLAME_WIDTH / 2, getSize() / 2 + FLAME_LENGTH) });
            polylines.add(new Vect2D[] { new Vect2D(-FLAME_WIDTH / 2, getSize() / 2),
                    new Vect2D(-FLAME_WIDTH / 2, getSize() / 2 + FLAME_LENGTH) });
        }

        if (upPressed) {
            polylines.add(new Vect2D[] { new Vect2D(FLAME_WIDTH / 2, -getSize() / 2),
                    new Vect2D(FLAME_WIDTH / 2, -getSize() / 2 - FLAME_LENGTH) });
            polylines.add(new Vect2D[] { new Vect2D(-FLAME_WIDTH / 2, -getSize() / 2),
                    new Vect2D(-FLAME_WIDTH / 2, -getSize() / 2 - FLAME_LENGTH) });
        }

        if (rightPressed) {
            polylines.add(new Vect2D[] { new Vect2D(-getSize() / 2, FLAME_WIDTH / 2),
                    new Vect2D(-getSize() / 2 - FLAME_LENGTH, FLAME_WIDTH / 2) });
            polylines.add(new Vect2D[] { new Vect2D(-getSize() / 2, -FLAME_WIDTH / 2),
                    new Vect2D(-getSize() / 2 - FLAME_LENGTH, -FLAME_WIDTH / 2) });
        }

        if (leftPressed) {
            polylines.add(new Vect2D[] { new Vect2D(getSize() / 2, FLAME_WIDTH / 2),
                    new Vect2D(getSize() / 2 + FLAME_LENGTH, FLAME_WIDTH / 2) });
            polylines.add(new Vect2D[] { new Vect2D(getSize() / 2, -FLAME_WIDTH / 2),
                    new Vect2D(getSize() / 2 + FLAME_LENGTH, -FLAME_WIDTH / 2) });
        }

        Vect2D[][] result = new Vect2D[polylines.size()][];
        for (int i = 0; i < result.length; i++) {
            result[i] = (Vect2D[]) polylines.get(i);
        }

        return result;
    }

    /**
     * draws the fuel amount or the seconds left to live for the drone
     * 
     * @param g Graphics context
     */
    private void drawFuelAmount(Graphics g, Canvas canvas) {

        // draw number of rockets left to use
        g.setColor(Color.white);
        Font f = new Font("Courier", Font.BOLD, 12);
        g.setFont(f);
        String str = ""
                + (isDead ? DEAD_TIME_SECONDS
                        - (int) ((System.currentTimeMillis() - deadStartTime) / 1000)
                        : (int) fuelAmount);

        double worldWidth = canvas.getURX() - canvas.getLLX();
        double worldHeight = canvas.getURY() - canvas.getLLY();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int x = canvas.worldToCanvasX(position.getX() + getSize());
        int y = canvas.worldToCanvasY(position.getY() + getSize());
        /*
        int x = (int) ((double) canvasWidth * position.getX() / worldWidth);
        int y = (int) ((double) canvasHeight * (1.0 - (position.getY() / worldHeight)));
        
        g.drawString(str, (int) (x + getSize()), (int) (y - getSize()));
        */
        canvas.drawString(g, new Vect2D(x, y), str);
    }


    // PRIVATE STATE VARIABLES
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private double fuelAmount; // draw fuel left
    private long deadStartTime; // time when drone died
    private boolean isDead = false; // state: dead or alive

    // DRONE CONSTANTS
    private static final int DEAD_TIME_SECONDS = 8; // seconds drone stays on screen after dying
    private static final double FLAME_LENGTH = 5; // length of flame in drone's coordinate system
    private static final double FLAME_WIDTH = 1; // width of flame in drone's coordinate system
    private static final double THRUST_STRENGTH = 2.5E4; // strength of a thrust
    public static final double DRONE_FUEL = 7500; // fuel drone starts with
    public static final double DRONE_FUEL_BURN_RATE = 150; // burn rate for the fuel
}