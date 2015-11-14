package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import asf.medieval.utility.JmePlane;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.List;

/**
 *
 * avoid obstacles (SteeringAgents with velocity == Zero)
 *
 * Created by daniel on 11/13/15.
 */
public class Avoid implements Behavior{

	private Vector3 steeringOut = new Vector3();

	public SteerAgent agent;

	public Array<SteerAgent> nearbyAgents = new Array<SteerAgent>(false, 16, SteerAgent.class);

	private JmePlane tempPlane = new JmePlane();



	@Override
	public void update(float delta) {
		Vector3 temp1= new Vector3();
		float cautionRange = agent.getMaxSpeed() / agent.getMaxTurnForce();  // " speed / turnSpeed"
		cautionRange = cautionRange + agent.getAvoidanceRadius();

		// TODO: libgdx plane should be able to do this
		// but the math is slightly different and i wont to test with the Jme Plane first
		JmePlane plane = tempPlane;
		plane.normal.set(agent.getVelocity());
		plane.constant = 1;

		Array<SteerAgent> obstacles = getObstacles(agent, 5);

		for (SteerAgent obstacle : obstacles) {
			if (obstacle == agent) {
				continue;
			}

			Vector3 relativeLocation = temp1.set(obstacle.getLocation()).sub(agent.getLocation());
			if (plane.whichSide(relativeLocation) != JmePlane.Side.Positive) {
				//me.setObstacleCautionRange(cautionRange, false);
				//continue; //obstacle is behind me, ignore it
			}

			float d2 = agent.getLocation().dst2(obstacle.getLocation());
			if (d2 < UtMath.sqr(cautionRange + obstacle.getAvoidanceRadius())) {
				//me.setObstacleCautionRange(cautionRange, true);

				float len2 = relativeLocation.len2();
				Vector3 force = relativeLocation.nor();
				//return force.negate().mult(1 / len2).mult(0.125f);
				steeringOut.set(force.scl(-1f).scl(1 / len2).scl(obstacle.getAvoidanceRadius() / agent.getAvoidanceRadius()));
				return;

			} else {
				//me.setObstacleCautionRange(cautionRange, false);
			}


		}


		// no obstacles to avoid, no force to apply..
		steeringOut.set(0, 0, 0);
	}

	private Array<SteerAgent> getObstacles(SteerAgent me, float withinRadius) {
		Array<SteerAgent> nearbyObstacles = new Array<SteerAgent>();
		nearbyObstacles.clear();

		float r2 = UtMath.sqr(withinRadius);

		for (SteerAgent agent : nearbyAgents) {
			if (agent == me) {
				continue;
			} else if (!agent.getVelocity().equals(Vector3.Zero)) {
				continue;
			}

			float d2 = me.getLocation().dst2(agent.getLocation());

			if (d2 < r2) // if it is within the radius
			{
				nearbyObstacles.add(agent);
			}
		}

		return nearbyObstacles;

	}

	@Override
	public Vector3 getForce() {
		return steeringOut;
	}


	/**
	 * Obstacle avoidance behavior gives a character the ability to maneuver in a cluttered environment by dodging around obstacles. There is an important distinction between obstacle avoidance
	 * and flee behavior. Flee will always cause a character to steer away from a given location, whereas obstacle avoidance takes action only when a nearby obstacle lies directly in front of the
	 * character.
	 *
	 * The implementation of obstacle avoidance behavior here will make a simplifying assumption that both the character and obstacle can be reasonably approximated as spheres.
	 *
	 * Keep in mind that this relates to obstacle avoidance not necessarily to collision detection.
	 *
	 * The goal of the behavior is to keep an imaginary cylinder of free space in front of the character. The cylinder lies along the character’s forward axis, has a diameter equal to the
	 * character’s bounding sphere, and extends from the character’s center for a distance based on the character’s speed and agility. An obstacle further than this distance away is not an
	 * immediate threat.
	 *
	 * @author Brent Owens
	 */
	private Vector3 calculateAvoidForceOld(SteerAgent me, List<SteerAgent> obstacles, float tpf) {
		// a turn force less than the speed will increase the range of the
		// collision cylinder. If the turn force is larger than the speed,
		// then the cylinder. This is just a rough, linear, approximation
		// of the distance needed to avoid a collision.
		float cautionRange = me.getMaxSpeed() / me.getMaxTurnForce() * tpf;  // " speed / turnSpeed"

		JmePlane plane = new JmePlane();
		plane.normal.set(me.getVelocity());
		plane.constant =1;

		float r1 = cautionRange + me.getAvoidanceRadius();
		// assuming obsticals are ordered from closest to farthest
		for (SteerAgent obstacle : obstacles) {
			if (obstacle == me) {
				continue;
			}
			Vector3 temp1 = new Vector3();
			Vector3 relativeLocation = temp1.set(obstacle.getLocation()).sub(me.getLocation());

			if (plane.whichSide(relativeLocation) != JmePlane.Side.Positive) {

				continue; // if it is behind, ignore it
			}


			//UtLog.fine.log(" %s < : %s", relativeLocation.len2(), (r1 + obstical.getAvoidanceRadius()) * (r1 + obstical.getAvoidanceRadius()));


			// if it is at least in the radius of the collision cylinder
			if (relativeLocation.len2() < (r1 + obstacle.getAvoidanceRadius()) * (r1 + obstacle.getAvoidanceRadius())) {
				// check cylinder collision

				// Project onto the back-plane(defined using the velocity vector as the normal for the plane)
				// and test the radius width intersection
				Vector3 projPoint = plane.getClosestPoint(relativeLocation);



				if (projPoint.len2() < (me.getAvoidanceRadius() + obstacle.getAvoidanceRadius()) * (me.getAvoidanceRadius() + obstacle.getAvoidanceRadius())) {
					// we have a collision.
					// negate the side-up projection we used to check the collision
					// and use that for steering
					//System.err.println("found collision, apply force: " + relativeLocation.scl(-1));
					return relativeLocation.scl(-1f);
				}
			}
		}



		return Vector3.Zero; // no collision
	}
}
