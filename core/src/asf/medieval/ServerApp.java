package asf.medieval;

import asf.medieval.net.GameServer;
import asf.medieval.net.GameServerConfig;
import com.badlogic.gdx.ApplicationListener;

/**
 * Created by daniel on 11/15/15.
 */
public class ServerApp implements ApplicationListener {

	public final String gameServerConfigLoc;
	public GameServerConfig gameServerConfig;
	public GameServer gameServer;

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

		gameServer = new GameServer();
		gameServer.bindServer(gameServerConfig);
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
