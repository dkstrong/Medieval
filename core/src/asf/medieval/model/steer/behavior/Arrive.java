package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/13/15.
 */
public strictfp class Arrive implements Behavior{

	private StrictVec2 force = new StrictVec2();

	public SteerController agent;
	public final StrictVec2 target = new StrictVec2();
	public final StrictPoint slowingRadiusSqr = new StrictPoint("1.5");

	private static final StrictPoint distSqr = new StrictPoint();
	private static final StrictPoint forceScl = new StrictPoint("0.5");
	@Override
	public void update(StrictPoint delta) {

		force.set(target).sub(agent.getLocation());
		force.len2(distSqr);
		force.nor().scl(agent.getMaxSpeed());

		if(distSqr.val < slowingRadiusSqr.val)
		{
			// inside the slowing area, scale down the force
			force.scl(distSqr.div(slowingRadiusSqr));
			force.sub(agent.getVelocity()).scl(forceScl);
		}else{
			force.sub(agent.getVelocity()).scl(forceScl);
		}

		//System.out.println(UtMath.round(force,2));


	}

	@Override
	public StrictVec2 getForce() {
		return force;
	}

}
