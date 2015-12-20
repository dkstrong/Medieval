package asf.medieval.model;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 12/17/15.
 */
public strictfp class KeepController extends StructureController{


	public final StrictPoint populationGrowRate = new StrictPoint("5");
	public final StrictPoint populationGrowT= new StrictPoint(populationGrowRate);

	public KeepController(Token token) {
		super(token);

		//this.token.direction.set("0.78539816339");
	}

	@Override
	public void update(StrictPoint delta)
	{
		if(token.owner.pop < token.owner.popcap){
			populationGrowT.sub(delta);
			if(populationGrowT.lessThanOrEqual(StrictPoint.ZERO)){
				populationGrowT.set(populationGrowRate);
				token.scenario.buildWorker(this);
			}
		}
	}

	@Override
	public StrictVec2 getWorkerTargetLocation(StrictVec2 store) {
		store.setAngleRad(token.direction, "10");
		store.add(token.location);
		return store;
	}

	@Override
	public void getNextWorkerTask(WorkerController worker) {
		if(worker.taskPhase <0){
			// Move to fire place
			worker.taskPhase = 0;
			worker.taskWait.set(StrictPoint.ZERO);
			getWorkerTargetLocation(worker.taskTargetLoc);
		}else{
			worker.taskWait.set("NaN");
		}



	}
}
