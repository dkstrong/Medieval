package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by daniel on 11/13/15.
 */
public class Arrival implements Behavior{

	private Vector2 force = new Vector2();

	public SteerAgent agent;
	public final Vector2 target = new Vector2();
	public float slowingRadiusSqr = 1.5f;
	@Override
	public void update(float delta) {

		force.set(target).sub(agent.getLocation());
		float distSqr = force.len2();
		force.nor().scl(agent.getMaxSpeed());

		if(distSqr < slowingRadiusSqr)
		{
			// inside the slowing area, scale down the force
			force.scl(distSqr / slowingRadiusSqr);
			force.sub(agent.getVelocity()).scl(0.5f);
		}else{
			force.sub(agent.getVelocity()).scl(0.5f);
		}

		//System.out.println(UtMath.round(force,2));


	}

	@Override
	public Vector2 getForce() {
		return force;
	}

}
