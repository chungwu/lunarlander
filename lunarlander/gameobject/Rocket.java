
package lunarlander.gameobject;

import java.awt.Color;
import java.awt.Graphics;


import lunarlander.LunarLanderLauncher;
import lunarlander.map.TerrainSegment;
import lunarlander.util.Matrix;
import lunarlander.util.Vect2D;
import lunarlander.canvas.*;

/**
 * This class represents Rockets or other projectiles shot from a lander. If something is fired from
 * it with some velocity, may bounce around, will cost rocket budget, or exhibit other behaviors
 * that resemble what the simple rockets already do, then it should probably extend this class.
 */
public class Rocket extends Bumpable {

    // protected fields
    protected int rocketType; // rocket type
    protected Color color; // color with which it should be drawn
    protected int numBounces; // number of bounces it has endured
    protected boolean bouncingBack; // state; whether it is in the process of bouncing from ground


    /**
     * Static method for creating a rocket of a certain type; use this to create rockets fired from
     * a certain lander. It's convenient because you only have to specify lander's position and
     * velocity instead of the rocket's; the method will calculate the rocket's position and
     * velocity based on those.
     * 
     * This is the recommended method of creating a rocket.
     * 
     * @param rocketType type of rocket
     * @param landerPosition firing lander's position vector
     * @param landerVelocity firing lander's velocity vector
     * @param landerAngle firing lander's angle
     * @param color color of the rocket
     * @return instance of Rocket fired from the lander
     */
    public static Rocket createRocket(int rocketType, Vect2D landerPosition, Vect2D landerVelocity,
            double landerAngle, Color color) {
        Vect2D disp = (new Vect2D(-Math.sin(landerAngle), Math.cos(landerAngle)))
                .scale(-LunarLander.LANDER_LENGTH - getSize(rocketType) / 2);

        // position of rocket is at the tail of the lander
        Vect2D position = landerPosition.add(disp);

        // momentum of rocket is affected by the initial rocket velocity and the velocity/angle of
        // the lander at the time
        Vect2D momentum = (landerVelocity.add((new Vect2D(Math.sin(landerAngle), -Math
                .cos(landerAngle))).scale(getInitVelocity(rocketType)))).scale(getMass(rocketType));

        if (rocketType == DRONE_ROCKET) {
            Drone drone = new Drone(position, momentum, color);
            drone.addToLists();
            return drone;
        }
        else {
            Rocket rocket = new Rocket(rocketType, position, momentum, color);
            rocket.addToLists();
            return rocket;
        }
    }

    /**
     * constructor for a Rocket
     * 
     * @param rocketType type of rocket
     * @param position rocket position
     * @param momentum rocket momentum
     * @param color rocket color
     */
    public Rocket(int rocketType, Vect2D position, Vect2D momentum, Color color) {
        this.rocketType = rocketType;
        this.position = position;
        this.momentum = momentum;
        this.color = color;
        this.numBounces = 0;
        this.bouncingBack = false;
    }

    // GETTER METHODS

    /**
     * @return mass of this Rocket
     */
    public double getMass() {
        return rocketConstants[ROCKET_MASS][rocketType];
    }

    /**
     * @return size of this Rocket
     */
    public double getSize() {
        return rocketConstants[ROCKET_SIZE][rocketType];
    }

    /**
     * @return initial velocity upon firing of this Rocket
     */
    public double getInitVelocity() {
        return rocketConstants[ROCKET_INIT_VELOCITY][rocketType];
    }

    /**
     * @return max number of bounces of this Rocket
     */
    public double getBounceLimit() {
        return rocketConstants[ROCKET_BOUNCE_LIMIT][rocketType];
    }

    /**
     * @return 0-1, how much to scale this rocket's momentum with each bounce
     */
    public double getBounceAttenuation() {
        return rocketConstants[ROCKET_ATTENUATION][rocketType];
    }

    /**
     * @return 0-1, feedback of this Rocket on the firing rocket, with 1 indicating feedback being
     *         the full momentum of this rocket when fired
     */
    public double getFeedback() {
        return rocketConstants[ROCKET_FEEDBACK][rocketType];
    }

    /**
     * @return max speed of this Rocket
     */
    public double getMaxSpeed() {
        return rocketConstants[ROCKET_MAX_SPEED][rocketType];
    }

    /**
     * @return cost of this Rocket
     */
    public double getCost() {
        return rocketConstants[ROCKET_COST][rocketType];
    }

    /**
     * implements Bumpable.getCenter()
     * 
     * center of Rocket is the position vector
     */
    public Vect2D getCenter() {
        return position;
    }

    /**
     * @return the type of the rocket
     */
    public int getRocketType() {
        return rocketType;
    }

    // SIMULATION METHODS

    /**
     * implements Bumpable.step()
     * 
     * Also performs bounce attenuation by scaling the momentum down by getBounceAttenuation() if
     * this is the first timestep that the rocket's bouncing back from the ground
     */
    public void step(double dt) {

        // run the usual step method
        super.step(dt);

        // if we're touching ground, then we're in the process of bouncing back
        if (isTouchingGround()) {
            bouncingBack = true;
        }

        // if we're in the process of bouncingBack and has left the ground, then update numBounces
        // and scale momentum by getBounceAttenuation()
        if (bouncingBack && !isTouchingGround()) {
            bouncingBack = false;
            numBounces++;
            momentum = momentum.scale(getBounceAttenuation());
        }
    }

    /**
     * returns true if the Rocket should be taken away from the world. That is, if it's touching the
     * ground and can no longer bounce
     * 
     * @return true for "please delete this Rocket", false otherwise
     */
    public boolean shouldDelete() {
        return isTouchingGround() && numBounces == getBounceLimit();
    }

    /**
     * Is the rocket touching the ground?
     * 
     * @return true if the rocket is touching the ground
     */
    public boolean isTouchingGround() {
        return position.getY() - getSize() / 2 < LunarLanderLauncher.game.moon
                .getTerrainHeight(position.getX());
    }

    /**
     * implements Bumpable.getCollisionMomentumROC()
     * 
     * Sums momentum from gravity, collision with ground, collision with landers and collision with
     * other rockets
     */
    protected Vect2D getCollisionMomentumROC(double dt) {
        return getGroundMomentumROC(dt).add(super.getCollisionMomentumROC(dt));
    }

    /**
     * calculates momentum change due to collision with ground
     * 
     * @param dt timestep size
     * @return momentum ROC due to collision with ground
     */
    protected Vect2D getGroundMomentumROC(double dt) {
        Vect2D normal = new Vect2D(0, 0);

        // if touching ground, then let it bounce!
        if (isTouchingGround() && numBounces < getBounceLimit()) {

            // get the segment we're on
            TerrainSegment segment = LunarLanderLauncher.game.moon
                    .getEncompassingTerrainSegment(position.getX());

            // get the coordinates of the segment
            double lx = segment.getLeftEndPoint().getX();
            double ly = segment.getLeftEndPoint().getY();
            double rx = segment.getRightEndPoint().getX();
            double ry = segment.getRightEndPoint().getY();

            // if the segment is perfectly level, then the normal vector is just directly upwards
            if (ly == ry) {
                normal = new Vect2D(0, 1);
            }
            // else, the normal vector is perpendicular to the terrain segment
            else {
                double slope = (ry - ly) / (rx - lx);
                normal = new Vect2D((slope < 0) ? 1 : -1, 1 / Math.abs(slope));
            }

            double delta = LunarLanderLauncher.game.moon.getTerrainHeight(position.getX())
                    - (position.getY() - getSize() / 2);
            normal = normal.scale(delta / normal.magnitude());
        }

        return normal.scale(K);
    }

    // DRAWING METHODS

    /**
     * implements Bumpable.draw()
     */
    public void draw(Graphics g, Canvas canvas) {
        canvas.drawPolyline(g, color, getRocketPolylines());
    }

    /**
     * returns an array of polyline points for drawing this rocket, translated and rotated with
     * respect to world coordinates
     * 
     * @return array of Vect2D that represents a series of polylines
     */
    protected Vect2D[] getRocketPolylines() {

        Vect2D[] flamePolylines = getVanillaRocketPolylines();

        double angle = momentum.angle();
        // Rotation matrix
        Matrix rotation = new Matrix(Math.cos(angle), -Math.sin(angle), Math.sin(angle), Math
                .cos(angle));

        // Translation vector
        Vect2D translation = new Vect2D(position.getX(), position.getY());

        for (int i = 0; i < flamePolylines.length; i++) {
            flamePolylines[i] = translation.add(rotation.times(flamePolylines[i]));
        }

        return flamePolylines;
    }

    /**
     * returns an array of polyline points for drawing this rocket with respect to the rocket's own
     * coordinate system
     * 
     * @return
     */
    private Vect2D[] getVanillaRocketPolylines() {
        return new Vect2D[] { new Vect2D(-getSize() / 2, -getSize() / 2),
                new Vect2D(getSize() / 2, -getSize() / 2),
                new Vect2D(getSize() / 2, getSize() / 2),
                new Vect2D(-getSize() / 2, getSize() / 2),
                new Vect2D(-getSize() / 2, -getSize() / 2) };
    }

    // STATIC GETTER METHODS

    /**
     * @param rocketType type of rocket
     * @return mass of the rocket
     */
    public static double getMass(int rocketType) {
        return rocketConstants[ROCKET_MASS][rocketType];
    }

    /**
     * @param rocketType type of rocket
     * @return size of the rocket
     */
    public static double getSize(int rocketType) {
        return rocketConstants[ROCKET_SIZE][rocketType];
    }

    /**
     * @param rocketType type of rocket
     * @return initial velocity of the rocket upon firing
     */
    public static double getInitVelocity(int rocketType) {
        return rocketConstants[ROCKET_INIT_VELOCITY][rocketType];
    }

    /**
     * @param rocketType type of rocket
     * @return max number of bounces of the rocket
     */
    public static double getBounceLimit(int rocketType) {
        return rocketConstants[ROCKET_BOUNCE_LIMIT][rocketType];
    }

    /**
     * @param rocketType type of rocket
     * @return 0-1, how much to scale this rocket's momentum with each bounce
     */
    public static double getBounceAttenuation(int rocketType) {
        return rocketConstants[ROCKET_ATTENUATION][rocketType];
    }

    /**
     * @param rocketType type of rocket
     * @return 0-1, feedback of this Rocket on the firing rocket, with 1 indicating feedback being
     *         the full momentum of this rocket when fired
     */
    public static double getFeedback(int rocketType) {
        return rocketConstants[ROCKET_FEEDBACK][rocketType];
    }

    /**
     * @param rocketType type of rocket
     * @return max speed of the rocket
     */
    public static double getMaxSpeed(int rocketType) {
        return rocketConstants[ROCKET_MAX_SPEED][rocketType];
    }

    /**
     * @param rocketType type of rocket
     * @param cost new cost
     */
    public static void setCost(int rocketType, int cost) {
        rocketConstants[ROCKET_COST][rocketType] = cost;
    }

    /**
     * @param rocketType type of rocket
     * @return cost of the rocket
     */
    public static double getCost(int rocketType) {
        return rocketConstants[ROCKET_COST][rocketType];
    }


    // ROCKET TYPE CONSTANTS
    public static final int SMALL_ROCKET = 0;
    public static final int BIG_ROCKET = 1;
    public static final int DRONE_ROCKET = 2;

    // ROCKET INFO CONSTANTS
    public static final int ROCKET_MASS = 0;
    public static final int ROCKET_SIZE = 1;
    public static final int ROCKET_INIT_VELOCITY = 2;
    public static final int ROCKET_BOUNCE_LIMIT = 3;
    public static final int ROCKET_ATTENUATION = 4;
    public static final int ROCKET_FEEDBACK = 5;
    public static final int ROCKET_MAX_SPEED = 6;
    public static final int ROCKET_COST = 7;

    // DEFAULT ROCKET PROPERTY CONSTANTS
    private static final double[] ROCKET_MASSES = { 40000, 750000, 100000 };
    private static final double[] ROCKET_SIZES = { 10, 25, 15 };
    private static final double[] ROCKET_INIT_VELOCITIES = { 40, 40, 5 };
    private static final double[] ROCKET_BOUNCE_LIMITS = { 2, 4, 0 };
    private static final double[] ROCKET_ATTENUATIONS = { 0.4, 0.6, 0 };
    private static final double[] ROCKET_FEEDBACKS = { 0.15, 0.03, 0.05 };
    private static final double[] ROCKET_MAX_SPEEDS = { 100, 100, 25 };
    private static final double[] ROCKET_COSTS = { 1, 5, 0 };

    // ROCKET CONSTANTS MATRIX
    public static final double[][] rocketConstants = { ROCKET_MASSES, ROCKET_SIZES,
            ROCKET_INIT_VELOCITIES, ROCKET_BOUNCE_LIMITS, ROCKET_ATTENUATIONS, ROCKET_FEEDBACKS,
            ROCKET_MAX_SPEEDS, ROCKET_COSTS };
}