package asf.medieval.model;

import asf.medieval.model.steer.InfantryController;

/**
 * Created by daniel on 11/19/15.
 */
public class BarracksController {
	public Token token;


	public int[] buildableModelIds;

	public BarracksController(Token token) {
		this.token = token;
		buildableModelIds = new int[]{
			ModelId.Knight.ordinal(),
			ModelId.Skeleton.ordinal(),
			ModelId.Jimmy.ordinal(),

		};
	}


	public boolean canBuild(int modelId){
		for (int buildableModelId : buildableModelIds) {
			if(buildableModelId == modelId)
				return true;
		}
		return false;
	}


}
