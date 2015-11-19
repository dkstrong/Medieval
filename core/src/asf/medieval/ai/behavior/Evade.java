package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * evades a moving SteeringAgent
 *
 * Created by daniel on 11/13/15.
 */
public class Evade implements Behavior{

	private Vector2 force = new Vector2();

	public SteerAgent agent;

	public SteerAgent target;

	Vector3 temp1 = new Vector3();
	/**
	 * Evasion is analogous to pursuit, except that flee is used to steer away from the predicted future vec of the target character.
	 *
	 * @author Brent Owens
	 */
	@Override
	public void update(float delta) {

		newVersion(delta);
	}

	private void newVersion(float delta)
	{
		float distance = agent.getLocation().dst(target.getLocation());
		float t = distance / agent.getMaxSpeed();

		Vector2 targetLocation = target.getFutureLocation(t);

		// does a basic flee to the future location of the target
		force.set(agent.getLocation()).sub(targetLocation);
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());

	}

	private void oldVersion(float delta)
	{
		Vector3 temp1 = new Vector3();

		Vector2 targetLocation = target.getFutureLocation(delta);
		// calculate speed difference to see how far ahead we need to leed
		float speedDiff = target.getMaxSpeed() - agent.getMaxSpeed();
		float desiredSpeed = (target.getMaxSpeed() + speedDiff) * delta;

		force.set(target.getVelocity()).scl(desiredSpeed);
		force.add(targetLocation);
		force.sub(agent.getLocation()).nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity()).scl(-1f);
	}


	@Override
	public Vector2 getForce() {
		return force;
	}
}
