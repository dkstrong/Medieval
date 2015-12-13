package asf.medieval.model.steer;

import asf.medieval.model.Token;
import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/18/15.
 */
public strictfp class StructureController extends SteerController {

	public StructureController(Token token) {
		super(token);
		mass.set("10");
		avoidanceRadius.set(token.mi.shape.radius);
	}

	public void update(StrictPoint delta)
	{

	}

}
