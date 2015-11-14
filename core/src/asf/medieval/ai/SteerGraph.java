package asf.medieval.ai;

import asf.medieval.ai.behavior.Behavior;
import asf.medieval.utility.JmePlane;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 11/13/2015.
 */
public class SteerGraph {
	public final Array<SteerAgent> agents = new Array<SteerAgent>(true, 32, SteerAgent.class);

	public SteerGraph() {
	}


	public boolean isObstructed(SteerAgent me, float tpf) {

		Vector3 futureLoc = me.getFutureLocation(tpf);

		if (futureLoc.equals(me.getLocation())) {
			//not moving, done here
			return false;
		}


		for (SteerAgent agent : agents) {
			if (agent == this) {
				continue; //cant obstruct yourself
			}

			//TODO: should i consider the distance of the other agents future location instead of current? does it matter?

			if (futureLoc.dst2(agent.getLocation()) < UtMath.sqr(me.getAvoidanceRadius() + agent.getAvoidanceRadius())) {
				return true;
			}

		}

		return false;
	}

}
