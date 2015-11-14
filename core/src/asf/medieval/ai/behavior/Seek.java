package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * Seek static target
 *
 * Created by daniel on 11/13/15.
 */
public class Seek implements Behavior{

	private Vector3 force = new Vector3();

	public SteerAgent agent;

	public final Vector3 staticTargetLocation = new Vector3();

	public float destinationTolerance = 1f;


	@Override
	public void update(float delta) {
		Vector3 temp1 = new Vector3();
		Vector3 desierdVel = temp1.set(staticTargetLocation).sub(agent.getLocation()).nor().scl(agent.getMaxSpeed());

		force.set(desierdVel).sub(agent.getVelocity());

		// zero the force if within a certain distance, as not to do the wavery thing
		if (agent.getLocation().dst2(staticTargetLocation) < UtMath.sqr(destinationTolerance)){
			force.scl(0,0,0);
		}

	}

	@Override
	public Vector3 getForce() {
		return force;
	}
}
