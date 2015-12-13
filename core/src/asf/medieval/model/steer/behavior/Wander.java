package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/13/15.
 */
public strictfp class Wander implements Behavior {

	private StrictVec2 force = new StrictVec2();

	public SteerController agent;
	public final StrictPoint circleDistance = new StrictPoint(10);
	public final StrictPoint circleRadius = new StrictPoint(1);
	public final StrictPoint angleChange = new StrictPoint(0);

	private final StrictVec2 displacement = new StrictVec2();
	private final StrictPoint wanderAngle = new StrictPoint();

	private static final StrictPoint TEMP_POINT = new StrictPoint();
	private static final StrictPoint TEMP_POINT2 = new StrictPoint();

	public Wander() {
		angleChange.set(StrictPoint.PI2).mul("0.1");
		wanderAngle.set(StrictPoint.PI2).mul("0.5"); // TODO: wanderAngle = PI2 * randomFloat, but provided from StrictRand in the Scenario
	}

	@Override
	public void update(StrictPoint delta) {
		// Calculate the circle center
		force.set(agent.getVelocity()).nor().scl(circleDistance);

		// Calculate the displacement force
		displacement.set("0","-1").scl(circleRadius);

		// Randomly change the vector direction  by making it change its current angle
		setAngle(displacement, wanderAngle);

		// Finally calculate and return the wander force
		force.add(displacement);

		// Change wanderAngle just a bit, so it won't have the same value in the  next game frame.

		// TODO: TEMP_POINT should be set to randomFloat instead of "0.5", provided from StrictRand
		TEMP_POINT.set("0.5").mul(angleChange).mul(delta);
		wanderAngle.add(TEMP_POINT);


	}



	private static void setAngle(StrictVec2 vector, StrictPoint value) {
		StrictPoint len = vector.len(TEMP_POINT);

		vector.x.set(TEMP_POINT2.set(value).cos().mul(len)); // x = cos(value) * len
		vector.y.set(TEMP_POINT2.set(value).sin().mul(len)); // y = sin(value) * len
	}

	@Override
	public StrictVec2 getForce() {
		return force;
	}
}
