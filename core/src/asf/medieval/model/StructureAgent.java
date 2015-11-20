package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/18/15.
 */
public class StructureAgent implements SteerAgent {

	public Token token;

	public float mass = 10f;
	public float avoidanceRadius = 1f;


	public StructureAgent(Token token) {
		this.token = token;
		avoidanceRadius = token.shape.radius;
	}

	public void update(float delta)
	{

	}

	@Override
	public Vector2 getVelocity() {
		return Vector2.Zero;
	}

	public Vector2 getVelocity(float delta) {
		return Vector2.Zero.cpy().scl(delta);
	}

	@Override
	public Vector2 getLocation() { return token.location; }

	@Override
	public Vector2 getFutureLocation(float delta) {
		return getLocation().cpy().add(getVelocity(delta));
	}

	@Override
	public float getAvoidanceRadius() {
		return avoidanceRadius;
	}

	@Override
	public float getMaxSpeed() {
		return 0;
	}

	@Override
	public float getMaxTurnForce() {
		return 0;
	}
}
