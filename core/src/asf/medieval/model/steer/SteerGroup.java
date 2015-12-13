package asf.medieval.model.steer;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/14/15.
 */
public strictfp class SteerGroup {

	public final Array<SteerController> agents = new Array<SteerController>(true, 32, SteerController.class);

	public final StrictVec2 location = new StrictVec2();
	public final StrictPoint rotation = new StrictPoint();

}
