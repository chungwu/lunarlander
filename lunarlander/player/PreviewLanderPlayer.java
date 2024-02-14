package lunarlander.player;
import lunarlander.gameobject.PreviewLunarLander;

/**
 * @author Chung
 * 
 * A Player that plays a PreviewLunarLander like a mad man
 */
public class PreviewLanderPlayer extends Player {

    /**
     * Constructor
     * 
     * @param lander PreviewLunarLander to play
     */
    public PreviewLanderPlayer(PreviewLunarLander lander) {
        this.lander = lander;
        stageStartTime = System.currentTimeMillis();
    }

    /**
     * plays the lander for one timestep; basically updates the state of the lander randomly at
     * every "stage" 
     */
    public void play() {
        if (System.currentTimeMillis() - stageStartTime > STAGE_LENGTH) {
            updateRandomly();
            stageStartTime = System.currentTimeMillis();
        }
    }

    /**
     * updates the state (angle, throttle) of the lander randomly
     */
    public void updateRandomly() {
        state = (int) (Math.random() * 8);
        lander.setDesiredAngle(Math.PI / 4 * state);
        lander.setThrottle(Math.random());
    }

    /**
     * implements Player.destruct()
     */
    public void destruct() {
        lander = null;
    }

    // CONSTANTS
    public static final long STAGE_LENGTH = 1000; // length of a "stage", in ms
    
    // STAGES
    public static final int FLY_U = 0;
    public static final int FLY_UL = 1;
    public static final int FLY_L = 2;
    public static final int FLY_DL = 3;
    public static final int FLY_D = 4;
    public static final int FLY_DR = 5;
    public static final int FLY_R = 6;
    public static final int FLY_UR = 7;

    protected int state;
    protected long stageStartTime = 0;
}