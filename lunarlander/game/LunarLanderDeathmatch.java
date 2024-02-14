package lunarlander.game;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import lunarlander.LunarLanderLauncher;
import lunarlander.Settings;
import lunarlander.canvas.GameCanvas;
import lunarlander.canvas.NetworkGameCanvas;
import lunarlander.gameobject.LunarLander;
import lunarlander.gameobject.NetworkLunarLander;
import lunarlander.network.Client;
import lunarlander.network.GameManager;
import lunarlander.network.Host;
import lunarlander.network.NetworkManager;
import lunarlander.network.packet.GameOptionsPacket;
import lunarlander.network.packet.PlayerPacket;
import lunarlander.player.HumanPlayer;
import lunarlander.player.NetworkPlayer;
import lunarlander.player.Player;
import lunarlander.thread.NetworkSimulationThread;
import lunarlander.util.Vect2D;

/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LunarLanderDeathmatch extends LunarLanderArmed implements GameManager {

    public LunarLanderDeathmatch(NetworkManager networkManager, List<? extends Player> players) {
        super();
        
        String prefix = getGamePrefix();
        turbo = Settings.getDouble(prefix+Settings.TURBO);
        safeVelocityX = Settings.getDouble(prefix+Settings.SAFE_VX);
        safeVelocityY = Settings.getDouble(prefix+Settings.SAFE_VY);
        safeAngle = Settings.getDouble(prefix+Settings.SAFE_ANGLE);
        maps = Settings.getMaps(prefix+Settings.MAPS);
        rocketBudget = Settings.getInt(prefix + Settings.ROCKET_BUDGET);
        mustLandToWin = Settings.getBoolean(prefix + Settings.MUST_LAND_TO_WIN);
        enableSmallRockets = Settings.getBoolean(prefix + Settings.ENABLE_SMALL_ROCKETS);
        enableBigRockets = Settings.getBoolean(prefix + Settings.ENABLE_BIG_ROCKETS);
        enableDrones = Settings.getBoolean(prefix + Settings.ENABLE_DRONES);
        
        indicateEnemy = Settings.getBoolean(prefix + Settings.INDICATE_ENEMY);
        indicateTeam = Settings.getBoolean(prefix + Settings.INDICATE_TEAMMATE);
        friendlyFire = Settings.getBoolean(prefix + Settings.FRIENDLY_FIRE);
        timeLimit = Settings.getDouble(prefix+Settings.TIME_LIMIT);
        pointLimit = Settings.getInt(prefix + Settings.POINT_LIMIT);
        
        this.players.clear();        
        this.players.addAll(players);
        
        this.networkCanvas = (NetworkGameCanvas) canvas;
        
        this.networkManager = networkManager;
        if (networkManager instanceof Client) {
            btRestart.setEnabled(false);
            btExit.setText("Disconnect");

            btExit.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        LunarLanderDeathmatch.this.networkManager.disconnect();
                    } catch (java.io.IOException error) {
                        
                    }
                    NetworkSimulationThread.getInstance(LunarLanderDeathmatch.this.networkManager).quitGame();
                    LunarLanderLauncher.game.destruct();
                    LunarLanderLauncher.displayTitleFrame();
                    LunarLanderLauncher.setupFrame();
                    LunarLanderLauncher.frame.repaint();
                }
            });
        } else {

            btExit.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    NetworkSimulationThread.getInstance(LunarLanderDeathmatch.this.networkManager).stopGame();
                    LunarLanderLauncher.game.destruct();
                }
            });
        }
    }
    public GameCanvas createGameCanvas() {
        return new NetworkGameCanvas(this);
    }
    
    /**
     * implements LunarLanderGame.getGameType()
     */
    public GameType getGameType() {
        return GameType.DEATHMATCH;
    }
    
    protected void createButtonPanel() {
        buttons = new JPanel();

        System.out.println("Creating buttons!");
        
        btRestart = new JButton("Start Game");
        btRestart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("Starting to play!");
                ((Host)networkManager).startPlaying();
            }
        });
        btRestart.setEnabled(true);
        buttons.add(btRestart);

        btExit = new JButton("End Game");
        buttons.add(btExit);
    }
    
    /* (non-Javadoc)
     * @see LunarLanderGame#reset()
     */
    public void reset() {                
        bumpables.clear();
        steppables.clear();
        time = 0;
        
        for (Player player : players) {
            createLanderForPlayer(player);
        }
        
        canvas.repaint();
        frame.requestFocusInWindow();
    }
    
    public void createLanderForPlayer(Player player) {
        
        if (player.getLander() != null) {
            player.getLander().removeFromLists();
        }
        
        double px = Math.random() * map.getWidth();
        double py = Math.min(moon.getWorldHeight() - LunarLander.LANDER_LENGTH, moon.getTerrainHeight(px) + 500);
        Vect2D position = new Vect2D(px, py);
        NetworkLunarLander lander = new NetworkLunarLander(
                player.getId(), position, initVel, initAngle, player, enableSmallRockets, enableBigRockets, enableDrones);
        if (lander.getId() != networkManager.getThisPlayer().getId()) {
            ((HumanPlayer) player).removeListener();
        }
        lander.setLandingAssist(true);
        lander.addToLists();
        player.setLander(lander);
        player.setControlledSpacecraft(lander);
    }

    /* (non-Javadoc)
     * @see LunarLanderGame#destruct()
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

    /* (non-Javadoc)
     * @see LunarLanderGame#getGameOverMessage()
     */
    public String getGameOverMessage() {
        // TODO Auto-generated method stub
        return null;
    }
    public void addPlayer(NetworkPlayer playerToAdd) {
        this.players.add(playerToAdd);
    }
    public void removePlayer(int playerId) {
        for (Iterator<Player> playersIt = players.iterator(); playersIt.hasNext();) {
            Player player = playersIt.next();
            if (player.getId() == playerId) {
                playersIt.remove();
                return;
            }
        }
    }
    public GameOptionsPacket getGameOptionsPacketToSend() {
        // TODO Auto-generated method stub
        return null;
    }
    public PlayerPacket getPlayerPacketToSend() {
        // TODO Auto-generated method stub
        return null;
    }
    public GameOptionsPacket getSyncGameOptionsPacketToSend() {
        // TODO Auto-generated method stub
        return null;
    }
    public PlayerPacket getSyncPlayerPacketToSend() {
        // TODO Auto-generated method stub
        return null;
    }
    public void receiveChatMessage(int fromPlayerId, String message) {
        for (Player player : getPlayers()) {
            if (player.getId() == fromPlayerId) {
                networkCanvas.addChatMessage((NetworkPlayer) player, message);
                break;
            }
        }
    }
    public void receiveStatusMessage(String message) {
        networkCanvas.addStatusMessage(message);
    }
    public void updateGameOptions(GameOptionsPacket packet) {
        // TODO Auto-generated method stub
        
    }
    public void updatePlayer(NetworkPlayer playerToUpdate) {
        for (Player p : players) {
            NetworkPlayer player = (NetworkPlayer) p;
            if (player.getId() == playerToUpdate.getId()) {
                player.setName(playerToUpdate.getName());
                player.setColor(playerToUpdate.getColor());
                player.setRole(playerToUpdate.getRole());
                player.setTeam(playerToUpdate.getTeam());
                break;
            }
        }
    }
    public List<Player> getPlayers() {
        return this.players;
    }
    
    
    
    
    public boolean indicateEnemy;
    public boolean indicateTeam;
    public boolean friendlyFire;
    public double timeLimit;
    public int pointLimit;
    
    private NetworkGameCanvas networkCanvas;
    private NetworkManager networkManager;
}
