package lunarlander.canvas;
import javax.swing.*;
import java.awt.*;

/**
 * Canvas that displays the game title
 */
public class TitleCanvas extends JPanel {

    /**
     * Draws "Lunar Lander"
     * @param g     Graphics context
     */
    public void paint(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);

        setBackground(Color.black);

        g.setColor(Color.white);
        Font f = new Font("Courier", Font.BOLD, Math.min(36, getHeight()));
        g.setFont(f);

        int strWidth = g.getFontMetrics().stringWidth("Lunar Lander");
        g.drawString("Lunar Lander", (getWidth() - strWidth) / 2, (getHeight() + f.getSize()) / 2);
    }
    
    private static final long serialVersionUID = 1L;
}