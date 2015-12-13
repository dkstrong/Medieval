package asf.medieval.model.steer.behavior;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by Danny on 11/13/2015.
 */
public strictfp interface Behavior {

	public void update(StrictPoint delta);

	public StrictVec2 getForce();
}
