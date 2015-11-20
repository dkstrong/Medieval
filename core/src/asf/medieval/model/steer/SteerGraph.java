package asf.medieval.model.steer;

import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Danny on 11/13/2015.
 */
public class SteerGraph {
	public final Array<SteerController> agents = new Array<SteerController>(true, 32, SteerController.class);

	public SteerGraph() {
	}


	public boolean isObstructed(SteerController me, float tpf) {

		Vector2 futureLoc = me.getFutureLocation(tpf);

		if (futureLoc.equals(me.getLocation())) {
			//not moving, done here
			return false;
		}


		for (SteerController agent : agents) {
			if (agent == me) {
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
