package lunarlander.util;

import java.io.Serializable;

/**
 * An immutable 2D vector
 * 
 * @author Michael Yu
 */
public class Vect2D implements Serializable {

    /**
     * Construct a new vector with coordinates (0,0)
     */
    public Vect2D() {
        this(0, 0);
    }

    /**
     * Construct a new vector with coordinates (x,y)
     * 
     * @param x is the x-component of the vector
     * @param y is the x-component of the vector
     */
    public Vect2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return the x-component of the vector
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y-component of the vector
     */
    public double getY() {
        return y;
    }
    
    /**
     * set x to given x
     * @param x new x
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * set y to given y
     * @param y new y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Calculate the dot product of 'this' vector and v
     * 
     * @param v is a vector to be dotted with 'this' vector
     * @return the dot product of 'this' vector and v
     */
    public double dot(Vect2D v) {
        return x * v.getX() + y * v.getY();
    }

    /**
     * Return the sum of 'this' vector and v
     * 
     * @param v is a vector to be added to 'this' vector
     * @return a new vector that is the sum of 'this' and v
     */
    public Vect2D add(Vect2D v) {
        return new Vect2D(x + v.getX(), y + v.getY());
    }
    
    /**
     * Return this vector - v
     * @param v argument to subtract
     * @return difference vector
     */
    public Vect2D subtract(Vect2D v) {
        return this.add(v.scale(-1));
    }

    /**
     * Scale 'this' vector by c
     * 
     * @param c is the number 'this' vector will be scaled by
     * @return a new vector that is 'this' vector scaled by c
     */
    public Vect2D scale(double c) {
        return new Vect2D(x * c, y * c);
    }

    /**
     * Return the magnitude of the vector
     * 
     * @return the magnitude of the vector
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Return the distance between this vector and v
     * 
     * @param v is a vector
     * @return the distance between this vector and v
     */
    public double distance(Vect2D v) {
        return v.add(this.scale(-1)).magnitude();
    }

    /**
     * Return the angle between this vector and the vector (1,0). The angle is normalized to lie in
     * the range (-Pi,Pi].
     * 
     * @return the angle between this vector and the vector (1,0).
     */
    public double angle() {
        return Math.atan(y / x);
    }
    
    /**
     * Checks vector equality
     */
    public boolean equals(Object o) {
        Vect2D v = (Vect2D) o;
        return x == v.x && y == v.y;
    }

    /**
     * Convert this vector to a string
     * 
     * @return the vector in string form
     */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }


    private double x; // The x-component of the vector
    private double y; // The y-component of the vector
}