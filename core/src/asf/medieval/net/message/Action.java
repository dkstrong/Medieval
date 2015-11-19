package asf.medieval.net.message;

import asf.medieval.model.Command;
import asf.medieval.model.Scenario;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/16/15.
 */
public class Action {

	public int playerId;
	public long lockstepFrame;

	public Array<Command> commands;

	public void performAction(Scenario scenario) {
		if (commands != null) {
			for (Command command : commands) {
				command.performCommand(playerId,scenario);

			}
		}
	}

	@Override
	public String toString() {
		return "Action{" +
			"playerId=" + playerId +
			", lockstepFrame=" + lockstepFrame +
			'}';
	}
}
