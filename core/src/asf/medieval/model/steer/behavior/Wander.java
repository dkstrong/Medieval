package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/13/15.
 */
public class Wander implements Behavior {

	private Vector2 force = new Vector2();

	public SteerController agent;
	public float circleDistance = 10;
	public float circleRadius = 1;
	public float angleChange;

	private final Vector2 displacement = new Vector2();
	private float wanderAngle;

	public Wander() {
		// TODO: need to get random instance from scenario or this wont be deterministic
		angleChange = MathUtils.PI2 * 0.1f;
		wanderAngle = MathUtils.random.nextFloat() * MathUtils.PI2;
	}

	@Override
	public void update(float delta) {
		// Calculate the circle center
		force.set(agent.getVelocity()).nor().scl(circleDistance);

		// Calculate the displacement force
		displacement.set(0,-1).scl(circleRadius);

		// Randomly change the vector direction  by making it change its current angle
		setAngle(displacement, wanderAngle);

		// Finally calculate and return the wander force
		force.add(displacement);

		// Change wanderAngle just a bit, so it won't have the same value in the  next game frame.
		wanderAngle += MathUtils.random.nextFloat() * angleChange *delta;


	}


	private static void setAngle(Vector2 vector, float value) {
		float len = vector.len();
		vector.x = (float)Math.cos(value) * len;
		vector.y = (float)Math.sin(value) * len;
	}

	@Override
	public Vector2 getForce() {
		return force;
	}
}
