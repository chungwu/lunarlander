package lunarlander.gameobject;

import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

import lunarlander.util.Vect2D;


/**
 * Records telemetry data for the LunarLander class
 * 
 * @author Michael Yu
 */
public class DataRecorder extends Steppable {

    /**
     * Initialize all the lists and take an initial measurement. Time is measured relative to when
     * this constructor is called.
     * 
     * @param lander is the lunar lander that this data recorder is "installed" on
     * @param capacity is the number of measurements this data recorder can hold
     * @param recordingInterval is the interval between measurements
     */
    public DataRecorder(LunarLander lander, int capacity, double recordingInterval) {
        this.lander = lander;
        this.capacity = capacity;
        this.recordingInterval = recordingInterval;
        this.currentTime = 0.0;
        this.nextRecordTime = currentTime + recordingInterval;

        times = new ArrayList<Double>(INITIAL_SIZE);
        times.add(new Double(currentTime));

        positions = new ArrayList<Vect2D>(INITIAL_SIZE);
        positions.add(lander.getPosition());

        velocities = new ArrayList<Vect2D>(INITIAL_SIZE);
        velocities.add(lander.getVelocity());

        angles = new ArrayList<Double>(INITIAL_SIZE);
        angles.add(new Double(lander.getAngle()));

        throttles = new ArrayList<Double>(INITIAL_SIZE);
        throttles.add(new Double(lander.getThrottle()));
    }

    /**
     * Creates a data recorder with unlimited capacity. Initialize all the lists and take an initial
     * measurement. Time is measured relative to when this constructor is called.
     * 
     * @param lander is the lunar lander that this data recorder is "installed" on
     */
    public DataRecorder(LunarLander lander, double recordingInterval) {
        this(lander, CAPACITY_UNLIMITED, recordingInterval);
    }

    /**
     * Record telemetry data from the lunar lander
     * 
     * @param time is the current time
     */
    private void record() {
        // Record measurements
        times.add(new Double(currentTime));
        positions.add(lander.getPosition());
        velocities.add(lander.getVelocity());
        angles.add(new Double(lander.getAngle()));
        throttles.add(new Double(lander.getThrottle()));
        
        // Remove old measurements
        while (capacity != CAPACITY_UNLIMITED && capacity < times.size()) {
            times.remove(0);
            positions.remove(0);
            velocities.remove(0);
            angles.remove(0);
            throttles.remove(0);
        }
    }
    
    /**
     * Advance one simulation timestep.  Record data if enough time has elapsed.
     */
    public void step(double dt) {
        currentTime += dt;
        
        if (currentTime >= nextRecordTime) {
            record();
            nextRecordTime += recordingInterval;
        }        
    }

    /**
     * Record all telemetry data to a file. The format is csv, with each line representing a
     * recording sample. The data is written in the following order: time, positionX, positionY,
     * velocityX, velocityY, rotation, throttle
     * 
     * @param filename is the name of the file to save telemetry data to
     * @throws IOException if something bad happened :-(
     */
    public void dumpToFile(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);

        for (int i = 0; i < times.size(); i++) {
            writer.write(times.get(i) + ",");

            Vect2D position = (Vect2D) positions.get(i);
            writer.write(position.getX() + "," + position.getY() + ",");

            Vect2D velocity = (Vect2D) velocities.get(i);
            writer.write(velocity.getX() + "," + velocity.getY() + ",");

            writer.write(angles.get(i) + ",");
            writer.write(throttles.get(i) + ",");
            writer.write("\n");
        }

        writer.close();
    }

    public List getTimes() {
        return times;
    }

    public List getPositions() {
        return positions;
    }

    public List getVelocities() {
        return velocities;
    }

    public List getAngles() {
        return angles;
    }

    public List getThrottles() {
        return throttles;
    }


    private LunarLander lander;
    private int capacity;
    private double recordingInterval;
    private double nextRecordTime;
    private double currentTime;
    
    private ArrayList<Double> times;
    private ArrayList<Vect2D> positions;
    private ArrayList<Vect2D> velocities;
    private ArrayList<Double> angles;
    private ArrayList<Double> throttles;

    private static final int INITIAL_SIZE = 2000; // Initial size of the ArrayLists

    public static final int CAPACITY_UNLIMITED = 0;
}