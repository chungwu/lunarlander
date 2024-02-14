/*
 * Created by Chung on Dec 14, 2004
 */

package lunarlander.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;

import lunarlander.LunarLanderLauncher;
import lunarlander.Settings;
import lunarlander.canvas.GameCanvas;
import lunarlander.map.*;
import lunarlander.player.*;
import lunarlander.thread.LocalSimulationThread;
import lunarlander.util.Vect2D;
import lunarlander.gameobject.*;


/**
 * LunarLanderGame -- base class for all LunarLanderGames
 */
public abstract class LunarLanderGame {

    public LunarLanderGame() {
        frame = LunarLanderLauncher.frame;

        // Create and add lander canvas
        canvas = createGameCanvas();
        canvas.setPreferredSize(LunarLanderLauncher.preferredCanvasDimension());
        frame.getContentPane().add(canvas, "Center");

        // Create and add btnStartOver button at the top
        createButtonPanel();
        frame.getContentPane().add(buttons, "South");

        // Setup frame
        frame.pack();
        frame.setFocusable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }
    
    /**
     * @return GameCanvas to be used for this game
     */
    protected abstract GameCanvas createGameCanvas();
    
    /**
     * resets the game
     */
    public abstract void reset();
    
    /**
     * destructs the game, "freeing" all resources (we do the best we can in Java!)
     */
    public abstract void destruct();

    /**
     * Return the game over message, or null if the game is not over
     * 
     * @return the game over message, or null if the game is not over
     */
    public abstract String getGameOverMessage();
    
    /**
     * @return the game type
     */
    public abstract GameType getGameType();
    
    /**
     * @return game prefix for Settings
     */
    public String getGamePrefix() {
        return getGameType().getPrefix();
    }
    
    /**
     * Create the button panel
     */
    protected void createButtonPanel() {
        buttons = new JPanel();

        btRestart = new JButton("Restart");
        btRestart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        btRestart.setEnabled(true);
        buttons.add(btRestart);

        btOptions = new JButton("Options");
        btOptions.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LunarLanderLauncher.mainOptions.displayOptions();
            }
        });
        buttons.add(btOptions);

        btExit = new JButton("Exit Game");
        btExit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LocalSimulationThread.getInstance().stopSimulation();
                LunarLanderLauncher.game.destruct();
                LunarLanderLauncher.displayTitleFrame();
                LunarLanderLauncher.setupFrame();
                LunarLanderLauncher.frame.repaint();
            }
        });
        buttons.add(btExit);
    }
    
    /**
     * @param type gametype; must not be a composite type
     * @return index into GAME_TYPES corresponding to this type; -1 if not found
     */
    public static int indexOfGameType(int type) {
        for(int i=0; i<GAME_TYPES.length; i++) {
            if (type == GAME_TYPES[i]) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * @param playerNumber PLAYER_ONE or PLAYER_TWO
     * @return corresponding Player
     */
    public Player getPlayer(int playerId) {
        for (Player player : players) {
            if (player.getId() == playerId) {
                return player;
            }
        }
        return null;
    }
    
    // GAME TYPES
    public static final int SINGLE = 1 << 0;
    public static final int DUO = 1 << 1;
    public static final int DEATHMATCH = 1 << 2;
    public static final int TEAM_DEATHMATCH = 1 << 3;
    public static final int CTF = 1 << 4;
    public static final int ANY = SINGLE | DUO | DEATHMATCH | TEAM_DEATHMATCH | CTF;
    
    public static final int[] GAME_TYPES = {ANY, SINGLE, DUO, DEATHMATCH, TEAM_DEATHMATCH, CTF};
    public static final String[] GAME_TYPE_NAMES = {"All", "Single-Player", "Duo", "Deathmatch", "Team Deathmatch", "Capture The Flag"};
    

    
    // GENERAL CONSTANTS
    public static final double DT = .01; // Simulation timestep
    public static final double DRAW_DT = .05; // Draw timestep

    public static final double TRACE_INTERVAL = 0.5;
    public static final double TRACE_ALIVE_TIME = 30.0;
    public static final int RECORDER_CAPACITY = (int) (TRACE_ALIVE_TIME / TRACE_INTERVAL);
    
    // DATA STRUCTURES

    // collection of players in the game
    public List<Player> players = Collections.synchronizedList(new ArrayList<Player>());

    // collection of Bumpable objects in the game
    public List<Bumpable> bumpables = Collections.synchronizedList(new ArrayList<Bumpable>());

    // collection of Steppable objects in the game
    public List<Steppable> steppables = Collections.synchronizedList(new ArrayList<Steppable>());

    // GAME COMPONENTS
    public GameMap[] maps; // terrain
    public GameMap map; // current map
    public Moon moon; // current moon
    public GameCanvas canvas; // Game canvas
    public double time; // Current time

    // DEFAULT SETTINGS
    protected Vect2D initVel = new Vect2D(0, 0); // meters/sec
    protected double initAngle = 0.0; // rad

    // CUSTOMIZABLE SETTINGS
    public double turbo; // turbo multiplier
    public double safeVelocityX; // safe landing x velocity
    public double safeVelocityY; // safe landing y velocity
    public double safeAngle; // safe landing angle
    public double gravity; // meters/sec^2

    // GRAPHICS COMPONENTS
    protected JFrame frame; // Graphics window
    protected JButton btRestart; // Start over button
    protected JButton btOptions; // Options
    protected JButton btExit; // Exit
    protected JPanel buttons;
}