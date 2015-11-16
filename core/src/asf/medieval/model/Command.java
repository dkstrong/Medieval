package asf.medieval.model;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by daniel on 11/16/15.
 */
public class Command {
	public int soldierId;
	public Vector3 location;

	public void performCommand(Scenario scenario)
	{
		if(soldierId > 0){
			// move command
			SoldierToken soldierToken = scenario.getSoldier(soldierId);
			soldierToken.setTarget(location);

		}else{
			// spawn command
			SoldierToken soldierToken = scenario.newSoldier();
			scenario.setNonOverlappingPosition(soldierToken, location);
		}
	}
}
