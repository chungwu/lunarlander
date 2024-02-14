package lunarlander.map;
/*
 * Created on Jan 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class RandomMap extends GameMap {

    /**
     * Constructor
     * 
     * @param width width of world
     * @param height height of world
     * @param gravity gravity of world
     * @param nlp number of long pads
     * @param nsp number of short pads
     */
    public RandomMap(double width, double height, double gravity, int nlp, int nsp) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.gravity = gravity;
        this.numLongPads = nlp;
        this.numShortPads = nsp;
        moon = new Moon(worldWidth, worldHeight, gravity, 5, numLongPads, numShortPads);
    }

    /**
     * implements Map.mapType()
     */
    public int getMapType() {
        return GameMap.RANDOM;
    }

    /**
     * implements Map.getMoon()
     */
    public Moon getMoon() {
        return moon;
    }
    
    /**
     * implements Map.getNewMoon(); creates a new random moon
     */
    public Moon getNewMoon() {
        moon = new Moon(worldWidth, worldHeight, gravity, 5, numLongPads, numShortPads);
        return moon;
    }

    /**
     * implements Map.getWidth()
     */
    public double getWidth() {
        return worldWidth;
    }

    /**
     * implements Map.getHeight()
     */
    public double getHeight() {
        return worldHeight;
    }

    /**
     * implements Map.getNumPads()
     */
    public int getNumPads(int padType) {
        return (padType == Terrain.LONG_LANDING_PAD) ? numLongPads : numShortPads;
    }

    /**
     * implements Map.getGravity()
     */
    public double getGravity() {
        return gravity;
    }

    /**
     * implements Map.toString(); contains "Random" and its width, number of long and short pads
     */
    public String toString() {
        return "Random (width: " + worldWidth + "; longs: " + numLongPads + "; shorts: "
                + numShortPads + ") " + gameTypeSuffix();
    }

    /**
     * implements Map.shortName(); just "Random"
     */
    public String shortName() {
        return "Random " + gameTypeSuffix();
    }

    /**
     * implements Map.equals(); two RandomMaps are equal if they have the same worldWidth,
     * worldHeight, gravity, number of long pads and number of short pads
     */
    public boolean equals(Object o) {
        if (o instanceof RandomMap) {
            RandomMap other = (RandomMap) o;
            return worldWidth == other.worldWidth && worldHeight == other.worldHeight
                    && gravity == other.gravity && numLongPads == other.numLongPads
                    && numShortPads == other.numShortPads;
        }
        else {
            return false;
        }
    }

    protected Moon moon;
    protected double worldWidth;
    protected double worldHeight;
    protected double gravity;
    protected int numLongPads;
    protected int numShortPads;
}