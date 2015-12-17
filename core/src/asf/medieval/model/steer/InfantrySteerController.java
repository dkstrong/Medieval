package asf.medieval.model.steer;

import asf.medieval.model.Token;
import asf.medieval.model.steer.SteerController;
import asf.medieval.model.steer.behavior.Arrive;
import asf.medieval.model.steer.behavior.ArriveCombat;
import asf.medieval.model.steer.behavior.Avoid;
import asf.medieval.model.steer.behavior.Behavior;
import asf.medieval.model.steer.behavior.Blend;
import asf.medieval.model.steer.behavior.FaceAgent;
import asf.medieval.model.steer.behavior.FaceVelocity;
import asf.medieval.model.steer.behavior.PostBehavior;
import asf.medieval.model.steer.behavior.Pursuit;
import asf.medieval.model.steer.behavior.Seek;
import asf.medieval.model.steer.behavior.Separation;
import asf.medieval.model.steer.behavior.Wander;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/18/15.
 */
public strictfp class InfantrySteerController extends SteerController {

	public Behavior behavior;
	public PostBehavior postBehavior;

	public InfantrySteerController(Token token) {
		super(token);
		maxSpeed.set("7");
		maxTurnForce.set("10");
		mass.set("0.5");
		avoidanceRadius.set("1");
	}

	private static final StrictVec2 force = new StrictVec2();

	private static final StrictPoint tempPoint = new StrictPoint();

	public void update(StrictPoint delta) {

		if (behavior != null) {
			behavior.update(delta);
			force.set(behavior.getForce());

			if (mass.lessThanOrEqual(StrictPoint.MIN_ROUNDING_ERROR)) {
				// very little mass, instantly hit max speed
				//velocity.set(force).nor().scl(maxSpeed);//.scl(maxSpeed);
				velocity.set(force).nor().scl(maxSpeed);
			} else {
				// use mass to calculate acceleration
				// TODO: should i multiply by delta here?
				StrictPoint maxTurnForceDelta = tempPoint.set(maxTurnForce).mul(delta);
				force.truncate(maxTurnForceDelta);
				force.div(mass);// convert to acceleration
				velocity.add(force).truncate(maxSpeed);
			}

			//System.out.println("final velocity: " +velocity +" ("+velocity.len(new StrictPoint())+")");

			if (!velocity.equals(StrictVec2.Zero)) {
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
			force.setZero();
			velocity.setZero();
		}

		if(postBehavior!=null)
			postBehavior.update(delta);


	}

	public void clearTarget() {
		behavior = null;
	}

	public void setTarget(StrictVec2 staticTarget) {

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
		blend.add(arrive, "1");
		blend.add(avoid, "8");
		blend.add(separation, "10");
		//blend.normalizeWeights();

		behavior = blend;


		postBehavior = new FaceVelocity(token);
	}

	public void setTarget(SteerController agentTarget) {
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
		blend.add(seek, "1");
		blend.add(avoid, "1");
		blend.add(separation, "1");
		//blend.normalizeWeights();

		behavior = blend;

		postBehavior = new FaceVelocity(token);
	}

	public void setCombatTarget(SteerController agentTarget) {
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
		blend.add(arrive, "1");
		blend.add(avoid, "4");

		behavior = null;

		postBehavior = new FaceAgent(token, agentTarget);

	}

	public void setDeath(StrictVec2 deathLocation) {
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
		blend.add(arrive, "1");
		blend.add(avoid, "4");

		behavior = null;

		postBehavior = new FaceVelocity(token);

	}

}
