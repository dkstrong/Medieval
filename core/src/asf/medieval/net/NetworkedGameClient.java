package asf.medieval.net;

import asf.medieval.model.Command;
import asf.medieval.model.Scenario;
import asf.medieval.net.message.Action;
import asf.medieval.net.message.ActionConfirmation;
import asf.medieval.net.message.ReadyToStart;
import asf.medieval.net.message.AddUser;
import asf.medieval.net.message.Login;
import asf.medieval.net.message.Register;
import asf.medieval.net.message.RegistrationRequired;
import asf.medieval.net.message.RemoveUser;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.utility.UtLog;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;

/**
 * Created by daniel on 11/15/15.
 */
public strictfp class NetworkedGameClient extends GameClient implements Disposable, GameHost {

	public String hostName;
	public int tcpPort;
	public int udpPort;
	public Client client;

	public NetworkedGameClient(User user, Scenario scenario) {
		super(user, scenario);

		com.esotericsoftware.minlog.Log.set(Log.LEVEL_NONE);
		client = new Client();
		UtNet.register(client);
		client.start();

		client.addListener(new Listener.ThreadedListener(new MessageListener(this)));

		UtLog.trace("client thread started");
	}

	public void connectToServer(String hostName, int tcpPort, int udpPort){
		this.hostName = hostName;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;



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

	private final IntMap<LongMap<Action>> receivedActions = new IntMap<LongMap<Action>>(8);
	private LongMap<Action> actionsToSend = new LongMap<Action>(8);
	private float frameLength = 0.05f; //50 miliseconds is .05       60fps is 0.016
	private final StrictPoint strictFrameLength = new StrictPoint("0.05");
	private float accumilatedTime = 0f;
	private int gameFrame = 0;
	private int gameFramesPerLocksetpTurn =4;

	private long lockstepFrame=0;

	@Override
	public void updateGameFrame(float delta)
	{
		//Basically same logic as FixedUpdate, but we can scale it by adjusting FrameLength
		accumilatedTime +=  delta;

		//in case the FPS is too slow, we may need to update the game multiple times a frame
		while(accumilatedTime > frameLength) {
			if(gameFrame ==0 )
			{
				// increment the lockstep frame and perform actions for the next frame
				if(hasActions(lockstepFrame + 1)){
					//System.out.println("has actions for frame: "+(lockstepFrame+1));
					// send out the actions from this frame

					Action action = actionsToSend.get(lockstepFrame);
					//System.out.println("send the actions i made during frame: "+(lockstepFrame)+": "+String.valueOf(action));
					client.sendTCP(action);

					// move to the next lockstep frame
					lockstepFrame++;
					// update the game model
					//System.out.println("update the game model to frame: "+(lockstepFrame));
					for (User player : users.values()) {
						LongMap<Action> receivedActionsForPlayer = receivedActions.get(player.id);
						Action a = receivedActionsForPlayer.get(lockstepFrame);
						a.performAction(scenario);
					}

					//prepare variables and things for getting users input in the next lockstep frame
					//System.out.println("prepare action for frame: "+(lockstepFrame));
					Action newAction = new Action();
					newAction.playerId = user.id;
					newAction.lockstepFrame = lockstepFrame;
					actionsToSend.put(lockstepFrame,newAction);

					scenario.update(strictFrameLength);
					gameFrame++;
				}else{
					//System.out.println("NO ACTIONS for frame: "+(lockstepFrame+1));
				}

			}
			else
			{
				// regular game frame, just update the model
				scenario.update(strictFrameLength);
				gameFrame++;
				if(gameFrame == gameFramesPerLocksetpTurn) {
					gameFrame = 0;
				}
			}

			accumilatedTime -= frameLength;
		}
	}

	@Override
	public void sendCommand(Command command)
	{
		Action action = actionsToSend.get(lockstepFrame);
		if(action.commands == null){
			action.commands = new Array<Command>(true, 4, Command.class);
		}
		action.commands.add(command);
	}

	private boolean hasSentReadyAction = false;

	@Override
	public void sendReadyAction(){
		if(!hasSentReadyAction && users.size >1)
		{
			hasSentReadyAction = true;
			ReadyToStart readyAction = new ReadyToStart();
			readyAction.playerId = user.id;
			client.sendTCP(readyAction);
		}

	}

	public int numLockstepFramesInBuffer()
	{
		for(int bufferUsed = 5; bufferUsed >0; bufferUsed--)
		{
			if(hasActions(lockstepFrame+bufferUsed)){
				return bufferUsed;
			}
		}
		return 0;
	}

	@Override
	public boolean isAllPlayersReady()
	{
		return hasActions(1);
	}

	private boolean hasActions(long lockstepFrame)
	{
		for (User player : users.values()) {
			LongMap<Action> receivedActionsForPlayer = receivedActions.get(player.id);
			Action action = receivedActionsForPlayer.get(lockstepFrame);
			if(action == null){
				return false;
			}
		}
		return true;
	}

	public void onConnected(Connection connection){
		Login login = new Login();
		login.user = user;
		client.sendTCP(login);
		UtLog.trace("sent server login message");
		// durring game...
		//MoveCharacter msg = new MoveCharacter();
		//client.sendTCP(msg);
	}

	public void onReceived(Connection c, Object message){
		if(!(message instanceof FrameworkMessage.KeepAlive)){
			UtLog.trace("received message: "+message.getClass().getSimpleName());
		}

		if(message instanceof Action){
			Action action = (Action) message;

			// store the action received
			LongMap<Action> receivedActionsForPlayer = receivedActions.get(action.playerId);
			receivedActionsForPlayer.put(action.lockstepFrame, action);

			// confirm to the server that we received this action
			if(action.playerId != user.id)
			{
				// we dont need to confirm our own actions...
				ActionConfirmation actionConfirmation = new ActionConfirmation();
				actionConfirmation.confirmedByPlayerId = user.id;
				actionConfirmation.playerId = action.playerId;
				actionConfirmation.lockstepFrame = action.lockstepFrame;
				client.sendTCP(actionConfirmation);
			}
		}else if (message instanceof RegistrationRequired) {
			Register register = new Register();
			register.name = "new name";
			register.otherStuff = "other stuff";
			client.sendTCP(register);
		}else if (message instanceof AddUser) {
			AddUser msg = (AddUser)message;
			if(client.getID() == msg.user.id){
				//this msg is about this local player, update his object
				user.set(msg.user);
				UtLog.info("received my own player information, updating my local record");

				// create the initial action to send so there is always and action to send...
				// TODO: this concept needs to be better merged with the "ReadyToStart".
				// this should be the ready to start. and the first call "perform actions"
				// is what kicks off the simlulation..
				Action initialAction = new Action();
				initialAction.playerId = user.id;
				initialAction.lockstepFrame = 0;
				actionsToSend.put(0,initialAction);
			}

			boolean newPlayer = !users.containsKey(msg.user.id);
			if(newPlayer){
				users.put(msg.user.id, msg.user);
				UtLog.info(msg.user.id + "-" + msg.user.name + " added");

				// update the receivedActions map to make sure there are
				// varaibles made for this new player.
				LongMap<Action> receivedActionsForPlayer = new LongMap<Action>(256);
				receivedActions.put(msg.user.id, receivedActionsForPlayer);

			}else{
				User existingPlayer = users.get(msg.user.id);
				existingPlayer.id = msg.user.id;
				existingPlayer.name = msg.user.name;
				UtLog.info(msg.user.id + "-" + msg.user.name + " updated");
			}

			scenario.addPlayer(msg.user);

		}else if (message instanceof RemoveUser) {
			RemoveUser msg = (RemoveUser)message;

			User existingPlayer = users.remove(msg.user.id);
			if (existingPlayer != null){
				UtLog.info(existingPlayer.id + "-" + existingPlayer.name + " removed");
			}

			scenario.removePlayer(msg.user);
		}
	}

	public void onDisconnected(Connection connection){
		UtLog.trace("disconnected from server");

		//System.exit(0);
	}

	public void onIdle (Connection connection) {
		UtLog.trace("idle");
	}


	@Override
	public String toString() {
		return "NetworkedGameClient{" +
			"\n\tframeLength=" + frameLength +
			"\n\tgameFrame=" + gameFrame +
			"\n\tlockstepFrame=" + lockstepFrame +
			"\n\thasSentReadyAction=" + hasSentReadyAction +
			"\n\tgameFramesPerLocksetpTurn=" + gameFramesPerLocksetpTurn +
			"\n\taccumilatedTime=" + accumilatedTime +
			"\n}";
	}

	public static void main(String[] args) {
		UtLog.logLevel = UtLog.TRACE;
		User player = new User();
		player.name ="Gergh";
		NetworkedGameClient networkedGameClient = new NetworkedGameClient(player,null);
		networkedGameClient.connectToServer("localHost", 27677, 27677);


	}

}
