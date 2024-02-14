package lunarlander.gameobject;

import java.awt.*;
import java.util.*;

import lunarlander.*;
import lunarlander.canvas.Canvas;
import lunarlander.game.LunarLanderGame;
import lunarlander.map.Moon;
import lunarlander.map.TerrainSegment;
import lunarlander.player.Player;
import lunarlander.util.Matrix;
import lunarlander.util.Vect2D;

/**
 * The lunar lander
 * 
 * @author Michael Yu
 */
public class LunarLander extends Bumpable implements Controllable {

	/**
	 * Construct a new lunar lander with the given initial position, velocity,
	 * and angle. Throttle should be set to 0 initially. The angle should be set
	 * to initialAngle, and desiredAngle should be set to the same. Remember to
	 * set totalMass and momentum too! The lander should start out with max
	 * fuel. Initialize status to STATUS_FLYING.
	 * 
	 * @param initialPosition
	 *            is the initial position of the lander
	 * @param initialVelocity
	 *            is the initial velocity of the lander
	 * @param initialAngle
	 *            is the initial angle of the lander
	 * @param player
	 *            the player
	 */
	public LunarLander(Vect2D initialPosition, Vect2D initialVelocity,
			double initialAngle, Player player) {

		throttle = 0;
		angle = initialAngle;
		desiredAngle = initialAngle;
		position = initialPosition;
		totalMass = MAX_FUEL + LANDER_MASS;
		momentum = initialVelocity.scale(totalMass);
		status = STATUS_FLYING;
		this.color = player.getColor();
		this.player = player;
		recorder = new DataRecorder(this, LunarLanderGame.RECORDER_CAPACITY,
				LunarLanderGame.TRACE_INTERVAL);
	}

	/**
	 * Construct a clone of the given lunar lander. This LunarLander should have
	 * the same instance variable values as the argument LunarLander (same
	 * position, same momentum, etc.)
	 * 
	 * @param lander
	 *            is the lunar lander to clone
	 */
	public LunarLander(LunarLander lander) {
		throttle = lander.throttle;
		angle = lander.angle;
		desiredAngle = lander.desiredAngle;
		position = lander.position;
		momentum = lander.momentum;
		totalMass = lander.totalMass;
		status = lander.status;
		color = lander.color;
	}

	// GETTER METHODS

	/**
	 * Get the status of the lander
	 * 
	 * @return The status of the lunar lander
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Get the angle of the lander
	 * 
	 * @return the angle of the lander
	 */
	public double getAngle() {
		return angle;
	}

	public double getDesiredAngle() {
		return desiredAngle;
	}
	
	/**
	 * Get the throttle
	 * 
	 * @return the throttle percentage
	 */
	public double getThrottle() {
		return throttle;
	}

	/**
	 * @return the amount of fuel remaining
	 */
	public double getFuel() {
		return getMass() - LANDER_MASS;
	}

	/**
	 * implements Bumpable.getCenter()
	 * 
	 * the center of a lander is not exactly the position vector
	 */
	public Vect2D getCenter() {
		return position.add(new Vect2D(LANDER_LENGTH * 0.3 * Math.sin(angle),
				LANDER_LENGTH * 0.3 * -Math.cos(angle)));
	}

	/**
	 * implements Bumpable.getSize()
	 */
	public double getSize() {
		return LANDER_LENGTH;
	}

	/**
	 * implements Bumpable.getMass()
	 */
	public double getMass() {
		return totalMass;
	}

	/**
	 * implements Bumpable.getMaxSpeed()
	 */
	public double getMaxSpeed() {
		return MAX_SPEED;
	}

	/**
	 * implements Controllable.isDead()
	 */
	public boolean isDead() {
		return status == STATUS_CRASHED;
	}

	// SETTER METHODS

	/**
	 * Set the throttle (between 0 and 1)
	 * 
	 * @param throttlePercentage
	 *            is the percentage of engine thrust to use
	 */
	public void setThrottle(double throttlePercentage) {
		if (getFuel() > 0 && status == STATUS_FLYING) {
			throttle = Math.max(0.0, Math.min(1.0, throttlePercentage));
		}
	}

	/**
	 * Set the desired angle of the lander. The lander has a maximum rotation
	 * rate, so it may take several timesteps before the lander actually rotates
	 * to this angle.
	 * 
	 * @param desiredAngle
	 *            is the target angle of the lander
	 */
	public void setDesiredAngle(double desiredAngle) {
		this.desiredAngle = desiredAngle;
		normalizeAngles();
	}
    
    /**
     * Set the angle of the lander immediately
     * @param angle angle to set to
     */
    public void setAngle(double angle) {
        this.angle = angle;
        setDesiredAngle(angle);
    }

	/**
	 * Set the color of this LunarLander
	 * 
	 * @param color
	 *            Color to set to
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Set landingAssist on/off
	 * 
	 * @param on
	 *            true if on
	 */
	public void setLandingAssist(boolean on) {
		landingAssist = on;
	}

	/**
	 * sets the position to something else
	 * 
	 * @param newPos
	 *            new position
	 */
	public void setPosition(Vect2D newPos) {
		position = newPos;
	}

	// SIMULATION METHODS

	/**
	 * implements Bumpable.step()
	 * 
	 * also responsible for updating its rockets. Won't call super.step() if no
	 * longer flying
	 */
	public void step(double dt) {
		// short circuit if we're not flying anymore
		if (status != STATUS_FLYING) {
			updateRecorder(dt);
			return;
		}

		if (!isDead()) {
			// Update the user inputs
			updateUserInputs(dt);
		}

		// do the usual stepping
		super.step(dt);

		// Update angle
		updateAngle(dt);

		// Update the lander's status
		updateStatus();

		// Update the data recorder
		updateRecorder(dt);
	}

	/**
	 * update the recorder for another timestep
	 * 
	 * @param dt
	 *            timestep size
	 */
	private void updateRecorder(double dt) {
		synchronized (recorder) {
			recorder.step(dt);
		}
	}

	/**
	 * Update throttle and desiredAngle
	 * 
	 * @param dt
	 *            is the timestep
	 */
	private void updateUserInputs(double dt) {

		if (leftPressed) {
			long pressedDuration = System.currentTimeMillis() - leftPressedTime;
			double turnRate = (pressedDuration < TAP_DURATION) ? TAP_TURN_RATE
					: TURN_RATE;
			double desired = getAngle() + turnRate * dt;

			// System.out.println("Angle: " + getAngle() + "; desired: " +
			// desired);

			// This guarantees we can bring the lander back to the
			// straight upright position
			if (getAngle() < 0 && desired > 0) {
				setDesiredAngle(0);
			} else {
				setDesiredAngle(desired);
			}
		}

		if (rightPressed) {
			long pressedDuration = System.currentTimeMillis()
					- rightPressedTime;
			double turnRate = (pressedDuration < TAP_DURATION) ? TAP_TURN_RATE
					: TURN_RATE;
			double desired = getAngle() - turnRate * dt;

			// This guarantees we can bring the lander back to the
			// straight upright position
			if (getAngle() > 0 && desired < 0) {
				setDesiredAngle(0);
			} else {
				setDesiredAngle(desired);
			}
		}

		if (upPressed) {
			setThrottle(getThrottle() + THROTTLE_RATE * dt);
		}

		if (downPressed) {
			setThrottle(getThrottle() - THROTTLE_RATE * dt);
		}
	}

	/**
	 * update angle based on desiredAngle and amount of fuel left. Normalizes
	 * angle and desiredAngle afterwards so they stay between -PI and PI
	 * 
	 * uses up fuel by subtracting from totalMass!
	 * 
	 * @param dt
	 *            timestep size
	 */
	protected void updateAngle(double dt) {
		double deltaTheta = Math.abs(desiredAngle - angle);
		deltaTheta = Math.min(deltaTheta, 2 * Math.PI - deltaTheta);
		deltaTheta = Math.min(deltaTheta, TURN_RATE * dt);
		deltaTheta = Math
				.min(deltaTheta, getFuel() / ROTATIONAL_FUEL_BURN_RATE);
		double sign = ((angle > desiredAngle && angle - desiredAngle < Math.PI) || (angle < desiredAngle && desiredAngle
				- angle > Math.PI)) ? -1.0 : 1.0;
		angle += sign * deltaTheta;
		normalizeAngles();

		// Subtract fuel due to rotation
		totalMass -= deltaTheta * ROTATIONAL_FUEL_BURN_RATE;
	}

	/**
	 * overrides Bumpable.getMomentumROC()
	 * 
	 * total momentum ROC is the sum of that from gravity, collision and the
	 * throttle
	 */
	protected Vect2D getMomentumROC(double dt) {
		return super.getMomentumROC(dt).add(getThrottleMomentumROC(dt));
	}

	/**
	 * calculates momentum change due to the throttle. SIDE EFFECT: updates
	 * totalMass.
	 * 
	 * @param dt
	 *            timestep size
	 * @return momentum ROC due to throttle
	 */
	protected Vect2D getThrottleMomentumROC(double dt) {
		throttle = Math.min(FUEL_BURN_RATE * throttle * dt, getFuel())
				/ (dt * FUEL_BURN_RATE);
		Vect2D roc = new Vect2D(-Math.sin(angle), Math.cos(angle));
		roc = roc.scale(FUEL_BURN_RATE * throttle * EXHAUST_VELOCITY);
		totalMass -= throttle * FUEL_BURN_RATE * dt;
		return roc;
	}

	/**
	 * Adjusts the angle and desiredAngle so that it falls in [-PI, PI)
	 */
	protected void normalizeAngles() {
		while (angle < -Math.PI) {
			angle += 2 * Math.PI;
		}
		while (angle >= Math.PI) {
			angle -= 2 * Math.PI;
		}
		while (desiredAngle < -Math.PI) {
			desiredAngle += 2 * Math.PI;
		}
		while (desiredAngle >= Math.PI) {
			desiredAngle -= 2 * Math.PI;
		}
	}

	/**
	 * Update the status of the lander from FLYING to LANDED or CRASHED
	 */
	protected void updateStatus() {
		// Check if lander is touching the ground
		if (isTouchingGround()) {
			if (landedSafely()) {
				// Lander has landed safely
				status = STATUS_LANDED;
				throttle = 0;
				// angle = 0;
			} else {
				// Lander has crashed
				status = STATUS_CRASHED;
				throttle = 0;
				player.setControlledSpacecraft(this);
			}
		}
	}

	/**
	 * Is the lander touching the ground?
	 * 
	 * @return true if the lander is touching the ground
	 */
	protected boolean isTouchingGround() {

		// Check each point comprising the lander for collision
		Vect2D[][] landerPolylines = getLanderPolylines();
		for (int i = 0; i < landerPolylines.length; i++) {
			for (int j = 0; j < landerPolylines[i].length; j++) {
				double x = landerPolylines[i][j].getX();
				double y = landerPolylines[i][j].getY();
				if (LunarLanderLauncher.game.moon.getTerrainHeight(x) >= y) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Has the lander has landed safely?
	 * 
	 * This ASSUMES that the lander isTouchingGround() already!
	 * 
	 * @return true if the lander has landed safely
	 */
	public boolean landedSafely() {
		LunarLanderGame game = LunarLanderLauncher.game;
		Moon moon = game.moon;
		Vect2D[][] landerPolylines = getLanderPolylines();

		// get left foot coordinate
		int leftFootIndex = landerPolylines.length - 1;
		double leftX = landerPolylines[leftFootIndex][0].getX();
		double leftY = landerPolylines[leftFootIndex][0].getY();
		
		// get right foot coordinate
		int rightFootIndex = leftFootIndex - 1;
		double rightX = landerPolylines[rightFootIndex][0].getX();
		double rightY = landerPolylines[rightFootIndex][0].getY();
		
		// get the encompassing pad
		TerrainSegment leftPad = moon.getEncompassingTerrainSegment(leftX);
		TerrainSegment rightPad = moon.getEncompassingTerrainSegment(rightX);

		// both legs must be over the same pad
		if (leftPad != rightPad) {
			return false;
		}
		
		if (!leftPad.isLandingPad()) {
			return false;
		}

		// get pad height
		double padHeight = leftPad.getLeftEndPoint().getY();

		// check if we've landed safely; that is:
		// 1. both the right and left feet are over the landing pad
		// 2. y-velocity less than safety maximum
		// 3. x-velocity less than safety maximum
		// 4. angle (in degrees) less than safety maximum
		if ((rightY <= padHeight || leftY <= padHeight)
				&& rightX >= leftPad.getLeftEndPoint().getX()
				&& leftX <= leftPad.getRightEndPoint().getX()
				&& (Math.abs(getVelocity().getY()) <= game.safeVelocityY)
				&& (Math.abs(getVelocity().getX()) <= game.safeVelocityX)
				&& (Math.abs(Math.toDegrees(getAngle())) <= game.safeAngle)) {
			return true;
		}

		/*
		 * if (rightY > padHeight) { System.out.println("Right leg not touching
		 * pad; rightY: " + rightY + "; padHeight: " + padHeight); }
		 * 
		 * if (leftY > padHeight) { System.out.println("left leg not touching
		 * pad; leftY: " + leftY + "; padHeight: " + padHeight); }
		 * 
		 * if (!(moon.isOverLandingPad(rightX) && moon.isOverLandingPad(leftX))) {
		 * System.out.println("Not both over landing pad"); }
		 */
		return false;
	}

	// DRAWING METHODS

	/**
	 * draws the lander on a given Canvas
	 * 
	 * @param g
	 *            Graphics context
	 * @param canvas
	 *            canvas to draw on
	 */
	public void draw(Graphics g, Canvas canvas) {

		// Draw main flame
		canvas.drawPolyline(g, Color.white, getFlamePolylines());

		// Draw lunar lander
		Vect2D[][] transformedLanderLines = getLanderPolylines();
		for (int i = 0; i < transformedLanderLines.length; i++) {
			Color c = null;
			if (isDead()) {
				c = Color.gray;
			} else {
				if (i != 0)
					c = Color.white;
				else
					c = color;
			}
			canvas.drawPolyline(g, c, transformedLanderLines[i]);
		}

		drawIndicator(g, canvas);

		// Draw trace
		if (Settings.getBoolean(Settings.TRACE)) {
			drawPreviousLanderPositions(g, canvas);
		}

		// Draw landing assist
		if (landingAssist) {
			drawLandingAssist(g, canvas);
		}
	}

	/**
	 * @return the lunar lander's polylines in world's coordinate system
	 */
	protected Vect2D[][] getLanderPolylines() {

		// First, construct the lines which make up the lander. Center them
		// about (0,0).
		Vect2D[][] landerPolylines = getVanillaLanderPolylines();

		// Now, Let's rotate and translate these lines to put them in the
		// correct location

		// Rotation matrix
		Matrix rotation = new Matrix(Math.cos(angle), -Math.sin(angle), Math
				.sin(angle), Math.cos(angle));

		// Translation vector
		Vect2D translation = new Vect2D(position.getX(), position.getY());

		for (int i = 0; i < landerPolylines.length; i++) {
			for (int j = 0; j < landerPolylines[i].length; j++) {
				landerPolylines[i][j] = translation.add(rotation
						.times(landerPolylines[i][j]));
			}
		}

		return landerPolylines;
	}

	/**
	 * @return the indicator's polylines in world's coordinate system
	 */
	protected Vect2D[][] getIndicatorPolylines(Canvas canvas) {
		Vect2D[][] landerPolylines = getVanillaLanderPolylines();
		Vect2D[][] allLines = new Vect2D[landerPolylines.length + 1][];
		Matrix rotation = new Matrix(Math.cos(angle), -Math.sin(angle), Math
				.sin(angle), Math.cos(angle));

		// Translation vector
		double topY = canvas.getURY() - 1.5 * LANDER_LENGTH;
		Vect2D translation = new Vect2D(position.getX(), topY);
		for (int i = 0; i < landerPolylines.length; i++) {
			for (int j = 0; j < landerPolylines[i].length; j++) {
				landerPolylines[i][j] = translation.add(rotation
						.times(landerPolylines[i][j]));
			}
			allLines[i] = landerPolylines[i];
		}
		allLines[allLines.length - 1] = getIndicatorFlamePolylines(canvas);
		return allLines;
	}

	/**
	 * @return lander's polylines in lander's coordinate system
	 */
	public static Vect2D[][] getVanillaLanderPolylines() {
		return new Vect2D[][] {
				// Cockpit
				{ new Vect2D(-.37 * LANDER_LENGTH, -.3 * LANDER_LENGTH),
						new Vect2D(-.43 * LANDER_LENGTH, -.18 * LANDER_LENGTH),
						new Vect2D(-.38 * LANDER_LENGTH, 0),
						new Vect2D(-.3 * LANDER_LENGTH, .1 * LANDER_LENGTH),
						new Vect2D(-.1 * LANDER_LENGTH, .2 * LANDER_LENGTH),
						new Vect2D(.1 * LANDER_LENGTH, .2 * LANDER_LENGTH),
						new Vect2D(.3 * LANDER_LENGTH, .1 * LANDER_LENGTH),
						new Vect2D(.38 * LANDER_LENGTH, 0),
						new Vect2D(.43 * LANDER_LENGTH, -.18 * LANDER_LENGTH),
						new Vect2D(.37 * LANDER_LENGTH, -.3 * LANDER_LENGTH) },

				// Middle section
				{ new Vect2D(-.45 * LANDER_LENGTH, -.3 * LANDER_LENGTH),
						new Vect2D(.45 * LANDER_LENGTH, -.3 * LANDER_LENGTH),
						new Vect2D(.45 * LANDER_LENGTH, -.5 * LANDER_LENGTH),
						new Vect2D(-.45 * LANDER_LENGTH, -.5 * LANDER_LENGTH),
						new Vect2D(-.45 * LANDER_LENGTH, -.3 * LANDER_LENGTH) },

				// Booster
				{ new Vect2D(-.2 * LANDER_LENGTH, -.5 * LANDER_LENGTH),
						new Vect2D(-.3 * LANDER_LENGTH, -.75 * LANDER_LENGTH),
						new Vect2D(.3 * LANDER_LENGTH, -.75 * LANDER_LENGTH),
						new Vect2D(.2 * LANDER_LENGTH, -.5 * LANDER_LENGTH),
						new Vect2D(-.2 * LANDER_LENGTH, -.5 * LANDER_LENGTH) },

				// Legs
				{ new Vect2D(.35 * LANDER_LENGTH, -.5 * LANDER_LENGTH),
						new Vect2D(.45 * LANDER_LENGTH, -.8 * LANDER_LENGTH) },
				{ new Vect2D(-.35 * LANDER_LENGTH, -.5 * LANDER_LENGTH),
						new Vect2D(-.45 * LANDER_LENGTH, -.8 * LANDER_LENGTH) },

				// Feet
				{ new Vect2D(.5 * LANDER_LENGTH, -.8 * LANDER_LENGTH),
						new Vect2D(.4 * LANDER_LENGTH, -.8 * LANDER_LENGTH) },
				{ new Vect2D(-.5 * LANDER_LENGTH, -.8 * LANDER_LENGTH),
						new Vect2D(-.4 * LANDER_LENGTH, -.8 * LANDER_LENGTH) } };
	}

	/**
	 * @return engine flame polylines in the world's coordinate system
	 */
	protected Vect2D[] getFlamePolylines() {

		Vect2D[] flamePolylines = getVanillaFlamePolylines();

		// Rotation matrix
		Matrix rotation = new Matrix(Math.cos(angle), -Math.sin(angle), Math
				.sin(angle), Math.cos(angle));

		// Translation vector
		Vect2D translation = new Vect2D(position.getX(), position.getY());

		flamePolylines[0] = translation.add(rotation.times(flamePolylines[0]));
		flamePolylines[1] = translation.add(rotation.times(new Vect2D(0, -.75
				* LANDER_LENGTH - 1.3 * throttle * LANDER_LENGTH)));
		flamePolylines[2] = translation.add(rotation.times(flamePolylines[2]));

		return flamePolylines;
	}

	/**
	 * @return engine flame polylines in lander's flame's coordinate system
	 */
	public static Vect2D[] getVanillaFlamePolylines() {
		return new Vect2D[] {
				new Vect2D(-.25 * LANDER_LENGTH, -.75 * LANDER_LENGTH),
				new Vect2D(0, -.75 * LANDER_LENGTH),
				new Vect2D(.25 * LANDER_LENGTH, -.75 * LANDER_LENGTH) };
	}

	/**
	 * @return engine flame polylines for the indicator in world's coordinate
	 *         system
	 */
	protected Vect2D[] getIndicatorFlamePolylines(Canvas canvas) {
		Vect2D[] flamePolylines = getVanillaFlamePolylines();

		// Rotation matrix
		Matrix rotation = new Matrix(Math.cos(angle), -Math.sin(angle), Math
				.sin(angle), Math.cos(angle));

		// Translation vector
		Vect2D translation = new Vect2D(position.getX(), canvas.getURY() - 1.5
				* LANDER_LENGTH);

		flamePolylines[0] = translation.add(rotation.times(flamePolylines[0]));
		flamePolylines[1] = translation.add(rotation.times(new Vect2D(0, -.75
				* LANDER_LENGTH - 1.3 * throttle * LANDER_LENGTH)));
		flamePolylines[2] = translation.add(rotation.times(flamePolylines[2]));

		return flamePolylines;
	}

	/**
	 * Draw offscreen indicator for this lander
	 * 
	 * @param g
	 *            Graphics context
	 * @param canvas
	 *            Canvas to draw on
	 */
	protected void drawIndicator(Graphics g, Canvas canvas) {
		if (position.getY() - LANDER_LENGTH > canvas.getURY()) {

			Vect2D[][] indicatorLines = getIndicatorPolylines(canvas);
			for (int i = 0; i < indicatorLines.length; i++) {
				canvas.drawPolyline(g, Color.lightGray, indicatorLines[i]);
			}

			double worldWidth = canvas.getURX() - canvas.getLLX();
			double worldHeight = canvas.getURY() - canvas.getLLY();
			int canvasWidth = canvas.getWidth();
			int canvasHeight = canvas.getHeight();
			g.setColor(color.brighter());
			Font f = new Font("Times New Roman", Font.BOLD, (int) (canvasHeight
					* 2 * LANDER_LENGTH / worldHeight));
			g.setFont(f);
			int strWidth = g.getFontMetrics().stringWidth("\u2191");
			// System.out.println("Str width: " + strWidth + ", position: " +
			// position.getX() + ",
			// landerlen: " + LANDER_LENGTH);
			g.drawString("\u2191", (int) (canvasWidth
					* (position.getX() - strWidth / 3) / worldWidth),
			// (int) (canvasHeight * LANDER_LENGTH*1.5 / worldHeight));
					f.getSize());
		}
	}

	/**
	 * Display dots at the previous lander positions in the given color for the
	 * given positions.
	 * 
	 * Also removes entries from the positions and times lists that are older
	 * than TRACE_ALIVE_TIME
	 * 
	 * Dims the color of the trail progressively in each "stage"; there are
	 * TRACE_STAGES number of stages.
	 * 
	 * @param g
	 *            is the graphics context
	 * @param canvas
	 *            Canvas to draw on
	 */
	public void drawPreviousLanderPositions(Graphics g, Canvas canvas) {

		// create the colors we want to use; start with the given color,
		// and progressively dim it
		Color[] colors = new Color[TRACE_STAGES];
		colors[0] = color;
		for (int i = 1; i < colors.length; i++) {
			colors[i] = colors[i - 1].darker();
		}

		// time length of each color stage
		double traceStageLength = LunarLanderGame.TRACE_ALIVE_TIME
				/ TRACE_STAGES;

		synchronized (recorder) {
			Iterator posIter = recorder.getPositions().iterator();

			// LunarLanderLauncher.game.time might change during this, but we
			// want to use the same
			// time for drawing the trail, so cache it locally here
			double currentTime = LunarLanderLauncher.game.time;
			for (Iterator timeIter = recorder.getTimes().iterator(); timeIter
					.hasNext();) {
				Double time = (Double) timeIter.next();
				int desiredStage = (int) ((currentTime - time.doubleValue()) / traceStageLength);

				// the desiredStage might be greater than TRACE_STAGES because
				// we might not have had
				// the chance to remove an old data. Thus use at most index
				// TRACE_STAGES-1.
				Color c = colors[Math.min(desiredStage, TRACE_STAGES - 1)];
				Vect2D position = (Vect2D) posIter.next();
				canvas.drawDot(g, c, position);
			}
		}
	}

	/**
	 * draw current velocity/angle above the lander
	 * 
	 * @param g
	 *            Graphics context
	 * @param canvas
	 *            Canvas to draw in
	 */
	public void drawLandingAssist(Graphics g, Canvas canvas) {

		TerrainSegment closestPad = LunarLanderLauncher.game.moon
				.getClosestLandingPad(position);
		
		if (closestPad == null) {
			return;
		}

		if ((closestPad.isUnder(position.getX()) && closestPad
				.getHeight(position) < ASSIST_DISTANCE)
				|| closestPad.getMidPoint().distance(position) < ASSIST_DISTANCE) {

			Font f = new Font("Courier", Font.BOLD, Math.min(10, canvas
					.getHeight()));
			g.setFont(f);

			String vX = "Vx: " + Canvas.round(getVelocity().getX(), 1);
			String vY = "Vy: " + Canvas.round(getVelocity().getY(), 1);
			String angle = "Angle: "
					+ Canvas.round(Math.toDegrees(getAngle()), 1);

			int canvasX = canvas.worldToCanvasX(position.getX());
			int canvasY = canvas.worldToCanvasY(position.getY() + 2
					* LANDER_LENGTH);

			LunarLanderGame game = LunarLanderLauncher.game;
			Color safe = Color.GREEN;
			Color unsafe = new Color(255, 50, 0);
						
			g.setColor((Math.abs(getVelocity().getX()) <= game.safeVelocityX) ? safe : unsafe);
			canvas.drawString(g,
					new Vect2D(canvasX, canvasY), vX);
			
			g.setColor((Math.abs(getVelocity().getY()) <= game.safeVelocityY) ? safe : unsafe);
			canvas.drawString(g, 
					new Vect2D(canvasX, canvasY + f.getSize()), vY);
			
			g.setColor((Math.abs(Math.toDegrees(getAngle())) <= game.safeAngle) ? safe : unsafe);
			canvas.drawString(g, new Vect2D(canvasX, canvasY + 2*f.getSize()), angle);

			if (Settings.getBoolean(Settings.DISPLAY_SAFETY)) {
				closestPad.setDrawSafetyDisplay(true);
			}
		}
	}

	// CONTROL METHODS

	/**
	 * implements Controllable.handleUp()
	 */
	public void handleUp(boolean pressed) {
		upPressed = pressed;
	}

	/**
	 * implements Controllable.handleDown()
	 */
	public void handleDown(boolean pressed) {
		downPressed = pressed;
	}

	/**
	 * implements Controllable.handleLeft()
	 */
	public void handleLeft(boolean pressed) {

		if (pressed && !leftPressed) {
			leftPressedTime = System.currentTimeMillis();
		}
		leftPressed = pressed;
	}

	/**
	 * implements Controllable.handleRight()
	 */
	public void handleRight(boolean pressed) {

		if (pressed && !rightPressed) {
			rightPressedTime = System.currentTimeMillis();
		}

		rightPressed = pressed;
	}

	/**
	 * implements Controllable.handleButton1Press()
	 * 
	 * Button 1 does nothing
	 */
	public void handleButton1Tap() {
	}

	/**
	 * implements Controllable.handleButton2Press()
	 * 
	 * Button 2 does nothing
	 */
	public void handleButton2Tap() {
	}

	/**
	 * implements Controllable.handleButton3Press()
	 * 
	 * Button 3 does nothing
	 */
	public void handleButton3Tap() {
	}

	// LANDER CONSTANTS
	public static final double TURN_RATE = .4; // rad/s

	public static final double TAP_TURN_RATE = .2; // rad/s

	public static final long TAP_DURATION = 100; // rad/s

	public static final double THROTTLE_RATE = .6; // 1/s

	public static final double ROTATIONAL_FUEL_BURN_RATE = 90.0; // kg/rad

	public static final double FUEL_BURN_RATE = 200.0; // kg/s

	public static final double LANDER_LENGTH = 10.0; // m

	public static final double LANDER_MASS = 35532.0; // kg

	public static final double MAX_FUEL = 25000.0; // kg

	public static final double EXHAUST_VELOCITY = 1700.0; // m/s

	public static final double MAX_SPEED = 50; // m/s

	public static final double MAX_HOVER_HEIGHT = 500;

	// STATUS CONSTANTS
	public static final int STATUS_FLYING = 0; // Lander is currently flying

	public static final int STATUS_LANDED = 1; // Lander has safely landed

	public static final int STATUS_CRASHED = 2; // Lander has crashed

	// TRACE CONSTANTS
	public static final int TRACE_STAGES = 7;

	// ASSIST INFO
	public static final double ASSIST_DISTANCE = 75;

	// STATE VARIABLES
	protected double totalMass; // Mass of lander and fuel (kg)

	protected double throttle; // Percent engine boost to use

	protected double angle; // Current angle, [-PI, PI) (rad)

	protected double desiredAngle; // Target angle, [-PI, PI) (rad)

	protected int status; // Lander's status (flying, crashed, etc.)

	protected Color color; // lander's color

	protected DataRecorder recorder;

	protected boolean upPressed;

	protected boolean downPressed;

	protected boolean leftPressed;

	protected long leftPressedTime;

	protected boolean rightPressed;

	protected long rightPressedTime;

	protected Player player;

	protected boolean landingAssist;

}