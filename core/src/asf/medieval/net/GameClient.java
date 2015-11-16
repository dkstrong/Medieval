package asf.medieval.net;

import asf.medieval.model.Command;

/**
 * Created by daniel on 11/16/15.
 */
public interface GameClient {
	void updateGameFrame(float delta);

	void sendCommand(Command command);
}
