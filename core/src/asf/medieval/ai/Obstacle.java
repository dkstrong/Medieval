package asf.medieval.ai;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by Danny on 11/13/2015.
 */
public abstract class Obstacle implements SteerAgent{

	private float avoidanceRadius;
	private final Vector3 velocity = new Vector3(0,0,0);

	public Obstacle(float avoidanceRadius) {
		this.avoidanceRadius = avoidanceRadius;
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public Vector3 getFutureLocation(float tpf) {
		return getLocation();
	}

	public float getAvoidanceRadius() {
		return avoidanceRadius;
	}

	public float getMaxSpeed() {
		return 0;
	}

	public float getMaxTurnForce() {
		return 0;
	}
}
