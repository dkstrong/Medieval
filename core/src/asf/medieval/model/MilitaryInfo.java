package asf.medieval.model;

import asf.medieval.strictmath.StrictShape;

/**
 * Created by daniel on 12/4/15.
 */
public strictfp class MilitaryInfo {

	public int id;
	public StrictShape shape;

	public int[] buildCosts;
	public int popCost;

	public MilitaryInfo() {
		buildCosts = new int[ResourceId.values().length];
	}

	public static MilitaryInfo[] standardConfiguration()
	{
		MilitaryInfo[] store = new MilitaryInfo[MilitaryId.values().length];

		// new Box(1f, 7.5f);
		StrictShape infantryShape = new StrictShape().fromRadius("1");

		MilitaryInfo knight = store[MilitaryId.Knight.ordinal()]=new MilitaryInfo();
		knight.id= MilitaryId.Knight.ordinal();
		knight.shape = infantryShape;
		knight.buildCosts[ResourceId.Gold.ordinal()] = 20;
		knight.buildCosts[ResourceId.Iron.ordinal()] = 15;
		knight.popCost = 1;

		MilitaryInfo skeleton = store[MilitaryId.Skeleton.ordinal()] = new MilitaryInfo();
		skeleton.id= MilitaryId.Skeleton.ordinal();
		skeleton.shape = infantryShape;
		skeleton.buildCosts[ResourceId.Gold.ordinal()] = 5;
		skeleton.popCost = 1;

		MilitaryInfo rockMonster = store[MilitaryId.RockMonster.ordinal()] =new MilitaryInfo();
		rockMonster.id= MilitaryId.RockMonster.ordinal();
		rockMonster.shape = infantryShape;
		rockMonster.buildCosts[ResourceId.Gold.ordinal()] = 10;
		rockMonster.buildCosts[ResourceId.Stone.ordinal()] = 110;
		rockMonster.popCost = 2;

		MilitaryInfo jimmy = store[MilitaryId.Jimmy.ordinal()] = new MilitaryInfo();
		jimmy.id= MilitaryId.Jimmy.ordinal();
		jimmy.shape = infantryShape;
		jimmy.buildCosts[ResourceId.Gold.ordinal()] = 100;
		jimmy.popCost = 2;

		return store;

	}

}
