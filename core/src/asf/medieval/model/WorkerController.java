package asf.medieval.model;

import asf.medieval.model.steer.InfantrySteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by daniel on 12/17/15.
 */
public class WorkerController {
	public Token token;
	public InfantrySteerController agent;

	public KeepController keep;
	public int assignment =0;
	private int assignmentApplied=-1;

	public WorkerController(Token token) {
		this.token = token;
		agent = (InfantrySteerController) token.agent;
		for (int i = 0; i < token.scenario.tokens.size; i++) {
			Token t = token.scenario.tokens.items[i];
			if(t.owner.playerId == token.owner.playerId && t.keep!=null){
				keep = t.keep;
				break;
			}
		}
	}

	private static final StrictVec2 tempVec = new StrictVec2();

	public void update(StrictPoint delta)
	{
		if(assignmentApplied != assignment){
			assignmentApplied = assignment;
			if(assignment ==0){
				StrictVec2 campfireLoc = keep.getCampfireLocation(tempVec);
				agent.setTarget(campfireLoc);
			}
		}

	}
}
