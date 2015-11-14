package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * evades a moving SteeringAgent
 *
 * Created by daniel on 11/13/15.
 */
public class Evade implements Behavior{

	private Vector3 steeringOut = new Vector3();

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

		Vector3 targetLocation = target.getFutureLocation(delta);
		// calculate speed difference to see how far ahead we need to leed
		float speedDiff = target.getMaxSpeed() - agent.getMaxSpeed();
		float desiredSpeed = (target.getMaxSpeed() + speedDiff) * delta;
		Vector3 targetVelocity = temp1.set(target.getVelocity()).scl(desiredSpeed);
		Vector3 projectedLocation = targetVelocity.add(targetLocation); // projectedLocation = targetLocation + targetVelocity
		Vector3 desierdVel = projectedLocation.sub(agent.getLocation()).nor().scl(agent.getMaxSpeed());
		Vector3 steering = desierdVel.sub(agent.getVelocity()).scl(-1f);  // negate the direction
		steeringOut.set(steering);
	}

	@Override
	public Vector3 getForce() {
		return steeringOut;
	}
}
