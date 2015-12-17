package asf.medieval.model;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class BarracksController {
	public Token token;


	public int[] buildableModelIds;

	public BarracksController(Token token) {
		this.token = token;
		buildableModelIds = new int[]{
			MilitaryId.Knight.ordinal(),
			MilitaryId.Skeleton.ordinal(),
			MilitaryId.Jimmy.ordinal(),

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
