package lunarlander.player;

public enum PlayerRole {
    PLAYER {
        public String toString() {
            return "Player";
        }
    },
    
    OBSERVER {
        public String toString() {
            return "Observer";
        }
    }
}
