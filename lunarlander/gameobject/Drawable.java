package lunarlander.gameobject;
import java.awt.Graphics;
import lunarlander.canvas.Canvas;

/**
 * @author mike
 *
 * This interface is implemented by objects that can be drawn on screen
 */
public interface Drawable {    
    public void draw(Graphics g, Canvas canvas);
}
