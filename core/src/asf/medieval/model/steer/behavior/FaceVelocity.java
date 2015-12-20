package asf.medieval.model.steer.behavior;

import asf.medieval.model.Token;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class FaceVelocity implements PostBehavior {
	public Token token;

	public FaceVelocity(Token token) {
		this.token = token;
	}

	private StrictVec2 temp = new StrictVec2();

	private static final StrictPoint len2 = new StrictPoint();

	@Override
	public void update(StrictPoint delta) {
		if(token.agent.getVelocity().len2(len2).val > 1)
		{
			//  look direction of movement
			//temp.set(token.agent.getVelocity()).nor().angleRad(StrictVec2.Y, token.direction);
			temp.set(token.agent.getVelocity()).nor().angleRad(token.direction);
		}
	}
}
