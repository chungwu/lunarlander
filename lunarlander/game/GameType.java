package lunarlander.game;

public enum GameType {

    ALL {
        public String toString() {
            return "All";
        }
        
        public String getPrefix() {
            return "all.";
        }
    },
    
    SINGLE {
        public String toString() {
            return "Single Player";
        }
        
        public String getPrefix() {
            return "single.";
        }
    },
    
    DUO {
        public String toString() {
            return "Duo Players";
        }
        
        public String getPrefix() {
            return "duo.";
        }
    },
    
    DEATHMATCH {
        public String toString() {
            return "Deathmatch";
        }
        
        public String getPrefix() {
            return "network.";
        }
    },
    
    TEAM_DEATHMATCH {
        public String toString() {
            return "Team Deathmatch";
        }
        
        public String getPrefix() {
            return "network.";
        }
    },
    
    CTF {
        public String toString() {
            return "Capture the Flag";
        }
        
        public String getPrefix() {
            return "network.";
        }
    };
    
    public abstract String getPrefix();
    
    public static final GameType[] NETWORK_GAMETYPES = {DEATHMATCH, TEAM_DEATHMATCH, CTF};
}
