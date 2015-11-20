package asf.medieval.model.steer.behavior;

import asf.medieval.model.Token;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/19/15.
 */
public class FaceVelocity implements PostBehavior {
	public Token token;

	public FaceVelocity(Token token) {
		this.token = token;
	}

	private Vector2 temp = new Vector2();
	@Override
	public void update(float delta) {
		if(token.agent.getVelocity().len2() > 1)
		{
			//  look direction of movement
			token.direction = temp.set(token.agent.getVelocity()).nor().angleRad(Vector2.Y);
		}
	}
}
