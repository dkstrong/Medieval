package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/13/15.
 */
public class ArriveCombat implements Behavior{

	private Vector2 force = new Vector2();

	public SteerAgent agent;
	public SteerAgent targetAgent;


	public float slowingRadiusSqr = 1.5f;

	private final Vector2 targetLocation = new Vector2();
	@Override
	public void update(float delta) {

		force.set(targetLocation).sub(agent.getLocation());
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
