package lunarlander.player;


import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import lunarlander.game.LunarLanderDuo;
import lunarlander.gameobject.LunarLander;
import lunarlander.LunarLanderLauncher;

/**
 * A local human player
 * 
 * @author mike
 */
public class HumanPlayer extends Player {

    /**
     * Create a new local human player
     * 
     * @param name the name of the player
     * @param color the color of the player
     * @param lander the lander controlled by the player
     * @param layout the keyboard layout
     */
    public HumanPlayer(String name, Color color, LunarLander lander, int layout) {
        super(name, color, lander);
        this.score = 0;

        switch (layout) {
            case LAYOUT_ONE:
                this.listener = new LayoutOne(this);
                break;
            case LAYOUT_TWO:
                this.listener = new LayoutTwo(this);
                break;
        }
        LunarLanderLauncher.frame.addKeyListener(this.listener);
    }
    
    public int getId() {
        if (this.listener instanceof LayoutOne) {
            return LunarLanderDuo.PLAYER_ONE;
        } else {
            return LunarLanderDuo.PLAYER_TWO;
        }
    }
    
    /**
     * Destructor.  Removes listeners.
     */
    public void destruct() {
        LunarLanderLauncher.frame.removeKeyListener(this.listener);
        lander = null;
        controlledSpacecraft = null;
    }
    
    public void removeListener() {
        LunarLanderLauncher.frame.removeKeyListener(this.listener);
    }


    private KeyListener listener;

    public static final int LAYOUT_ONE = 1;
    public static final int LAYOUT_TWO = 2;


    /**
     * Keyboard control using arrows for directional keys, and ";","'","/" for generic buttons
     * 
     * @author mike
     */
    private static class LayoutOne implements KeyListener {

        /**
         * Construct a new keyboard listener
         * 
         * @param player is the HumanPlayer associated with this listener
         */
        public LayoutOne(HumanPlayer player) {
            this.player = player;
        }

        /**
         * This method is called when the user taps a key. It updates the state variables for the
         * event handler
         * 
         * @param e is the key event
         */
        public void keyTyped(KeyEvent e) {
            
            if (player.controlledSpacecraft == null) {
                return;
            }
            
            switch (e.getKeyChar()) {
                case '/':
                    player.controlledSpacecraft.handleButton1Tap();
                    break;
                case ';':
                    player.controlledSpacecraft.handleButton2Tap();
                    break;
                case '\'':
                    player.controlledSpacecraft.handleButton3Tap();
                    break;
            }
        }

        /**
         * This method is called when the user holds down a key. It updates the state variables for
         * the event handler
         * 
         * @param e is the key event
         */
        public void keyPressed(KeyEvent e) {
            if (player.controlledSpacecraft == null) {
                return;
            }
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    player.controlledSpacecraft.handleUp(true);
                    break;
                case KeyEvent.VK_DOWN:
                    player.controlledSpacecraft.handleDown(true);
                    break;
                case KeyEvent.VK_LEFT:
                    player.controlledSpacecraft.handleLeft(true);
                    break;
                case KeyEvent.VK_RIGHT:
                    player.controlledSpacecraft.handleRight(true);
                    break;
            }
        }

        /**
         * This method is called when the user releases a key. It updates the state variables for
         * the event handler
         * 
         * @param e is the key event
         */
        public void keyReleased(KeyEvent e) {
            if (player.controlledSpacecraft == null) {
                return;
            }
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    player.controlledSpacecraft.handleUp(false);
                    break;
                case KeyEvent.VK_DOWN:
                    player.controlledSpacecraft.handleDown(false);
                    break;
                case KeyEvent.VK_LEFT:
                    player.controlledSpacecraft.handleLeft(false);
                    break;
                case KeyEvent.VK_RIGHT:
                    player.controlledSpacecraft.handleRight(false);
                    break;
            }
        }


        private HumanPlayer player;
    }


    /**
     * Keyboard control using WASD for directional keys, and 1,2,Q for generic buttons
     * 
     * @author mike
     */
    private static class LayoutTwo implements KeyListener {

        /**
         * Construct a new keyboard listener
         * 
         * @param player is the HumanPlayer associated with this listener
         */
        public LayoutTwo(HumanPlayer player) {
            this.player = player;
        }

        /**
         * This method is called when the user taps a key. It updates the state variables for the
         * event handler
         * 
         * @param e is the key event
         */
        public void keyTyped(KeyEvent e) {
            if (player.controlledSpacecraft == null) {
                return;
            }
            
            switch (e.getKeyChar()) {
                case 'q':
                    player.controlledSpacecraft.handleButton1Tap();
                    break;
                case '1':
                    player.controlledSpacecraft.handleButton2Tap();
                    break;
                case '2':
                    player.controlledSpacecraft.handleButton3Tap();
                    break;
            }
        }

        /**
         * This method is called when the user holds down a key. It updates the state variables for
         * the event handler
         * 
         * @param e is the key event
         */
        public void keyPressed(KeyEvent e) {
            if (player.controlledSpacecraft == null) {
                return;
            }
            
            switch (e.getKeyChar()) {
                case 'w':
                    player.controlledSpacecraft.handleUp(true);
                    break;
                case 's':
                    player.controlledSpacecraft.handleDown(true);
                    break;
                case 'a':
                    player.controlledSpacecraft.handleLeft(true);
                    break;
                case 'd':
                    player.controlledSpacecraft.handleRight(true);
                    break;
            }
        }

        /**
         * This method is called when the user releases a key. It updates the state variables for
         * the event handler
         * 
         * @param e is the key event
         */
        public void keyReleased(KeyEvent e) {
            if (player.controlledSpacecraft == null) {
                return;
            }
            
            switch (e.getKeyChar()) {
                case 'w':
                    player.controlledSpacecraft.handleUp(false);
                    break;
                case 's':
                    player.controlledSpacecraft.handleDown(false);
                    break;
                case 'a':
                    player.controlledSpacecraft.handleLeft(false);
                    break;
                case 'd':
                    player.controlledSpacecraft.handleRight(false);
                    break;
            }
        }


        private HumanPlayer player;
    }
}