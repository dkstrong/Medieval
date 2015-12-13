package asf.medieval.net;

import asf.medieval.model.Command;
import asf.medieval.model.Player;
import asf.medieval.model.Scenario;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 11/16/15.
 */
public strictfp abstract class GameClient {
	public IntMap<User> users = new IntMap<User>();
	/**
	 * the local player
	 */
	public User user;
	public Scenario scenario;

	public GameClient(User user, Scenario scenario) {
		this.user = user;
		this.scenario = scenario;
		if(this.user == null) throw new AssertionError("user can not be null");
	}

	public abstract void updateGameFrame(float delta);

	public abstract void sendCommand(Command command);

	public abstract void sendReadyAction();

	public abstract boolean isAllPlayersReady();

}
