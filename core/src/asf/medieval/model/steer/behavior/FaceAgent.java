package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.model.Token;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class FaceAgent implements PostBehavior {
	public Token token;

	public SteerController target;

	public FaceAgent(Token token, SteerController target) {
		this.token = token;
		this.target = target;
	}

	private static final StrictVec2 temp = new StrictVec2();

	@Override
	public void update(StrictPoint delta) {

		// token.direction = (target.location - token.location).nor().angleRad();
		temp.set(target.getLocation()).sub(token.location).nor().angleRad(token.direction);

	}
}
