package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/13/15.
 */
public class Blend implements Behavior{

	private Vector3 steeringOut = new Vector3();

	public SteerAgent agent;

	public Array<Behavior> behaviors = new Array<Behavior>();


	@Override
	public void update(float delta) {
		steeringOut.set(0,0,0);
		for (Behavior behavior : behaviors) {
			behavior.update(delta);
			steeringOut.add(behavior.getForce());
		}

	}

	@Override
	public Vector3 getForce() {
		return steeringOut;
	}
}
