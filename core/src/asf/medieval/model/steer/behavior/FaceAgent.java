package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.model.Token;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/19/15.
 */
public class FaceAgent implements PostBehavior {
	public Token token;

	public SteerController target;

	public FaceAgent(Token token, SteerController target) {
		this.token = token;
		this.target = target;
	}

	private Vector2 temp = new Vector2();
	@Override
	public void update(float delta) {


		token.direction = temp.set(target.getLocation()).sub(token.location).nor().angleRad(Vector2.Y);

	}
}
