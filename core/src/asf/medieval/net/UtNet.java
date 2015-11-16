package asf.medieval.net;


import asf.medieval.net.message.AddPlayer;
import asf.medieval.net.message.Login;
import asf.medieval.net.message.Register;
import asf.medieval.net.message.RegistrationRequired;
import asf.medieval.net.message.RemovePlayer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Created by daniel on 11/15/15.
 */
public class UtNet {

	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Login.class);
		kryo.register(RegistrationRequired.class);
		kryo.register(Register.class);
		kryo.register(AddPlayer.class);
		kryo.register(RemovePlayer.class);
		kryo.register(Player.class);
		kryo.register(Action.class);
	}


}
