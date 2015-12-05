package asf.medieval.net;

import asf.medieval.model.Command;
import asf.medieval.model.Player;
import asf.medieval.model.Scenario;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 11/16/15.
 */
public class OfflineGameClient extends GameClient {

	@Override
	public void updateGameFrame(float delta) {
		scenario.update(delta);
	}

	@Override
	public void sendCommand(Command command) {
		command.performCommand(user.id,scenario);
	}

	@Override
	public void sendReadyAction() {
		user.loading = 1;
		for (User p : users.values()) {
			scenario.addPlayer(p);
		}
	}

	@Override
	public boolean isAllPlayersReady() {
		return user.loading==1;
	}

	@Override
	public String toString() {
		return "OfflineGameClient{}";
	}
}
