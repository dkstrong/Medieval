package asf.medieval.model.steer;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Danny on 11/13/2015.
 */
public strictfp class SteerGraph {
	public final Array<SteerController> agents = new Array<SteerController>(true, 32, SteerController.class);

	public SteerGraph() {
	}

	private static final StrictPoint tempPoint1 = new StrictPoint();
	private static final StrictPoint tempPoint2 = new StrictPoint();

	public boolean isObstructed(SteerController me, StrictPoint delta) {

		StrictVec2 futureLoc = me.getFutureLocation(delta);

		if (futureLoc.equals(me.getLocation())) {
			//not moving, done here
			return false;
		}


		for (SteerController agent : agents) {
			if (agent == me) {
				continue; //cant obstruct yourself
			}

			//TODO: should i consider the distance of the other agents future location instead of current? does it matter?
			StrictPoint futureDist2 = futureLoc.dst2(agent.getLocation(), tempPoint1);
			StrictPoint avoidance2 = tempPoint2.set(me.getAvoidanceRadius()).add(agent.getAvoidanceRadius()).sqr();

			if (futureDist2.val < avoidance2.val) {
				return true;
			}

		}

		return false;
	}

}
