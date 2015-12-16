package asf.medieval.view;

import asf.medieval.model.ResourceId;

/**
 * Created by daniel on 12/5/15.
 */
public class ResourceViewInfo {
	public String name;

	public static ResourceViewInfo[] standardConfiguration()
	{
		ResourceViewInfo[] store = new ResourceViewInfo[ResourceId.values().length];

		ResourceViewInfo gold = new ResourceViewInfo();
		gold.name = "Gold";

		ResourceViewInfo food = new ResourceViewInfo();
		food.name = "Food";

		ResourceViewInfo wood = new ResourceViewInfo();
		wood.name = "Wood";

		ResourceViewInfo stone = new ResourceViewInfo();
		stone.name = "Stone";

		ResourceViewInfo iron = new ResourceViewInfo();
		iron.name = "Iron";

		store[ResourceId.Gold.ordinal()] = gold;
		store[ResourceId.Food.ordinal()] = food;
		store[ResourceId.Wood.ordinal()] = wood;
		store[ResourceId.Stone.ordinal()] = stone;
		store[ResourceId.Iron.ordinal()] = iron;


		return store;
	}
}
