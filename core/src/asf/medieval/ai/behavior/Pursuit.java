package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector3;

/**
 *  pursues a moving SteeringAgent
 *
 * Created by daniel on 11/13/15.
 */
public class Pursuit implements Behavior{

	public SteerAgent agent;
	public SteerAgent target;

	private Vector3 force = new Vector3();

	@Override
	public void update(float delta) {

		newVersion(delta);

	}

	private void newVersion(float delta)
	{
		float distance = agent.getLocation().dst(target.getLocation());
		float t = distance / agent.getMaxSpeed();

		Vector3 targetLocation = target.getFutureLocation(t);

		// does a basic seek to the future location of the target
		force.set(targetLocation).sub(agent.getLocation());
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	private void oldVersion(float delta)
	{
		Vector3 targetLocation = target.getFutureLocation(delta);
		// calculate speed difference to see how far ahead we need to leed
		float speedDiff = target.getMaxSpeed() - agent.getMaxSpeed();
		float desiredSpeed = (target.getMaxSpeed() + speedDiff) * delta;


		force.set(target.getVelocity()).scl(desiredSpeed);
		force.add(targetLocation);
		force.sub(agent.getLocation()).nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	@Override
	public Vector3 getForce() {
		return force;
	}
}
