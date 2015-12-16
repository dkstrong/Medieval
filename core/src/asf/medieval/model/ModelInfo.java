package asf.medieval.model;

import asf.medieval.strictmath.StrictShape;

/**
 * Created by daniel on 12/4/15.
 */
public strictfp class ModelInfo {

	public StrictShape shape;

	public boolean mine;
	public int mineResourceId;

	public boolean structure;
	public int[] buildCosts;
	public int popCost;

	public ModelInfo() {
		buildCosts = new int[ResourceId.values().length];
	}

	public static ModelInfo[] standardConfiguration()
	{
		ModelInfo[] store = new ModelInfo[ModelId.values().length];

		ModelInfo tree = new ModelInfo();
		tree.shape = new StrictShape().fromRadius("1");
		tree.structure = true;
		tree.mine = true;
		tree.mineResourceId = ResourceId.Wood.ordinal();


		ModelInfo church = new ModelInfo();
		church.shape = new StrictShape().fromExtents("9.5", "10.2", "0","0");
		church.structure = true;
		church.buildCosts[ResourceId.Gold.ordinal()] = 100;
		church.buildCosts[ResourceId.Wood.ordinal()] = 25;

		// new Box(1f, 7.5f);
		StrictShape infantryShape = new StrictShape().fromRadius("1");

		ModelInfo knight = new ModelInfo();
		knight.shape = infantryShape;
		knight.buildCosts[ResourceId.Gold.ordinal()] = 20;
		knight.buildCosts[ResourceId.Iron.ordinal()] = 15;
		knight.popCost = 1;

		ModelInfo skeleton = new ModelInfo();
		skeleton.shape = infantryShape;
		skeleton.buildCosts[ResourceId.Gold.ordinal()] = 5;
		skeleton.popCost = 1;

		ModelInfo rockMonster = new ModelInfo();
		rockMonster.shape = infantryShape;
		rockMonster.buildCosts[ResourceId.Gold.ordinal()] = 10;
		rockMonster.buildCosts[ResourceId.Stone.ordinal()] = 110;
		rockMonster.popCost = 2;

		ModelInfo jimmy = new ModelInfo();
		jimmy.shape = infantryShape;
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
