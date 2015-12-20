package asf.medieval.model;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class FarmController extends StructureController{

	public FarmController(Token token) {
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
		if(worker.taskPhase <0){
			// Move to Farm
			worker.taskPhase = 0;
			worker.taskWait.set(StrictPoint.ZERO);
			getWorkerTargetLocation(worker.taskTargetLoc);
			worker.task = null;
		}else if(worker.taskPhase == 0){
			// Collect Food
			worker.taskPhase = 1;
			worker.taskWait.set("1.5");
			getWorkerTargetLocation(worker.taskTargetLoc);
			worker.task = new Task() {
				@Override
				public void doTask(WorkerController worker) {
					worker.heldResourcesId = ResourceId.Food.ordinal();
					worker.heldResources = 10;
				}
			};
		}else if(worker.taskPhase == 1){
			// Carry Food to Granary
			// TODO: what if there is no granary, need to account for waiting on granary to be made,
			// TODO: destroyed granary, or destroying granary on way to deliver food..
			worker.taskPhase = 2;
			worker.taskWait.set(StrictPoint.ZERO);
			final GranaryController granary = token.scenario.getGranaryToFill(token.owner.playerId,null);
			granary.getWorkerTargetLocation(worker.taskTargetLoc);
			worker.task = new Task() {
				@Override
				public void doTask(WorkerController worker) {
					granary.food += worker.heldResources;
					worker.heldResourcesId = -1;
					worker.heldResources = 0;
					worker.taskPhase = -1;
				}
			};
		}
	}
}
