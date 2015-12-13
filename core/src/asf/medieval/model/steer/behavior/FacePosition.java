package asf.medieval.model.steer.behavior;

import asf.medieval.model.Token;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class FacePosition implements PostBehavior {
	public Token token;

	public StrictVec2 target;

	public FacePosition(Token token) {
		this.token = token;
	}

	private static final StrictVec2 temp = new StrictVec2();

	@Override
	public void update(StrictPoint delta) {
		// token.direction = (target - token.location).nor().angleRad();
		temp.set(target).sub(token.location).nor().angleRad(StrictVec2.Y, token.direction);

	}
}
