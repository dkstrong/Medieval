package asf.medieval.ai;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/14/15.
 */
public class SteerGroup {

	public final Array<SteerAgent> agents = new Array<SteerAgent>(true, 32, SteerAgent.class);

	public final Vector3 location = new Vector3();
	public final Vector3 rotation = new Vector3();

}
