package lunarlander.thread;

import java.util.Iterator;

import lunarlander.LunarLanderLauncher;
import lunarlander.game.LunarLanderGame;
import lunarlander.gameobject.Steppable;

/**
 * This thread runs the simulation for 1-player and 2-player (local) games.
 * 
 * @author mike
 */
public class LocalSimulationThread extends SimulationThread {

    /**
     * Create the thread that runs the simulation
     */
    public static LocalSimulationThread getInstance() {
        if (thread == null) {
            thread = new LocalSimulationThread();
            thread.start();
        }
        return thread;
    }

    /**
     * Construct a new thread
     */
    private LocalSimulationThread() {
        super("Lunar Lander Simulation Thread");
        status = Status.STOPPED;
    }

    /**
     * Run
     */
    public void run() {
        while (true) {

            status = Status.STOPPED;

            // Sleep until we see the start message
            while (message != Message.START) {
                tryToSleep(FOREVER);
            }

            // Simulation loop
            startTime = System.currentTimeMillis();
            while (true) {
                if (message == Message.PAUSE) {
                    status = Status.PAUSED;

                    // Sleep until we receive the start or stop message
                    while (!(message == Message.START || message == Message.STOP)) {
                        tryToSleep(FOREVER);
                    }
                }

                if (message == Message.STOP) {
                    break;
                }

                status = Status.RUNNING;
                String gameOverMessage = LunarLanderLauncher.game.getGameOverMessage();
                if (gameOverMessage == null) {
                    long currentTime = System.currentTimeMillis();

                    double oldGameTime = LunarLanderLauncher.game.time;
                    double targetGameTime = LunarLanderLauncher.game.turbo
                            * (double) (currentTime - startTime) / 1000.0;
                    simulate(targetGameTime);

                    if (PRINT_STATS) {
                        System.out.println("Simulate for " + (targetGameTime - oldGameTime)
                                + " s; took " + (System.currentTimeMillis() - currentTime) + " ms");
                    }

                    long prePaintTime = System.currentTimeMillis();
                    LunarLanderLauncher.game.canvas.updateActiveScreen();

                    if (PRINT_STATS) {
                        System.out.println("Painting took "
                                + (System.currentTimeMillis() - prePaintTime) + " ms");
                    }

                    framesDrawn++;
                    if (System.currentTimeMillis() - startMeasureTime > 1000) {
                        frameRate = (frameRate + framesDrawn) / 2;
                        startMeasureTime = System.currentTimeMillis();
                        framesDrawn = 0;
                    }
                    
                    sleepForOneFrame(currentTime);                    
                }
                else {
                    LunarLanderLauncher.game.canvas.setDisplayMessage(gameOverMessage);
                    LunarLanderLauncher.game.canvas.updateActiveScreen();
                    break;
                }
            }

            if (PRINT_STATS) {
            	System.out.println("Final frame rate running average: " + frameRate);
            }

            // Game is over, idle until we get the stop message
            while (message != Message.STOP) {
                status = Status.FINISHED;
                tryToSleep(FOREVER);
            }
        }
    }   

    /**
     * Pause the simulation
     */
    public void pauseSimulation() {
        if (status == Status.RUNNING) {
            message = Message.PAUSE;
            pauseTime = System.currentTimeMillis();
            sleepUntilStatus(Status.PAUSED);
        }
    }

    /**
     * Unpause the simulation
     *  
     */
    public void unpauseSimulation() {
        if (status == Status.PAUSED) {
            startTime += System.currentTimeMillis() - pauseTime;
            pauseTime = System.currentTimeMillis();
            sendStartMessage();
        }
    }

    /**
     * Reset and Restart the simulation
     */
    public void restartSimulation() {
        stopSimulation();
        sendStartMessage();
    }

    /**
     * Stop the simulation
     */
    public void stopSimulation() {
        message = Message.STOP;
        thread.interrupt();
        sleepUntilStatus(Status.STOPPED);
    }

    /**
     * Set the current message to MSG_START and wake up the SimulationThread
     */
    private void sendStartMessage() {
        message = Message.START;
        thread.interrupt();
    }

    /**
     * Block until the simulation thread becomes the desired status.
     * 
     * @param desiredStatus wait until the simulation thread enters this status
     */
    public void sleepUntilStatus(Status desiredStatus) {
        if (Thread.currentThread() != thread) {
            while (status != desiredStatus) {
                tryToSleep(300);
            }
        }
    }

    /**
     * Run the simulation for one draw cycle
     */
    private void simulate(double targetTime) {
        LunarLanderGame game = LunarLanderLauncher.game;
        long start = System.currentTimeMillis();
        while (game.time < targetTime) {
            if (message == Message.STOP) {
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

    // Singleton instance
    private static LocalSimulationThread thread;
    
    // Status for the thread
    private static enum Status {
        STOPPED, RUNNING, PAUSED, FINISHED
    }
    private Status status;
    
    // Messages sent to the thread    
    private static enum Message {
        NONE, START, PAUSE, STOP
    }
    private Message message;
    
    // Instance variables
    private long startTime;
    private long pauseTime;
    

    
    protected double frameRate = 0;
    protected int framesDrawn = 0;
    protected long startMeasureTime = 0;
}
