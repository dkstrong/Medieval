package asf.medieval.net;

import asf.medieval.net.message.Action;
import asf.medieval.net.message.ActionConfirmation;
import asf.medieval.net.message.AddPlayer;
import asf.medieval.net.message.Login;
import asf.medieval.net.message.Register;
import asf.medieval.net.message.RegistrationRequired;
import asf.medieval.net.message.RemovePlayer;
import asf.medieval.utility.UtLog;
import com.badlogic.gdx.utils.LongMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;

/**
 * Created by daniel on 11/16/15.
 */
public interface GameHost {

	public void onConnected(Connection connection);

	public void onReceived(Connection c, Object message);

	public void onDisconnected(Connection connection);

	public void onIdle (Connection connection);
}
