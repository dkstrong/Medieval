package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector3;

/**
 * flees from a static target
 *
 * Created by daniel on 11/13/15.
 */
public class Flee implements Behavior{

	private Vector3 force = new Vector3();

	public SteerAgent agent;
	public final Vector3 target = new Vector3();

	@Override
	public void update(float delta) {
		force.set(agent.getLocation()).sub(target);
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	@Override
	public Vector3 getForce() {
		return force;
	}
}