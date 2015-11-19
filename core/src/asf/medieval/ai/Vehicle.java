package asf.medieval.ai;

import asf.medieval.ai.behavior.Behavior;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

/**
 * You can extend this class and use it as an easy example
 * of how to calculate velocities using the steering system
 *
 * to move the agent just do
 *
 * vehicle.updateVelocity();
 * agent.location += vehicle.velocity
 *
 * Created by Danny on 11/13/2015.
 */
public abstract class Vehicle implements SteerAgent {

	private float maxSpeed;
	private float maxTurnForce;// = 2;
	private float mass;// = 1;
	private float avoidanceRadius;
	private final Vector2 velocity = new Vector2();
	//
	//private SteerGraph steerGraph;
	public Behavior behavior;

	public Vehicle() {
		this(1.5f, 2.0f, 1.0f, 0.5f);
	}

	public Vehicle(float maxSpeed, float avoidanceRadius) {
		this(maxSpeed, 2, 1, avoidanceRadius);
	}

	public Vehicle(float maxSpeed, float maxTurnForce, float mass, float avoidanceRadius) {
		this.maxSpeed = maxSpeed;
		this.maxTurnForce = maxTurnForce;
		this.mass = mass;
		this.avoidanceRadius = avoidanceRadius;
	}

	private final Vector2 force = new Vector2();
	public void updateVelocity(SteerGraph steerGraph, float delta) {


		if(behavior != null)
		{
			behavior.update(delta);
			force.set(behavior.getForce());
		}
		else
		{
			force.set(0,0);
		}

		UtMath.truncate(force, maxTurnForce * delta);


		if (mass < Float.MIN_VALUE) {
			UtMath.scaleAdd(force.nor(), maxSpeed, Vector2.Zero);
			//force.nor().scaleAdd(maxSpeed, Vector3.Zero);
			velocity.set(force);
		} else {
			Vector2 acceleration = force.scl(1f/mass);
			velocity.add(acceleration);
			UtMath.truncate(velocity, maxSpeed);
		}


	}





	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getVelocity(float delta) {
		Vector2 velCpy = velocity.cpy();
		velCpy.scl(delta);
		//velCpy.rotation *= delta;
		return velCpy;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity.set(velocity);
	}


	@Override
	public Vector2 getFutureLocation(float tpf) {

		Vector2 loc = getLocation().cpy();
		Vector2 velocityScaled = getVelocity(tpf);

		loc.add(velocityScaled);
		//loc.rotation += velocityScaled.rotation;


		return loc;
	}

	@Override
	public float getAvoidanceRadius() {
		return avoidanceRadius;
	}

	public void setAvoidanceRadius(float avoidanceRadius) {
		this.avoidanceRadius = avoidanceRadius;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public float getMaxTurnForce() {
		return maxTurnForce;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public void setMaxTurnForce(float maxTurnForce) {
		this.maxTurnForce = maxTurnForce;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public float getMass() {
		return mass;
	}

}