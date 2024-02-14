package lunarlander.gameobject;

import lunarlander.util.Vect2D;

/**
 * @author Chung
 * 
 * Controllable interface is implemented by an object in the game that can be controlled by the user
 * by pressing and holding up, down, left, and right. It also has three general buttons which cannot
 * be held down (only tapped).
 */
public interface Controllable {

    /**
     * User pressed/released UP.
     */
    public void handleUp(boolean pressed);

    /**
     * User pressed/released DOWN.
     */
    public void handleDown(boolean pressed);

    /**
     * User pressed/released LEFT.
     */
    public void handleLeft(boolean pressed);

    /**
     * User pressed/released RIGHT.
     */
    public void handleRight(boolean pressed);

    /**
     * User tapped BUTTON 1
     */
    public void handleButton1Tap();

    /**
     * User tapped BUTTON 2
     */
    public void handleButton2Tap();

    /**
     * User tapped BUTTON 3
     */
    public void handleButton3Tap();
    
    /**
     * return position Vect2D of the object
     * @return
     */
    public Vect2D getPosition();
    
    /**
     * @return true if craft is dead
     */
    public boolean isDead();
}