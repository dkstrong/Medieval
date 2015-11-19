package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * flees from a static target
 *
 * Created by daniel on 11/13/15.
 */
public class Flee implements Behavior{

	private Vector2 force = new Vector2();

	public SteerAgent agent;
	public final Vector2 target = new Vector2();

	@Override
	public void update(float delta) {
		force.set(agent.getLocation()).sub(target);
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	@Override
	public Vector2 getForce() {
		return force;
	}
}