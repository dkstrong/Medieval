package asf.medieval.model;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by daniel on 12/16/15.
 */
public strictfp abstract class StructureController {

	public Token token;

	public StructureController(Token token) {
		this.token = token;
		int currentPopUse = 0;
		while(currentPopUse < token.si.popCost ) {
			WorkerController idleWorker = token.scenario.getIdleWorker(token.owner.playerId, token.location);
			if (idleWorker == null){
				currentPopUse = token.si.popCost; // exit out, no idle workers
			}else{
				idleWorker.assignedStructure = this;
				currentPopUse++;
			}

		}
	}

	public abstract void update(StrictPoint delta);

	public abstract StrictVec2 getWorkerTargetLocation(StrictVec2 store);

	public abstract void getNextWorkerTask(WorkerController worker);

	public strictfp interface Task{
		public void doTask(WorkerController worker);
	}
}
