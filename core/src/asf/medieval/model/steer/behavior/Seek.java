package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * Seek static target
 *
 * Created by daniel on 11/13/15.
 */
public strictfp class Seek implements Behavior{

	private StrictVec2 force = new StrictVec2();

	public SteerController agent;
	public final StrictVec2 target = new StrictVec2();

	@Override
	public void update(StrictPoint delta) {
		force.set(target).sub(agent.getLocation());
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());

	}

	@Override
	public StrictVec2 getForce() {
		return force;
	}
}
