package lunarlander;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Hashtable;
import java.awt.Color;

import lunarlander.game.GameType;
import lunarlander.map.*;
import java.io.*;

/**
 * @author Chung
 * 
 * Settings is a global hashtable saving the current game settings of everything. Its methods are
 * typically called by GUI code or when initializing a new LunarLanderGame.
 */
public class Settings {

    private static Hashtable<String, Object> settings; // Hashtable of settings
    private static Hashtable<String, Object> defaultSettings; // Hashtable of default settings


    /**
     * GETTER METHODS -- gets the value of entry in settings Hashtable corresponding to "property"
     * 
     * @param property name of property
     * @return value stored
     */
    public static double getDouble(String property) {
        return Double.parseDouble((String) settings.get(property));
    }

    public static int getInt(String property) {
        return Integer.parseInt((String) settings.get(property));
    }

    public static Color getColor(String property) {
        return (Color) settings.get(property);
    }

    public static String getString(String property) {
        return (String) settings.get(property);
    }

    public static boolean getBoolean(String property) {
        return Boolean.valueOf((String) settings.get(property)).booleanValue();
    }

    public static GameMap[] getMaps(String property) {
        return (GameMap[]) settings.get(property);
    }

    public static Object get(String property) {
        return settings.get(property);
    }

    /**
     * SETTER METHODS -- stores a new value into the Hashtable corresponding to "property"
     * 
     * @param property name of property
     * @param value new value to store
     */
    public static void setDouble(String property, double value) {
        settings.put(property, Double.toString(value));
    }

    public static void setInt(String property, int value) {
        settings.put(property, Integer.toString(value));
    }

    public static void setColor(String property, Color value) {
        settings.put(property, value);
    }

    public static void setString(String property, String value) {
        settings.put(property, value);
    }

    public static void setBoolean(String property, boolean value) {
        settings.put(property, Boolean.toString(value));
    }

    public static void setMaps(String property, GameMap[] maps) {
        settings.put(property, maps);
    }

    public static void set(String property, Object value) {
        settings.put(property, value);
    }

    /**
     * DEFAULT GETTER METHODS -- like the getter methods, but fetches from the default Hashtable
     * instead (and so returns "default" values)
     * 
     * @param property name of property
     * @return default value for that property
     */
    public static double getDefaultDouble(String property) {
        return Double.parseDouble((String) defaultSettings.get(property));
    }

    public static int getDefaultInt(String property) {
        return Integer.parseInt((String) defaultSettings.get(property));
    }

    public static Color getDefaultColor(String property) {
        return (Color) defaultSettings.get(property);
    }

    public static String getDefaultString(String property) {
        return (String) defaultSettings.get(property);
    }

    public static boolean getDefaultBoolean(String property) {
        return Boolean.valueOf((String) defaultSettings.get(property)).booleanValue();
    }

    public static void save() {
        try {
            Properties propertiesToSave = getPropertiesToSave();

            propertiesToSave.store(new FileOutputStream("settings.ll", false),
                    "Lunar Lander Settings");
        }
        catch (FileNotFoundException e) {
            System.err.println("Error creating LunarLander settings file: " + e);
        }
        catch (IOException e) {
            System.err.println("Error updating LunarLander settings file: " + e);
        }
    }

    public static void load() {

        // File settingsFile = new File("settings.ll");
        try {
            defaultSettings = loadProperties("defaults.ll");
        }
        catch (IOException e) {
            System.err.println("Did not find default settings file 'defaults.ll'!  Aborting...");
            System.exit(1);
        }

        try {
            settings = loadProperties("settings.ll");
        }
        catch (IOException e) {
            System.err
                    .println("Did not find settings file 'settings.ll'!  Using default settings...");
            settings = new Hashtable<String,Object>(defaultSettings);
        }
    }

    private static Hashtable<String, Object> loadProperties(String filename) throws IOException {
        Hashtable<String, Object> settings = new Hashtable<String, Object>();
        Properties properties = new Properties();

        properties.load(new FileInputStream(filename));

        for (int i = 0; i < PERSISTED_PROPERTIES.length; i++) {
            settings.put(PERSISTED_PROPERTIES[i], properties.getProperty(PERSISTED_PROPERTIES[i]));
        }

        settings.put(PLAYER_ONE + COLOR, new Color(Integer.parseInt(properties
                .getProperty(PLAYER_ONE + COLOR + RED)), Integer.parseInt(properties
                .getProperty(PLAYER_ONE + COLOR + GREEN)), Integer.parseInt(properties
                .getProperty(PLAYER_ONE + COLOR + BLUE))));

        settings.put(PLAYER_TWO + COLOR, new Color(Integer.parseInt(properties
                .getProperty(PLAYER_TWO + COLOR + RED)), Integer.parseInt(properties
                .getProperty(PLAYER_TWO + COLOR + GREEN)), Integer.parseInt(properties
                .getProperty(PLAYER_TWO + COLOR + BLUE))));

        try {
            settings.put(HOST_IP, (InetAddress.getLocalHost()).getHostAddress());
        }
        catch (UnknownHostException e) {
            setString(HOST_IP, "localhost");
        }

        return settings;
    }

    private static Properties getPropertiesToSave() {
        Properties saved = new Properties();

        for (int i = 0; i < PERSISTED_PROPERTIES.length; i++) {
            saved.setProperty(PERSISTED_PROPERTIES[i], (String) settings
                    .get(PERSISTED_PROPERTIES[i]));
        }

        saved.setProperty(PLAYER_ONE + COLOR + RED, "" + getColor(PLAYER_ONE + COLOR).getRed());
        saved.setProperty(PLAYER_ONE + COLOR + GREEN, "" + getColor(PLAYER_ONE + COLOR).getGreen());
        saved.setProperty(PLAYER_ONE + COLOR + BLUE, "" + getColor(PLAYER_ONE + COLOR).getBlue());

        saved.setProperty(PLAYER_TWO + COLOR + RED, "" + getColor(PLAYER_TWO + COLOR).getRed());
        saved.setProperty(PLAYER_TWO + COLOR + GREEN, "" + getColor(PLAYER_TWO + COLOR).getGreen());
        saved.setProperty(PLAYER_TWO + COLOR + BLUE, "" + getColor(PLAYER_TWO + COLOR).getBlue());

        return saved;
    }


    /**
     * Initializes the settings and defaultSettings Hashtables with default values for each property
     */
    /*
     * public static void initializeDefaults() { settings = new Hashtable(); // configure global
     * defaults setBoolean(ANTIALIAS, true); setBoolean(DISPLAY_SAFETY, true); setInt(RESOLUTION,
     * 0); setBoolean(TRACE, true); setInt(SELECTED_NETWORK_GAME, 0); // refers to selected network
     * game setDouble(ASPECT_RATIO, 800.0/600.0);
     * 
     * try { setString(HOST_IP, (InetAddress.getLocalHost()).getHostAddress()); } catch
     * (UnknownHostException e) { setString(HOST_IP, "localhost"); }
     * 
     * setString(REMOTE_IP, ""); // configure player1 defaults setString(PLAYER_ONE + NAME, "Player
     * 1"); setInt(PLAYER_ONE + TEAM, NetworkPlayer.TEAM_ONE); setInt(PLAYER_ONE + ROLE,
     * NetworkPlayer.PLAYER); setColor(PLAYER_ONE + COLOR, Color.orange); setBoolean(PLAYER_ONE +
     * LANDING_ASSIST, true); // configure player2 defaults setString(PLAYER_TWO + NAME, "Player
     * 2"); setInt(PLAYER_TWO + TEAM, NetworkPlayer.TEAM_TWO); setInt(PLAYER_TWO + ROLE,
     * NetworkPlayer.PLAYER); setColor(PLAYER_TWO + COLOR, Color.green); setBoolean(PLAYER_TWO +
     * LANDING_ASSIST, true); // configure Single defaults setDouble(SINGLE + INITIAL_HEIGHT, 500);
     * setDouble(SINGLE + WORLD_WIDTH, 1000); setDouble(SINGLE + WORLD_HEIGHT, 750);
     * setDouble(SINGLE + GRAVITY, 2.0); setInt(SINGLE + LONG_PADS, 2); setInt(SINGLE + SHORT_PADS,
     * 3); setDouble(SINGLE + TURBO, 5.0); setDouble(SINGLE + SAFE_VX, 3.0); setDouble(SINGLE +
     * SAFE_VY, 6.0); setDouble(SINGLE + SAFE_ANGLE, 5.0); setBoolean(SINGLE + LANDING_ASSIST,
     * true); // configure Duo defaults setDouble(DUO + INITIAL_HEIGHT, 250); setDouble(DUO +
     * WORLD_WIDTH, 600); setDouble(DUO + WORLD_HEIGHT, 450); setDouble(DUO + GRAVITY, 2.0);
     * setInt(DUO + LONG_PADS, 1); setInt(DUO + SHORT_PADS, 1); setDouble(DUO + TURBO, 5.0);
     * setDouble(DUO + SAFE_VX, 3.0); setDouble(DUO + SAFE_VY, 6.0); setDouble(DUO + SAFE_ANGLE,
     * 5.0); setBoolean(DUO + LANDING_ASSIST, true); setBoolean(DUO + MUST_LAND_TO_WIN, false);
     * setBoolean(DUO + ENABLE_SMALL_ROCKETS, true); setBoolean(DUO + ENABLE_BIG_ROCKETS, true);
     * setBoolean(DUO + ENABLE_DRONES, true); setBoolean(DUO + ROCKETS_DAMAGE, true); setInt(DUO +
     * ROCKET_BUDGET, 5); // configure Deathmatch defaults setDouble(DEATHMATCH + INITIAL_HEIGHT,
     * 500); setDouble(DEATHMATCH + WORLD_WIDTH, 1000); setDouble(DEATHMATCH + WORLD_HEIGHT, 750);
     * setDouble(DEATHMATCH + GRAVITY, 2.0); setInt(DEATHMATCH + LONG_PADS, 1); setInt(DEATHMATCH +
     * SHORT_PADS, 1); setDouble(DEATHMATCH + TURBO, 5.0); setDouble(DEATHMATCH + SAFE_VX, 3.0);
     * setDouble(DEATHMATCH + SAFE_VY, 6.0); setDouble(DEATHMATCH + SAFE_ANGLE, 5.0);
     * setBoolean(DEATHMATCH + LANDING_ASSIST, true); setBoolean(DEATHMATCH + ENABLE_SMALL_ROCKETS,
     * true); setBoolean(DEATHMATCH + ENABLE_BIG_ROCKETS, true); setBoolean(DEATHMATCH +
     * ENABLE_DRONES, true); setBoolean(DEATHMATCH + ROCKETS_DAMAGE, true); setInt(DEATHMATCH +
     * ROCKET_BUDGET, 5); setBoolean(DEATHMATCH + INDICATE_ENEMY, true); setInt(DEATHMATCH +
     * POINT_LIMIT, 30); setDouble(DEATHMATCH + TIME_LIMIT, 20); // configure team TEAM_DEATHMATCH
     * defaults setDouble(TEAM_DEATHMATCH + INITIAL_HEIGHT, 500); setDouble(TEAM_DEATHMATCH +
     * WORLD_WIDTH, 1000); setDouble(TEAM_DEATHMATCH + WORLD_HEIGHT, 750); setDouble(TEAM_DEATHMATCH +
     * GRAVITY, 2.0); setInt(TEAM_DEATHMATCH + LONG_PADS, 1); setInt(TEAM_DEATHMATCH + SHORT_PADS,
     * 1); setDouble(TEAM_DEATHMATCH + TURBO, 5.0); setDouble(TEAM_DEATHMATCH + SAFE_VX, 3.0);
     * setDouble(TEAM_DEATHMATCH + SAFE_VY, 6.0); setDouble(TEAM_DEATHMATCH + SAFE_ANGLE, 5.0);
     * setBoolean(TEAM_DEATHMATCH + LANDING_ASSIST, true); setBoolean(TEAM_DEATHMATCH +
     * ENABLE_SMALL_ROCKETS, true); setBoolean(TEAM_DEATHMATCH + ENABLE_BIG_ROCKETS, true);
     * setBoolean(TEAM_DEATHMATCH + ENABLE_DRONES, true); setBoolean(TEAM_DEATHMATCH +
     * ROCKETS_DAMAGE, true); setInt(TEAM_DEATHMATCH + ROCKET_BUDGET, 5); setBoolean(TEAM_DEATHMATCH +
     * INDICATE_ENEMY, true); setBoolean(TEAM_DEATHMATCH + INDICATE_TEAMMATE, true);
     * setBoolean(TEAM_DEATHMATCH + FRIENDLY_FIRE, true); setInt(TEAM_DEATHMATCH + POINT_LIMIT, 30);
     * setDouble(TEAM_DEATHMATCH + TIME_LIMIT, 20); // configure team CTF defaults setDouble(CTF +
     * INITIAL_HEIGHT, 500); setDouble(CTF + WORLD_WIDTH, 1000); setDouble(CTF + WORLD_HEIGHT, 750);
     * setDouble(CTF + GRAVITY, 2.0); setInt(CTF + LONG_PADS, 1); setInt(CTF + SHORT_PADS, 1);
     * setDouble(CTF + TURBO, 5.0); setDouble(CTF + SAFE_VX, 3.0); setDouble(CTF + SAFE_VY, 6.0);
     * setDouble(CTF + SAFE_ANGLE, 5.0); setBoolean(CTF + LANDING_ASSIST, true); setBoolean(CTF +
     * ENABLE_SMALL_ROCKETS, true); setBoolean(CTF + ENABLE_BIG_ROCKETS, true); setBoolean(CTF +
     * ENABLE_DRONES, true); setBoolean(CTF + ROCKETS_DAMAGE, true); setInt(CTF + ROCKET_BUDGET, 5);
     * setBoolean(CTF + INDICATE_ENEMY, true); setBoolean(CTF + INDICATE_TEAMMATE, true);
     * setBoolean(CTF + FRIENDLY_FIRE, true); setInt(CTF + POINT_LIMIT, 30); setDouble(CTF +
     * TIME_LIMIT, 20); // store defaults elsewhere defaultSettings = (Hashtable) settings.clone(); }
     */

    // GLOBAL SETTINGS
    public static final String ANTIALIAS = "antialias";
    public static final String DISPLAY_SAFETY = "display_safety";
    public static final String RESOLUTION = "resolution";
    public static final String TRACE = "trace";
    public static final String SELECTED_NETWORK_GAME = "selected_game";
    public static final String HOST_IP = "host_ip";
    public static final String REMOTE_IP = "remote_ip";
    public static final String ASPECT_RATIO = "aspect_ratio";

    // PREFIXES
    public static final String SINGLE = "single.";
    public static final String DUO = "duo.";
    public static final String NETWORK = "network.";
    public static final String PLAYER_ONE = "player_one.";
    public static final String PLAYER_TWO = "player_two.";

    // PLAYER PROPERTIES
    public static final String NAME = "name";
    public static final String TEAM = "team";
    public static final String ROLE = "role";
    public static final String COLOR = "color";
    public static final String RED = ".red";
    public static final String BLUE = ".blue";
    public static final String GREEN = ".green";

    // GAME PROPERTIES
    public static final String MAPS = "maps";
    public static final String MAPS_VECTOR = "maps_vector";
    public static final String INITIAL_HEIGHT = "initial_height";
    public static final String SAFE_VX = "safe_vx";
    public static final String SAFE_VY = "safe_vy";
    public static final String SAFE_ANGLE = "safe_angle";
    public static final String WORLD_WIDTH = "world_width";
    public static final String WORLD_HEIGHT = "world_height";
    public static final String GRAVITY = "gravity";
    public static final String LONG_PADS = "long_pads";
    public static final String SHORT_PADS = "short_pads";
    public static final String TURBO = "turbo";
    public static final String LANDING_ASSIST = "landing_assist";

    public static final String MUST_LAND_TO_WIN = "must_land_to_win";
    public static final String ENABLE_SMALL_ROCKETS = "enable_small_rockets";
    public static final String ENABLE_BIG_ROCKETS = "enable_big_rockets";
    public static final String ENABLE_DRONES = "enable_drones";
    public static final String ROCKET_BUDGET = "rocket_budget";
    public static final String ROCKETS_DAMAGE = "rockets_damage";

    public static final String INDICATE_ENEMY = "indicate_enemy";
    public static final String INDICATE_TEAMMATE = "indicate_teammate";
    public static final String TIME_LIMIT = "time_limit";
    public static final String POINT_LIMIT = "point_limit";
    public static final String FRIENDLY_FIRE = "friendly_fire";

    private static final String[] PERSISTED_PROPERTIES = {

    ANTIALIAS, DISPLAY_SAFETY, TRACE, ASPECT_RATIO, RESOLUTION, SELECTED_NETWORK_GAME,

    PLAYER_ONE + NAME, PLAYER_TWO + NAME, PLAYER_ONE + LANDING_ASSIST, PLAYER_TWO + LANDING_ASSIST,
            PLAYER_ONE + TEAM, PLAYER_ONE + ROLE, PLAYER_TWO + TEAM, PLAYER_TWO + ROLE,

            SINGLE + INITIAL_HEIGHT, SINGLE + WORLD_WIDTH, SINGLE + WORLD_HEIGHT, SINGLE + GRAVITY,
            SINGLE + TURBO, SINGLE + SAFE_VX, SINGLE + SAFE_VY, SINGLE + SAFE_ANGLE,
            SINGLE + LANDING_ASSIST, SINGLE + LONG_PADS, SINGLE + SHORT_PADS,

            DUO + LANDING_ASSIST, DUO + MUST_LAND_TO_WIN, DUO + ENABLE_SMALL_ROCKETS,
            DUO + ENABLE_BIG_ROCKETS, DUO + ENABLE_DRONES, DUO + ROCKETS_DAMAGE, DUO + LONG_PADS,
            DUO + SHORT_PADS, DUO + ROCKET_BUDGET, DUO + INITIAL_HEIGHT, DUO + WORLD_WIDTH,
            DUO + WORLD_HEIGHT, DUO + GRAVITY, DUO + TURBO, DUO + SAFE_VX, DUO + SAFE_VY,
            DUO + SAFE_ANGLE,

            /*
            DEATHMATCH + LANDING_ASSIST, DEATHMATCH + ENABLE_SMALL_ROCKETS,
            DEATHMATCH + ENABLE_BIG_ROCKETS, DEATHMATCH + ENABLE_DRONES,
            DEATHMATCH + ROCKETS_DAMAGE, DEATHMATCH + INDICATE_ENEMY, DEATHMATCH + LONG_PADS,
            DEATHMATCH + SHORT_PADS, DEATHMATCH + ROCKET_BUDGET, DEATHMATCH + POINT_LIMIT,
            DEATHMATCH + INITIAL_HEIGHT, DEATHMATCH + WORLD_WIDTH, DEATHMATCH + WORLD_HEIGHT,
            DEATHMATCH + GRAVITY, DEATHMATCH + TURBO, DEATHMATCH + SAFE_VX, DEATHMATCH + SAFE_VY,
            DEATHMATCH + SAFE_ANGLE, DEATHMATCH + TIME_LIMIT,
            */

            NETWORK + LANDING_ASSIST, NETWORK + ENABLE_SMALL_ROCKETS,
            NETWORK + ENABLE_BIG_ROCKETS, NETWORK + ENABLE_DRONES,
            NETWORK + ROCKETS_DAMAGE, NETWORK + INDICATE_ENEMY,
            NETWORK + FRIENDLY_FIRE, NETWORK + INDICATE_TEAMMATE,
            NETWORK + LONG_PADS, NETWORK + SHORT_PADS,
            NETWORK + ROCKET_BUDGET, NETWORK + POINT_LIMIT,
            NETWORK + INITIAL_HEIGHT, NETWORK + WORLD_WIDTH,
            NETWORK + WORLD_HEIGHT, NETWORK + GRAVITY, NETWORK + TURBO,
            NETWORK + SAFE_VX, NETWORK + SAFE_VY, NETWORK + SAFE_ANGLE,
            NETWORK + TIME_LIMIT

            /*
            CTF + LANDING_ASSIST, CTF + ENABLE_SMALL_ROCKETS, CTF + ENABLE_BIG_ROCKETS,
            CTF + ENABLE_DRONES, CTF + ROCKETS_DAMAGE, CTF + INDICATE_ENEMY, CTF + FRIENDLY_FIRE,
            CTF + INDICATE_TEAMMATE, CTF + LONG_PADS, CTF + SHORT_PADS, CTF + ROCKET_BUDGET,
            CTF + POINT_LIMIT, CTF + INITIAL_HEIGHT, CTF + WORLD_WIDTH, CTF + WORLD_HEIGHT,
            CTF + GRAVITY, CTF + TURBO, CTF + SAFE_VX, CTF + SAFE_VY, CTF + SAFE_ANGLE,
            CTF + TIME_LIMIT
            */
    };

}