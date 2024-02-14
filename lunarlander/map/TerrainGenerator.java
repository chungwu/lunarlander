package lunarlander.map;

import java.util.Iterator;
import java.util.List;

import lunarlander.util.Matrix;
import lunarlander.util.Vect2D;

/**
 * This class generates a random terrain. It iteratively splits the terrain into smaller pieces and
 * randomly displaces the height of each segment. <p/>More details about the random fractal terrain
 * generation algorithm can be found here: http://www.gameprogrammer.com/fractal.html
 * 
 * @author Michael Yu
 */
public class TerrainGenerator {

    /**
     * Generate a completely flat terrain
     * 
     * @param width is the width of the generated terrain
     * @param height is the height of the generated terrain
     * @return a flat terrain with the given width and height
     */
    public static Terrain generateFlat(double width, double height) {
        Terrain terrain = new Terrain(new double[] { 0 }, width);
        terrain.getEncompassingSegment(0.0).setLandingPad(true);
        terrain.getEncompassingSegment(0.0).setMultiplier(1);
        return terrain;
    }

    /**
     * Generate a random terrain
     * 
     * @param width is the width of the generated terrain
     * @param low is the minimum altitude of the terrain
     * @param high is the maximum altitude of the terrain
     * @param iterations is the number of multiply we subdivide the terrain
     * @param H is the smoothing factor. It must be between 0 and 1. The greater the value, the
     *            smoother the generated terrain will be.
     * @return the randomly generated terrain
     */
    public static Terrain generateRandom(double width, double low, double high, int iterations,
            double H, int numLongPads, int numShortPads) {

        double[] heightMap = new double[1 << iterations];
        double range = 1000.0;

        for (int i = 0; i < iterations; i++) {
            range *= H;
            int stepSize = (1 << iterations) / (1 << i);
            for (int j = 0; j < heightMap.length; j += stepSize) {
                heightMap[j + stepSize / 2] = (heightMap[j] + heightMap[(j + stepSize)
                        % heightMap.length]) / 2;
                heightMap[j + stepSize / 2] += (Math.random() - 0.5) / 0.5 * range;
            }
        }

        Terrain result = new Terrain(heightMap, width);
        normalize(result, low, high);
        createLandingPads(result, numLongPads, numShortPads);

        return result;
    }

    /**
     * Creates a Terrain with a specific heightMap and specified landing pad locations
     * 
     * @param width width of Terrain
     * @param heightMap array of height values
     * @param longPads left-end x-locations of all long pads
     * @param shortPads left-end x-locations of all short pads
     * @return Terrain created with these parameters
     */
    public static Terrain generateSpecific(double width, List<TerrainSegment> segments) {
        return new Terrain(segments, width);
    }

    /**
     * Normalize the terrain so that its height ranges from [low, high]
     * 
     * @param terrain is the terrain to be normalized
     * @param low is the minimum altitude of the terrain
     * @param high is the maximum altitude of the terrain
     */
    private static void normalize(Terrain terrain, double low, double high) {
        double maxHeight = maxHeight(terrain);
        double minHeight = minHeight(terrain);

        for (Iterator it = terrain.getSegments().iterator(); it.hasNext();) {
            TerrainSegment segment = (TerrainSegment) it.next();

            // Translate the terrain so that the lowest height is 0
            Vect2D translation = new Vect2D(0, -minHeight);
            segment.setLeftEndPoint(segment.getLeftEndPoint().add(translation));
            segment.setRightEndPoint(segment.getRightEndPoint().add(translation));

            // Scale the terrain so that the highest height is high-low
            Matrix scaleY = new Matrix(1, 0, 0, (high - low) / (maxHeight - minHeight));
            segment.setLeftEndPoint(scaleY.times(segment.getLeftEndPoint()));
            segment.setRightEndPoint(scaleY.times(segment.getRightEndPoint()));

            // Translate the terrain so that the lowest height is 'low'
            Vect2D translation2 = new Vect2D(0, low);
            segment.setLeftEndPoint(segment.getLeftEndPoint().add(translation2));
            segment.setRightEndPoint(segment.getRightEndPoint().add(translation2));
        }
    }

    /**
     * Return the maximum height of the given terrain
     * 
     * @param terrain is the terrain to query
     * @return the maximum height of the given terrain
     */
    private static double maxHeight(Terrain terrain) {
        double soFar = Double.NEGATIVE_INFINITY;

        for (Iterator it = terrain.getSegments().iterator(); it.hasNext();) {
            TerrainSegment segment = (TerrainSegment) it.next();
            if (segment.getLeftEndPoint().getY() > soFar) {
                soFar = segment.getLeftEndPoint().getY();
            }
        }

        return soFar;
    }

    /**
     * Return the minimum height of the given terrain
     * 
     * @param terrain is the terrain to query
     * @return the minimum height of the given terrain
     */
    private static double minHeight(Terrain terrain) {
        double soFar = Double.POSITIVE_INFINITY;

        for (TerrainSegment segment : terrain.getSegments()) {
            if (segment.getLeftEndPoint().getY() < soFar) {
                soFar = segment.getLeftEndPoint().getY();
            }
        }

        return soFar;
    }

    /**
     * Given a terrain, randomly select a few segments and transform them into landing pads
     * 
     * @param terrain is the terrain which we will add landing pads to
     */
    private static void createLandingPads(Terrain terrain, int numLongPads, int numShortPads) {
        for (int i = 0; i < numLongPads; i++) {
            createRandomLandingPad(terrain, Terrain.LONG_LANDING_PAD);
        }
        for (int j = 0; j < numShortPads; j++) {
            createRandomLandingPad(terrain, Terrain.SHORT_LANDING_PAD);
        }
    }

    /**
     * creates a random landing pad of the specified type. After the method call, "terrain" will
     * contain a landing pad of the specified type at some random location. It will not create a pad
     * that will overlap another existing pad; if it's not possible to create a pad that doesn't
     * overlap another pad, then the method (unfortunately) loops forever.
     * 
     * @param terrain Terrain to create the pad to
     * @param padType SHORT_LANDING_PAD or LONG_LANDING_PAD
     */
    private static void createRandomLandingPad(Terrain terrain, int padType) {
        double padLength = Terrain.getPadLength(padType);

        while (true) {

            // randomly pick a location
            double padLocation = Math.random() * terrain.getWorldWidth();

            if (padLocation + padLength > terrain.getWorldWidth()) {
                continue;
            }

            // we want to make sure that adding a pad here won't overlap another landing pad
            TerrainSegment firstSegment = terrain.getEncompassingSegment(padLocation);
            TerrainSegment lastSegment = terrain.getEncompassingSegment(padLocation + padLength);

            List<TerrainSegment> segments = terrain.getSegments();
            boolean hasAnotherPad = false;

            for (int i = segments.indexOf(firstSegment); i <= segments.indexOf(lastSegment); i++) {
                TerrainSegment segment = segments.get(i);
                if (segment.isLandingPad()) {
                    hasAnotherPad = true;
                    break;
                }
            }

            if (hasAnotherPad) {
                continue;
            }
            else {
                // found a valid pad location!
                createLandingPad(terrain, padType, padLocation);
                break;
            }
        }
    }

    /**
     * Creates a specific type of landing pad for the terrain with left end point at location x.
     * After method call, the terrain will contain such a landing pad. Note that it's illegal to
     * specify an x value that will create a terrain that crosses the world boundary; in this case,
     * an IllegalArgumentException is thrown.
     * 
     * @param terrain Terrain to create the pad for
     * @param padType SHORT_LANDING_PAD or LONG_LANDING_PAD
     * @param x left-end x coordinate of the pad
     */
    private static void createLandingPad(Terrain terrain, int padType, double x) {
        double padLength = Terrain.getPadLength(padType);

        double startingX = x;
        double endingX = x + padLength;

        // we won't allow landing pads that will cross the world boundary
        if (endingX > terrain.getWorldWidth()) {
            throw new IllegalArgumentException("This landing pad at " + x
                    + " will cross the world boundary");
        }

        double height = terrain.getHeight(x);

        TerrainSegment firstSegment = terrain.getEncompassingSegment(startingX);
        TerrainSegment lastSegment = terrain.getEncompassingSegment(endingX);

        List<TerrainSegment> segments = terrain.getSegments();

        int startingIndex = segments.indexOf(firstSegment);

        // at this point, add three new segments:
        // 1. left point of firstSegment to left point of landingPad
        // 2. landingPad
        // 3. right point of landingPad to right point of lastSegment
        TerrainSegment leftOfPad = new TerrainSegment(firstSegment.getLeftEndPoint(), new Vect2D(
                startingX, height));

        TerrainSegment landingPad = new TerrainSegment(new Vect2D(startingX, height), new Vect2D(
                endingX, height), Terrain.getPadMultiplier(padType));

        TerrainSegment rightOfPad = new TerrainSegment(new Vect2D(endingX, height), lastSegment
                .getRightEndPoint());

        segments.add(startingIndex, leftOfPad);
        segments.add(startingIndex + 1, landingPad);
        segments.add(startingIndex + 2, rightOfPad);

        // now we need to delete all the pads that we have replaced
        int numToDelete = segments.indexOf(lastSegment) - segments.indexOf(firstSegment) + 1;
        for (int i = 0; i < numToDelete; i++) {
            segments.remove(startingIndex + 3);
        }
    }
}