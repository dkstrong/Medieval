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

	public final Vector3 staticTargetLocation = new Vector3();

	/**
	 * Flee is simply the inverse of seek and acts to steer the character so that its velocity is radially aligned away from the target.
	 * <br> The desired velocity points in the opposite direction.
	 *
	 * @author Brent Owens
	 */
	@Override
	public void update(float delta) {
		Vector3 temp1 = new Vector3();
		Vector3 desierdVel = temp1.set(staticTargetLocation).sub(agent.getLocation()).nor().scl(agent.getMaxSpeed());
		Vector3 steering = desierdVel.sub(agent.getVelocity()).scl(-1f); // negate flee
		force.set(steering);
	}

	@Override
	public Vector3 getForce() {
		return force;
	}
}
