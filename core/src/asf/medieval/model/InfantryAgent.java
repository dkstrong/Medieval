package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import asf.medieval.ai.behavior.Arrival;
import asf.medieval.ai.behavior.Avoid;
import asf.medieval.ai.behavior.Behavior;
import asf.medieval.ai.behavior.Blend;
import asf.medieval.ai.behavior.Pursuit;
import asf.medieval.ai.behavior.Seek;
import asf.medieval.ai.behavior.Separation;
import asf.medieval.ai.behavior.Wander;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by daniel on 11/18/15.
 */
public class InfantryAgent implements SteerAgent {

	public Token token;

	public float maxSpeed = 7f;
	public float maxTurnForce = 10;
	public float mass = 0.4f;
	public float avoidanceRadius = 1f;
	public final Vector3 velocity = new Vector3();
	public Behavior behavior;

	private final Vector3 force = new Vector3();

	public InfantryAgent(Token token) {
		this.token = token;
	}

	public void update(float delta)
	{
		if(behavior != null)
		{
			behavior.update(delta);
			force.set(behavior.getForce());
			force.y =0;

		}
		else
		{
			force.set(0,0,0);
		}


		if (mass < Float.MIN_VALUE) {
			force.nor().scl(maxSpeed);
			//UtMath.scaleAdd(force.nor(), maxSpeed, Vector3.Zero);
			//force.nor().scaleAdd(maxSpeed, Vector3.Zero);
			velocity.set(force);
		} else {
			UtMath.truncate(force, maxTurnForce * delta);
			force.scl(1f/mass);
			UtMath.truncate(velocity.add(force), maxSpeed);
		}


		if (!velocity.equals(Vector3.Zero)) {
			//System.out.println("velocity: "+velocity);
			//if (!canStepIntoOtherAgents && agent.isObstructed(tpf)) {
			//        setVelocity(velocity.negate());
			//}
			token.location.mulAdd(velocity, delta);

			//Quaternion rotTo = spatial.getLocalRotation().clone();
			//rotTo.lookAt(velocity.normalize(), Vector3f.UNIT_Y);
			//spatial.setLocalRotation(rotTo);
		}
		token.location.y = token.scenario.heightField.getElevation(token.location);
	}

	public void clearTarget() {
		behavior = null;
	}

	public void setTarget(Vector3 staticTarget) {

		Seek seek = new Seek();
		seek.agent = this;
		seek.target.set(staticTarget);

		Arrival arrival = new Arrival();
		arrival.agent = this;
		arrival.target.set(staticTarget);

		Wander wander = new Wander();
		wander.agent = this;

		Avoid avoid = new Avoid();
		avoid.agent = this;
		avoid.nearbyAgents = token.scenario.steerGraph.agents;

		Separation separation = new Separation();
		separation.agent = this;
		separation.nearbyAgents = token.scenario.steerGraph.agents;

		Blend blend = new Blend();
		blend.agent = this;
		blend.add(arrival,0.25f);
		blend.add(avoid,4.25f);
		blend.add(separation,4f);
		blend.calcWeights();

		behavior = blend;
	}

	public void setTarget(SteerAgent agentTarget) {
		Pursuit seek = new Pursuit();
		seek.agent = this;
		seek.target = agentTarget;

		Avoid avoid = new Avoid();
		avoid.agent = this;
		avoid.nearbyAgents = token.scenario.steerGraph.agents;

		Separation separation = new Separation();
		separation.agent = this;
		separation.nearbyAgents = token.scenario.steerGraph.agents;

		Blend blend = new Blend();
		blend.agent = this;
		blend.add(seek,1);
		blend.add(avoid,1);
		blend.add(separation,1);

		behavior = blend;
	}

	public void setTargetAttack(SteerAgent agentTarget)
	{
		Pursuit seek = new Pursuit();
		seek.agent = this;
		seek.target = agentTarget;

		Avoid avoid = new Avoid();
		avoid.agent = this;
		avoid.nearbyAgents = token.scenario.steerGraph.agents;

		Separation separation = new Separation();
		separation.agent = this;
		separation.nearbyAgents = token.scenario.steerGraph.agents;

		Blend blend = new Blend();
		blend.agent = this;
		blend.add(seek,1);
		blend.add(avoid,1);
		blend.add(separation,1);

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
	public Vector3 getLocation() { return token.location; }

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
