package asf.medieval;

import asf.medieval.model.Scenario;
import asf.medieval.model.ScenarioFactory;
import asf.medieval.net.GameServer;
import asf.medieval.net.GameServerConfig;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.StatusPrinter;
import com.badlogic.gdx.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by daniel on 11/15/15.
 */
public class ServerApp implements ApplicationListener {

	private static final Logger LOG = LoggerFactory.getLogger(ServerApp.class);

	public static boolean printLogFile  = false;
	private final String gameServerConfigLoc;
	private GameServerConfig gameServerConfig;
	private GameServer gameServer;

	public ServerApp()
	{
		gameServerConfigLoc = null;
	}

	public ServerApp(GameServerConfig gameServerConfig) {
		gameServerConfigLoc = null;
		this.gameServerConfig = gameServerConfig;
	}

	public ServerApp(String gameServerConfigLoc) {
		this.gameServerConfigLoc = gameServerConfigLoc;
	}

	@Override
	public void create() {
		if(gameServerConfigLoc != null)
			gameServerConfig = new GameServerConfig(gameServerConfigLoc);
		else if(gameServerConfig == null)
			gameServerConfig = new GameServerConfig();

		// TODO: if game server config has a file location, then apply that to the UtLog here..

		//Scenario scenario = ScenarioFactory.scenarioTest();

		try {
			gameServer = new GameServer(gameServerConfig.tcpPort, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		if(gameServer!=null){
			gameServer.dispose();
			gameServer = null;
		}

	}
}
