package asf.medieval.net;

import asf.medieval.ServerApp;
import asf.medieval.desktop.DesktopLauncher;
import asf.medieval.model.Player;
import asf.medieval.utility.UtLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by daniel on 11/15/15.
 */
public class GameServerTest {
	private static ServerApp serverApp;
	private static final String hostName = "localhost";
	private static final GameServerConfig gameServerConfig = new GameServerConfig();

	@BeforeClass
	public static void beginTests() {
		UtLog.logLevel = UtLog.TRACE;
		serverApp = DesktopLauncher.launchServerApp(gameServerConfig);
	}

	@AfterClass
	public static void endTests() {
		Gdx.app.exit();
	}

	private Array<NetworkedGameClient> gameClients = new Array<NetworkedGameClient>();

	@Before
	public void setUp() throws Exception
	{

	}

	@After
	public void tearDown() throws Exception {
		Thread.sleep(500);
		for (NetworkedGameClient networkedGameClient : gameClients) {
			networkedGameClient.dispose();
		}
		Thread.sleep(500);
	}



	public void clientLogin() throws Exception
	{
		Thread.sleep(500);
		Player player = new Player();
		player.name = "Test Player: "+gameClients.size;
		NetworkedGameClient networkedGameClient =new NetworkedGameClient();
		networkedGameClient.connectToServer(hostName, gameServerConfig.tcpPort, gameServerConfig.udpPort, player);

		gameClients.add(networkedGameClient);
		boolean passedTest = false;
		int loops = 0;
		while(networkedGameClient.client.isConnected()){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(networkedGameClient.player.id >0) {
				passedTest = networkedGameClient.client.getID() == networkedGameClient.player.id;
				break;
			}else if(loops++ > 15){
				networkedGameClient.dispose();
				passedTest = false;
				break;
			}
		}


		Thread.sleep(500);

		assertTrue("Client Log in and get assigned an id",passedTest);


	}
}