package asf.medieval.net;

import asf.medieval.model.Command;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;

/**
 * Created by daniel on 11/16/15.
 */
public class PendingActions {

	private final NetworkedGameClient networkedGameClient;


	private final IntMap<LongMap<Action>> receivedActions = new IntMap<LongMap<Action>>(8);

	public PendingActions(NetworkedGameClient networkedGameClient) {
		this.networkedGameClient = networkedGameClient;

		for (Player player : networkedGameClient.players.values()) {
			LongMap<Action> receivedActionsForPlayer = new LongMap<Action>(256);
			receivedActions.put(player.id, receivedActionsForPlayer);
		}

	}

	public void receiveAction(Action action)
	{
		LongMap<Action> receivedActionsForPlayer = receivedActions.get(action.playerId);
		receivedActionsForPlayer.put(action.lockstepFrame, action);

	}

	public boolean hasActions(long lockstepFrame)
	{
		for (Player player : networkedGameClient.players.values()) {
			LongMap<Action> receivedActionsForPlayer = receivedActions.get(player.id);
			Action action = receivedActionsForPlayer.get(lockstepFrame);
			if(action == null){
				return false;
			}
		}
		return true;
	}

	public void performActions(long lockstepFrame)
	{
		for (Player player : networkedGameClient.players.values()) {
			LongMap<Action> receivedActionsForPlayer = receivedActions.get(player.id);
			Action action = receivedActionsForPlayer.get(lockstepFrame);

			for (Command command : action.commands) {
				command.performCommand(networkedGameClient.scenario);

			}
		}
	}
}