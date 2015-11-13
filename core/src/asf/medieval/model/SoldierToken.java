package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import asf.medieval.ai.Vehicle;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class SoldierToken implements Token {
	Scenario scenario;

	public final Vector3 location = new Vector3();
	public float boundingRadius = 1;
	public float height = 7.5f;

	protected VehicleInternal agent;


	@Override
	public void init(Scenario scenario) {
		this.scenario = scenario;
		agent = new VehicleInternal();

	}


	@Override
	public void update(float delta) {
		if (agent != null) {

			agent.updateVelocity(scenario.steerGraph, delta);

			Vector3 velocity = agent.getVelocity();

			if (!velocity.equals(Vector3.Zero)) {
				System.out.println("velocity: "+velocity);
				//if (!canStepIntoOtherAgents && agent.isObstructed(tpf)) {
				//        setVelocity(velocity.negate());
				//}

				location.add(velocity.cpy().scl(delta));

				//Quaternion rotTo = spatial.getLocalRotation().clone();
				//rotTo.lookAt(velocity.normalize(), Vector3f.UNIT_Y);
				//spatial.setLocalRotation(rotTo);
			}


			//check for close to target then kill the target command
			if (agent.getStaticTarget() != null) {
				if (agent.getLocation().dst2(agent.getStaticTarget()) < UtMath.sqr(0.1f)) {
					clearTarget();
				}
			} else if (agent.getTarget() != null) {
				if (agent.getLocation().dst2(agent.getTarget().getLocation()) < UtMath.sqr(0.1f)) {
					clearTarget();
				}
			}
		}
	}


	public void clearTarget() {
		agent.clearTarget();
	}

	public void setTarget(Vector3 staticTarget) {
		agent.setTarget(staticTarget);
	}

	public void setTarget(SteerAgent agentTarget) {
		agent.setTarget(agentTarget);
	}

	protected class VehicleInternal extends Vehicle {

		public Vector3 getLocation() {
			return SoldierToken.this.location;
		}

	}
}
