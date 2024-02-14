package lunarlander.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import lunarlander.game.LunarLanderGame;
import lunarlander.game.LunarLanderDuo;
import lunarlander.gameobject.Controllable;
import lunarlander.map.Moon;
import lunarlander.player.Player;

/**
 * Game canvas for the non-networked multiplayer game
 * 
 * @author Chung Wu
 */
public class DuoGameCanvas extends GameCanvas {

	public DuoGameCanvas(LunarLanderGame llg) {
		super(llg);
	}

	/**
	 * Update the viewing window; zoom in or out or scroll if necessary
	 */
	public void updateViewingWindow() {
		Controllable craft1 = ((Player) (lunarLanderGame.players
				.get(LunarLanderDuo.PLAYER_ONE))).getControlledSpacecraft();
		Controllable craft2 = ((Player) (lunarLanderGame.players
				.get(LunarLanderDuo.PLAYER_TWO))).getControlledSpacecraft();

		double higherY = Math.max(craft1.getPosition().getY(), craft2
				.getPosition().getY());

		double lowerY = Math.min(craft1.getPosition().getY(), craft2
				.getPosition().getY());

		Moon moon = lunarLanderGame.moon;

		// Calculate margin
		double windowHeight = ury - lly;
		double topMargin = .1 * windowHeight;
		double bottomMargin = .35 * windowHeight;

		// Pan up
		if (higherY - lly > windowHeight - topMargin) {
			lly = higherY - windowHeight + topMargin;
			ury = lly + windowHeight;
		}

		// Pan down
		if (lowerY - lly < bottomMargin) {
			lly = Math.max(0.0, lowerY - bottomMargin);
			ury = lly + windowHeight;
		}
	}

	/**
	 * Paint the title screen
	 * 
	 * @param g
	 *            is the graphics context
	 */
	public void paintTitleScreen(Graphics g) {
		g.setColor(Color.white);
		Font f = new Font("Courier", Font.BOLD, Math.min(36, getHeight()));
		g.setFont(f);

		int strWidth = g.getFontMetrics().stringWidth("Lunar Lander");
		g.drawString("Lunar Lander", (getWidth() - strWidth) / 2,
				(getHeight() + f.getSize()) / 2);

		Font f2 = new Font("Courier", Font.BOLD, 20);
		g.setFont(f2);
		g.setColor(Color.yellow);
		int strWidth2 = g.getFontMetrics().stringWidth("(Two Player)");
		g.drawString("(Two Player)", (getWidth() - strWidth2) / 2,
				(getHeight() + f2.getSize()) / 2 + f.getSize());
	}

	private static final long serialVersionUID = 1L;
}