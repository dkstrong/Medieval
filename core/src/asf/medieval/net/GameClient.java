package asf.medieval.net;

import asf.medieval.net.message.*;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

/**
 * Created by daniel on 11/15/15.
 */
public class GameClient {

	UI ui;
	Client client;
	Player player;

	public GameClient () {
		if(player == null){
			player = new Player();
			player.name = "noob";
		}

		client = new Client();
		client.start();

		UtNet.register(client);

		client.addListener(new Listener.ThreadedListener(new MessageListener()));

		ui = new UI();

		try {
			client.connect(5000, "localhost", UtNet.port);
			// Server communication after connection can go here, or in Listener#connected().
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		Login login = new Login();
		login.player = player;
		client.sendTCP(login);

		// durring game...
		//MoveCharacter msg = new MoveCharacter();
		//client.sendTCP(msg);

	}

	static class UI {
		IntMap<Player> players = new IntMap<Player>();

		public void addPlayer(Player player) {
			boolean newPlayer = !players.containsKey(player.id);
			if(newPlayer){
				players.put(player.id, player);
				System.out.println(player.id+"-"+player.name + " added");
			}else{
				Player existingPlayer = players.get(player.id);
				existingPlayer.id = player.id;
				existingPlayer.name = player.name;
				System.out.println(player.id+"-"+player.name + " updated");
			}


		}

		public void removePlayer (Player player) {
			Player existingPlayer = players.remove(player.id);
			if (existingPlayer != null){
				System.out.println(existingPlayer.id+"-"+existingPlayer.name + " removed");
			}

		}
	}

	private class MessageListener extends Listener {
		public void connected (Connection connection) {
		}

		public void received (Connection c, Object message) {
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

		public void disconnected (Connection connection) {
			System.exit(0);
		}
	}

}
