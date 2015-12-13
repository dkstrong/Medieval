package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * avoid neighbors (SteeringAgents with velocity != Zero)
 *
 * Created by daniel on 11/13/15.
 */
public strictfp class Separation implements Behavior{


	private StrictVec2 steeringOut = new StrictVec2();

	public SteerController agent;

	public Array<SteerController> nearbyAgents = new Array<SteerController>(false, 16, SteerController.class);

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
	 */

	private static final StrictVec2 temp1 = new StrictVec2();
	private static final StrictVec2 temp2 = new StrictVec2();
	private static final StrictPoint len2 = new StrictPoint();
	@Override
	public void update(StrictPoint delta) {
		StrictVec2 location = agent.getLocation();
		StrictVec2 steering = temp1.setZero();

		Array<SteerController> neighbours = getNeighbors(agent, StrictPoint.FIVE);

		for (SteerController o : neighbours) {
			if (o == agent) {
				continue;
			}

			StrictVec2 loc = temp2.set(o.getLocation()).sub(location);
			loc.len2(len2);
			//loc.nor().scl("-1").div(len2).scl("0.25");
			loc.nor().negate().div(len2).scl("0.25");

			// instead of negate if i do scl(1,-1) i can get the "forming a line" effect)
			//loc.nor().scl("-1", "1").div(len2).scl("0.25");
			// if i use the drection vector of the group for the scale parameter,
			// i could use it to make the seperation match the line up behavior...


			steering.add(loc);
		}

		steeringOut.set(steering);
	}

	private static final Array<SteerController> nearbyNeighbors = new Array<SteerController>();
	private static final StrictPoint r2 = new StrictPoint();
	private static final StrictPoint d2 = new StrictPoint();
	private Array<SteerController> getNeighbors(SteerController me, StrictPoint withinRadius) {
		nearbyNeighbors.clear();

		r2.set(withinRadius).sqr();

		for (SteerController agent : nearbyAgents) {
			if (agent == me) {
				continue;
			} else if (agent.getVelocity().equals(StrictVec2.Zero)) {
				continue;
			}else if(agent.token.owner.team != me.token.owner.team){
				//continue;
			}

			me.getLocation().dst2(agent.getLocation(),d2);

			if (d2.val < r2.val) // if it is within the radius
			{
				nearbyNeighbors.add(agent);
			}
		}

		return nearbyNeighbors;

	}


	@Override
	public StrictVec2 getForce() {
		return steeringOut;
	}
}
