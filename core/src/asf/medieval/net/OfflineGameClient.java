package asf.medieval.net;

import asf.medieval.model.Command;
import asf.medieval.model.Scenario;
import asf.medieval.strictmath.VecHelper;
import asf.medieval.strictmath.StrictPoint;

/**
 * Created by daniel on 11/16/15.
 */
public strictfp class OfflineGameClient extends GameClient {

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

	private final StrictPoint strictDelta = new StrictPoint();


	@Override
	public void updateGameFrame(float delta) {
		strictDelta.fromFloat(delta);
		scenario.update(strictDelta);
	}

	@Override
	public void sendCommand(Command command) {
		command.performCommand(user.id,scenario);
	}

	@Override
	public void sendReadyAction() {
		user.loading = new StrictPoint(StrictPoint.ONE);

	}

	@Override
	public boolean isAllPlayersReady() {
		return user.loading.greaterThanOrEqual(StrictPoint.ONE);
	}

	@Override
	public String toString() {
		return "OfflineGameClient{}";
	}
}
