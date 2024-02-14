package lunarlander.gameobject;

import lunarlander.network.packet.LanderState;
import lunarlander.player.NetworkPlayer;
import lunarlander.player.Player;
import lunarlander.util.Vect2D;

public class NetworkLunarLander extends ArmedLunarLander {

	public NetworkLunarLander(int id, Vect2D initialPosition, Vect2D initialVelocity,
			double initialAngle, Player player, boolean canFireSmallRockets,
			boolean canFireBigRockets, boolean canFireDrones) {
		super(initialPosition, initialVelocity, initialAngle, player,
				canFireSmallRockets, canFireBigRockets, canFireDrones);
		this.id = id;
		// TODO Auto-generated constructor stub
	}
    
    public NetworkLunarLander(NetworkPlayer player, LanderState state) {
        this(player.getId(), null, null, 0, player, true, true, true);
        update(state);
    }

	public int getId() {
		return id;
	}
    
    public void update(LanderState newState) {
        totalMass = newState.totalMass;
        throttle = newState.throttle;
        angle = newState.angle;
        desiredAngle = newState.desiredAngle;
        status = newState.status;
        position = newState.position;
        momentum = newState.momentum;
    }
	
	private int id;
}
