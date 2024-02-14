package lunarlander.thread;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lunarlander.LunarLanderLauncher;
import lunarlander.canvas.NetworkGameCanvas;
import lunarlander.game.LunarLanderGame;
import lunarlander.gameobject.Steppable;
import lunarlander.network.*;
import lunarlander.network.packet.Packet;
import lunarlander.player.NetworkPlayer;

/**
 * This thread runs the simulation for network games.
 * 
 * @author mike
 */
public class NetworkSimulationThread extends SimulationThread {

    
    // HACK!  requestor-specific
    public static NetworkSimulationThread getInstance(Object requestor) {
        NetworkSimulationThread thread = threads.get(requestor);
        if (thread == null) {
            thread = new NetworkSimulationThread("Lunar Lander Network Simulation Thread");
            threads.put(requestor, thread);
            thread.start();
            System.out.println("Creating NST for the first time!!!");
        } else {
            System.out.println("Oh, already got an NST");
        }
        return thread;
    }
    
    /**
     * Construct a new thread
     */
    protected NetworkSimulationThread(String name) {
        super(name);
        status = Status.STOPPED;
        setDaemon(true); // Make the thread die when the main application exits        
    }


    public void startRendezvous() {
        message = Message.START_RENDEZVOUS;
        System.out.println("GOT MESSAGE: " + message + ", status: " + status);
    }
    
    public void startGame() {
        message = Message.START_GAME;
        System.out.println("GOT MESSAGE: " + message + ", status: " + status);
    }
    
    public void stopGame() {
        message = Message.STOP_GAME;
        System.out.println("GOT MESSAGE: " + message + ", status: " + status);
    }
    
    public void startPlaying() {
        message = Message.START_PLAYING;
        System.out.println("GOT MESSAGE: " + message + ", status: " + status);        
    }
    
    public void quitGame() {

        message = Message.QUIT_GAME;
        System.out.println("GOT MESSAGE: " + message + ", status: " + status);
    }
    
    /**
     * Run
     */
    public void run() {        
        while (true) {
                        
            while (message != Message.START_RENDEZVOUS) {
                tryToSleep(1000);
            }
            
            status = Status.RENDEZVOUS;
            
            System.out.println("Status is now: " + status);
            
            while (message != Message.START_GAME) {
                long startTime = System.currentTimeMillis();
                networkManager.processPackets();
                
                try {
                    networkManager.sendUpdates();
                } catch (IOException e) {
                    System.err.println("ERROR sending updates: " + e);
                }
                sleepForOneFrame(startTime);
            }
            
            status = Status.IN_GAME;
            
            while (message != Message.STOP_GAME && message != Message.QUIT_GAME &&
                   message != Message.START_PLAYING) {                                
                
                long startTime = System.currentTimeMillis();
                
                if (message == Message.PAUSE) {
                    status = Status.PAUSED;                                        
                }
                
                LunarLanderLauncher.game.canvas.updateActiveScreen();
                
                networkManager.processPackets();

                try {
                    networkManager.sendUpdates();
                } catch (IOException e) {
                    System.err.println("ERROR sending updates: " + e);
                }
                sleepForOneFrame(startTime);
            }
            
            if (message == Message.START_PLAYING) {
                status = Status.PLAYING;
            }
            
            long startTime = System.currentTimeMillis();
            
            while (message != Message.STOP_GAME && message != Message.QUIT_GAME) {
                long currentTime = System.currentTimeMillis();
                double oldGameTime = LunarLanderLauncher.game.time;
                double targetGameTime = LunarLanderLauncher.game.turbo
                        * (double) (currentTime - startTime) / 1000.0;
                simulate(targetGameTime);
                
                LunarLanderLauncher.game.canvas.updateActiveScreen();

                networkManager.processPackets();

                try {
                    networkManager.sendUpdates();
                } catch (IOException e) {
                    System.err.println("ERROR sending updates: " + e);
                }
                
                sleepForOneFrame(currentTime);    
            }
        }
    }

    /**
     * Block until the simulation thread becomes the desired status.
     * 
     * @param desiredStatus wait until the simulation thread enters this status
     */
    public void sleepUntilStatus(Status desiredStatus) {
        //if (Thread.currentThread() != thread) {
            while (status != desiredStatus) {
                tryToSleep(300);
            }
        //}
    }
    
    /**
     * @return true if the thread is currently hosting a game
     */
    public boolean isHost() {
        return role == NetworkRole.SERVER;
    }
    
    /**
     * @return true if the thread is currently in client mode
     */
    public boolean isClient() {
        return role == NetworkRole.CLIENT;
    }
    
    /**
     * @return true if the thread is currently serving as a dedicated server
     */
    public boolean isDedicatedServer() {
        return false;
    }            
    
    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    } 
    
    /**
     * Run the simulation for one draw cycle
     */
    private void simulate(double targetTime) {
        LunarLanderGame game = LunarLanderLauncher.game;
        while (game.time < targetTime) {
            if (message == Message.STOP_GAME) {
                break;
            }

            game.time += LunarLanderGame.DT;

            synchronized (game.steppables) {
                for (Iterator it = game.steppables.iterator(); it.hasNext();) {
                    Steppable s = (Steppable) it.next();
                    s.step(LunarLanderGame.DT);
                    if (s.shouldDelete()) {
                        it.remove();
                        game.bumpables.remove(s);
                    }
                }
            }
        }
    }
    
    // Networking constants
    public static final int TCP_PORT_NUMBER = 5720;

    // Message constants   
    private static enum Message {
        NONE, START_RENDEZVOUS, START_GAME, START_PLAYING, PAUSE, STOP_GAME, QUIT_GAME
    }
    
    // Status constants
    private static enum Status {
        STOPPED, RENDEZVOUS, IN_GAME, PLAYING, PAUSED
    }
     
    private static Map<Object, NetworkSimulationThread> threads = new HashMap<Object, NetworkSimulationThread>();
    
    
    // Role of this player
    private NetworkRole role;
    private Message message;
    private Status status;
    private NetworkManager networkManager;
}