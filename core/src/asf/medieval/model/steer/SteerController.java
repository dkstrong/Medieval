package asf.medieval.model.steer;

import asf.medieval.model.Token;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Danny on 11/13/2015.
 */
public abstract class SteerController {

	public Token token;
	public float maxSpeed = 7f;
	public float maxTurnForce = 10;
	public float mass = 0.4f;
	public float avoidanceRadius = 1f;

	public final Vector2 velocity = new Vector2();

	public abstract void update(float delta);

	public Vector2 getVelocity(){
		return velocity;
	}

	public Vector2 getVelocity(float delta) {
		return velocity.cpy().scl(delta);
	}

	public Vector2 getLocation(){
		return token.location;
	}

	public Vector2 getFutureLocation(float delta){
		return getLocation().cpy().add(getVelocity(delta));
	}

	public float getAvoidanceRadius(){
		return avoidanceRadius;
	}

	public float getMaxSpeed(){
		return maxSpeed;
	}

	public float getMaxTurnForce(){
		return maxTurnForce;
	}
}
