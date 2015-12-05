package asf.medieval.model;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 12/4/15.
 */
public class ModelInfo {

	public boolean structure;
	public int[] buildCosts;
	public int popCost;

	public ModelInfo(boolean structure) {
		this.structure = structure;
		buildCosts = new int[ResourceId.values().length];
	}

	public static void standardConfiguration(IntMap<ModelInfo> store)
	{
		ModelInfo church = new ModelInfo(true);
		church.buildCosts[ResourceId.Gold.ordinal()] = 100;
		church.buildCosts[ResourceId.Wood.ordinal()] = 25;

		ModelInfo knight = new ModelInfo(false);
		knight.buildCosts[ResourceId.Gold.ordinal()] = 20;
		knight.buildCosts[ResourceId.Iron.ordinal()] = 15;
		knight.popCost = 1;

		ModelInfo skeleton = new ModelInfo(false);
		skeleton.buildCosts[ResourceId.Gold.ordinal()] = 5;
		skeleton.popCost = 1;

		ModelInfo rockMonster = new ModelInfo(false);
		rockMonster.buildCosts[ResourceId.Gold.ordinal()] = 10;
		rockMonster.buildCosts[ResourceId.Stone.ordinal()] = 110;
		rockMonster.popCost = 2;

		ModelInfo jimmy = new ModelInfo(false);
		jimmy.buildCosts[ResourceId.Gold.ordinal()] = 100;
		jimmy.popCost = 2;

		store.put(ModelId.Church.ordinal(),church);
		store.put(ModelId.Knight.ordinal(),knight);
		store.put(ModelId.Skeleton.ordinal(),skeleton);
		store.put(ModelId.RockMonster.ordinal(),rockMonster);
		store.put(ModelId.Jimmy.ordinal(),jimmy);
	}

}
