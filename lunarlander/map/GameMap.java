package lunarlander.map;
import lunarlander.game.LunarLanderGame;

/**
 * @author Chung
 * 
 * represents a Map
 */
public abstract class GameMap {

    /**
     * @return map type, RANDOM, FLAT or LOADED
     */
    public abstract int getMapType();

    /**
     * @return game type, SINGLE, DUO, etc.
     */
    public int getGameType() {
        return getMoon().getGameType();
    }

    /**
     * @param type game type, SINGLE, DUO, etc.
     * @return true if this map is exactly that game type
     */
    public boolean isGameType(int type) {
        return (getGameType() & type) == type;
    }
    
    /**
     * @param type game type
     * @return true if this map can be played as the argument game type
     */
    public boolean canBePlayedAs(int type) {
        return (getGameType() & type) != 0;
    }

    /**
     * @return a Moon corresponding to this Map
     */
    public abstract Moon getMoon();

    /**
     * @return a spanking new instance of the Moon (if the Moon may be changed, etc.)
     */
    public Moon getNewMoon() {
        return getMoon();
    }

    /**
     * @return width of this Map
     */
    public abstract double getWidth();

    /**
     * @return height of this Map
     */
    public abstract double getHeight();

    /**
     * @return number of landing pads of the specified type in this Map
     */
    public abstract int getNumPads(int padType);

    /**
     * @return gravity for this Map
     */
    public abstract double getGravity();

    /**
     * @return the "short" name for this Map, displayed in drop-down menu
     */
    public abstract String shortName();

    /**
     * @return the "full" name for this Map, displayed in list box
     */
    public abstract String toString();

    /**
     * @return a string denoting the game types of this map
     */
    public String gameTypeSuffix() {
        StringBuffer buffer = new StringBuffer();
        buffer.append('[');

        if (this.isGameType(LunarLanderGame.ANY)) {
            buffer.append("Any");
        }
        else {

            if (this.isGameType(LunarLanderGame.SINGLE)) {
                buffer.append("S, ");
            }
            if (this.isGameType(LunarLanderGame.DUO)) {
                buffer.append("D, ");
            }
            if (this.isGameType(LunarLanderGame.DEATHMATCH)) {
                buffer.append("DM, ");
            }
            if (this.isGameType(LunarLanderGame.TEAM_DEATHMATCH)) {
                buffer.append("TM, ");
            }
            if (this.isGameType(LunarLanderGame.CTF)) {
                buffer.append("CTF, ");
            }
            buffer.delete(buffer.length() - 2, buffer.length());
        }
        buffer.append(']');
        return buffer.toString();
    }

    /**
     * @return true if this Map equals the argument Map, for map selection purposes
     */
    public abstract boolean equals(Object o);


    // MAP TYPE CONSTANTS
    public static final int RANDOM = 0;
    public static final int FLAT = 1;
    public static final int LOADED = 2;
}