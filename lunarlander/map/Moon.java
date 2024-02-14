package lunarlander.map;

import java.awt.Graphics;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.*;
import java.awt.Color;

import lunarlander.LunarLanderLauncher;
import lunarlander.canvas.Canvas;
import lunarlander.game.LunarLanderGame;
import lunarlander.gameobject.Drawable;
import lunarlander.network.packet.MapPacket;
import lunarlander.util.Vect2D;

/**
 * A moon. Each moon has a unique surface (terrain) for the lunar lander to land on
 * 
 * @author Michael Yu
 */
public class Moon implements Drawable {

    /**
     * Constructs a random terrain
     * 
     * @param worldWidth width of world
     * @param worldHeight height of world
     * @param gravity gravity of world
     * @param iterations number of iterations to break up the terrain
     * @param numLongPads number of long landing pads
     * @param numShortPads number of short landing pads
     */
    public Moon(double worldWidth, double worldHeight, double gravity, int iterations,
            int numLongPads, int numShortPads) {
        // Terrain terrain = TerrainGenerator.generateRandom(worldWidth, 10.0, 2 * worldHeight / 5,
        // iterations, 0.5, numLongPads, numShortPads);
        Terrain terrain = TerrainGenerator.generateRandom(worldWidth, 10.0, 0.4 * worldWidth,
                iterations, 0.5, numLongPads, numShortPads);
        this.terrain = terrain;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.gravity = gravity;

        isFlat = false;
        isRandom = true;
        gameType = LunarLanderGame.ANY;
    }

    /**
     * Constructs a flat terrain
     * 
     * @param worldWidth width of world
     * @param worldHeight height of world
     * @param gravity gravity of world
     */
    public Moon(double worldWidth, double worldHeight, double gravity) {
        this.terrain = TerrainGenerator.generateFlat(worldWidth, worldHeight);
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.gravity = gravity;
        this.mapName = "Flat";
        isFlat = true;
        isRandom = false;
        gameType = LunarLanderGame.ANY;
    }
    
    public Moon(MapPacket packet) {
        this.terrain = new Terrain(packet.segments, packet.worldWidth);
        this.worldWidth = packet.worldWidth;
        this.worldHeight = packet.worldHeight;
        this.gravity = packet.gravity;
        this.mapName = packet.mapName;
    }

    /**
     * Construct a Moon using the given map file
     * 
     * @param file File containing the Moon terrain info
     * @throws MoonFileException if cannot find file, or file format invalid
     */
    public Moon(File file) throws MoonFileException {

        this.mapFile = file;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = readUsefulLine(reader);

            mapName = line;

            line = readUsefulLine(reader);
            gameType = Integer.parseInt(line.trim());

            line = readUsefulLine(reader);

            String[] size = line.split(",");
            worldWidth = Double.parseDouble(size[0].trim());
            worldHeight = Double.parseDouble(size[1].trim());

            line = readUsefulLine(reader);
            gravity = Double.parseDouble(line.trim());

            LinkedList<TerrainSegment> segments = new LinkedList<TerrainSegment>();

            line = readUsefulLine(reader);
            String[] firstPointParts = line.split(",");
            Vect2D firstPoint = new Vect2D(0, Double.parseDouble(firstPointParts[1]));
            Vect2D prevPoint = firstPoint;
            int multiplier = Integer.parseInt(firstPointParts[2]);

            line = readUsefulLine(reader);
            
            while (line != null) {
                String[] parts = line.split(",");
                Vect2D newPoint = new Vect2D(Double.parseDouble(parts[0]), Double
                        .parseDouble(parts[1]));
                segments.addLast(new TerrainSegment(prevPoint, newPoint, multiplier));
                prevPoint = newPoint;
                multiplier = Integer.parseInt(parts[2]);
                line = readUsefulLine(reader);
            }

            if (segments.isEmpty()) {
            	// there was only one point in the map!  Make the whole thing a segment
            	segments.addLast(new TerrainSegment(firstPoint, new Vect2D(worldWidth, firstPoint.getY()), multiplier));
            } else {
            	segments.addLast(new TerrainSegment(prevPoint, new Vect2D(worldWidth, segments.get(0)
            			.getLeftEndPoint().getY()), multiplier));
            }

            terrain = TerrainGenerator.generateSpecific(worldWidth, segments);

            /*
             * String[] heights = line.split(","); double[] heightMap = new double[heights.length];
             * for (int i = 0; i < heightMap.length; i++) { heightMap[i] =
             * Double.parseDouble(heights[i].trim()); }
             * 
             * line = readUsefulLine(reader);
             * 
             * double[] longPadCoordinates; if (line.trim().equals("-1")) { longPadCoordinates = new
             * double[0]; } else {
             * 
             * String[] longPads = line.split(","); longPadCoordinates = new
             * double[longPads.length]; for (int i = 0; i < longPadCoordinates.length; i++) {
             * longPadCoordinates[i] = Double.parseDouble(longPads[i].trim()); } }
             * 
             * line = readUsefulLine(reader); double[] shortPadCoordinates;
             * 
             * if (line.trim().equals("-1")) { shortPadCoordinates = new double[0]; } else {
             * 
             * String[] shortPads = line.split(","); shortPadCoordinates = new
             * double[shortPads.length]; for (int i = 0; i < shortPadCoordinates.length; i++) {
             * shortPadCoordinates[i] = Double.parseDouble(shortPads[i].trim()); } } terrain =
             * TerrainGenerator.generateSpecific(worldWidth, heightMap, longPadCoordinates,
             * shortPadCoordinates);
             * 
             * 
             * System.out.println("MAP: " + mapName); List<TerrainSegment> segments =
             * terrain.getSegments(); for(int i=0; i<segments.size(); i++) { TerrainSegment seg =
             * segments.get(i); System.out.println(seg.getLeftEndPoint().getX() + "," +
             * seg.getLeftEndPoint().getY() + "," + seg.getMultiplier()); }
             */
        }
        catch (IOException e) {
            throw new MoonFileException("Error reading moon terrain file " + file);
        }
        catch (Exception e) {
        	e.printStackTrace();
            throw new MoonFileException("Invalid moon terrain file " + file);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    throw new MoonFileException("Unable to close moon terrain file");
                }
            }
        }
    }

    /**
     * @return game type this map is for
     */
    public int getGameType() {
        return gameType;
    }

    /**
     * Get the acceleration due to gravity on this moon
     * 
     * @return the acceleration due to gravity on this moon
     */
    public double getGravity() {
        return gravity;
    }

    /**
     * Return the height of the terrain
     * 
     * @param x is the x position
     * @return the height of the terrain at positions getX
     */
    public double getTerrainHeight(double x) {
        return terrain.getHeight(x);
    }

    /**
     * Is there a landing pad directly below?
     * 
     * @param x is the x position
     * @return true if there is a landing pad at the given x position, false otherwise
     */
    public boolean isOverLandingPad(double x) {
        return terrain.isOverLandingPad(x);
    }

    /**
     * @param position point in space
     * @return closest landing pad to that point
     */
    public TerrainSegment getClosestLandingPad(Vect2D position) {
        return terrain.getClosestLandingPad(position);
    }

    /**
     * Get the score multiplier if the lunar lander landed on the surface of this moon at position x
     * 
     * @param x is the x position of the landing spot
     * @return the score multiplier for landing at position x
     */
    public int getScoreMultiplier(double x) {
        if (isOverLandingPad(x)) {
            return terrain.getEncompassingSegment(x).getMultiplier();
        }
        else {
            return 0;
        }
    }

    /**
     * Get the world height (maximum altitude that is considered within the moon's region)
     * 
     * @return the world height
     */
    public double getWorldHeight() {
        return worldHeight;
    }

    /**
     * Get the world width (the circumference of the moon)
     * 
     * @return the world width
     */
    public double getWorldWidth() {
        return worldWidth;
    }

    /**
     * Get the terrain segment that contains the given x position
     * 
     * @param x is the x position
     * @return the terrain segment that contains the given x position
     */
    public TerrainSegment getEncompassingTerrainSegment(double x) {
        return terrain.getEncompassingSegment(x);
    }

    /**
     * Get the closest landing pad (closest in terms of horizontal distance to the left of the
     * x-coordinate).
     * 
     * @param x is the x-coordinate
     * @return the left-closest landing pad
     */
    public TerrainSegment getLeftClosestLandingPad(double x) {
        List<TerrainSegment> landingPads = getLandingPads();
        TerrainSegment closestSegmentSoFar = null;
        double closestDistanceSoFar = Double.POSITIVE_INFINITY;

        for (TerrainSegment s : landingPads) {
            double landingPadX = (s.getRightEndPoint().getX() + s.getLeftEndPoint().getX()) / 2.0;
            double distance = (landingPadX > x) ? worldWidth - (landingPadX - x) : x - landingPadX;
            if (closestDistanceSoFar > distance) {
                closestDistanceSoFar = distance;
                closestSegmentSoFar = s;
            }
        }

        return closestSegmentSoFar;
    }

    /**
     * Get the closest landing pad (closest in terms of horizontal distance to the right of the
     * x-coordinate).
     * 
     * @param x is the x-coordinate
     * @return the right-closest landing pad
     */
    public TerrainSegment getRightClosestLandingPad(double x) {
        List<TerrainSegment> landingPads = getLandingPads();
        TerrainSegment closestSegmentSoFar = null;
        double closestDistanceSoFar = Double.POSITIVE_INFINITY;

        for (TerrainSegment s : landingPads) {
            double landingPadX = (s.getRightEndPoint().getX() + s.getLeftEndPoint().getX()) / 2.0;
            double distance = (landingPadX < x) ? worldWidth - (x - landingPadX) : landingPadX - x;
            if (closestDistanceSoFar > distance) {
                closestDistanceSoFar = distance;
                closestSegmentSoFar = s;
            }
        }

        return closestSegmentSoFar;
    }

    /**
     * @return a list of all the landing pads on the moon. The landing pads are returned in order
     *         from left to right
     */
    public List<TerrainSegment> getLandingPads() {
        ArrayList<TerrainSegment> landingPads = new ArrayList<TerrainSegment>();
        for (TerrainSegment s : terrain.getSegments()) {
            if (s.isLandingPad()) {
                landingPads.add(s);
            }
        }
        return landingPads;
    }

    /**
     * Get the terrain
     * 
     * @return the terrain
     */
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * implements Drawable.draw(0
     */
    public void draw(Graphics g) {
        draw(g, LunarLanderLauncher.game.canvas);
    }

    /**
     * Draw the moon's terrain
     * 
     * @param g is the graphics context
     */
    public void draw(Graphics g, Canvas canvas) {
        terrain.draw(g, canvas);

        // draw the height limit
        int canvasCeiling = canvas.worldToCanvasY(this.worldHeight);
        g.setColor(Color.RED);
        g.drawLine(0, canvasCeiling, canvas.getWidth(), canvasCeiling);
    }

    /**
     * @return File of this map, or null if generated
     */
    public File getMapFile() {
        return mapFile;
    }

    /**
     * @return name of this map, or null if generated
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @return true if this Moon is generated from a file
     */
    public boolean isGeneratedFromFile() {
        return mapFile != null;
    }

    /**
     * @return true if this Moon was randomly-generated
     */
    public boolean isRandom() {
        return isRandom;
    }

    /**
     * @return true if this Moon is flat
     */
    public boolean isFlat() {
        return isFlat;
    }

    /**
     * @param padType type of pad
     * @return number of landing pads of this type
     */
    public int getNumOfPads(int padType) {
        int count = 0;
        int mult = Terrain.getPadMultiplier(padType);

        List<TerrainSegment> segments = terrain.getSegments();
        for (TerrainSegment segment : segments) {
            if (segment.isLandingPad() && segment.getMultiplier() == mult) {
                count++;
            }
        }
        return count;
    }

    /**
     * reads a "useful" line from a BufferedReader that's not whitespace and doesn't start with '#'.
     * Returns null if no more lines to be read.
     * 
     * @param reader BufferedReader to read from
     * @return line read
     * @throws IOException
     */
    private static String readUsefulLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && (line.trim().length() == 0 || line.charAt(0) == '#')) {
            line = reader.readLine();
        }
        return line;
    }


    private boolean isRandom;
    private boolean isFlat;
    private int gameType;
    private String mapName;
    private File mapFile;
    private Terrain terrain;
    private double worldWidth;
    private double worldHeight;
    private double gravity; // Constant acceleration due to gravity, in m / s^2
}