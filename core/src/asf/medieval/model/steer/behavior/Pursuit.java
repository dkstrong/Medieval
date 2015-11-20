package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import com.badlogic.gdx.math.Vector2;

/**
 *  pursues a moving SteeringAgent
 *
 * Created by daniel on 11/13/15.
 */
public class Pursuit implements Behavior{

	public SteerController agent;
	public SteerController target;

	private Vector2 force = new Vector2();

	@Override
	public void update(float delta) {

		newVersion(delta);

	}

	private void newVersion(float delta)
	{
		float distance = agent.getLocation().dst(target.getLocation());
		float t = distance / agent.getMaxSpeed();

		Vector2 targetLocation = target.getFutureLocation(t);

		// does a basic seek to the future location of the target
		force.set(targetLocation).sub(agent.getLocation());
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	private void oldVersion(float delta)
	{
		Vector2 targetLocation = target.getFutureLocation(delta);
		// calculate speed difference to see how far ahead we need to leed
		float speedDiff = target.getMaxSpeed() - agent.getMaxSpeed();
		float desiredSpeed = (target.getMaxSpeed() + speedDiff) * delta;


		force.set(target.getVelocity()).scl(desiredSpeed);
		force.add(targetLocation);
		force.sub(agent.getLocation()).nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	@Override
	public Vector2 getForce() {
		return force;
	}
}
