package lunarlander.gameobject;
/**
 * @author mike
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public abstract class Steppable {

    /**
     * simulates this object for one timestep
     * 
     * @param dt timestep size
     */
    public abstract void step(double dt);

    /**
     * @return true if this object should be deleted from the world
     */
    public boolean shouldDelete() {
        return false;
    }

}