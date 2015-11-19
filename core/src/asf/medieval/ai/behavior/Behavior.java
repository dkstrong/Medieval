package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Danny on 11/13/2015.
 */
public interface Behavior {


	public void update(float delta);

	public Vector2 getForce();
}
