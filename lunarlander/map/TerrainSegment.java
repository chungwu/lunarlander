package lunarlander.map;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.io.Serializable;

import lunarlander.LunarLanderLauncher;
import lunarlander.canvas.Canvas;
import lunarlander.gameobject.Drawable;
import lunarlander.util.Vect2D;

/**
 * A terrain segment
 * 
 * @author Michael Yu
 */
public class TerrainSegment implements Drawable, Serializable {

	/**
	 * Construct a new terrain segment
	 * 
	 * @param leftEndPoint
	 *            the left end point of the line segement
	 * @param rightEndPoint
	 *            the right end point of the line segement
	 * @param landingPad
	 *            true if this segment is a landing pad
	 * @param multiplier
	 *            the score multiplier for this landing pad
	 */
	public TerrainSegment(Vect2D leftEndPoint, Vect2D rightEndPoint,
			int multiplier) {
		this.leftEndPoint = leftEndPoint;
		this.rightEndPoint = rightEndPoint;
		this.landingPad = (multiplier != 0);
		this.multiplier = multiplier;
	}

	/**
	 * Construct a new terrain segment that is not a landing pad
	 * 
	 * @param leftEndPoint
	 *            the left end point of the line segement
	 * @param rightEndPoint
	 *            the right end point of the line segement
	 */
	public TerrainSegment(Vect2D leftEndPoint, Vect2D rightEndPoint) {
		this(leftEndPoint, rightEndPoint, 0);
	}

	/**
	 * Get the right end point of the line segment
	 * 
	 * @return the right end point of the line segment
	 */
	public Vect2D getRightEndPoint() {
		return rightEndPoint;
	}

	/**
	 * Change the right end point of the line segment
	 * 
	 * @param rightEndPoint
	 *            the new right end point of the line segment
	 */
	public void setRightEndPoint(Vect2D rightEndPoint) {
		this.rightEndPoint = rightEndPoint;
	}

	/**
	 * Get the left end point of the line segment
	 * 
	 * @return the left end point of the line segment
	 */
	public Vect2D getLeftEndPoint() {
		return leftEndPoint;
	}

	/**
	 * Change the left end point of the line segment
	 * 
	 * @param leftEndPoint
	 *            the new left end point of the line segment
	 */
	public void setLeftEndPoint(Vect2D leftEndPoint) {
		this.leftEndPoint = leftEndPoint;
	}

	/**
	 * @return midpoint of the segment
	 */
	public Vect2D getMidPoint() {
		return leftEndPoint.add(rightEndPoint).scale(0.5);
	}

	/**
	 * Is this terrain segment a landing pad?
	 * 
	 * @return true if this line segment is a landing pad, false otherwise
	 */
	public boolean isLandingPad() {
		return landingPad;
	}

	/**
	 * Toggle whether this terrain segment is a landing pad or not
	 * 
	 * @param landingPad
	 *            should be true if this terrain segment should become a landing
	 *            pad. Should be false otherwise
	 */
	public void setLandingPad(boolean landingPad) {
		this.landingPad = landingPad;
	}

	/**
	 * Get the score multiplier for this terrain segment (only valid if this
	 * segment is a landing pad)
	 * 
	 * @return the multiplier for this landing pad
	 */
	public int getMultiplier() {
		return multiplier;
	}

	/**
	 * Change the multiplier score for this landing pad
	 * 
	 * @param multiplier
	 *            the new multiplier for this landing pad
	 */
	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	/**
	 * implements Drawable.draw(0
	 */
	public void draw(Graphics g) {
		draw(g, LunarLanderLauncher.game.canvas);
	}

	/**
	 * Draw the terrain segment and score multiplier text
	 * 
	 * Turns off drawSafetyDisplay if it was on
	 * 
	 * @param g
	 *            the graphics context to draw to
	 */
	public void draw(Graphics g, Canvas canvas) {
		// Draw the terrain segment
		drawTerrainSegment(g, canvas);

		// Draw the multiplier text
		if (isLandingPad()) {
			if (drawSafetyDisplay) {
				drawSafetyDisplay(g, canvas);
				drawSafetyDisplay = false;
			} else if (TerrainSegment.isMultiplierDisplayed()) {
				drawMultiplierText(g, canvas);
			}
		}
	}

	/**
	 * @param x
	 *            an x world coordinate
	 * @return true if this terrain segment is under that x
	 */
	public boolean isUnder(double x) {
		return leftEndPoint.getX() <= x && x <= rightEndPoint.getX();
	}

	/**
	 * calculates the distance above ground of a point encompassed by this
	 * segment
	 * 
	 * @param x
	 *            x coordinate of the point
	 * @param y
	 *            y coordinate of the point
	 * @return distance above ground at x
	 */
	public double getHeight(Vect2D position) {
		Vect2D left = getLeftEndPoint();
		Vect2D right = getRightEndPoint();
		double rise = right.getY() - left.getY();
		double run = right.getX() - left.getX();
		return position.getY()
				- (left.getY() + (position.getX() - left.getX()) * rise / run);
	}

	/**
	 * Draw the score multiplier text
	 * 
	 * @param g
	 *            the graphics context to draw to
	 */
	public void drawMultiplierText(Graphics g, Canvas canvas) {
		double worldWidth = canvas.getWorldWidth();

		double llx = canvas.getLLX();
		double lly = canvas.getLLY();
		double urx = canvas.getURX();
		double ury = canvas.getURY();

		double centerX = leftEndPoint.getX()
				+ (rightEndPoint.getX() - leftEndPoint.getX()) / 2;
		int x1 = (int) ((double) canvas.getWidth() * ((centerX - llx) / (urx - llx)));
		int x2 = (int) ((double) canvas.getWidth() * ((centerX - worldWidth - llx) / (urx - llx)));
		int x3 = (int) ((double) canvas.getWidth() * ((centerX + worldWidth - llx) / (urx - llx)));

		double centerY = leftEndPoint.getY()
				+ (rightEndPoint.getY() - leftEndPoint.getY()) / 2;
		int y = (int) ((double) canvas.getHeight() * (1.0 - (centerY - lly)
				/ (ury - lly)));

		Font f = new Font("Courier", Font.PLAIN, Math.min(12, canvas
				.getHeight()));
		g.setFont(f);
		int strWidth = g.getFontMetrics().stringWidth(multiplier + "X");

		// Draw the text three times, in the left, center, and right viewing
		// windows
		g.setColor(Color.white);
		g.drawString(multiplier + "X", x1 - strWidth / 2, y + f.getSize() + 3);
		g.drawString(multiplier + "X", x2 - strWidth / 2, y + f.getSize() + 3);
		g.drawString(multiplier + "X", x3 - strWidth / 2, y + f.getSize() + 3);
	}

	/**
	 * Draw the terrain segment
	 * 
	 * @param g
	 *            the graphics context to draw to
	 */
	public void drawTerrainSegment(Graphics g, Canvas canvas) {
		double worldWidth = canvas.getWorldWidth();

		double llx = canvas.getLLX();
		double lly = canvas.getLLY();
		double urx = canvas.getURX();
		double ury = canvas.getURY();

		g.setColor(isLandingPad() ? Color.yellow : Color.white);

		int y1 = (int) ((double) canvas.getHeight() * (1.0 - (leftEndPoint
				.getY() - lly)
				/ (ury - lly)));
		int y2 = (int) ((double) canvas.getHeight() * (1.0 - (rightEndPoint
				.getY() - lly)
				/ (ury - lly)));

		// Draw in the center viewing window
		int x1 = (int) ((double) canvas.getWidth()
				* (leftEndPoint.getX() - llx) / (urx - llx));
		int x2 = (int) ((double) canvas.getWidth()
				* (rightEndPoint.getX() - llx) / (urx - llx));
		g.drawLine(x1, y1, x2, y2);

		// Draw in the left viewing window
		x1 = (int) ((double) canvas.getWidth()
				* (leftEndPoint.getX() - worldWidth - llx) / (urx - llx));
		x2 = (int) ((double) canvas.getWidth()
				* (rightEndPoint.getX() - worldWidth - llx) / (urx - llx));
		g.drawLine(x1, y1, x2, y2);

		// Draw in the right viewing window
		x1 = (int) ((double) canvas.getWidth()
				* (leftEndPoint.getX() + worldWidth - llx) / (urx - llx));
		x2 = (int) ((double) canvas.getWidth()
				* (rightEndPoint.getX() + worldWidth - llx) / (urx - llx));
		g.drawLine(x1, y1, x2, y2);
	}

	/**
	 * draw safety landing info under the terrain segment
	 * 
	 * @param g
	 *            graphics context
	 * @param canvas
	 *            canvas to draw on
	 */
	protected void drawSafetyDisplay(Graphics g, Canvas canvas) {
		double worldWidth = canvas.getWorldWidth();
		double llx = canvas.getLLX();
		double lly = canvas.getLLY();
		double urx = canvas.getURX();
		double ury = canvas.getURY();

		double centerX = (leftEndPoint.getX() + rightEndPoint.getX()) / 2;
		int x1 = (int) ((double) canvas.getWidth() * ((centerX - llx) / (urx - llx)));
		int x2 = (int) ((double) canvas.getWidth() * ((centerX - worldWidth - llx) / (urx - llx)));
		int x3 = (int) ((double) canvas.getWidth() * ((centerX + worldWidth - llx) / (urx - llx)));

		double centerY = leftEndPoint.getY()
				+ (rightEndPoint.getY() - leftEndPoint.getY()) / 2;
		int y = (int) ((double) canvas.getHeight() * (1.0 - (centerY - lly)
				/ (ury - lly)));

		Font f = new Font("Courier", Font.BOLD, Math
				.min(10, canvas.getHeight()));
		g.setFont(f);

		String safeVelocityX = "Safe Vx: "
				+ LunarLanderLauncher.game.safeVelocityX;
		String safeVelocityY = "Safe Vy: "
				+ LunarLanderLauncher.game.safeVelocityY;
		String safeAngle = "Safe Angle: " + LunarLanderLauncher.game.safeAngle;

		g.setColor(Color.green.brighter().brighter().brighter());

		// draw safeX
		int strWidth = g.getFontMetrics().stringWidth(safeVelocityX);
		g.drawString(safeVelocityX, x1 - strWidth / 2, y + f.getSize() + 3);
		g.drawString(safeVelocityX + "X", x2 - strWidth / 2, y + f.getSize()
				+ 3);
		g.drawString(safeVelocityX + "X", x3 - strWidth / 2, y + f.getSize()
				+ 3);

		// draw safeY
		strWidth = g.getFontMetrics().stringWidth(safeVelocityY);
		g.drawString(safeVelocityY, x1 - strWidth / 2, y + 2 * f.getSize() + 3);
		g.drawString(safeVelocityY + "X", x2 - strWidth / 2, y + 2
				* f.getSize() + 3);
		g.drawString(safeVelocityY + "X", x3 - strWidth / 2, y + 2
				* f.getSize() + 3);

		// draw safeAngle
		strWidth = g.getFontMetrics().stringWidth(safeAngle);
		g.drawString(safeAngle, x1 - strWidth / 2, y + 3 * f.getSize() + 3);
		g.drawString(safeAngle + "X", x2 - strWidth / 2, y + 3 * f.getSize()
				+ 3);
		g.drawString(safeAngle + "X", x3 - strWidth / 2, y + 3 * f.getSize()
				+ 3);
	}

	/**
	 * @param on
	 *            sets
	 */
	public void setDrawSafetyDisplay(boolean on) {
		drawSafetyDisplay = on;
	}

	/**
	 * Should all terrain segments draw the score multiplier to the screen?
	 * 
	 * @return true if the multiplier should be drawn on the screen
	 */
	public static boolean isMultiplierDisplayed() {
		return multiplierDisplayed;
	}

	/**
	 * Change whether all terrain segments should draw the score multiplier on
	 * screen
	 * 
	 * @param multiplierDisplayed
	 *            true if the multiplier should be drawn on screen, false
	 *            otherwise
	 */
	public static void setMultiplierDisplayed(boolean multiplierDisplayed) {
		TerrainSegment.multiplierDisplayed = multiplierDisplayed;
	}

	private Vect2D leftEndPoint; // The left end point of this line segment

	private Vect2D rightEndPoint; // The right end point of this line segment

	private boolean landingPad; // Is this terrain segment a landing pad?

	private int multiplier; // The score multipler for this landing pad

	private boolean drawSafetyDisplay;

	private static boolean multiplierDisplayed; // Should all terrain segments
												// draw the score
	// multiplier?
}