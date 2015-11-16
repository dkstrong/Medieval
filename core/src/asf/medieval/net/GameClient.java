package asf.medieval.net;


import asf.medieval.net.message.AddPlayer;
import asf.medieval.net.message.Login;
import asf.medieval.net.message.Register;
import asf.medieval.net.message.RegistrationRequired;
import asf.medieval.net.message.RemovePlayer;
import asf.medieval.utility.UtLog;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by daniel on 11/15/15.
 */
public class GameClient implements Disposable {

	public UI ui;
	public Client client;
	public Player player;

	public GameClient (String hostName, int port, Player player) {
		this.player = player;
		if(this.player == null) throw new AssertionError("player can not be null");
		com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_NONE);
		client = new Client();
		client.start();
		UtLog.trace("started client");
		UtNet.register(client);

		client.addListener(new Listener.ThreadedListener(new MessageListener()));

		ui = new UI();

		try {
			client.connect(5000, hostName, port);
			// Server communication after connection can go here, or in Listener#connected().
		} catch (IOException ex) {
			ex.printStackTrace();
		}



	}



	@Override
	public void dispose() {
		try {
			client.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
		UtLog.trace("disposed client");
	}

	static class UI {
		IntMap<Player> players = new IntMap<Player>();

		public void addPlayer(Player player) {
			boolean newPlayer = !players.containsKey(player.id);
			if(newPlayer){
				players.put(player.id, player);
				UtLog.info(player.id + "-" + player.name + " added");
			}else{
				Player existingPlayer = players.get(player.id);
				existingPlayer.id = player.id;
				existingPlayer.name = player.name;
				UtLog.info(player.id + "-" + player.name + " updated");
			}


		}

		public void removePlayer (Player player) {
			Player existingPlayer = players.remove(player.id);
			if (existingPlayer != null){
				UtLog.info(existingPlayer.id + "-" + existingPlayer.name + " removed");
			}

		}
	}

	private class MessageListener extends Listener {
		public void connected (Connection connection) {
			UtLog.trace("connected to server");
			Login login = new Login();
			login.player = player;
			client.sendTCP(login);

			// durring game...
			//MoveCharacter msg = new MoveCharacter();
			//client.sendTCP(msg);
		}

		public void received (Connection c, Object message) {
			UtLog.trace("received message: "+message.getClass().getCanonicalName());
			if (message instanceof RegistrationRequired) {
				Register register = new Register();
				register.name = "new name";
				register.otherStuff = "other stuff";
				client.sendTCP(register);
			}else if (message instanceof AddPlayer) {
				AddPlayer msg = (AddPlayer)message;
				ui.addPlayer(msg.character);
			}else if (message instanceof RemovePlayer) {
				RemovePlayer msg = (RemovePlayer)message;
				ui.removePlayer(msg.character);
			}
		}

		public void disconnected(Connection connection) {
			UtLog.trace("disconnected from server");
			//System.exit(0);
		}
	}

}
