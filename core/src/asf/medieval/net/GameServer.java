package asf.medieval.net;

import asf.medieval.model.Scenario;
import asf.medieval.net.message.*;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.*;

/**
 * Created by daniel on 11/15/15.
 */
public class GameServer {
	Server server;

	IntMap<PlayerConnection> loggedInPlayerConnections = new IntMap<PlayerConnection>();

	Scenario scenario;

	public GameServer (Scenario scenario) throws IOException {
		this.scenario = scenario;
		server = new Server() {
			protected Connection newConnection () {
				return new PlayerConnection();
			}
		};

		UtNet.register(server);

		server.addListener(new MessageListener());
		server.bind(UtNet.port);
		server.start();
	}

	// This holds per connection state.
	static class PlayerConnection extends Connection {
		public Player player;
	}

	private class MessageListener extends Listener{
		public void received (Connection c, Object message) {
			// We know all connections for this server are actually CharacterConnections.
			PlayerConnection connection = (PlayerConnection)c;

			if (message instanceof Login) {
				// Ignore if already logged in.
				if (connection.player != null)
					return;
				Login login = (Login)message;

				// Reject if the name is invalid or already logged in or something like that
				if (!isValid(login.player.name)) {
					login.player.name = "Lazy Typist";
					//c.close();
					return;
				}
				// Append suffix to name if name is taken
				for (PlayerConnection other : loggedInPlayerConnections.values()) {
					if(other.player.name.equals(login.player.name)){
						login.player.name +=" (1)";
					}
				}

				// if login credentials were bad or something..
				//character = loadCharacter(name);
				//if (player == null) {
				//	c.sendTCP(new RegistrationRequired());
				//	return;
				//}

				// valid login, add him to the list and notify
				// everyone else on the server
				connection.player = login.player;
				connection.player.id = c.getID();
				loggedInPlayerConnections.put(connection.player.id, connection);

				AddPlayer addPlayer = new AddPlayer();
				addPlayer.character = connection.player;

				// inform players of new login
				// also inform new login of existing players
				for (PlayerConnection loggedInPc : loggedInPlayerConnections.values()) {
					loggedInPc.sendTCP(addPlayer);
					if(loggedInPc.player.id != connection.player.id){
						AddPlayer informExisting = new AddPlayer();
						informExisting.character = loggedInPc.player;
						connection.sendTCP(informExisting);
					}
				}

			}
		}

		private boolean isValid (String value) {
			if (value == null) return false;
			value = value.trim();
			if (value.length() == 0) return false;
			return true;
		}

		public void disconnected (Connection c) {
			PlayerConnection connection = (PlayerConnection)c;
			if (connection.player != null) {
				loggedInPlayerConnections.remove(connection.player.id);


				RemovePlayer removePlayer = new RemovePlayer();
				removePlayer.character = connection.player;
				server.sendToAllTCP(removePlayer);
			}
		}
	}

}
