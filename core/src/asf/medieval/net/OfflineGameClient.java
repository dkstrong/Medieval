package asf.medieval.net;

import asf.medieval.model.Command;
import asf.medieval.model.Scenario;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 11/16/15.
 */
public class OfflineGameClient implements GameClient {
	public IntMap<Player> players = new IntMap<Player>();
	public Player player;
	public Scenario scenario;

	@Override
	public void updateGameFrame(float delta) {
		scenario.update(delta);
	}

	@Override
	public void sendCommand(Command command) {
		command.performCommand(scenario);
	}

	@Override
	public void sendReadyAction() {
		player.loading = 1;
	}

	@Override
	public boolean isAllPlayersReady() {
		return player.loading==1;
	}

	@Override
	public String toString() {
		return "OfflineGameClient{}";
	}
}
