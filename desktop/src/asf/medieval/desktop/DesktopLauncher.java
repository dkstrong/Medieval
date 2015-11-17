package asf.medieval.desktop;

import asf.medieval.ServerApp;
import asf.medieval.net.GameServerConfig;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import asf.medieval.MedievalApp;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Medieval";
		String osName = System.getProperty("os.name").toLowerCase();

		config.width = 1280;
		config.height = 720;
		config.resizable = true;

		config.width = 960;
		config.height = 540;


		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		config.vSyncEnabled = false;
		config.samples = 0;
		config.r = 5;
		config.g = 6;
		config.b = 5;
		config.a = 0;
		new LwjglApplication(new MedievalApp(), config);
	}

	public static ServerApp launchServerApp(GameServerConfig gameServerConfig) {

		ServerApp serverApp = new ServerApp(gameServerConfig);
		HeadlessApplicationConfiguration conf = new HeadlessApplicationConfiguration();
		new HeadlessApplication(serverApp, conf);

		return serverApp;

	}
}
