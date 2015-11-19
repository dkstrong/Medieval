package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by daniel on 11/13/15.
 */
public class Arrival implements Behavior{

	private Vector3 force = new Vector3();

	public SteerAgent agent;
	public final Vector3 target = new Vector3();
	public float slowingRadiusSqr = 1f;
	@Override
	public void update(float delta) {

		force.set(target).sub(agent.getLocation());
		float distSqr = force.len2();
		force.nor().scl(agent.getMaxSpeed());

		if(distSqr < slowingRadiusSqr)
		{
			// inside the slowing area, scale down the force
			force.scl(distSqr/slowingRadiusSqr);
			force.set(agent.getVelocity()).scl(-3f);
		}

		force.sub(agent.getVelocity());

	}

	@Override
	public Vector3 getForce() {
		return force;
	}
}
