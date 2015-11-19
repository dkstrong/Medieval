package asf.medieval.model;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by daniel on 11/16/15.
 */
public class Command {
	public int tokenId;
	public Vector3 location;
	public boolean structure;

	public void performCommand(int playerId, Scenario scenario)
	{
		if(tokenId > 0){
			// move command
			Token token = scenario.getSoldier(tokenId);
			InfantryAgent agent = (InfantryAgent)token.agent;
			agent.setTarget(location);

		}else{
			// spawn command
			if(structure){
				Token token = scenario.newStructure(playerId,location);
			}else{
				Token token = scenario.newSoldier(playerId, location);
			}
		}
	}
}
