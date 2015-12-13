package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 *  pursues a moving SteeringAgent
 *
 * Created by daniel on 11/13/15.
 */
public strictfp class Pursuit implements Behavior{

	public SteerController agent;
	public SteerController target;

	private StrictVec2 force = new StrictVec2();

	private static final StrictPoint tempPoint = new StrictPoint();

	@Override
	public void update(StrictPoint delta) {
		newVersion(delta);
	}

	private void newVersion(StrictPoint delta)
	{
		StrictPoint distance = agent.getLocation().dst(target.getLocation(),tempPoint);
		StrictPoint t = distance.div(agent.getMaxSpeed());

		StrictVec2 targetLocation = target.getFutureLocation(t);

		// does a basic seek to the future location of the target
		force.set(targetLocation).sub(agent.getLocation());
		force.nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	private void oldVersion(StrictPoint delta)
	{
		StrictVec2 targetLocation = target.getFutureLocation(delta);
		// calculate speed difference to see how far ahead we need to leed
		StrictPoint speedDiff = tempPoint.set(target.getMaxSpeed()).sub(agent.getMaxSpeed());
		StrictPoint desiredSpeed = speedDiff.add(target.getMaxSpeed()).mul(delta);

		force.set(target.getVelocity()).scl(desiredSpeed);
		force.add(targetLocation);
		force.sub(agent.getLocation()).nor().scl(agent.getMaxSpeed());
		force.sub(agent.getVelocity());
	}

	@Override
	public StrictVec2 getForce() {
		return force;
	}
}
