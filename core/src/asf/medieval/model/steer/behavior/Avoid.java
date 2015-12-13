package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPlane;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import asf.medieval.strictmath.StrictVec3;
import com.badlogic.gdx.utils.Array;

/**
 *
 * avoid obstacles (SteeringAgents with velocity == Zero)
 *
 *
 * http://gamedevelopment.tutsplus.com/tutorials/understanding-steering-behaviors-collision-avoidance--gamedev-7777
 *
 * Created by daniel on 11/13/15.
 */
public strictfp class Avoid implements Behavior{

	private StrictVec2 steeringOut = new StrictVec2();

	public SteerController agent;
	public Array<SteerController> nearbyAgents = new Array<SteerController>(false, 16, SteerController.class);

	private static final StrictPlane tempPlane = new StrictPlane();
	private static final StrictPoint cautionRange = new StrictPoint();
	private static final StrictVec2 temp1= new StrictVec2();
	private static final StrictVec3 tempVec3d = new StrictVec3();
	private static final StrictPoint d2 = new StrictPoint();
	private static final StrictPoint avoidance2 = new StrictPoint();
	private static final StrictPoint len2 = new StrictPoint();

	@Override
	public void update(StrictPoint delta) {
		cautionRange.set(agent.getMaxSpeed()).div(agent.getMaxTurnForce()); // " speed / turnSpeed"
		cautionRange.add(agent.getAvoidanceRadius());

		StrictPlane plane = tempPlane;

		plane.normal.set(
			agent.getVelocity().x,
			StrictPoint.ZERO,
			agent.getVelocity().y);
		plane.constant.set(StrictPoint.ONE);

		Array<SteerController> obstacles = getObstacles(agent, StrictPoint.FIVE);

		for (SteerController obstacle : obstacles) {
			if (obstacle == agent) {
				continue;
			}

			StrictVec2 relativeLocation = temp1.set(obstacle.getLocation()).sub(agent.getLocation());
			StrictVec3 relLoc3d = tempVec3d.set(relativeLocation.x, StrictPoint.ZERO, relativeLocation.y);

			if (plane.whichSide(relLoc3d) != StrictPlane.Side.Positive) {
				//me.setObstacleCautionRange(cautionRange, false);
				//continue; //obstacle is behind me, ignore it
			}

			agent.getLocation().dst2(obstacle.getLocation(), d2);
			// avoidance2 = sqr(cautionRange + obstacleRaidus)
			avoidance2.set(cautionRange).add(obstacle.getAvoidanceRadius()).sqr();

			if (d2.val < avoidance2.val) {
				//me.setObstacleCautionRange(cautionRange, true);

				relativeLocation.len2(len2);
				StrictVec2 force = relativeLocation;

				//force.nor().negate().scl(1 / len2).scl(obstacleRadius / agentRadius);
				//force.nor().negate().scl(1 / len2).scl(0.125);
				steeringOut.set(force).nor().negate().
					div(len2).
					scl(obstacle.getAvoidanceRadius()).
					div(agent.getAvoidanceRadius());
				return;

			} else {
				//me.setObstacleCautionRange(cautionRange, false);
			}


		}

		// no obstacles to avoid, no force to apply..
		steeringOut.setZero();
	}

	private static final StrictPoint r2 = new StrictPoint();
	private static final Array<SteerController> nearbyObstacles = new Array<SteerController>();
	private static final StrictPoint tempD2 = new StrictPoint();

	private Array<SteerController> getObstacles(SteerController me, StrictPoint withinRadius) {
		nearbyObstacles.clear();
		r2.set(withinRadius).sqr();

		for (SteerController agent : nearbyAgents) {
			if (agent == me) {
				continue;
			} else if (!agent.getVelocity().equals(StrictVec2.Zero)) {
				continue;
			}

			me.getLocation().dst2(agent.getLocation(), tempD2);
			if (tempD2.val < r2.val) // if it is within the radius
			{
				nearbyObstacles.add(agent);
			}
		}

		return nearbyObstacles;

	}

	@Override
	public StrictVec2 getForce() {
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
	private static final StrictPoint r1 = new StrictPoint();
	private static final StrictVec3 projPoint = new StrictVec3();
	private static  final StrictPoint nonPredictedAvoidance2 = new StrictPoint();
	private void calculateAvoidForceOld(StrictPoint delta) {
		// a turn force less than the speed will increase the range of the
		// collision cylinder. If the turn force is larger than the speed,
		// then the cylinder. This is just a rough, linear, approximation
		// of the distance needed to avoid a collision.
		cautionRange.set(agent.getMaxSpeed()).div(agent.getMaxTurnForce()); // " speed / turnSpeed"
		cautionRange.mul(delta);

		StrictPlane plane = tempPlane;

		plane.normal.set(
			agent.getVelocity().x,
			StrictPoint.ZERO,
			agent.getVelocity().y);
		plane.constant.set(StrictPoint.ONE);

		r1.set(cautionRange).add(agent.getAvoidanceRadius());

		Array<SteerController> obstacles = getObstacles(agent, StrictPoint.FIVE);
		// TODO: sort obstacles from closest to furtherst..
		// assuming obsticals are ordered from closest to farthest
		for (SteerController obstacle : obstacles) {
			if (obstacle == agent) {
				continue;
			}
			StrictVec2 relativeLocation = temp1.set(obstacle.getLocation()).sub(agent.getLocation());
			StrictVec3 relLoc3d = tempVec3d.set(relativeLocation.x, StrictPoint.ZERO, relativeLocation.y);

			if (plane.whichSide(relLoc3d) != StrictPlane.Side.Positive) {
				continue; // if it is behind, ignore it
			}
			relativeLocation.len2(d2);
			// if it is at least in the radius of the collision cylinder
			avoidance2.set(r1).add(obstacle.getAvoidanceRadius()).sqr();
			if (d2.val < avoidance2.val) {
				// check cylinder collision

				// Project onto the back-plane(defined using the velocity vector as the normal for the plane)
				// and test the radius width intersection
				plane.getClosestPoint(relLoc3d,projPoint);
				projPoint.len2(len2);
				nonPredictedAvoidance2.set(agent.getAvoidanceRadius()).add(obstacle.getAvoidanceRadius()).sqr();
				if (len2.val < nonPredictedAvoidance2.val) {
					// we have a collision.
					// negate the side-up projection we used to check the collision
					// and use that for steering
					//System.err.println("found collision, apply force: " + relativeLocation.scl(-1));
					steeringOut.set(relativeLocation).negate();
					return;
				}
			}
		}

		// no collision
		steeringOut.setZero();
	}
}
