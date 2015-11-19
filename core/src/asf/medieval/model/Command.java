package asf.medieval.model;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by daniel on 11/16/15.
 */
public class Command {
	public int tokenId;
	public Vector3 location;
	public boolean structure;

	public void performCommand(Scenario scenario)
	{
		if(tokenId > 0){
			// move command
			SoldierToken soldierToken = scenario.getSoldier(tokenId);
			soldierToken.setTarget(location);

		}else{
			// spawn command
			if(structure){
				StructureToken token = scenario.newStructure(location);
			}else{
				SoldierToken token = scenario.newSoldier();
				scenario.setNonOverlappingPosition(token, location);
			}
		}
	}
}
