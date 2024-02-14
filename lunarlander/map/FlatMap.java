package lunarlander.map;
/**
 * @author Chung
 * 
 * Represents a Flat map
 */
public class FlatMap extends GameMap {

    /**
     * Constructor; creates the flat moon
     * 
     * @param width width of the world
     * @param height height of the world
     * @param gravity gravity of the world
     */
    public FlatMap(double width, double height, double gravity) {
        moon = new Moon(width, height, gravity);
    }

    /**
     * implements Map.mapType()
     */
    public int getMapType() {
        return GameMap.FLAT;
    }

    /**
     * implements Map.getMoon()
     */
    public Moon getMoon() {
        return moon;
    }

    /**
     * implements Map.getWidth()
     */
    public double getWidth() {
        return moon.getWorldWidth();
    }

    /**
     * implements Map.getHeight()
     */
    public double getHeight() {
        return moon.getWorldHeight();
    }

    /**
     * implements Map.getGravity()
     */
    public double getGravity() {
        return moon.getGravity();
    }

    /**
     * implements Map.getNumLongPads(); flat maps only has one long pad
     */
    public int getNumPads(int padType) {
        return (padType == Terrain.LONG_LANDING_PAD) ? 1 : 0;
    }

    /**
     * implements Map.toString(); writes "Flat" and its width
     */
    public String toString() {
        return "Flat (width: " + moon.getWorldWidth() + ") " + gameTypeSuffix();
    }

    /**
     * implements Map.shortName(); just "Flat"
     */
    public String shortName() {
        return "Flat " + gameTypeSuffix();
    }

    /**
     * implements Map.equalst(); two FlatMaps are equal if they have the same width, height and
     * gravity
     */
    public boolean equals(Object o) {
        if (o instanceof FlatMap) {
            FlatMap other = (FlatMap) o;
            return getWidth() == other.getWidth() && getHeight() == other.getHeight()
                    && getGravity() == other.getGravity();
        }
        else {
            return false;
        }
    }


    protected Moon moon;
}