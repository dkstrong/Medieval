package asf.medieval.net;

import asf.medieval.model.Command;
import asf.medieval.model.Player;
import asf.medieval.model.Scenario;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 11/16/15.
 */
public abstract class GameClient {
	public IntMap<Player> players = new IntMap<Player>();
	public Player player;
	public Scenario scenario;

	public abstract void updateGameFrame(float delta);

	public abstract void sendCommand(Command command);

	public abstract void sendReadyAction();

	public abstract boolean isAllPlayersReady();

}
