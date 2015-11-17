package asf.medieval.net;

import asf.medieval.model.Command;

/**
 * Created by daniel on 11/16/15.
 */
public interface GameClient {
	public void updateGameFrame(float delta);

	public void sendCommand(Command command);

	public void sendReadyAction();

	public boolean isAllPlayersReady();

}
