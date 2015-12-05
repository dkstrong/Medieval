package asf.medieval.model;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 12/4/15.
 */
public class ModelInfo {

	public boolean resource;
	public int resourceId;

	public boolean structure;
	public int[] buildCosts;
	public int popCost;

	public ModelInfo(boolean structure) {
		this.structure = structure;
		buildCosts = new int[ResourceId.values().length];
	}

	public static ModelInfo[] standardConfiguration()
	{
		ModelInfo[] store = new ModelInfo[ModelId.values().length];

		ModelInfo tree = new ModelInfo(true);
		tree.resource = true;
		tree.resourceId = ResourceId.Wood.ordinal();

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

		store[ModelId.Tree.ordinal()] = tree;
		store[ModelId.Church.ordinal()] = church;
		store[ModelId.Knight.ordinal()] = knight;
		store[ModelId.Skeleton.ordinal()] = skeleton;
		store[ModelId.RockMonster.ordinal()] = rockMonster;
		store[ModelId.Jimmy.ordinal()] = jimmy;

		return store;

	}

}
