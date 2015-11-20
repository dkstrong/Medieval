package asf.medieval.model.steer;

import asf.medieval.model.Token;
import asf.medieval.model.steer.SteerController;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/18/15.
 */
public class StructureController extends SteerController {
	public float mass = 10f;
	public float avoidanceRadius = 1f;


	public StructureController(Token token) {
		this.token = token;
		mass = 10f;
		avoidanceRadius = token.shape.radius;
	}

	public void update(float delta)
	{

	}

}
