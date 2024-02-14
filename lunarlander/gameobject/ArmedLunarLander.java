package lunarlander.gameobject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;

import lunarlander.*;
import lunarlander.canvas.Canvas;
import lunarlander.game.LunarLanderDuo;
import lunarlander.player.Player;
import lunarlander.util.Vect2D;

/**
 * A LunarLander with small rockets, large rockets, and killer drone
 * 
 * @author chung
 * @author mike
 */
public class ArmedLunarLander extends LunarLander {

    /**
     * Construct a new lunar lander with the given initial position, velocity, and angle. Throttle
     * should be set to 0 initially. The angle should be set to initialAngle, and desiredAngle
     * should be set to the same. Remember to set totalMass and momentum too! The lander should
     * start out with max fuel. Initialize status to STATUS_FLYING.
     * 
     * @param initialPosition is the initial position of the lander
     * @param initialVelocity is the initial velocity of the lander
     * @param initialAngle is the initial angle of the lander
     * @param player the player
     */
    public ArmedLunarLander(Vect2D initialPosition, Vect2D initialVelocity, double initialAngle,
            Player player, boolean canFireSmallRockets, boolean canFireBigRockets,
            boolean canFireDrones) {

        super(initialPosition, initialVelocity, initialAngle, player);
        droning = false;
        rockets = new Vector<Rocket>();
        this.canFireSmallRockets = canFireSmallRockets;
        this.canFireBigRockets = canFireBigRockets;
        this.canFireDrones = canFireDrones;

        life = 100;
    }

    /**
     * Construct a clone of the given lunar lander. This LunarLander should have the same instance
     * variable values as the argument LunarLander (same position, same momentum, etc.)
     * 
     * @param lander is the armed lunar lander to clone
     */
    public ArmedLunarLander(ArmedLunarLander lander) {
        super(lander);
        droning = lander.droning;
        drone = lander.drone;
        rockets = lander.rockets;
        this.canFireSmallRockets = lander.canFireSmallRockets;
        this.canFireBigRockets = lander.canFireBigRockets;
        this.canFireDrones = lander.canFireDrones;
        this.life = lander.life;
    }

    // GETTER METHODS
    /**
     * @return how much life the lander has
     */
    public double getLife() {
        return (status == STATUS_CRASHED) ? 0 : Math.max(0, life);
    }
    
    /**
     * @return true if life dips below 0
     */
    public boolean isDead() {
        return super.isDead() || life < 0;
    }

    // FIRING METHODS

    /**
     * fire a rocket of the specified type. Creates the rocket and adds it to this's rockets list
     * 
     * @param rocketType type of rocket
     */
    public void fire(int rocketType) {
        // if we can fire rockets...
        if (canFireRocket(rocketType)) {
            Rocket rocket;

            if (rocketType == Rocket.DRONE_ROCKET) {
                // if we want to fire a drone rocket, set ourselves into droning mode and penalize
                // for fuel cost
                rocket = Drone.createDrone(position, getVelocity(), angle, player.getColor());
                droning = true;
                player.setControlledSpacecraft((Drone) rocket);
                totalMass -= Drone.DRONE_FUEL;
                upPressed = downPressed = leftPressed = rightPressed = false;
            }
            else {
                rocket = Rocket.createRocket(rocketType, position, getVelocity(), angle, color);
            }
            // add new rocket to rockets Vector
            rockets.add(rocket);

            // update momentum for feedback from the rocket
            Vect2D deltaMomentum = (new Vect2D(-Math.sin(angle), Math.cos(angle))).scale(rocket
                    .getMass()
                    * rocket.getInitVelocity());
            momentum = momentum.add(deltaMomentum.scale(rocket.getFeedback()));
        }
    }

    /**
     * @return the total rocket cost used at this moment
     */
    public int rocketCost() {
        int currentCost = 0;
        for (int i = 0; i < rockets.size(); i++) {
            Rocket r = (Rocket) rockets.get(i);
            if (r instanceof Drone) {
                return ((LunarLanderDuo) LunarLanderLauncher.game).rocketBudget;
            }
            currentCost += r.getCost();
        }
        return currentCost;
    }

    /**
     * lander can fire a rocket if we have enough rocket budget. If it's a Drone rocket, we also
     * need enough fuel.
     * 
     * @param rocketType type of rocket
     * @return true if can fire rocket, false otherwise
     */
    public boolean canFireRocket(int rocketType) {
        if (status == STATUS_FLYING && !droning) {
            if (rocketType == Rocket.DRONE_ROCKET
                    && (getFuel() < Drone.DRONE_FUEL || rocketCost() != 0)) {
                return false;
            }
            else {
                return rocketCost() + Rocket.getCost(rocketType) <= ((LunarLanderDuo) LunarLanderLauncher.game).rocketBudget;
            }
        }
        else {
            return false;
        }
    }

    // SIMULATION METHODS

    /**
     * implements Bumpable.step()
     * 
     * also responsible for updating its rockets. Won't call super.step() if no longer flying
     */
    public void step(double dt) {

        updateRockets();

        // if in droning mode, see if we need to turn on thruster
        if (droning) {
            updateHoverInputs();
        }

        if (Settings.getBoolean(LunarLanderLauncher.game.getGamePrefix() + Settings.ROCKETS_DAMAGE)) {
            updateLife(dt);
        }

        // do the usual stepping
        super.step(dt);
    }

    /**
     * Check to see if any of the rockets have expired
     */
    public void updateRockets() {
        for (Iterator it = rockets.iterator(); it.hasNext();) {
            Rocket r = (Rocket) it.next();
            if (r.shouldDelete()) {
                it.remove();

                if (droning && r.getRocketType() == Rocket.DRONE_ROCKET) {
                    player.setControlledSpacecraft(player.getLander());
                    droning = false;
                }
            }
        }
    }

    /**
     * update throttle/angle/etc when lander is in droning mode. Basically tries to hover the lander
     * below a certain height set by worldHeight * MAX_HOVER_HEIGHT_FACTOR
     *  
     */
    private void updateHoverInputs() {
        // set ourselves upright
        setDesiredAngle(0.0);

        Vect2D velocity = getVelocity();

        // if we're above the hover height, then let it fall!
        if (position.getY() > MAX_HOVER_HEIGHT) {
            setThrottle(0.0);
        }
        // if we're under hover height and falling, then turn thruster on
        else if (velocity.getY() < 0) {
            setThrottle(0.75);
        }
        // else, turn thruster off
        else {
            setThrottle(0.0);
        }
    }

    /**
     * update damange the lander took
     * @param dt timestep size
     */
    private void updateLife(double dt) {
        Vect2D collision = this.getCollisionMomentumROC(dt).scale(dt / K);
        life -= collision.magnitude() * 200;
        
        if (isDead()) {
            setThrottle(0.0);
        }
    }

    // DRAWING METHODS

    /**
     * Implements Bumpable.draw(). Draws the lander, a circle around it if it's droning, its
     * offscreen indicator if desired, and its rockets and rocket budget if desired.
     */
    public void draw(Graphics g, Canvas canvas) {
        super.draw(g, canvas);

        if (status == STATUS_FLYING) {
            drawRocketsLeft(g, canvas);
            drawDroning(g, canvas);
        }

        if (Settings.getBoolean(LunarLanderLauncher.game.getGamePrefix() + Settings.ROCKETS_DAMAGE)) {
            drawLife(g, canvas);
        }
    }

    /**
     * draw graphics effects that indicates the lander's in droning mode
     * 
     * @param g Graphics context
     */
    private void drawDroning(Graphics g, Canvas canvas) {
        if (droning) {
            g.setColor(color.darker().darker());
            canvas.drawOval(g, position.getX() - getSize(), position.getY() + getSize() / 2,
                    getSize() * 2, getSize() * 2);
        }
    }

    /**
     * Draw number of rockets left to use
     * 
     * @param g the graphics context
     */
    private void drawRocketsLeft(Graphics g, Canvas canvas) {

        g.setColor(Color.white);
        Font f = new Font("Courier", Font.BOLD, 12);
        g.setFont(f);
        String rocketsLeft = ""
                + (Settings.getInt(LunarLanderLauncher.game.getGamePrefix() + Settings.ROCKET_BUDGET) 
                - rocketCost());

        int x = canvas.worldToCanvasX(position.getX() + 1.5 * LANDER_LENGTH);
        int y = canvas.worldToCanvasY(position.getY() - LANDER_LENGTH / 2);
        canvas.drawString(g, new Vect2D(x, y), rocketsLeft);
    }

    /**
     * Draw the life of the lander
     * 
     * @param g Graphics context
     * @param canvas canvas to draw on
     */
    private void drawLife(Graphics g, Canvas canvas) {
        g.setColor(new Color(255, 50, 50));
        Font f = new Font("Courier", Font.BOLD, 12);
        g.setFont(f);
        String life = "" + (int) Math.ceil(getLife());

        int x = canvas.worldToCanvasX(position.getX() - 1.5 * LANDER_LENGTH);
        int y = canvas.worldToCanvasY(position.getY() - LANDER_LENGTH / 2);
        canvas.drawString(g, new Vect2D(x, y), life);
    }

    // CONTROL METHODS

    /**
     * implements Controllable.handleButton1Press()
     * 
     * Button 1 fire a small rocket
     */
    public void handleButton1Tap() {
        if (canFireSmallRockets) {
            fire(Rocket.SMALL_ROCKET);
        }
    }

    /**
     * implements Controllable.handleButton2Press()
     * 
     * Button 2 fires a big rocket
     */
    public void handleButton2Tap() {
        if (canFireBigRockets) {
            fire(Rocket.BIG_ROCKET);
        }
    }

    /**
     * implements Controllable.handleButton3Press()
     * 
     * Button 3 deploys the drone
     */
    public void handleButton3Tap() {
        if (canFireDrones) {
            fire(Rocket.DRONE_ROCKET);
        }
    }


    private Vector<Rocket> rockets; // lander's rockets
    private Drone drone; // lander's Drone
    private boolean droning = false; // state: droning or not
    private boolean canFireSmallRockets;
    private boolean canFireBigRockets;
    private boolean canFireDrones;
    private double life;
}