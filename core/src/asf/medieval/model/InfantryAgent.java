package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import asf.medieval.ai.behavior.Arrive;
import asf.medieval.ai.behavior.ArriveCombat;
import asf.medieval.ai.behavior.Avoid;
import asf.medieval.ai.behavior.Behavior;
import asf.medieval.ai.behavior.Blend;
import asf.medieval.ai.behavior.FaceAgent;
import asf.medieval.ai.behavior.FaceVelocity;
import asf.medieval.ai.behavior.PostBehavior;
import asf.medieval.ai.behavior.Pursuit;
import asf.medieval.ai.behavior.Seek;
import asf.medieval.ai.behavior.Separation;
import asf.medieval.ai.behavior.Wander;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/18/15.
 */
public class InfantryAgent implements SteerAgent {

	public Token token;

	public float maxSpeed = 7f;
	public float maxTurnForce = 10;
	public float mass = 0.4f;
	public float avoidanceRadius = 1f;
	public final Vector2 velocity = new Vector2();
	public Behavior behavior;
	public PostBehavior postBehavior;


	public InfantryAgent(Token token) {
		this.token = token;
	}

	private final Vector2 force = new Vector2();

	public void update(float delta) {
		if (behavior != null) {
			behavior.update(delta);
			force.set(behavior.getForce());

			if (mass < Float.MIN_VALUE) {
				// very little mass, instantly hit max speed
				velocity.set(force.nor().scl(maxSpeed));
			} else {
				// use mass to calculate acceleration
				// TODO: should i multiply by delta here?
				UtMath.truncate(force, maxTurnForce * delta);
				force.scl(1f / mass); // convert to acceleration
				UtMath.truncate(velocity.add(force), maxSpeed);
			}

			if (!velocity.equals(Vector2.Zero)) {
				//System.out.println("velocity: "+velocity);
				//if (!canStepIntoOtherAgents && agent.isObstructed(tpf)) {
				//        setVelocity(velocity.negate());
				//}
				token.location.mulAdd(velocity, delta);

				//Quaternion rotTo = spatial.getLocalRotation().clone();
				//rotTo.lookAt(velocity.normalize(), Vector3f.UNIT_Y);
				//spatial.setLocalRotation(rotTo);

			}

		} else {
			force.set(0, 0);
			velocity.set(0,0);
		}

		if(postBehavior!=null)
			postBehavior.update(delta);


	}

	public void clearTarget() {
		behavior = null;
	}

	public void setTarget(Vector2 staticTarget) {

		Seek seek = new Seek();
		seek.agent = this;
		seek.target.set(staticTarget);

		Arrive arrive = new Arrive();
		arrive.agent = this;
		arrive.target.set(staticTarget);

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
		blend.add(arrive, 1f);
		blend.add(avoid, 8f);
		blend.add(separation, 10f);
		//blend.normalizeWeights();

		behavior = blend;


		postBehavior = new FaceVelocity(token);
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
		blend.add(seek, 1);
		blend.add(avoid, 1);
		blend.add(separation, 1);
		//blend.normalizeWeights();

		behavior = blend;

		postBehavior = new FaceVelocity(token);
	}

	public void setCombatTarget(SteerAgent agentTarget) {
		ArriveCombat arrive = new ArriveCombat();
		arrive.agent = this;
		arrive.targetAgent = agentTarget;

		Avoid avoid = new Avoid();
		avoid.agent = this;
		avoid.nearbyAgents = token.scenario.steerGraph.agents;

		Separation separation = new Separation();
		separation.agent = this;
		separation.nearbyAgents = token.scenario.steerGraph.agents;

		Blend blend = new Blend();
		blend.agent = this;
		blend.add(arrive, 1);
		blend.add(avoid, 4);

		behavior = null;

		postBehavior = new FaceAgent(token, agentTarget);

	}

	public void setDeath(Vector2 deathLocation) {
		Arrive arrive = new Arrive();
		arrive.agent = this;
		arrive.target.set(deathLocation);

		Avoid avoid = new Avoid();
		avoid.agent = this;
		avoid.nearbyAgents = token.scenario.steerGraph.agents;

		Separation separation = new Separation();
		separation.agent = this;
		separation.nearbyAgents = token.scenario.steerGraph.agents;

		Blend blend = new Blend();
		blend.agent = this;
		blend.add(arrive, 1);
		blend.add(avoid, 4);

		behavior = null;

		postBehavior = new FaceVelocity(token);

	}

	@Override
	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getVelocity(float delta) {
		return velocity.cpy().scl(delta);
	}


	@Override
	public Vector2 getLocation() {
		return token.location;
	}

	@Override
	public Vector2 getFutureLocation(float delta) {
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
