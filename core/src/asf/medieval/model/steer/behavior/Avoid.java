package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.utility.JmePlane;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.List;

/**
 *
 * avoid obstacles (SteeringAgents with velocity == Zero)
 *
 *
 * http://gamedevelopment.tutsplus.com/tutorials/understanding-steering-behaviors-collision-avoidance--gamedev-7777
 *
 * Created by daniel on 11/13/15.
 */
public class Avoid implements Behavior{

	private Vector2 steeringOut = new Vector2();

	public SteerController agent;

	public Array<SteerController> nearbyAgents = new Array<SteerController>(false, 16, SteerController.class);

	private JmePlane tempPlane = new JmePlane();



	@Override
	public void update(float delta) {
		Vector2 temp1= new Vector2();
		float cautionRange = agent.getMaxSpeed() / agent.getMaxTurnForce();  // " speed / turnSpeed"
		cautionRange = cautionRange + agent.getAvoidanceRadius();

		// TODO: libgdx plane should be able to do this
		// but the math is slightly different and i wont to test with the Jme Plane first
		JmePlane plane = tempPlane;
		plane.normal.set(agent.getVelocity().x,0,agent.getVelocity().y);
		plane.constant = 1;

		Array<SteerController> obstacles = getObstacles(agent, 5);

		for (SteerController obstacle : obstacles) {
			if (obstacle == agent) {
				continue;
			}

			Vector2 relativeLocation = temp1.set(obstacle.getLocation()).sub(agent.getLocation());
			Vector3 relLoc3d = new Vector3(relativeLocation.x,0,relativeLocation.y);
			if (plane.whichSide(relLoc3d) != JmePlane.Side.Positive) {
				//me.setObstacleCautionRange(cautionRange, false);
				//continue; //obstacle is behind me, ignore it
			}

			float d2 = agent.getLocation().dst2(obstacle.getLocation());
			if (d2 < UtMath.sqr(cautionRange + obstacle.getAvoidanceRadius())) {
				//me.setObstacleCautionRange(cautionRange, true);

				float len2 = relativeLocation.len2();
				Vector2 force = relativeLocation.nor();
				//return force.negate().mult(1 / len2).mult(0.125f);
				steeringOut.set(force.scl(-1f).scl(1 / len2).scl(obstacle.getAvoidanceRadius() / agent.getAvoidanceRadius()));
				return;

			} else {
				//me.setObstacleCautionRange(cautionRange, false);
			}


		}


		// no obstacles to avoid, no force to apply..
		steeringOut.set(0, 0);
	}

	private Array<SteerController> getObstacles(SteerController me, float withinRadius) {
		Array<SteerController> nearbyObstacles = new Array<SteerController>();
		nearbyObstacles.clear();

		float r2 = UtMath.sqr(withinRadius);

		for (SteerController agent : nearbyAgents) {
			if (agent == me) {
				continue;
			} else if (!agent.getVelocity().equals(Vector2.Zero)) {
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
	public Vector2 getForce() {
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
	private Vector2 calculateAvoidForceOld(SteerController me, List<SteerController> obstacles, float tpf) {
		// a turn force less than the speed will increase the range of the
		// collision cylinder. If the turn force is larger than the speed,
		// then the cylinder. This is just a rough, linear, approximation
		// of the distance needed to avoid a collision.
		float cautionRange = me.getMaxSpeed() / me.getMaxTurnForce() * tpf;  // " speed / turnSpeed"

		JmePlane plane = new JmePlane();
		plane.normal.set(me.getVelocity().x,0,me.getVelocity().y);
		plane.constant =1;

		float r1 = cautionRange + me.getAvoidanceRadius();
		// assuming obsticals are ordered from closest to farthest
		for (SteerController obstacle : obstacles) {
			if (obstacle == me) {
				continue;
			}
			Vector2 temp1 = new Vector2();
			Vector2 relativeLocation = temp1.set(obstacle.getLocation()).sub(me.getLocation());
			Vector3 relLoc3d = new Vector3(relativeLocation.x,0,relativeLocation.y);

			if (plane.whichSide(relLoc3d) != JmePlane.Side.Positive) {

				continue; // if it is behind, ignore it
			}


			//UtLog.fine.log(" %s < : %s", relativeLocation.len2(), (r1 + obstical.getAvoidanceRadius()) * (r1 + obstical.getAvoidanceRadius()));


			// if it is at least in the radius of the collision cylinder
			if (relativeLocation.len2() < (r1 + obstacle.getAvoidanceRadius()) * (r1 + obstacle.getAvoidanceRadius())) {
				// check cylinder collision

				// Project onto the back-plane(defined using the velocity vector as the normal for the plane)
				// and test the radius width intersection
				Vector3 projPoint = plane.getClosestPoint(relLoc3d);



				if (projPoint.len2() < (me.getAvoidanceRadius() + obstacle.getAvoidanceRadius()) * (me.getAvoidanceRadius() + obstacle.getAvoidanceRadius())) {
					// we have a collision.
					// negate the side-up projection we used to check the collision
					// and use that for steering
					//System.err.println("found collision, apply force: " + relativeLocation.scl(-1));
					return relativeLocation.scl(-1f);
				}
			}
		}



		return Vector2.Zero; // no collision
	}
}
