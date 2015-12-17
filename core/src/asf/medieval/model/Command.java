package asf.medieval.model;

import asf.medieval.model.steer.InfantryController;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/16/15.
 */
public strictfp class Command {
	public int tokenId;
	public StrictVec2 location;
	public int structureId;
	public int militaryId;

	public void performCommand(int playerId, Scenario scenario)
	{
		if(tokenId > 0){
			// move command
			Token token = scenario.getSoldier(tokenId);
			InfantryController agent = (InfantryController)token.agent;
			agent.setTarget(location);

		}else{
			// spawn command
			if(structureId >=0){
				scenario.buildStructure(playerId, location, structureId);
			}else{
				scenario.buildMilitary(playerId, location, militaryId);
			}
		}
	}

	public static Command moveCommand(int tokenId, StrictVec2 location){
		Command command = new Command();
		command.tokenId = tokenId;
		command.location = location;
		return command;

	}

	public static Command buildStructure(int structureId, StrictVec2 location){
		Command command = new Command();
		command.structureId = structureId;
		command.militaryId = -1;
		command.location = location;
		return command;
	}

	public static Command buildMilitary(int militaryId, StrictVec2 location){
		Command command = new Command();
		command.structureId = -1;
		command.militaryId = militaryId;
		command.location = location;
		return command;
	}
}
