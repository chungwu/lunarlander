package lunarlander.game;

import java.awt.*;
import java.util.Iterator;

import lunarlander.canvas.DuoGameCanvas;
import lunarlander.canvas.GameCanvas;
import lunarlander.*;
import lunarlander.gameobject.ArmedLunarLander;
import lunarlander.gameobject.LunarLander;
import lunarlander.player.HumanPlayer;
import lunarlander.player.Player;
import lunarlander.thread.LocalSimulationThread;
import lunarlander.util.Vect2D;

/**
 * Local two-player LunarLanderGame
 * 
 * @author Michael Yu
 */
public class LunarLanderDuo extends LunarLanderArmed {

    /**
     * Constructor
     * 
     * @param options options user chose to start the game with
     */
    public LunarLanderDuo() {

        super();

        // Create players
        players.add(new HumanPlayer(Settings.getString(Settings.PLAYER_ONE + Settings.NAME),
                Settings.getColor(Settings.PLAYER_ONE + Settings.COLOR), null,
                HumanPlayer.LAYOUT_ONE));
        players.add(new HumanPlayer(Settings.getString(Settings.PLAYER_TWO + Settings.NAME),
                Settings.getColor(Settings.PLAYER_TWO + Settings.COLOR), null,
                HumanPlayer.LAYOUT_TWO));

        String prefix = getGamePrefix();
        turbo = Settings.getDouble(prefix+Settings.TURBO);
        safeVelocityX = Settings.getDouble(prefix+Settings.SAFE_VX);
        safeVelocityY = Settings.getDouble(prefix+Settings.SAFE_VY);
        safeAngle = Settings.getDouble(prefix+Settings.SAFE_ANGLE);
        rocketBudget = Settings.getInt(Settings.DUO + Settings.ROCKET_BUDGET);
        mustLandToWin = Settings.getBoolean(Settings.DUO + Settings.MUST_LAND_TO_WIN);
        enableSmallRockets = Settings.getBoolean(Settings.DUO + Settings.ENABLE_SMALL_ROCKETS);
        enableBigRockets = Settings.getBoolean(Settings.DUO + Settings.ENABLE_BIG_ROCKETS);
        enableDrones = Settings.getBoolean(Settings.DUO + Settings.ENABLE_DRONES);
        maps = Settings.getMaps(Settings.DUO+Settings.MAPS);
    }

    /**
     * implements LunarLanderGame.createGameCanvas()
     */
    protected GameCanvas createGameCanvas() {
        return new DuoGameCanvas(this);
    }

    /**
     * implements LunarLanderGame.destruct()
     */
    public void destruct() {
        synchronized (LunarLanderLauncher.game.players) {
            for (Iterator it = players.iterator(); it.hasNext();) {
                Player p = (Player) it.next();
                p.destruct();
            }
            players.clear();
        }
        bumpables.clear();
        LunarLanderLauncher.game = null;
    }

    /**
     * implements LunarLanderGame.getGameType()
     */
    public GameType getGameType() {
        return GameType.DUO;
    }    

    /**
     * Return the appropriate game over message
     * 
     * @return the game over message based
     */
    public String getGameOverMessage() {
        Player p1, p2;
        p1 = ((Player) players.get(PLAYER_ONE));
        p2 = ((Player) players.get(PLAYER_TWO));

        if (mustLandToWin) {
            if (p1.getLander().getStatus() == LunarLander.STATUS_LANDED) {
                p1.setScore(p1.getScore()
                        + 2
                        * moon.getEncompassingTerrainSegment(p1.getLander().getPosition().getX())
                                .getMultiplier());
                return p1.getName() + " Wins!";
            }
            else if (p2.getLander().getStatus() == LunarLander.STATUS_LANDED) {
                p2.setScore(p2.getScore()
                        + 2
                        * moon.getEncompassingTerrainSegment(p2.getLander().getPosition().getX())
                                .getMultiplier());
                return p2.getName() + " Wins!";
            }
            else if (p1.getLander().getStatus() == LunarLander.STATUS_FLYING
                    || p2.getLander().getStatus() == LunarLander.STATUS_FLYING) {
                return null;
            }
            else {
                return "Tie!";
            }
        }
        else {
            if (p1.getLander().getStatus() == LunarLander.STATUS_LANDED
                    || p2.getLander().getStatus() == LunarLander.STATUS_CRASHED) {
                p1.setScore(p1.getScore()
                        + ((p1.getLander().getStatus() == LunarLander.STATUS_LANDED) ? 2 * moon
                                .getEncompassingTerrainSegment(p1.getLander().getPosition().getX())
                                .getMultiplier() : 1));
                return p1.getName() + " Wins!";
            }
            else if (p2.getLander().getStatus() == LunarLander.STATUS_LANDED
                    || p1.getLander().getStatus() == LunarLander.STATUS_CRASHED) {
                p2.setScore(p2.getScore()
                        + ((p2.getLander().getStatus() == LunarLander.STATUS_LANDED) ? 2 * moon
                                .getEncompassingTerrainSegment(p2.getLander().getPosition().getX())
                                .getMultiplier() : 1));
                return p2.getName() + " Wins!";
            }
            else {
                return null;
            }
        }
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

        canvas.setDisplayMessage(null);
        canvas.reset();

        Vect2D position1 = new Vect2D(0, 0);
        Vect2D position2 = new Vect2D(0, 0);

        Player p1 = ((Player) players.get(PLAYER_ONE));
        Player p2 = ((Player) players.get(PLAYER_TWO));

        ArmedLunarLander lander1 = new ArmedLunarLander(position1, initVel, initAngle, p1,
                enableSmallRockets, enableBigRockets, enableDrones);
        ArmedLunarLander lander2 = new ArmedLunarLander(position2, initVel, initAngle, p2,
                enableSmallRockets, enableBigRockets, enableDrones);
        lander1
                .setLandingAssist(Settings
                        .getBoolean(Settings.PLAYER_ONE + Settings.LANDING_ASSIST));
        lander2
                .setLandingAssist(Settings
                        .getBoolean(Settings.PLAYER_TWO + Settings.LANDING_ASSIST));
        lander1.addToLists();
        lander2.addToLists();
        p1.setLander(lander1);
        p1.setControlledSpacecraft(lander1);
        p2.setLander(lander2);
        p2.setControlledSpacecraft(lander2);

        double px1 = Math.random() * map.getWidth();
        double px2 = (px1 + map.getWidth() / 2) % map.getWidth();
        double py1 = Math.min(moon.getWorldHeight() - LunarLander.LANDER_LENGTH, moon.getTerrainHeight(px1)
                + Settings.getDefaultDouble(Settings.DUO + Settings.INITIAL_HEIGHT));
        double py2 = Math.min(moon.getWorldHeight() - LunarLander.LANDER_LENGTH, moon.getTerrainHeight(px2)
                + Settings.getDefaultDouble(Settings.DUO + Settings.INITIAL_HEIGHT));
        double py = Math.max(py1, py2);
        lander1.setPosition(new Vect2D(px1, py));
        lander2.setPosition(new Vect2D(px2, py));

        canvas.repaint();
        frame.requestFocusInWindow();

        LocalSimulationThread.getInstance().restartSimulation();
    }


    public static final Color[] DEFAULT_COLORS = { Color.orange, Color.green };
    public static final int PLAYER_ONE = 0;
    public static final int PLAYER_TWO = 1;
}