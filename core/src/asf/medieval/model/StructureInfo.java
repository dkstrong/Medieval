package asf.medieval.model;

import asf.medieval.strictmath.StrictShape;

/**
 * Created by daniel on 12/16/15.
 */
public class StructureInfo {

	public int id;
	public StrictShape shape;

	public int[] buildCosts;
	public int popCost; // how many workers are needed to run this building

	public boolean mine;
	public int mineResourceId;

	public TokenInitializer tokenInitializer;

	public StructureInfo() {
		buildCosts = new int[ResourceId.values().length];
	}


	public static StructureInfo[] standardConfiguration()
	{
		StructureInfo[] store = new StructureInfo[StructureId.values().length];

		StructureInfo tree = store[StructureId.Tree.ordinal()] =new StructureInfo();
		tree.id = StructureId.Tree.ordinal();
		tree.shape = new StrictShape().fromRadius("1");
		tree.mine = true;
		tree.mineResourceId = ResourceId.Wood.ordinal();
		tree.tokenInitializer = new TokenInitializer() {
			@Override
			public void initializeToken(Token token) {
				token.mineable = new MineController(token);
			}
		};

		StructureInfo keep = store[StructureId.Keep.ordinal()] =new StructureInfo();
		keep.id = StructureId.Keep.ordinal();
		keep.shape = new StrictShape().fromExtents("10", "10", "0","0");
		keep.tokenInitializer = new TokenInitializer() {
			@Override
			public void initializeToken(Token token) {
				token.keep = new KeepController(token);
			}
		};

		StructureInfo granary = store[StructureId.Granary.ordinal()] =new StructureInfo();
		granary.id = StructureId.Granary.ordinal();
		granary.shape = new StrictShape().fromExtents("10", "10", "0","0");
		granary.buildCosts[ResourceId.Wood.ordinal()] = 150;

		StructureInfo house = store[StructureId.House.ordinal()] =new StructureInfo();
		house.id = StructureId.House.ordinal();
		house.shape = new StrictShape().fromExtents("10", "10", "0","0");
		house.buildCosts[ResourceId.Wood.ordinal()] = 80;

		StructureInfo farm = store[StructureId.Farm.ordinal()] =new StructureInfo();
		farm.id = StructureId.Farm.ordinal();
		farm.shape = new StrictShape().fromExtents("10", "10", "0","0");
		farm.mine = true;
		farm.mineResourceId = ResourceId.Food.ordinal();
		farm.buildCosts[ResourceId.Wood.ordinal()] = 40;

		StructureInfo church = store[StructureId.Church.ordinal()] =new StructureInfo();
		church.id = StructureId.Church.ordinal();
		church.shape = new StrictShape().fromExtents("9.5", "10.2", "0","0");
		church.buildCosts[ResourceId.Gold.ordinal()] = 100;
		church.buildCosts[ResourceId.Wood.ordinal()] = 25;
		church.tokenInitializer = new TokenInitializer() {
			@Override
			public void initializeToken(Token token) {
				token.barracks = new BarracksController(token);
				token.barracks.buildableMilitaryIds = new int[]{
					MilitaryId.Knight.ordinal(),
					MilitaryId.Skeleton.ordinal(),
					MilitaryId.Jimmy.ordinal(),

				};
			}
		};



		return store;
	}
}
