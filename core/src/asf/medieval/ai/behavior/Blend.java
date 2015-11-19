package asf.medieval.ai.behavior;

import asf.medieval.ai.SteerAgent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/13/15.
 */
public class Blend implements Behavior{

	private Vector2 steeringOut = new Vector2();

	public SteerAgent agent;

	private Array<WeightedBehavior> weightedBehaviors = new Array<WeightedBehavior>(false, 3, WeightedBehavior.class);

	public void add(Behavior behavior, float weight)
	{
		weightedBehaviors.add(new WeightedBehavior(behavior, weight));
	}

	/**
	 * ensure all weights add up to 1
	 */
	public void calcWeights()
	{
		float totalWeight = 0;
		for (WeightedBehavior wb : weightedBehaviors) {
			totalWeight+=wb.weight;
		}

		for (WeightedBehavior wb : weightedBehaviors) {
			wb.weight = wb.weight / totalWeight;
		}

	}

	@Override
	public void update(float delta) {
		steeringOut.set(0,0);
		for (WeightedBehavior wb : weightedBehaviors) {
			wb.behavior.update(delta);
			steeringOut.mulAdd(wb.behavior.getForce(),wb.weight);
		}

	}

	@Override
	public Vector2 getForce() {
		return steeringOut;
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
