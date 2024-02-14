package lunarlander.thread;
/**
 * SimulationThreads are responsible for stepping the objects, drawing to the screen, etc.
 * 
 * @author mike
 */
public abstract class SimulationThread extends Thread {
    
    public static final boolean PRINT_STATS = false;
    
    /**
     * Construct a new thread
     */
    protected SimulationThread(String name) {
        super(name);
        setDaemon(true); // Make the thread die when the main application exits
    }

    /**
     * Sleep for the given amount of time. If an InterruptedException occurs, just ignore it and
     * return.
     * 
     * @param ms the time to sleep in ms
     */
    protected void tryToSleep(long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            // Do nothing
        }
    }
    
    protected void sleepForOneFrame(long frameStartTime) {
        long sleepTime = PERIOD - (System.currentTimeMillis() - frameStartTime);
        if (sleepTime > 0) {
            try {
                long preSleepTime = 0;
                if (PRINT_STATS) {
                    System.out.println("Sleeping for " + sleepTime + "ms");
                    preSleepTime = System.currentTimeMillis();
                }
                Thread.sleep(sleepTime);
                if (PRINT_STATS) {
                    System.out.println("Actually slept for "
                            + (System.currentTimeMillis() - preSleepTime) + "ms");
                }
            }
            catch (Exception e) {
                // Do nothing
            }
        } else {
            try {
                if (PRINT_STATS) {
                    System.out.println("Still sleeping for at least 1ms");
                }
                Thread.sleep(1);
            }
            catch(Exception e) {
                // Do nothing
            }
        }
    }

    // Frame rate constants
    public static final double DESIRED_FRAMERATE = 30;
    public static final long PERIOD = (long) (1 / DESIRED_FRAMERATE * 1000);
    
    // Sleep time constants
    public static final long FOREVER = Long.MAX_VALUE;
}