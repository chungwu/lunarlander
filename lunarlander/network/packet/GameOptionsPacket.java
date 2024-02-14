package lunarlander.network.packet;
import java.io.Serializable;

import lunarlander.Settings;
import lunarlander.options.*;

public class GameOptionsPacket extends Packet implements Serializable {

    public GameOptionsPacket(CommonNetworkGameOptions gameOptions) {
        super();
        
        gameOptions.saveSettings();
        String prefix = gameOptions.getGameType().getPrefix();
        
        gameType = Settings.getInt(Settings.SELECTED_NETWORK_GAME);
        turbo = Settings.getDouble(prefix + Settings.TURBO);
        safeVelocityX = Settings.getDouble(prefix + Settings.SAFE_VX);
        safeVelocityY = Settings.getDouble(prefix + Settings.SAFE_VY);
        safeAngle = Settings.getDouble(prefix + Settings.SAFE_ANGLE);
        rocketBudget = Settings.getInt(prefix + Settings.ROCKET_BUDGET);
        enableSmallRockets = Settings.getBoolean(prefix + Settings.ENABLE_SMALL_ROCKETS);
        enableBigRockets = Settings.getBoolean(prefix + Settings.ENABLE_BIG_ROCKETS);
        enableDrones = Settings.getBoolean(prefix + Settings.ENABLE_DRONES);
        rocketsDamage = Settings.getBoolean(prefix + Settings.ROCKETS_DAMAGE);
        
        indicateEnemy = Settings.getBoolean(prefix + Settings.INDICATE_ENEMY);
        timeLimit = Settings.getDouble(prefix + Settings.TIME_LIMIT);
        pointLimit = Settings.getInt(prefix + Settings.POINT_LIMIT);
        indicateTeam = Settings.getBoolean(prefix + Settings.INDICATE_TEAMMATE);
        friendlyFire = Settings.getBoolean(prefix + Settings.FRIENDLY_FIRE);
    }

    public String toString() {
        return "GAMEOPTIONS gameType[" + gameType + "]safeX[" + safeVelocityX + "]safeY[" + safeVelocityY + "]smallrockets[" + enableSmallRockets + "]";
    }
    
    public int gameType;
    public double turbo;
    public double safeVelocityX;
    public double safeVelocityY;
    public double safeAngle;
    public int rocketBudget;
    public boolean enableSmallRockets;
    public boolean enableBigRockets;
    public boolean enableDrones;
    public boolean rocketsDamage;
    
    public boolean indicateEnemy;
    public double timeLimit;
    public int pointLimit;
    public boolean indicateTeam;
    public boolean friendlyFire;
}
