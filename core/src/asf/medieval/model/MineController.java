package asf.medieval.model;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class MineController extends StructureController{

	public int remainingResource;

	public MineController(Token token) {
		super(token);

		remainingResource = 100;
	}

	@Override
	public void update(StrictPoint delta)
	{

	}



	@Override
	public StrictVec2 getWorkerTargetLocation(StrictVec2 store) {
		return store.set(token.location);
	}

	@Override
	public void getNextWorkerTask(WorkerController worker) {

	}
}
