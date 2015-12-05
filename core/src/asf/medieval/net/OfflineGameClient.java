package asf.medieval.net;

import asf.medieval.model.Command;
import asf.medieval.model.Player;
import asf.medieval.model.Scenario;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 11/16/15.
 */
public class OfflineGameClient extends GameClient {

	public OfflineGameClient(User user, Scenario scenario) {
		super(user, scenario);
	}

	public void connectToServer()
	{

		users.put(1,user);

		User computerUser = new User();
		computerUser.id = 2;
		computerUser.team = 2;
		computerUser.name="Computer";
		users.put(2,computerUser);

		for (User p : users.values()) {
			scenario.addPlayer(p);
		}

	}

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
