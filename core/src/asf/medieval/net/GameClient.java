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
import com.esotericsoftware.minlog.Log;

import java.io.IOException;

/**
 * Created by daniel on 11/15/15.
 */
public class GameClient implements Disposable {

	public String hostName;
	public int tcpPort;
	public int udpPort;
	public Client client;
	public IntMap<Player> players = new IntMap<Player>();
	public Player player;

	public GameClient () {

		com.esotericsoftware.minlog.Log.set(Log.LEVEL_NONE);
		client = new Client();
		UtNet.register(client);
		client.start();

		client.addListener(new Listener.ThreadedListener(new MessageListener()));

		UtLog.trace("client thread started");
	}

	public void connectToServer(String hostName, int tcpPort, int udpPort, Player player){
		this.hostName = hostName;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		this.player = player;
		if(this.player == null) throw new AssertionError("player can not be null");

		try {
			client.connect(5000, hostName, tcpPort, udpPort);
			// Server communication after connection can go here, or in Listener#connected().
		} catch (IOException ex) {
			UtLog.warning("exception thrown while Client was connecting to server", ex);
		}

		UtLog.trace("client connected");


	}

	public void disconnectFromServer(){
		client.close();
		UtLog.trace("client closed connection");
	}


	@Override
	public void dispose() {
		client.stop();
		try {
			client.dispose();
		} catch (IOException e) {
			UtLog.warning("exception thrown while disposing Client",e);
		}
		UtLog.trace("client stopped and disposed");
	}

	public boolean isConnected(){
		return client.isConnected();
	}

	private class MessageListener extends Listener {
		public void connected (Connection connection) {

			Login login = new Login();
			login.player = player;
			client.sendTCP(login);
			UtLog.trace("sent server login message");
			// durring game...
			//MoveCharacter msg = new MoveCharacter();
			//client.sendTCP(msg);
		}

		public void received (Connection c, Object message) {
			UtLog.trace("received message: "+message.getClass().getSimpleName());
			if (message instanceof RegistrationRequired) {
				Register register = new Register();
				register.name = "new name";
				register.otherStuff = "other stuff";
				client.sendTCP(register);
			}else if (message instanceof AddPlayer) {
				AddPlayer msg = (AddPlayer)message;
				if(client.getID() == msg.player.id){
					//this msg is about this local player, update his object
					player.set(msg.player);
					UtLog.info("received my own player information, updating my local record");
				}

				boolean newPlayer = !players.containsKey(msg.player.id);
				if(newPlayer){
					players.put(msg.player.id, msg.player);
					UtLog.info(msg.player.id + "-" + msg.player.name + " added");
				}else{
					Player existingPlayer = players.get(msg.player.id);
					existingPlayer.id = msg.player.id;
					existingPlayer.name = msg.player.name;
					UtLog.info(msg.player.id + "-" + msg.player.name + " updated");
				}


			}else if (message instanceof RemovePlayer) {
				RemovePlayer msg = (RemovePlayer)message;

				Player existingPlayer = players.remove(msg.player.id);
				if (existingPlayer != null){
					UtLog.info(existingPlayer.id + "-" + existingPlayer.name + " removed");
				}
			}
		}

		public void disconnected(Connection connection) {
			UtLog.trace("disconnected from server");

			//System.exit(0);
		}
	}

	public static void main(String[] args) {
		UtLog.logLevel = UtLog.TRACE;
		GameClient gameClient = new GameClient();
		Player player = new Player();
		player.name ="Gergh";
		gameClient.connectToServer("localHost", 27677, 27677,player);


	}

}
