package asf.medieval.ai;

import asf.medieval.utility.JmePlane;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 11/13/2015.
 */
public class SteerGraph {
	private final List<SteerAgent> agents;

	public SteerGraph() {
		agents = new ArrayList<SteerAgent>(8);
	}


	public void addAgent(SteerAgent agent) {
		this.agents.add(agent);
	}

	public void removeAgent(SteerAgent agent) {
		this.agents.remove(agent);
	}

	/**
	 * do not modify this list!
	 *
	 * @return
	 */
	public List<SteerAgent> getAgents() {
		return agents;
	}

	private Vector3 steeringForce = new Vector3();

	public Vector3 calcSteeringForce(SteerAgent me, Vector3 target, float tpf) {
		return calcSteeringForce(me, target, tpf, Behavior.Seek, Behavior.Avoid, Behavior.Separation);
	}

	public Vector3 calcSteeringForce(SteerAgent me, Vector3 target, float tpf, Behavior... behaviors) {
		steeringForce.set(0, 0, 0);
		for (Behavior behavior : behaviors) {
			switch (behavior) {
				case Seek:
				case Persuit:
					steeringForce.add(calculateSeekForce(me, target));
					break;
				case Flee:
				case Evade:
					steeringForce.add(calculateFleeForce(me, target));
					break;
				case Avoid:
					steeringForce.add(calculateAvoidForceNew(me, getObstacles(me, 5), tpf));
					break;
				case Separation:
					steeringForce.add(calculateSeparationForce(me, getNeighbors(me, 5)));
					break;
				case PassThrough:
					break;
				default:
					throw new AssertionError(behavior.name());
			}
		}

		return steeringForce;
	}

	public Vector3 calcSteeringForce(SteerAgent me, SteerAgent target, float tpf) {
		return calcSteeringForce(me, target, tpf, Behavior.Persuit, Behavior.Avoid, Behavior.Separation);
	}

	public Vector3 calcSteeringForce(SteerAgent me, SteerAgent target, float tpf, Behavior... behaviors) {
		steeringForce.set(0, 0, 0);
		for (Behavior behavior : behaviors) {
			switch (behavior) {
				case Seek:
					steeringForce.add(calculateSeekForce(me, target.getLocation()));
					break;
				case Persuit:
					steeringForce.add(calculatePersuitForce(me, target, tpf));
					break;
				case Flee:
					steeringForce.add(calculateFleeForce(me, target.getLocation()));
					break;
				case Evade:
					steeringForce.add(calculateEvadeForce(me, target, tpf));
					break;
				case Avoid:
					steeringForce.add(calculateAvoidForceNew(me, getObstacles(me, 5), tpf));
					//steeringForce.addLocal(calculateSeparationForce(me, getObstacles(me, 5)));
					break;
				case Separation:
					steeringForce.add(calculateSeparationForce(me, getNeighbors(me, 5)));
					break;
				case PassThrough:
					break;
				default:
					throw new AssertionError(behavior.name());
			}
		}

		return steeringForce;
	}

	private Vector3 temp1 = new Vector3();
	private Vector3 temp2 = new Vector3();
	private List<SteerAgent> tempList = new ArrayList<SteerAgent>(10);

	/**
	 * Pursuit is similar to seek except that the quarry (target) is another moving character.
	 *
	 * <br> Effective pursuit requires a prediction of the target’s future vec. // (Vector3f location,
	 *
	 * @author Brent Owens
	 */
	private Vector3 calculatePersuitForce(SteerAgent me, SteerAgent target, float tpf) {
		Vector3 targetLocation = target.getFutureLocation(tpf);
		// calculate speed difference to see how far ahead we need to leed
		float speedDiff = target.getMaxSpeed() - me.getMaxSpeed();
		float desiredSpeed = (target.getMaxSpeed() + speedDiff) * tpf;

		Vector3 targetVelocity = temp1.set(target.getVelocity()).scl(desiredSpeed);
		Vector3 projectedLocation = targetVelocity.add(targetLocation); // projectedLocation = targetLocation + targetVelocity
		Vector3 desierdVel = projectedLocation.sub(me.getLocation()).nor().scl(me.getMaxSpeed());
		Vector3 steering = desierdVel.sub(me.getVelocity());

		return steering;
	}

	/**
	 * Evasion is analogous to pursuit, except that flee is used to steer away from the predicted future vec of the target character.
	 *
	 * @author Brent Owens
	 */
	private Vector3 calculateEvadeForce(SteerAgent me, SteerAgent target, float tpf) {
		Vector3 targetLocation = target.getFutureLocation(tpf);
		// calculate speed difference to see how far ahead we need to leed
		float speedDiff = target.getMaxSpeed() - me.getMaxSpeed();
		float desiredSpeed = (target.getMaxSpeed() + speedDiff) * tpf;
		Vector3 targetVelocity = temp1.set(target.getVelocity()).scl(desiredSpeed);
		Vector3 projectedLocation = targetVelocity.add(targetLocation); // projectedLocation = targetLocation + targetVelocity
		Vector3 desierdVel = projectedLocation.sub(me.getLocation()).nor().scl(me.getMaxSpeed());
		Vector3 steering = desierdVel.sub(me.getVelocity()).scl(-1f);  // negate the direction
		return steering;
	}

	/**
	 * Seek (or pursuit of a static target) acts to steer the character towards a specified vec in global space.
	 * <br> This behavior adjusts the character so that its velocity is radially aligned towards the target.
	 *
	 * @author Brent Owens
	 */
	private Vector3 calculateSeekForce(SteerAgent me, Vector3 staticTargetLocation) {
		Vector3 desierdVel = temp1.set(staticTargetLocation).sub(me.getLocation()).nor().scl(me.getMaxSpeed());
		Vector3 steering = desierdVel.sub(me.getVelocity());
		return steering;
	}

	/**
	 * Flee is simply the inverse of seek and acts to steer the character so that its velocity is radially aligned away from the target.
	 * <br> The desired velocity points in the opposite direction.
	 *
	 * @author Brent Owens
	 */
	private Vector3 calculateFleeForce(SteerAgent me, Vector3 staticTargetLocation) {
		Vector3 desierdVel = temp1.set(staticTargetLocation).sub(me.getLocation()).nor().scl(me.getMaxSpeed());
		Vector3 steering = desierdVel.sub(me.getVelocity()).scl(-1f); // negate flee
		return steering;
	}

	/**
	 * Separation steering behavior gives a character the ability to maintain a certain separation distance from others nearby. This can be used to prevent characters from crowding together.
	 *
	 * For each nearby character, a repulsive force is computed by subtracting the positions of our character and the nearby character, normalizing, and then applying a 1/r weighting. (That is,
	 * the vec offset vector is scaled by 1/r^2.) Note that 1/r is just a setting that has worked well, not a fundamental value. These repulsive forces for each nearby character are summed
	 * together to produce the overall steering force.
	 *
	 * The supplied neighbours should only be the nearby neighbours in the field of view of the character that is steering. It is good to ignore anything behind the character.
	 *
	 * @author Brent Owens
	 * @author Daniel Strong - modified neighbors loop to make sure "me" doesnt consider itself a neighbor
	 *
	 * @param me
	 * @param neighbours
	 * @return
	 */
	private Vector3 calculateSeparationForce(SteerAgent me, List<SteerAgent> neighbours) {

		Vector3 location = me.getLocation();
		Vector3 steering = temp1.set(0, 0, 0);

		for (SteerAgent o : neighbours) {
			if (o == me) {
				continue;
			}

			Vector3 loc = temp2.set(o.getLocation()).sub(location);
			float len2 = loc.len2();
			loc.nor();
			steering.add(loc.scl(-1f).scl(1f / len2).scl(0.25f));
		}

		return steering;
	}

	private JmePlane tempPlane = new JmePlane();

	private Vector3 calculateAvoidForceNew(SteerAgent me, List<SteerAgent> obstacles, float tpf) {

		float cautionRange = me.getMaxSpeed() / me.getMaxTurnForce();  // " speed / turnSpeed"
		cautionRange = cautionRange + me.getAvoidanceRadius();

		// TODO: libgdx plane should be able to do this
		// but the math is slightly different and i wont to test with the Jme Plane first
		JmePlane plane = tempPlane;
		plane.normal.set(me.getVelocity());
		plane.constant = 1;

		for (SteerAgent obstacle : obstacles) {
			if (obstacle == me) {
				continue;
			}

			Vector3 relativeLocation = temp1.set(obstacle.getLocation()).sub(me.getLocation());
			if (plane.whichSide(relativeLocation) != JmePlane.Side.Positive) {
				//me.setObstacleCautionRange(cautionRange, false);
				//continue; //obstacle is behind me, ignore it
			}

			float d2 = me.getLocation().dst2(obstacle.getLocation());
			if (d2 < UtMath.sqr(cautionRange + obstacle.getAvoidanceRadius())) {
				//me.setObstacleCautionRange(cautionRange, true);

				float len2 = relativeLocation.len2();
				Vector3 force = relativeLocation.nor();
				//return force.negate().mult(1 / len2).mult(0.125f);
				return force.scl(-1f).scl(1 / len2).scl(obstacle.getAvoidanceRadius() / me.getAvoidanceRadius());

			} else {
				//me.setObstacleCautionRange(cautionRange, false);
			}


		}



		return temp1.set(0, 0, 0);
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
	private Vector3 calculateAvoidForce(SteerAgent me, List<SteerAgent> obstacles, float tpf) {
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

	private List<SteerAgent> getObstacles(SteerAgent me, float withinRadius) {
		List<SteerAgent> nearbyObstacles = tempList;
		nearbyObstacles.clear();

		float r2 = UtMath.sqr(withinRadius);

		for (SteerAgent agent : agents) {
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

	private List<SteerAgent> getNeighbors(SteerAgent me, float withinRadius) {
		List<SteerAgent> nearbyNeighbors = tempList;
		nearbyNeighbors.clear();

		float r2 = UtMath.sqr(withinRadius);

		for (SteerAgent agent : agents) {
			if (agent == me) {
				continue;
			} else if (agent.getVelocity().equals(Vector3.Zero)) {
				continue;
			}

			float d2 = me.getLocation().dst2(agent.getLocation());

			if (d2 < r2) // if it is within the radius
			{
				nearbyNeighbors.add(agent);
			}
		}

		return nearbyNeighbors;

	}
}
