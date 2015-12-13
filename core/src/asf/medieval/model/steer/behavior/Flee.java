package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 * flees from a static target
 *
 * Created by daniel on 11/13/15.
 */
public strictfp class Flee implements Behavior{

	private StrictVec2 force = new StrictVec2();

	public SteerController agent;
	public final StrictVec2 target = new StrictVec2();

	@Override
	public void update(StrictPoint delta) {
		force.set(agent.getLocation()).sub(target);
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	@Override
	public StrictVec2 getForce() {
		return force;
	}
}