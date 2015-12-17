package asf.medieval.model;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class BarracksController {
	public Token token;

	public int[] buildableMilitaryIds;

	public BarracksController(Token token) {
		this.token = token;
	}


	public boolean canBuild(int militaryId){
		if(buildableMilitaryIds == null)
			return false;
		for (int buildableMilitaryId : buildableMilitaryIds) {
			if(buildableMilitaryId == militaryId)
				return true;
		}
		return false;
	}


}
