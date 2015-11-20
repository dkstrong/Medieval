package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * Seek static target
 *
 * Created by daniel on 11/13/15.
 */
public class Seek implements Behavior{

	private Vector2 force = new Vector2();

	public SteerController agent;
	public final Vector2 target = new Vector2();

	@Override
	public void update(float delta) {
		force.set(target).sub(agent.getLocation());
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	@Override
	public Vector2 getForce() {
		return force;
	}
}
