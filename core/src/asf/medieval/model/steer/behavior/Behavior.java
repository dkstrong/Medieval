package asf.medieval.model.steer.behavior;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Danny on 11/13/2015.
 */
public interface Behavior {


	public void update(float delta);

	public Vector2 getForce();
}
