package asf.medieval.model;

import asf.medieval.strictmath.StrictShape;

/**
 * Created by daniel on 12/16/15.
 */
public class WorkerInfo {

	public StrictShape shape;

	public static WorkerInfo[] standardConfiguration(){
		WorkerInfo[] store = new WorkerInfo[WorkerId.values().length];

		WorkerInfo worker = store[WorkerId.Worker.ordinal()] = new WorkerInfo();
		worker.shape = new StrictShape().fromRadius("1");
		return store;
	}

}
