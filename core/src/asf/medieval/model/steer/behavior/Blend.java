package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/13/15.
 */
public class Blend implements Behavior{

	private Vector2 force = new Vector2();

	public SteerController agent;

	private Array<WeightedBehavior> weightedBehaviors = new Array<WeightedBehavior>(false, 3, WeightedBehavior.class);

	public void add(Behavior behavior, float weight)
	{
		weightedBehaviors.add(new WeightedBehavior(behavior, weight));
	}

	public void normalizeWeights(){
		float totalWeight=0;
		for (WeightedBehavior wb : weightedBehaviors) {
			totalWeight += wb.weight;
		}

		for (WeightedBehavior wb : weightedBehaviors) {
			wb.weight = wb.weight / totalWeight;
		}

	}

	@Override
	public void update(float delta) {
		force.set(0, 0);
		for (WeightedBehavior wb : weightedBehaviors) {
			wb.behavior.update(delta);
			force.mulAdd(wb.behavior.getForce(), wb.weight);
		}

	}

	@Override
	public Vector2 getForce() {
		return force;
	}

	private static class WeightedBehavior{
		Behavior behavior;
		float weight;

		public WeightedBehavior(Behavior behavior, float weight) {
			this.behavior = behavior;
			this.weight = weight;
		}
	}
}