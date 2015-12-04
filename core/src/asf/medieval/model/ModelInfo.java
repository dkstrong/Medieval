package asf.medieval.model;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 12/4/15.
 */
public class ModelInfo {

	public boolean structure;

	public ModelInfo(boolean structure) {
		this.structure = structure;
	}

	public static void standardConfiguration(IntMap<ModelInfo> store)
	{
		store.put(ModelId.Church.ordinal(),new ModelInfo(true));
		store.put(ModelId.Knight.ordinal(),new ModelInfo(false));
		store.put(ModelId.Skeleton.ordinal(),new ModelInfo(false));
		store.put(ModelId.RockMonster.ordinal(),new ModelInfo(false));
		store.put(ModelId.Jimmy.ordinal(),new ModelInfo(false));
	}

}
