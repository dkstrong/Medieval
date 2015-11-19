package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import asf.medieval.utility.UtMath;
import asf.medieval.shape.Box;
import asf.medieval.shape.Shape;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class StructureToken implements Token, SteerAgent {
	private Scenario scenario;
	public Player owner;
	public int id;

	public final Vector3 location = new Vector3();

	public Shape shape;

	public float mass = 0.4f;
	public float avoidanceRadius = 9f;

	@Override
	public void init(Scenario scenario) {
		this.scenario = scenario;
		location.y = scenario.heightField.getElevation(location);

		float width = 9.5f;
		float height = 10f;
		float depth = 10.2f;
		shape = new Box( width, height, depth, width*0.05f, height, -depth*0.75f);
		avoidanceRadius = UtMath.largest(width,depth);
		mass= 10f;

	}

	@Override
	public void update(float delta) {

	}

	@Override
	public Vector3 getVelocity() {
		return Vector3.Zero;
	}

	public Vector3 getVelocity(float delta) {
		return Vector3.Zero.cpy().scl(delta);
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
		return 0;
	}

	@Override
	public float getMaxTurnForce() {
		return 0;
	}
}
