package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.List;

/**
 * avoid neighbors (SteeringAgents with velocity != Zero)
 *
 * Created by daniel on 11/13/15.
 */
public class Separation implements Behavior{


	private Vector2 steeringOut = new Vector2();

	public SteerAgent agent;

	public Array<SteerAgent> nearbyAgents = new Array<SteerAgent>(false, 16, SteerAgent.class);

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

	private Vector2 temp1 = new Vector2();
	private Vector2 temp2 = new Vector2();
	@Override
	public void update(float delta) {
		Vector2 location = agent.getLocation();
		Vector2 steering = temp1.set(0, 0);

		Array<SteerAgent> neighbours = getNeighbors(agent, 5);

		for (SteerAgent o : neighbours) {
			if (o == agent) {
				continue;
			}

			Vector2 loc = temp2.set(o.getLocation()).sub(location);
			float len2 = loc.len2();
			loc.nor();
			steering.add(loc.scl(-1f).scl(1f / len2).scl(0.25f));
		}

		steeringOut.set(steering);
	}


	private Array<SteerAgent> getNeighbors(SteerAgent me, float withinRadius) {
		Array<SteerAgent> nearbyNeighbors = new Array<SteerAgent>();
		nearbyNeighbors.clear();

		float r2 = UtMath.sqr(withinRadius);

		for (SteerAgent agent : nearbyAgents) {
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


	@Override
	public Vector2 getForce() {
		return steeringOut;
	}
}
