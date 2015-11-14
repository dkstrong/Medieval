package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import asf.medieval.ai.Vehicle;
import asf.medieval.ai.behavior.*;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class SoldierToken implements Token, SteerAgent {
	Scenario scenario;

	public final Vector3 location = new Vector3();
	public float radius = 1;
	public float height = 7.5f;

	private float maxSpeed = 6.5f;
	private float maxTurnForce = 10;
	private float mass = 0.4f;
	private float avoidanceRadius = 1f;
	private final Vector3 velocity = new Vector3();

	public Behavior behavior;


	@Override
	public void init(Scenario scenario) {
		this.scenario = scenario;

	}

	@Override
	public void update(float delta) {
		updateVelocityAndLocation(delta);
	}

	private final Vector3 force = new Vector3();

	private void updateVelocityAndLocation(float delta)
	{
		location.add(force);
	}

	private void updateVelocityAndLocationOld(float delta)
	{
		if(behavior != null)
		{
			behavior.update(delta);
			force.set(behavior.getForce());
		}
		else
		{
			force.set(0,0,0);
		}

		UtMath.truncate(force, maxTurnForce * delta);


		if (mass < Float.MIN_VALUE) {
			UtMath.scaleAdd(force.nor(), maxSpeed, Vector3.Zero);
			//force.nor().scaleAdd(maxSpeed, Vector3.Zero);
			velocity.set(force);
		} else {
			Vector3 acceleration = force.scl(1f/mass);
			velocity.add(acceleration);
			UtMath.truncate(velocity, maxSpeed);
		}


		if (!velocity.equals(Vector3.Zero)) {
			//System.out.println("velocity: "+velocity);
			//if (!canStepIntoOtherAgents && agent.isObstructed(tpf)) {
			//        setVelocity(velocity.negate());
			//}

			location.add(velocity.cpy().scl(delta));

			//Quaternion rotTo = spatial.getLocalRotation().clone();
			//rotTo.lookAt(velocity.normalize(), Vector3f.UNIT_Y);
			//spatial.setLocalRotation(rotTo);
		}
		location.y = 0;
	}


	public void clearTarget() {
		behavior = null;
	}

	public void setTarget(Vector3 staticTarget) {

		Seek seek = new Seek();
		seek.agent = this;
		seek.staticTargetLocation.set(staticTarget);

		Avoid avoid = new Avoid();
		avoid.agent = this;
		avoid.nearbyAgents = scenario.steerGraph.agents;

		Separation separation = new Separation();
		separation.agent = this;
		separation.nearbyAgents = scenario.steerGraph.agents;

		Blend blend = new Blend();
		blend.agent = this;
		blend.behaviors.addAll(seek, avoid, separation);

		behavior = blend;
	}

	public void setTarget(SteerAgent agentTarget) {
		Pursuit seek = new Pursuit();
		seek.agent = this;
		seek.target = agentTarget;

		Avoid avoid = new Avoid();
		avoid.agent = this;
		avoid.nearbyAgents = scenario.steerGraph.agents;

		Separation separation = new Separation();
		separation.agent = this;
		separation.nearbyAgents = scenario.steerGraph.agents;

		Blend blend = new Blend();
		blend.agent = this;
		blend.behaviors.addAll(seek, avoid, separation);

		behavior = blend;
	}


	@Override
	public Vector3 getVelocity() {
		return velocity;
	}

	public Vector3 getVelocity(float delta) {
		return velocity.cpy().scl(delta);
	}

	@Override
	public Vector3 getLocation() {
		return location;
	}

	@Override
	public Vector3 getFutureLocation(float delta) {
		return getLocation().cpy().add(getVelocity(delta));
	}

	@Override
	public float getAvoidanceRadius() {
		return avoidanceRadius;
	}

	@Override
	public float getMaxSpeed() {
		return maxSpeed;
	}

	@Override
	public float getMaxTurnForce() {
		return maxTurnForce;
	}
}
