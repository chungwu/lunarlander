package lunarlander.map;
import java.io.File;

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
public class LoadedMap extends GameMap {

    /**
     * Constructor
     * 
     * @param file file containing the map description
     * @throws MoonFileException if error reading the parameter file
     */
    public LoadedMap(File file) throws MoonFileException {
        this.file = file;
        moon = new Moon(file);
    }

    /**
     * implements Map.mapType()
     */
    public int getMapType() {
        return GameMap.LOADED;
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
     * implements Map.getNumPads()
     */
    public int getNumPads(int padType) {
        return moon.getNumOfPads(padType);
    }

    /**
     * implements Map.getGravity()
     */
    public double getGravity() {
        return moon.getGravity();
    }

    /**
     * implements Map.toString(); contains map name and its file name
     */
    public String toString() {
    	String mapName = moon.getMapName();
    	if (mapName.length() > 24) {
    		mapName = (mapName.substring(0, 24)) + "...";
    	}
        return mapName + " (" + file.getName() + ") " + gameTypeSuffix();
    }

    /**
     * implements Map.shortName(); contains just the map name
     */
    public String shortName() {
    	String mapName = moon.getMapName();
    	if (mapName.length() > 24) {
    		mapName = (mapName.substring(0, 24)) + "...";
    	}
        return mapName + " " + gameTypeSuffix();
    }

    /**
     * implements Map.equals(); two LoadedMaps are equal if their files are equal.
     */
    public boolean equals(Object o) {
        if (o instanceof LoadedMap) {
            LoadedMap other = (LoadedMap) o;
            return file.equals(other.file);
        }
        else {
            return false;
        }
    }


    protected Moon moon;
    protected File file;
}