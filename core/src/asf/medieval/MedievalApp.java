package asf.medieval;

import asf.medieval.net.GameServerConfig;
import asf.medieval.utility.UtLog;
import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Random;

public class MedievalApp extends ApplicationAdapter {
	public Preferences prefs;

	public MedievalWorld.Settings worldSettings;
	public MedievalWorld world;

	public Stage stage;
	public Skin skin;
	public TextureAtlas pack;
	public I18NBundle i18n;


	@Override
	public void create () {
		Gdx.input.setCatchMenuKey(true);
		Gdx.input.setCatchBackKey(true);
		Gdx.graphics.setContinuousRendering(true);
		initPrefs();
		initTextures();
		initI18n();
		loadStraightInToGame();
	}
	private void initPrefs()
	{
		prefs = Gdx.app.getPreferences(Globals.gameName);
		int settingsVersion = prefs.getInteger(Globals.settingsVersion, Globals.version);


		prefs.putInteger(Globals.settingsVersion, Globals.version);
	}

	private void initTextures()
	{
		skin = new Skin(Gdx.files.internal("Packs/GameSkin.json"));
		pack = skin.getAtlas();

	}

	private void initI18n()
	{
		FileHandle baseFileHandle = Gdx.files.internal("i18n/Menu");
		Locale locale = new Locale("en");
		i18n = I18NBundle.createBundle(baseFileHandle, locale);
	}

	@Override
	public void resize(int width, int height) {
		//System.out.println("app resize: "+width+"x"+height);
		if(stage != null)
			stage.getViewport().update(width, height, true);
		// TODO: resize screen if it is not null
		if(world!=null)
			world.resize(width, height);


	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		if(delta > 0.06f) delta = 0.06f;

		if(world != null) world.render(delta);
		else  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// TODO if screen not null, render screen

		if(stage != null){
			stage.act(delta);
			stage.draw();
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}


	@Override
	public void dispose() {
		//if (dialogScreen != null) {
		//	dialogScreen.hide();
		//	dialogScreen = null;
		//}
		setScreen(null);

		if (skin != null) {
			skin.dispose();
			skin = null;
			pack = null;
		}

		//if (backgroundTexture != null) {
		//	backgroundTexture.dispose();
		//	backgroundTexture = null;
		//	backgroundImage = null;
		//}

	}


	private void loadStraightInToGame()
	{
		MedievalWorld.Settings settings = new MedievalWorld.Settings();



		settings.random = new Random(1);
		/*
		settings.gameServerConfig = new GameServerConfig();
		Client client = new Client();
		InetAddress inetAddress = client.discoverHost(settings.gameServerConfig.udpPort, 500);
		try {
			client.dispose();
		} catch (IOException e) {
			e.printStackTrace();
			UtLog.error("exception throw while disposing client", e);
		}

		if(inetAddress == null){
			settings.server = true;
		}else{
			settings.client = true;
		}
		*/

		loadWorld(settings);

	}

	public void loadWorld(MedievalWorld.Settings settings)
	{
		if(world!=null)
			throw new IllegalStateException("world is already loaded");

		worldSettings = settings;
		world = new MedievalWorld(this, settings);
		// when loading world form app start this is redundnat...
		world.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	}

	public void unloadWorld()
	{
		if(world != null)
		{
			world.dispose();
			world = null;
		}
	}



	public void onSimulationStarted() {
		setScreen(null);
	}

	public void exitApp()
	{
		if(world != null)
		{
			// TODO: do something like save the world
		}
		Gdx.app.exit();
	}

	public void setScreen(ScreenId screenId)
	{

	}
}
