package asf.medieval.ai;

import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

/**
 * Created by Danny on 11/13/2015.
 */
public abstract class Vehicle implements SteerAgent {

	private float maxSpeed;
	private float maxTurnForce;// = 2;
	private float mass;// = 1;
	private float avoidanceRadius;
	private final Vector3 velocity = new Vector3();
	//
	//private SteerGraph steerGraph;
	private Behavior[] behavior;
	private Vector3 staticTarget;
	private SteerAgent agentTarget;

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

	private final Vector3 force = new Vector3();

	public void updateVelocity(SteerGraph steerGraph, float delta) {


		if (staticTarget != null) {
			force.set(steerGraph.calcSteeringForce(this, staticTarget, delta, behavior));
		} else if (agentTarget != null) {
			force.set(steerGraph.calcSteeringForce(this, agentTarget, delta, behavior));
		} else {
			force.set(0, 0, 0);
		}

		truncate(force, maxTurnForce * delta);

		if (mass < Float.MIN_VALUE) {
			UtMath.scaleAdd(force.nor(), maxSpeed, Vector3.Zero);
			//force.nor().scaleAdd(maxSpeed, Vector3.Zero);
			velocity.set(force);
		} else {
			Vector3 acceleration = force.scl(1f/mass);
			velocity.add(acceleration);
			truncate(velocity, maxSpeed);
		}


	}

	/**
	 * truncate the length of the vector to the given limit
	 */
	private void truncate(Vector3 source, float limit) {

		if (source.len2() <= UtMath.sqr(limit)) {
			//return source;
		} else {
			UtMath.scaleAdd(source.nor(), limit, Vector3.Zero);
			//source.nor().scaleAdd(limit, Vector3.Zero);
		}
	}

	public SteerAgent getTarget() {
		return agentTarget;
	}

	public Vector3 getStaticTarget() {
		return staticTarget;
	}

	public void clearTarget() {
		this.behavior = null;
		this.staticTarget = null;
		this.agentTarget = null;
		this.velocity.set(0, 0, 0);
	}

	public void setTarget(Vector3 staticTarget) {
		setTarget(staticTarget, Behavior.Seek, Behavior.Avoid, Behavior.Separation);
	}

	public void setTarget(SteerAgent agentTarget) {
		setTarget(agentTarget, Behavior.Persuit, Behavior.Avoid, Behavior.Separation);
	}

	public void setTarget(Vector3 staticTarget, Behavior... behavior) {
		this.behavior = behavior;
		this.staticTarget = staticTarget;
		agentTarget = null;
	}

	public void setTarget(SteerAgent agentTarget, Behavior... behavior) {
		this.behavior = behavior;
		this.agentTarget = agentTarget;
		staticTarget = null;
	}

	public boolean isObstructed(SteerGraph steerGraph, float tpf) {

		Vector3 futureLoc = getFutureLocation(tpf);

		if (futureLoc.equals(getLocation())) {
			//not moving, done here
			return false;
		}

		List<SteerAgent> agents = steerGraph.getAgents();

		for (SteerAgent agent : agents) {
			if (agent == this) {
				continue; //cant obstruct yourself
			}

			//TODO: should i consider the distance of the other agents future location instead of current? does it matter?

			if (futureLoc.dst2(agent.getLocation()) < UtMath.sqr(getAvoidanceRadius() + agent.getAvoidanceRadius())) {
				return true;
			}

		}

		return false;
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public Vector3 getVelocity(float delta) {
		Vector3 velCpy = velocity.cpy();
		velCpy.scl(delta);
		//velCpy.rotation *= delta;
		return velCpy;
	}

	public void setVelocity(Vector3 velocity) {
		this.velocity.set(velocity);
	}


	@Override
	public Vector3 getFutureLocation(float tpf) {

		Vector3 loc = getLocation().cpy();
		Vector3 velocityScaled = getVelocity(tpf);

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