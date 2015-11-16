package asf.medieval.net;

import asf.medieval.ServerApp;
import asf.medieval.desktop.DesktopLauncher;
import asf.medieval.utility.UtLog;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import com.badlogic.gdx.utils.Array;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

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

		ServerApp serverApp = DesktopLauncher.launchServer(gameServerConfig);
	}

	@AfterClass
	public static void endTests() {
		//Gdx.app.exit();
	}

	private Array<GameClient> gameClients = new Array<GameClient>();

	@Before
	public void setUp() throws Exception
	{

	}

	@After
	public void tearDown() throws Exception {
		for (GameClient gameClient : gameClients) {
			//gameClient.dispose();
		}
	}


	@Test
	public void clientLogin() throws Exception
	{
		Player player = new Player();
		player.name = "Test Player: "+gameClients.size;
		GameClient gameClient =new GameClient(hostName, gameServerConfig.tcpPort, player);

		gameClients.add(gameClient);

		assertTrue(true);


	}
}