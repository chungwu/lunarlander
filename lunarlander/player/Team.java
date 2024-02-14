package lunarlander.player;

import java.awt.Color;

public enum Team {
    RED {
        public String toString() {
            return "Red";
        }
        
        public Color getColor() {
            return red;
        }
    },
    
    GREEN {
        public String toString() {
            return "Green";
        }
        
        public Color getColor() {
            return Color.green;
        }
    };
    
    public abstract Color getColor();
    
    private static final Color red = new Color(250, 50, 0);
}
