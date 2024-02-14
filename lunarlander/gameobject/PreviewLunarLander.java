package lunarlander.gameobject;

import java.awt.*;
import java.util.Iterator;

import lunarlander.canvas.Canvas;
import lunarlander.canvas.MiniLanderCanvas;
import lunarlander.game.LunarLanderGame;
import lunarlander.player.Player;
import lunarlander.player.PlayerRole;
import lunarlander.player.Team;
import lunarlander.util.Vect2D;



/**
 * @author Chung
 * 
 * PreviewLunarLander is a lander that is drawn in the Player Options pane. It flies around randomly
 * and looks very sexy.
 */
public class PreviewLunarLander extends LunarLander {

    /**
     * Construct a new lunar lander with the given initial position, velocity, and angle.
     * 
     * @param initialPosition is the initial position of the lander
     * @param initialVelocity is the initial velocity of the lander
     * @param initialAngle is the initial angle of the lander
     * @param player the player
     */
    public PreviewLunarLander(Vect2D initialPosition, Vect2D initialVelocity, double initialAngle,
            Player player) {
        super(initialPosition, initialVelocity, initialAngle, player);
        team = Team.values()[0];
        drawTeamColor = false;
        role = PlayerRole.PLAYER;
    }

    /**
     * overrides LunarLander.getFuel(); we never run out of fuel
     */
    public double getFuel() {
        return MAX_FUEL;
    }

    /**
     * overrides LunarLander.getMass(); we always have full mass
     */
    public double getMass() { 
        return LANDER_MASS + MAX_FUEL;
    }

    /**
     * overrides LunarLander.getMomentumROC(); the only force on a preview lander is the throttle;
     * we don't even have gravity
     */
    protected Vect2D getMomentumROC(double dt) {
        return getThrottleMomentumROC(dt);
    }

    /**
     * overrides LunarLander.normalizePosition(); also wraps around in the Y direction
     */
    protected void normalizePosition() {
        double positionX = position.getX();
        double positionY = position.getY();

        // Wrap around
        while (positionX >= MiniLanderCanvas.WORLD_WIDTH) {
            positionX -= MiniLanderCanvas.WORLD_WIDTH;
        }
        while (positionX < 0) {
            positionX += MiniLanderCanvas.WORLD_WIDTH;
        }

        while (positionY >= MiniLanderCanvas.WORLD_HEIGHT) {
            positionY -= MiniLanderCanvas.WORLD_HEIGHT;
        }

        while (positionY < 0) {
            positionY += MiniLanderCanvas.WORLD_HEIGHT;
        }

        position = new Vect2D(positionX, positionY);
    }

    /**
     * overrides LunarLander.isTouchingGround(); never!
     */
    protected boolean isTouchingGround() {
        return false;
    }

    // DRAWING METHODS

    /**
     * overrides LunarLander.draw(); we always draw trace.
     */
    public void draw(Graphics g, Canvas canvas) {

        // Draw main flame
        canvas.drawPolyline(g, Color.white, getFlamePolylines());

        // Draw lunar lander
        Vect2D[][] transformedLanderLines = getLanderPolylines();
        for (int i = 0; i < transformedLanderLines.length; i++) {
            Color c = null;
            if (status == STATUS_CRASHED) {
                c = Color.gray;
            }
            else {
                if (role == PlayerRole.OBSERVER) {
                    c = Color.gray;
                }
                else if (i != 0) {
                    if (drawTeamColor) {
                        c = team.getColor();
                    }
                    else {
                        c = Color.white;
                    }
                }
                else {
                    c = color;
                }
            }
            canvas.drawPolyline(g, c, transformedLanderLines[i]);
        }
        drawPreviousLanderPositions(g, canvas);
    }

    /**
     * overrides LunarLander.drawPreviousLanderPositions(); the currentTime doesn't depend on game
     * time anymore
     */
    public void drawPreviousLanderPositions(Graphics g, Canvas canvas) {

        // create the colors we want to use; start with the given color,
        // and progressively dim it
        Color[] colors = new Color[TRACE_STAGES];
        colors[0] = (drawTeamColor) ? team.getColor() : color;
        for (int i = 1; i < colors.length; i++) {
            colors[i] = colors[i - 1].darker();
        }

        // time length of each color stage
        double traceStageLength = LunarLanderGame.TRACE_ALIVE_TIME / TRACE_STAGES;

        synchronized (recorder) {
            Iterator posIter = recorder.getPositions().iterator();

            // LunarLanderLauncher.game.time might change during this, but we want to use the same
            // time for drawing the trail, so cache it locally here
            double currentTime = ((Double) recorder.getTimes().get(recorder.getTimes().size() - 1))
                    .doubleValue();
            for (Iterator timeIter = recorder.getTimes().iterator(); timeIter.hasNext();) {
                Double time = (Double) timeIter.next();
                int desiredStage = (int) ((currentTime - time.doubleValue()) / traceStageLength);

                // the desiredStage might be greater than TRACE_STAGES because we might not have had
                // the chance to remove an old data. Thus use at most index TRACE_STAGES-1.
                Color c = colors[Math.min(desiredStage, TRACE_STAGES - 1)];
                Vect2D position = (Vect2D) posIter.next();
                canvas.drawDot(g, c, position);
            }
        }
    }

    /**
     * overrides LunarLander.getMaxSpeed(); we'd like a lower max speed than a normal lander
     */
    public double getMaxSpeed() {
        return MAX_SPEED;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setDrawTeamColor(boolean on) {
        drawTeamColor = on;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }


    protected Team team;
    protected boolean drawTeamColor;
    protected PlayerRole role;

    public static final double MAX_SPEED = 10;
}