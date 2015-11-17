package asf.medieval.net;

import asf.medieval.model.Scenario;
import asf.medieval.net.message.Action;
import asf.medieval.net.message.ActionConfirmation;
import asf.medieval.net.message.AddPlayer;
import asf.medieval.net.message.Login;
import asf.medieval.net.message.ReadyToStart;
import asf.medieval.net.message.RemovePlayer;
import asf.medieval.utility.UtLog;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.LongMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Server;

import java.io.*;

/**
 * Created by daniel on 11/15/15.
 */
public class GameServer implements Disposable, GameHost {
	private Server server;
	private boolean bound = false;

	public IntMap<PlayerConnection> loggedInPlayerConnections = new IntMap<PlayerConnection>();

	public Scenario scenario;

	public GameServer () {
		com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_NONE);
		server = new Server() {
			protected Connection newConnection () {
				return new PlayerConnection();
			}
		};

		UtNet.register(server);
		server.addListener(new MessageListener(this));
		server.start();
		UtLog.trace("server started");
	}

	public void bindServer(GameServerConfig gameServerConfig)
	{
		if(gameServerConfig==null)
			throw new IllegalArgumentException("gameServerConfig can not be null");
		if(bound)
			throw new IllegalStateException("Server is already bound");

		try {
			server.bind(gameServerConfig.tcpPort, gameServerConfig.udpPort);
			bound = true;
			UtLog.trace("server bound to port: "+gameServerConfig.tcpPort);
		} catch (IOException e) {
			UtLog.warning("server threw exception while binding to port: "+gameServerConfig.tcpPort, e);
			bound = false;
		}

	}

	public boolean isBound(){
		return bound;
	}

	public void unbindServer()
	{
		bound = false;
		server.close();
		UtLog.trace("server unbound connection");
	}

	@Override
	public void dispose() {

		try {
			//server.stop();
			server.dispose();
		} catch (IOException e) {
			UtLog.warning("server threw exception while disposing", e);
		}
		UtLog.trace("server stopped and disposed");

	}



	// This holds per connection state.
	private static class PlayerConnection extends Connection {
		public Player player;
		public LongMap<UnconfirmedAction> unconfirmedActions = new LongMap<UnconfirmedAction>(8);
	}

	private static class UnconfirmedAction{
		private Action action;
		private IntSet confirmedByPlayers = new IntSet(2);

		public UnconfirmedAction(Action action) {
			this.action = action;
			confirmedByPlayers.add(action.playerId);
		}
	}



	@Override
	public void onReceived(Connection c, Object message) {
		if(!(message instanceof FrameworkMessage.KeepAlive)){
			UtLog.trace("received message: " + message.getClass().getSimpleName());
		}

		// We know all connections for this server are actually CharacterConnections.
		PlayerConnection connection = (PlayerConnection)c;

		if(message instanceof ActionConfirmation){
			ActionConfirmation confirmation = (ActionConfirmation) message;
			//System.out.println(String.valueOf(confirmation));
			PlayerConnection actionMakerConnection = loggedInPlayerConnections.get(confirmation.playerId);
			UnconfirmedAction unconfirmedAction = actionMakerConnection.unconfirmedActions.get(confirmation.lockstepFrame);
			unconfirmedAction.confirmedByPlayers.add(confirmation.confirmedByPlayerId);

			if(unconfirmedAction.confirmedByPlayers.size == loggedInPlayerConnections.size){
				// action has been confirmed by all players, so we can send it to the action maker now..
				actionMakerConnection.sendTCP(unconfirmedAction.action);
				// remove this from unconfirmed action list..
				actionMakerConnection.unconfirmedActions.remove(confirmation.lockstepFrame);
			}

		}else if(message instanceof Action){
			Action action = (Action) message;
			action.lockstepFrame+=2;
			connection.unconfirmedActions.put(action.lockstepFrame,new UnconfirmedAction(action));
			//System.out.println(String.valueOf(action));

			server.sendToAllExceptTCP(action.playerId, action);


		}else if(message instanceof ReadyToStart){
			ReadyToStart ready = (ReadyToStart) message;
			//System.out.println(String.valueOf(ready));
			Action readyAction = new Action();
			readyAction.playerId = ready.playerId;
			readyAction.lockstepFrame = 1;
			connection.unconfirmedActions.put(readyAction.lockstepFrame,new UnconfirmedAction(readyAction));

			server.sendToAllExceptTCP(readyAction.playerId, readyAction);

		}else if (message instanceof Login) {

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

			UtLog.trace("Accepted player login, informing all users...");

			// valid login, add him to the list and notify
			// everyone else on the server
			connection.player = login.player;
			connection.player.id = c.getID();
			loggedInPlayerConnections.put(connection.player.id, connection);

			AddPlayer addPlayer = new AddPlayer();
			addPlayer.player = connection.player;

			// inform players of new login
			// also inform new login of existing players
			for (PlayerConnection loggedInPc : loggedInPlayerConnections.values()) {
				loggedInPc.sendTCP(addPlayer);
				if(loggedInPc.player.id != connection.player.id){
					AddPlayer informExisting = new AddPlayer();
					informExisting.player = loggedInPc.player;
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

	@Override
	public void onConnected(Connection connection) {

	}

	@Override
	public void onDisconnected(Connection c) {
		PlayerConnection connection = (PlayerConnection)c;
		UtLog.trace("one of my clients disconnected: "+connection.player);
		if (connection.player != null) {
			loggedInPlayerConnections.remove(connection.player.id);


			RemovePlayer removePlayer = new RemovePlayer();
			removePlayer.player = connection.player;
			server.sendToAllTCP(removePlayer);
		}
	}

	@Override
	public void onIdle(Connection connection) {

	}

	public static void main(String[] args) {
		UtLog.logLevel = UtLog.TRACE;
		GameServer gameServer = new GameServer();
		gameServer.bindServer(new GameServerConfig());


	}

}
