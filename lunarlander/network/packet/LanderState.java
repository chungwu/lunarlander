package lunarlander.network.packet;

import java.io.Serializable;

import lunarlander.gameobject.NetworkLunarLander;
import lunarlander.util.Vect2D;

public class LanderState implements Serializable {

	public LanderState(NetworkLunarLander lander) {
		id = lander.getId();
		totalMass = lander.getMass();
		throttle = lander.getThrottle();
		angle = lander.getAngle();
		desiredAngle = lander.getDesiredAngle();
		status = lander.getStatus();
        position = lander.getPosition();
        momentum = lander.getMomentum();
	}

	public int id;
	public double totalMass;
	public double throttle;
	public double angle;
	public double desiredAngle;
	public int status;
    public Vect2D position;
    public Vect2D momentum;
}
