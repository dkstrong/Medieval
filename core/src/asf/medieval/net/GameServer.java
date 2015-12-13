package asf.medieval.net;

import asf.medieval.model.Scenario;
import asf.medieval.net.message.Action;
import asf.medieval.net.message.ActionConfirmation;
import asf.medieval.net.message.AddUser;
import asf.medieval.net.message.Login;
import asf.medieval.net.message.ReadyToStart;
import asf.medieval.net.message.RemoveUser;
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
public strictfp class GameServer implements Disposable, GameHost {
	private Server server;
	private boolean bound = false;

	public IntMap<UserConnection> loggedInPlayerConnections = new IntMap<UserConnection>();

	public Scenario scenario;

	public GameServer () {
		com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_NONE);
		server = new Server() {
			protected Connection newConnection () {
				return new UserConnection();
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
	private static class UserConnection extends Connection {
		public User user;
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
		UserConnection connection = (UserConnection)c;

		if(message instanceof ActionConfirmation){
			ActionConfirmation confirmation = (ActionConfirmation) message;
			//System.out.println(String.valueOf(confirmation));
			UserConnection actionMakerConnection = loggedInPlayerConnections.get(confirmation.playerId);
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

			for (UserConnection userConnection : loggedInPlayerConnections.values()) {
				if(userConnection.user.id != action.playerId){
					userConnection.sendTCP(action);
				}
			}

		}else if(message instanceof ReadyToStart){
			ReadyToStart ready = (ReadyToStart) message;
			//System.out.println(String.valueOf(ready));
			Action readyAction = new Action();
			readyAction.playerId = ready.playerId;
			readyAction.lockstepFrame = 1;
			connection.unconfirmedActions.put(readyAction.lockstepFrame,new UnconfirmedAction(readyAction));

			for (UserConnection userConnection : loggedInPlayerConnections.values()) {
				if(userConnection.user.id != readyAction.playerId){
					userConnection.sendTCP(readyAction);
				}
			}

		}else if (message instanceof Login) {

			// Ignore if already logged in.
			if (connection.user != null)
				return;
			Login login = (Login)message;

			// Reject if the name is invalid or already logged in or something like that
			if (!isValid(login.user.name)) {
				login.user.name = "Lazy Typist";
				//c.close();
				return;
			}
			// Append suffix to name if name is taken
			for (UserConnection other : loggedInPlayerConnections.values()) {
				if(other.user.name.equals(login.user.name)){
					login.user.name +=" (1)";
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
			connection.user = login.user;
			connection.user.cid = c.getID();
			// TODO: properly assign id and team from the scenario configuration.
			// id number can not be changed after this point.
			connection.user.id = connection.user.cid;
			connection.user.team = connection.user.cid;

			loggedInPlayerConnections.put(connection.user.id, connection);

			AddUser addUser = new AddUser();
			addUser.user = connection.user;

			// inform players of new login
			// also inform new login of existing players
			for (UserConnection loggedInPc : loggedInPlayerConnections.values()) {
				loggedInPc.sendTCP(addUser);
				if(loggedInPc.user.id != connection.user.id){
					AddUser informExisting = new AddUser();
					informExisting.user = loggedInPc.user;
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
		UserConnection connection = (UserConnection)c;
		UtLog.trace("one of my clients disconnected: "+connection.user);
		if (connection.user != null) {
			loggedInPlayerConnections.remove(connection.user.id);


			RemoveUser removeUser = new RemoveUser();
			removeUser.user = connection.user;
			server.sendToAllTCP(removeUser);
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
