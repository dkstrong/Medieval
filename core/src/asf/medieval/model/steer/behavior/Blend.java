package asf.medieval.model.steer.behavior;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/13/15.
 */
public strictfp class Blend implements Behavior{

	private StrictVec2 force = new StrictVec2();
	public SteerController agent;
	private Array<WeightedBehavior> weightedBehaviors = new Array<WeightedBehavior>(false, 3, WeightedBehavior.class);

	public void add(Behavior behavior, String weight)
	{
		weightedBehaviors.add(new WeightedBehavior(behavior, weight));
	}

	public void normalizeWeights(){
		StrictPoint totalWeight = new StrictPoint(StrictPoint.ZERO);

		for (WeightedBehavior wb : weightedBehaviors) {
			totalWeight.add(wb.weight);
		}

		for (WeightedBehavior wb : weightedBehaviors) {
			wb.weight.div(totalWeight);
		}
	}

	@Override
	public void update(StrictPoint delta) {
		force.setZero();
		for (WeightedBehavior wb : weightedBehaviors) {
			wb.behavior.update(delta);
			force.mulAdd(wb.behavior.getForce(), wb.weight);
		}
	}

	@Override
	public StrictVec2 getForce() {
		return force;
	}

	private static class WeightedBehavior{
		Behavior behavior;
		StrictPoint weight;

		public WeightedBehavior(Behavior behavior, String weight) {
			this.behavior = behavior;
			this.weight = new StrictPoint(weight);
		}
	}
}
