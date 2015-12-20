package asf.medieval.model;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class BarracksController extends StructureController{

	public int[] buildableMilitaryIds;

	public BarracksController(Token token) {
		super(token);
	}

	@Override
	public void update(StrictPoint delta)
	{

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


	@Override
	public StrictVec2 getWorkerTargetLocation(StrictVec2 store) {
		store.setAngleRad(token.direction, "10");
		store.add(token.location);
		return store;
	}

	@Override
	public void getNextWorkerTask(WorkerController worker) {

	}
}
