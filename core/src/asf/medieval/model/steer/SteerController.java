package asf.medieval.model.steer;

import asf.medieval.model.Token;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Danny on 11/13/2015.
 */
public strictfp abstract class SteerController {

	public Token token;
	public final StrictPoint maxSpeed = new StrictPoint("7");
	public final StrictPoint maxTurnForce = new StrictPoint("10");
	public final StrictPoint mass = new StrictPoint("0.4");
	public final StrictPoint avoidanceRadius = new StrictPoint("1");

	public final StrictVec2 velocity = new StrictVec2();

	public SteerController(Token token) {
		this.token = token;
	}

	public abstract void update(StrictPoint delta);

	public StrictVec2 getVelocity(){
		return velocity;
	}

	public StrictVec2 getVelocity(StrictPoint delta) {
		return velocity.cpy().scl(delta);
	}

	public StrictVec2 getLocation(){
		return token.location;
	}

	public StrictVec2 getFutureLocation(StrictPoint delta){
		return getLocation().cpy().add(getVelocity(delta));
	}

	public StrictPoint getAvoidanceRadius(){
		return avoidanceRadius;
	}

	public StrictPoint getMaxSpeed(){
		return maxSpeed;
	}

	public StrictPoint getMaxTurnForce(){
		return maxTurnForce;
	}
}
