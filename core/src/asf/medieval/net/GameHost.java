package asf.medieval.net;

import com.esotericsoftware.kryonet.Connection;

/**
 * Created by daniel on 11/16/15.
 */
public strictfp interface GameHost {

	public void onConnected(Connection connection);

	public void onReceived(Connection c, Object message);

	public void onDisconnected(Connection connection);

	public void onIdle (Connection connection);
}
