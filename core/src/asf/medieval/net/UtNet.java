package asf.medieval.net;

import asf.medieval.model.Command;
import asf.medieval.net.message.Action;
import asf.medieval.net.message.ActionConfirmation;
import asf.medieval.net.message.ReadyToStart;
import asf.medieval.net.message.AddUser;
import asf.medieval.net.message.Login;
import asf.medieval.net.message.Register;
import asf.medieval.net.message.RegistrationRequired;
import asf.medieval.net.message.RemoveUser;
import asf.medieval.utility.UtLog;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Created by daniel on 11/15/15.
 */
public class UtNet {

	static public void register (EndPoint endPoint) {
		UtLog.logLevel = UtLog.WARNING;
		Kryo kryo = endPoint.getKryo();
		kryo.register(Login.class);
		kryo.register(RegistrationRequired.class);
		kryo.register(Register.class);
		kryo.register(AddUser.class);
		kryo.register(RemoveUser.class);
		kryo.register(User.class);
		kryo.register(Action.class);
		kryo.register(ReadyToStart.class);
		kryo.register(Command.class);
		kryo.register(ActionConfirmation.class);
		kryo.register(com.badlogic.gdx.utils.Array.class);
		kryo.register(asf.medieval.model.Command[].class);
		kryo.register(com.badlogic.gdx.math.Vector3.class);
		kryo.register(com.badlogic.gdx.math.Vector2.class);


	}


}
