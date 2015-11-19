package asf.medieval.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Danny on 11/13/2015.
 */
public interface SteerAgent {

	public void update(float delta);

	public Vector2 getVelocity();

	public Vector2 getLocation();

	/**
	 * used with prediction steering like persue and evade
	 *
	 * @param delta
	 * @return
	 */
	public Vector2 getFutureLocation(float delta);

	/**
	 * used with obstacle avoidance steering
	 *
	 * @return
	 */
	public float getAvoidanceRadius();

	public float getMaxSpeed();

	public float getMaxTurnForce();
}
