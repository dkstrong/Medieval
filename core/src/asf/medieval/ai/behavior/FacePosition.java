package asf.medieval.ai.behavior;

import asf.medieval.model.Token;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/19/15.
 */
public class FacePosition implements PostBehavior {
	public Token token;

	public Vector2 target;

	public FacePosition(Token token) {
		this.token = token;
	}

	private Vector2 temp = new Vector2();
	@Override
	public void update(float delta) {
		token.direction = temp.set(target).sub(token.location).nor().angleRad(Vector2.Y);
	}
}
