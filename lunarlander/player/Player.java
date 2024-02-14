package lunarlander.player;


import java.awt.Color;

import lunarlander.gameobject.Controllable;
import lunarlander.gameobject.LunarLander;

/**
 * Base class for all players in the Lunar Lander game. A player has a name, a color, and a
 * lunar lander, a controllable object (the spacecraft currently being controlled), and a score
 * 
 * @author mike
 */
public abstract class Player {       
    
    /**
     * Constructor
     * @param name player name
     * @param color player color
     * @param lander player's lander
     */
    public Player(String name, Color color, LunarLander lander) {
        this.name = name;
        this.color = color;
        this.lander = lander;
        this.controlledSpacecraft = lander;
    }
    
    public Player() {
        
    }
    
    public int getId() {
        return 0;
    }
    
    /**
     * Destructor.
     */
    public abstract void destruct();
    
    /**
     * @return Returns the color.
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * @param color The color to set.
     */
    public void setColor(Color color) {
        this.color = color;
        if (lander != null) {
            lander.setColor(color);
        }
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return Returns the controlledSpacecraft.
     */
    public Controllable getControlledSpacecraft() {
        if (!controlledSpacecraft.isDead()) {
            return controlledSpacecraft;
        } else {
            return lander;
        }
    }
    
    /**
     * @param controlledSpacecraft The controlledSpacecraft to set.
     */
    public void setControlledSpacecraft(Controllable controlledSpacecraft) {
        this.controlledSpacecraft = controlledSpacecraft;
    }
    
    /**
     * @return Returns the lander.
     */
    public LunarLander getLander() {
        return lander;
    }

    /**
     * @param lander The lander to set.
     */
    public void setLander(LunarLander lander) {
        this.lander = lander;
    }
    
    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * @param score The score
     */
    public void setScore(int score) {
        this.score = score;
    }
    
    /**
     * @return name of player
     */
    public String toString() {
        return name;
    }
    
    
    protected String name;
    protected Color color;
    protected Controllable controlledSpacecraft;
    protected LunarLander lander;
    protected int score;
}