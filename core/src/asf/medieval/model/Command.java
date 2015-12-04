package asf.medieval.model;

import asf.medieval.model.steer.InfantryController;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/16/15.
 */
public class Command {
	public int tokenId;
	public Vector2 location;
	public int modelId;

	public void performCommand(int playerId, Scenario scenario)
	{
		if(tokenId > 0){
			// move command
			Token token = scenario.getSoldier(tokenId);
			InfantryController agent = (InfantryController)token.agent;
			agent.setTarget(location);

		}else{
			// spawn command
			scenario.newToken(playerId, location, modelId);
		}
	}
}
