package asf.medieval.model;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class GranaryController extends StructureController{

	public int food = 0;

	public GranaryController(Token token) {
		super(token);

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
