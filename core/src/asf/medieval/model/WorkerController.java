package asf.medieval.model;

import asf.medieval.model.steer.InfantrySteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by daniel on 12/17/15.
 */
public strictfp class WorkerController {
	public Token token;
	public InfantrySteerController agent;

	public int heldResourcesId = -1;
	public int heldResources = 0;

	public StructureController assignedStructure;
	private StructureController assignedStructureApplied;

	public int taskPhase =-1;
	public final StrictPoint taskWait = new StrictPoint();
	public final StrictVec2 taskTargetLoc = new StrictVec2();
	public boolean appliedTargetLoc = false;
	public StructureController.Task task = null;


	public WorkerController(Token token) {
		this.token = token;
		agent = (InfantrySteerController) token.agent;
	}

	private void resetTaskVars()
	{
		appliedTargetLoc = false;
	}

	public void update(StrictPoint delta)
	{
		if(assignedStructure != assignedStructureApplied){
			assignedStructureApplied = assignedStructure;
			taskPhase = -1;
			resetTaskVars();
			assignedStructure.getNextWorkerTask(this);
		}else{
			if(appliedTargetLoc && token.location.epsilonEquals(taskTargetLoc,StrictPoint.ONE)){
				if(task!=null)
					task.doTask(this);
				resetTaskVars();
				assignedStructure.getNextWorkerTask(this);
			}
		}

		taskWait.sub(delta);
		if(taskWait.lessThanOrEqual(StrictPoint.ZERO)){
			if(!appliedTargetLoc){
				appliedTargetLoc = true;
				agent.setTarget(taskTargetLoc);
			}


		}

	}


}
