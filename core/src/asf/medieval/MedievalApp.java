package asf.medieval;

import asf.medieval.net.GameServerConfig;
import asf.medieval.utility.UtLog;
import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Random;

public class MedievalApp extends ApplicationAdapter {
	public Preferences prefs;
	private AbstractScreen screen;
	protected Group stageScreen, stageDialog;


	public MedievalWorld.Settings worldSettings;
	public MedievalWorld world;

	public Stage stage;
	public Skin skin;
	public TextureAtlas pack;
	private Texture backgroundTexture;
	public Image backgroundImage;
	public I18NBundle i18n;


	@Override
	public void create () {
		Gdx.input.setCatchMenuKey(true);
		Gdx.input.setCatchBackKey(true);
		Gdx.graphics.setContinuousRendering(true);
		initPrefs();
		initTextures();
		initI18n();
		setScreen(ScreenId.Main);
		//loadStraightInToGame();
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

		backgroundTexture = new Texture(Gdx.files.internal("Textures/Terrain/desert512.jpg"));
		backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		backgroundImage = new Image(new TiledDrawable(new TextureRegion(backgroundTexture)), Scaling.fill);

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
		if(world!=null)
			world.resize(width, height);
		if(stage != null)
			stage.getViewport().update(width, height, true);
		if (stageScreen != null)
			stageScreen.setBounds(0, 0, width, height);
		//if (stageDialog != null)
		//	stageDialog.setBounds(0, 0, width, height);
		if (screen != null)
			screen.resize(width, height);



	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		if(delta > 0.06f) delta = 0.06f;


		// client.update()

		if(world != null) world.render(delta);
		else  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(screen!=null)
			screen.render(delta);

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

		setScreen(null);

		if (skin != null) {
			skin.dispose();
			skin = null;
			pack = null;
		}

		if (backgroundTexture != null) {
			backgroundTexture.dispose();
			backgroundTexture = null;
			backgroundImage = null;
		}

		//client.dispose();

	}



	private void loadStraightInToGame()
	{
		MedievalWorld.Settings settings = new MedievalWorld.Settings();

		settings.random = new Random(1);

		final boolean onlineGame = false;
		if(onlineGame)
		{
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
				settings.hostName = inetAddress.getHostAddress();
			}
		}




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
		setScreen(ScreenId.Loading);

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

	public ScreenId getScreenId(){
		return screen == null ? null : screen.getScreenId();
	}

	public void setScreen(ScreenId screenId) {
		if (screenId == null) {
			setScreenInstance(null);
			return;
		}

		if (this.screen != null && this.screen.getScreenId() == screenId) {
			return; // already on this screen...
		}

		setScreenInstance(makeScreenInstance(screenId));

		if (screen.killsWorld() && world != null) {
			// we enqueue unloading the world because setScreen() may be called
			//in the middle of render which would mess things up because the world would be
			// disposed in the middle of rendering
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					unloadWorld();
					// TODO: here is where i would play menu music if i had some
					///music.setPlaylist(SongId.MainTheme, SongId.Arabesque, SongId.RitualNorm);
					//music.playSong(SongId.MainTheme);
					//music.setPaused(false);
				}
			});
		}



	}

	private AbstractScreen makeScreenInstance(ScreenId screenId) {
		switch (screenId) {
			case Main:
				return new MainScreen(this);
			case Loading:
				return new LoadingScreen(this);
			case Pause:
				return new PauseScreen(this);
			case GameOverScreen:
				return new GameOverScreen(this);
			default:
				throw new AssertionError(screenId);
		}
	}

	private void setScreenInstance(AbstractScreen screen) {
		if (this.screen != null)
			this.screen.hide();
		this.screen = screen;
		if (this.screen != null) {
			if (stage == null) {
				stage = new Stage(new ScreenViewport());
				stage.addActor(stageScreen = new Group());
				stage.addActor(stageDialog = new Group());
				// theres some kind of weird positioning bug involving adding containrs to groups
				// setting the groups bounds to the screen size seems to fix it
				stageScreen.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				// though if i size the dialog group then you cant click anything- theres some
				// might weird stuff going on with sceneui
			}
			stageScreen.clearChildren();
			this.screen.show();
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		} else {
			if (stage != null) {
				stageScreen = null;
				stageDialog = null;
				stage.dispose();
				stage = null;
			}
		}
	}
}
