package lunarlander.game;

import java.io.*;

import lunarlander.canvas.GameCanvas;
import lunarlander.canvas.SingleGameCanvas;
import lunarlander.gameobject.LunarLander;
import lunarlander.LunarLanderLauncher;
import lunarlander.Settings;
import lunarlander.player.HumanPlayer;
import lunarlander.player.Player;
import lunarlander.thread.LocalSimulationThread;
import lunarlander.util.Vect2D;

/**
 * Single-player LunarLanderGame
 * 
 * @author Michael Yu
 */
public class LunarLanderSingle extends LunarLanderGame {

    /**
     * Constructor
     * 
     * @param options options user chose to start the game with
     */
    public LunarLanderSingle() {

        super();

        // Create players
        players.add(new HumanPlayer(Settings.getString(Settings.PLAYER_ONE + Settings.NAME),
                Settings.getColor(Settings.PLAYER_ONE + Settings.COLOR), null,
                HumanPlayer.LAYOUT_ONE));
        
        // Setup common game options
        String prefix = getGamePrefix();
        turbo = Settings.getDouble(prefix+Settings.TURBO);
        safeVelocityX = Settings.getDouble(prefix+Settings.SAFE_VX);
        safeVelocityY = Settings.getDouble(prefix+Settings.SAFE_VY);
        safeAngle = Settings.getDouble(prefix+Settings.SAFE_ANGLE);
        maps = Settings.getMaps(prefix+Settings.MAPS);
    }

    /**
     * implements LunarLanderGame.createGameCanvas()
     */
    protected GameCanvas createGameCanvas() {
        return new SingleGameCanvas(this);
    }
    
    /**
     * implements LunarLanderGame.destruct()
     */
    public void destruct() {
        getPlayer().destruct();
        players.clear();
        bumpables.clear();
        steppables.clear();
        LunarLanderLauncher.game = null;
    }

    /**
     * implements LunarLanderGame.getGameType()
     */
    public GameType getGameType() {
        return GameType.SINGLE;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return (Player) players.get(0);
    }

    /**
     * @return the lander
     */
    public LunarLander getLander() {
        return getPlayer().getLander();
    }

    /**
     * @return the score
     */
    public int getScore() {
        return getPlayer().getScore();
    }

    /**
     * Return the appropriate game over message for the current score
     * 
     * @return the game over message based on the current score
     */
    public String getGameOverMessage() {
        // Not game over yet
        if (getLander().getStatus() == LunarLander.STATUS_FLYING) {
            return null;
        }
        // Landed safely!
        else if (getLander().getStatus() == LunarLander.STATUS_LANDED) {
            updateScore();
            int index = (int) ((GAME_OVER_MESSAGES.length - 1) * Math.min(1.0,
                    (double) getScore() / 130000.0));
            return GAME_OVER_MESSAGES[index];
        }
        // Crashed!
        else {
            return "CRASHED!";
        }
    }

    /**
     * Calculate the score if the lander lands safely with the given velocity and fuel remaining.
     * This method assumes the lander has landed safely!
     * 
     * @param velocity is the landing velocity
     * @param fuel is the fuel remaining
     * @return the score
     */
    public int calculateScore(Vect2D velocity, double fuel) {
        double vx = velocity.getX();
        double vy = velocity.getY();
        double fuelPercentage = fuel / LunarLander.MAX_FUEL;
        double vxPercentage = (safeVelocityX - Math.abs(vx)) / safeVelocityX;
        double vyPercentage = (safeVelocityY - Math.abs(vy)) / safeVelocityY;

        int score = (int) (40000 * (fuelPercentage - 0.5) + 30000 * Math.pow(vxPercentage, 2) + 30000 * Math
                .pow(vyPercentage, 2));
        score *= moon.getScoreMultiplier(getLander().getPosition().getX());
        return score;
    }

    /**
     * Reset the game
     */
    public void reset() {
        LocalSimulationThread.getInstance().stopSimulation();

        bumpables.clear();
        steppables.clear();
        time = 0;

        // pick a map to use
        map = maps[(int) (Math.random() * maps.length)];
        moon = map.getNewMoon();

        Vect2D position = new Vect2D(0, 0);

        LunarLander lander = new LunarLander(position, initVel, initAngle, getPlayer());
        lander.setLandingAssist(Settings.getBoolean(Settings.PLAYER_ONE + Settings.LANDING_ASSIST));
        lander.addToLists();
        getPlayer().setLander(lander);
        getPlayer().setControlledSpacecraft(lander);

        canvas.setDisplayMessage(null);
        ((SingleGameCanvas) canvas).zoomOut();
        canvas.reset();

        double px = Math.random() * map.getWidth();
        lander.setPosition(new Vect2D(px, Math.min(moon.getTerrainHeight(px) +
                Settings.getDefaultDouble(Settings.SINGLE + Settings.INITIAL_HEIGHT),
                        moon.getWorldHeight() - 2 * lander.getSize())));

        canvas.repaint();
        frame.requestFocusInWindow();
        LocalSimulationThread.getInstance().restartSimulation();
    }

    /**
     * Update the game score
     */
    private void updateScore() {
        if (getLander().getStatus() == LunarLander.STATUS_LANDED) {
            getPlayer().setScore(calculateScore(getLander().getVelocity(), getLander().getFuel()));
        }
        else {
        	System.out.println("didn't land safely!");
            getPlayer().setScore(0);
        }
    }


    private static final String[] GAME_OVER_MESSAGES = { 
    	"Barely Made It", "Somebody Lost Their Lunch...",
    	"Hard Landing!", "Jittery!", "A Little Shaky...", "Not Too Bad!",
    	"Good Landing!", "Great Landing!", "Smooth Landing!", "Aldrin-like!",
    	"Near-Perfect Landing!", "11" };
}