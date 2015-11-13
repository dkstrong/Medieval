package asf.medieval.ai;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by Danny on 11/13/2015.
 */
public interface SteerAgent {

	public Vector3 getVelocity();

	public Vector3 getLocation();

	/**
	 * used with prediction steering like persue and evade
	 *
	 * @param tpf
	 * @return
	 */
	public Vector3 getFutureLocation(float tpf);

	/**
	 * used with obstacle avoidance steering
	 *
	 * @return
	 */
	public float getAvoidanceRadius();

	public float getMaxSpeed();

	public float getMaxTurnForce();
}
