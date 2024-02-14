
package lunarlander.gameobject;

import java.util.*;
import java.awt.*;

import lunarlander.LunarLanderLauncher;
import lunarlander.util.Vect2D;


/**
 * Base class for all objects in that can collide with another object in the Lunar Lander
 * simulation.
 * <p />
 * Collisions are handled using the spring model. Each object has an infinite number of springs
 * extending from the center of the object to the edges of the object. When two bumpable objects
 * collide, we calculate the force applied to each object using Hook's Law.
 * 
 * @author Chung Wu
 */
public abstract class Bumpable extends Steppable implements Drawable {

    public static final double K = 20000000; // Spring constant used for collisions

    // PROTECTED FIELDS
    protected Vect2D position; // position vector of the Bumpable
    protected Vect2D momentum; // momentum vector of the Bumpable


    // CONSTRUCTOR

    public Bumpable() {

    }

    /**
     * adds this Bumpable to the relevant data structures
     */
    public void addToLists() {
        LunarLanderLauncher.game.bumpables.add(this);
        LunarLanderLauncher.game.steppables.add(this);
    }
    
    public void removeFromLists() {
        LunarLanderLauncher.game.bumpables.remove(this);
        LunarLanderLauncher.game.steppables.remove(this);
    }

    // GETTER METHODS

    /**
     * gets the position vector of this Bumpable
     * 
     * @return position vector
     */
    public Vect2D getPosition() {
        return position;
    }
    
    public Vect2D getMomentum() {
        return momentum;
    }

    /**
     * gets the velocity vector of this Bumpable
     * 
     * @return velocity vector
     */
    public Vect2D getVelocity() {
        return momentum.scale(1.0 / getMass());
    }

    /**
     * Return the radius of this object
     * 
     * @return the radius of this object
     */
    public double getRadius() {
        return getSize() / 2;
    }

    /**
     * Return the coordinates of the center of this object
     * 
     * @return the coordinates of the center of this object
     */
    public abstract Vect2D getCenter();

    /**
     * Return either the width or height of this object, whichever is larger
     * 
     * @return either the width or height of this object, whichever is larger
     */
    public abstract double getSize();

    /**
     * gets the max speed of this Bumpable
     * 
     * @return max speed
     */
    public abstract double getMaxSpeed();

    /**
     * gets the mass of this Bumpable
     * 
     * @return mass
     */
    public abstract double getMass();

    // DRAWING METHODS

    /**
     * Draws this Bumpable with the given Graphics context on the current game canvas
     * 
     * @param g Graphics context to use
     */
    public void draw(Graphics g) {
        draw(g, LunarLanderLauncher.game.canvas);
    }
    
    /**
     * Draws the Bumpable with the given Graphics context on the given canvas
     * @param g
     * @param canvas
     */
    public abstract void draw(Graphics g, lunarlander.canvas.Canvas canvas);

    // SIMULATION METHODS
    /**
     * Simulates the motion of this Bumpable for one timestep. Updates position and momentum.
     * Momentum updated using getMomentumROC(). Normalizes position vector. Scales momentum to stay
     * within getMaxSpeed().
     * 
     * @param dt size of timestep
     */
    public void step(double dt) {

        Vect2D totalMomentumROC = getMomentumROC(dt);

        position = position.add(getVelocity().scale(dt));
        normalizePosition();

        momentum = momentum.add(totalMomentumROC.scale(dt));

        if (momentum.magnitude() > getMass() * getMaxSpeed()) {
            momentum = momentum.scale(getMass() * getMaxSpeed() / momentum.magnitude());
        }
    }

    /**
     * Gets the momentum ROC for one timestep. By default, just returns the sum of momentum ROC from
     * gravity and from collision.
     * 
     * @param dt timestep size
     * @return total momentum ROC
     */
    protected Vect2D getMomentumROC(double dt) {
        return getGravityMomentumROC(dt).add(getCollisionMomentumROC(dt));
    }

    /**
     * Gets the momentum ROC due to gravity
     * 
     * @param dt timestep size
     * @return momentum ROC due to gravity
     */
    protected Vect2D getGravityMomentumROC(double dt) {
        return new Vect2D(0.0, -LunarLanderLauncher.game.moon.getGravity() * getMass());
    }

    /**
     * Gets the momentum ROC due to collision with various things
     * 
     * @param dt timestep size
     * @return momentum ROC due to collision
     */
    protected Vect2D getCollisionMomentumROC(double dt) {
        return getBumpablesMomentumROC(LunarLanderLauncher.game.bumpables).add(getCeilingMomentumROC(dt));
    }
    
    /**
     * Gets the momentum ROC due to bumping into the ceiling
     * 
     * @param dt timestep size
     * @return momentum ROC due to bumping into ceiling
     */
    protected Vect2D getCeilingMomentumROC(double dt) {
        double delta = LunarLanderLauncher.game.moon.getWorldHeight() - position.getY() + this.getRadius();
        if (delta > 0) {
            return new Vect2D(0.0, 0.0);
        } else {
            return new Vect2D(0.0, K * delta);
        }
    }

    /**
     * Adjusts the horizontal position so that if falls in [0, WORLD_WIDTH]
     */
    protected void normalizePosition() {
        double positionX = position.getX();
        double positionY = position.getY();

        // Wrap around
        while (positionX >= LunarLanderLauncher.game.moon.getWorldWidth()) {
            positionX -= LunarLanderLauncher.game.moon.getWorldWidth();
        }
        while (positionX < 0) {
            positionX += LunarLanderLauncher.game.moon.getWorldWidth();
        }

        position = new Vect2D(positionX, positionY);
    }

    /**
     * For each target object in the bumpables array, calculate the displacement of this object's
     * spring due to the target object. Return the sum of all the displacement vectors.
     * 
     * The returned vector is scaled by K; this is intended to be rate of change of momentum due to
     * collision for "this" Bumpable from the other bumpables.
     * 
     * @param bumpables an Set of bumpable objects that may be pushing against this object
     * @return the sum of all the spring displacement vectors scaled by K.
     */
    public Vect2D getBumpablesMomentumROC(java.util.List bumpables) {
        Vect2D totalV = new Vect2D(0, 0);
        Vect2D thisCenter = this.getCenter();
        if (bumpables != null) {
            synchronized (bumpables) {
                for (Iterator it = bumpables.iterator(); it.hasNext();) {
                    Bumpable b = (Bumpable) it.next();
                    if (b != this) {
                        Vect2D bCenter = b.getCenter();
                        double dist = bCenter.distance(thisCenter);
                        if (dist < this.getRadius() + b.getRadius()) {
                            Vect2D v = thisCenter.add(bCenter.scale(-1));
                            v = v.scale((this.getRadius() + b.getRadius() - v.magnitude())
                                    / v.magnitude());
                            totalV = totalV.add(v);
                        }
                    }
                }
            }
        }
        return totalV.scale(K);
    }
}