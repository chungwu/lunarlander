package lunarlander.game;
import lunarlander.LunarLanderLauncher;
import lunarlander.options.JoinGameOptions;
import lunarlander.options.PlayerOptions;
import lunarlander.options.StartServerGameOptions;

/*
 * Created on Jan 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class LunarLanderNetwork extends LunarLanderGame {

    public LunarLanderNetwork(StartServerGameOptions gameOptions, PlayerOptions playerOptions) {
        mode = SERVER_MODE;
    }
    
    public LunarLanderNetwork(JoinGameOptions gameOptions, PlayerOptions playerOptions) {
        mode = CLIENT_MODE;
    }
    
    /* (non-Javadoc)
     * @see LunarLanderGame#reset()
     */
    public void reset() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see LunarLanderGame#destruct()
     */
    public void destruct() {
        // TODO Auto-generated method stub

        LunarLanderLauncher.game = null;
    }

    /* (non-Javadoc)
     * @see LunarLanderGame#getGameOverMessage()
     */
    public String getGameOverMessage() {
        // TODO Auto-generated method stub
        return null;
    }
    
    protected int mode;
    
    public static final int SERVER_MODE = 0;
    public static final int CLIENT_MODE = 1;

}
