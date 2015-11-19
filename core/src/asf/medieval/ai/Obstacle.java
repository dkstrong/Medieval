package asf.medieval.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Danny on 11/13/2015.
 */
public abstract class Obstacle implements SteerAgent{

	private float avoidanceRadius;
	private final Vector2 velocity = new Vector2(0,0);

	public Obstacle(float avoidanceRadius) {
		this.avoidanceRadius = avoidanceRadius;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getFutureLocation(float tpf) {
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
