package asf.medieval.view;

import asf.medieval.MedievalApp;
import asf.medieval.model.Scenario;
import asf.medieval.model.ScenarioFactory;
import asf.medieval.model.SoldierToken;
import asf.medieval.net.NetworkedGameClient;
import asf.medieval.net.GameClient;
import asf.medieval.net.GameServer;
import asf.medieval.net.GameServerConfig;
import asf.medieval.net.OfflineGameClient;
import asf.medieval.net.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Random;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class MedievalWorld implements Disposable, Scenario.Listener {


	public static class Settings {
		public GameServerConfig gameServerConfig;
		public boolean server;
		public String hostName;
		public boolean client;
		public Random random;
	}

	protected final MedievalApp app;
	protected final Settings settings;
	public final CameraManager cameraManager;
	public final Environment environment;
	public final Stage stage;
	public final DecalBatch decalBatch;
	public final DirectionalShadowLight shadowLight;
	public final ModelBatch shadowBatch;
	public final ModelBatch modelBatch;
	public final AssetManager assetManager;
	private boolean loading = true;
	private boolean paused = false;
	private final InputMultiplexer inputMultiplexer;
	/**
	 * during loading phase this is the input processor, when the simulation starts
	 * then inputMultiplexer is used
	 */
	private final InternalLoadingInputAdapter internalLoadingInputAdapter = new InternalLoadingInputAdapter();
	public TextureAtlas pack;

	public GameServer gameServer;
	public GameClient gameClient;

	public final Scenario scenario;
	protected final Array<GameObject> gameObjects;
	private HudGameObject hudGameObject;
	public TerrainGameObject terrainGameObject;

	public MedievalWorld(MedievalApp app, Settings settings)  {
		this.app = app;
		this.settings = settings;
		cameraManager = new CameraManager();
		environment = new Environment();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.54f, 0.54f, 0.54f, 1f));
		environment.set(new ColorAttribute(ColorAttribute.Fog, 0.13f, 0.13f, 0.13f, 1f));
		environment.add(new DirectionalLight().set(0.65f, 0.65f, 0.65f, -1f, -0.8f, 0.3f));

		environment.add((shadowLight = new DirectionalShadowLight(2048, 2048, 200,200, .1f, 800f))
			.set(0.65f, 0.65f, 0.65f, -1f, -0.8f, 0.3f));
		environment.shadowMap = shadowLight;

		stage = new Stage(new ScreenViewport());
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		decalBatch = new DecalBatch(32, new CameraGroupStrategy(cameraManager.cam));

		modelBatch = new ModelBatch();
		assetManager = new AssetManager();
		inputMultiplexer = new InputMultiplexer(internalLoadingInputAdapter, stage);

		Gdx.input.setInputProcessor(internalLoadingInputAdapter);


		assetManager.load("Packs/Game.atlas", TextureAtlas.class);
		assetManager.load("Models/Characters/Skeleton.g3db", Model.class);

		gameObjects = new Array<GameObject>(false, 128, GameObject.class);


		scenario = ScenarioFactory.scenarioFlat(settings.random);

		if(settings.server){
			gameServer = new GameServer();
			gameServer.bindServer(settings.gameServerConfig);
		}

		if(settings.server ||  settings.client){
			String hostname = settings.server ? "localhost" : settings.hostName;
			NetworkedGameClient networkedNetworkedGameClient = new NetworkedGameClient();
			networkedNetworkedGameClient.scenario = scenario;
			Player player = new Player();
			player.name = System.getProperty("user.name");
			networkedNetworkedGameClient.connectToServer(hostname, settings.gameServerConfig.tcpPort, settings.gameServerConfig.udpPort, player);
			gameClient = networkedNetworkedGameClient;
		}else{
			OfflineGameClient offlineGameClient = new OfflineGameClient();
			offlineGameClient.scenario = scenario;
			Player player = new Player();
			player.id = 1;
			player.name = System.getProperty("user.name");
			offlineGameClient.player = player;
			offlineGameClient.players.put(1,player);
			gameClient = offlineGameClient;
		}

	}

	private boolean hasDoneLoading =false;

	private void startSimulation()
	{
		if(!hasDoneLoading){
			hasDoneLoading = true;

			hasDoneLoading = true;
			pack =  assetManager.get("Packs/Game.atlas", TextureAtlas.class);
			//sounds.init();
			//fxManager.init();
			//dungeonApp.music.setPlaylist(SongId.MainTheme, SongId.Arabesque, SongId.RitualNorm);
			//dungeonApp.music.playSong(SongId.RitualNorm);

			addGameObject(hudGameObject = new HudGameObject(this));

			//addGameObject(characterGameObject = new CharacterGameObject(this, mission.characterToken));
			//cameraManager.setChaseTarget(characterGameObject);


			//addGameObject(new SceneGameObject(this));
			addGameObject(terrainGameObject=new TerrainGameObject(this));


			scenario.setListener(this);

			inputMultiplexer.addProcessor(cameraManager.twRtsCamController);
			inputMultiplexer.addProcessor(hudGameObject);

		}

		gameClient.sendReadyAction();
		if(gameClient.isAllPlayersReady()){
			loading = false;
			app.onSimulationStarted(); // inform the dungeon app to close the loading screen
			setPaused(false); // call this to apply the gameplay input processors
			Gdx.gl.glClearColor(100 / 255f, 149 / 255f, 237 / 255f, 1f);
		}
	}

	public <T extends GameObject> T addGameObject(T gameObject) {
		gameObjects.add(gameObject);
		return gameObject;
	}

	public void removeGameObject(GameObject gameObject) {
		gameObjects.removeValue(gameObject, true);
	}

	@Override
	public void onNewSoldier(SoldierToken soldierToken) {

		addGameObject(new SoldierGameObject(this,soldierToken));
	}

	public void render(final float delta) {
		if (loading) {
			if (paused)
				Gdx.graphics.requestRendering();
			if (hasDoneLoading || assetManager.update()) {
				startSimulation();
			}
		} else {
			// update
			if (!paused) {
				//hudSpatial.updateInput(delta);
				gameClient.updateGameFrame(delta);
				float modelDelta = 0.05f;
				float deltaRatio = modelDelta / delta;
				float adjustedDelta = delta/deltaRatio;
				for (final GameObject gameObject : gameObjects) {
					gameObject.update(delta);
				}
			}
			cameraManager.update(delta);
			stage.act(delta);

			// render
			Gdx.gl.glClearColor(100 / 255f, 149 / 255f, 237 / 255f, 1f); // shadow light or shadow batch seems to reset the clear color- so do it every time...
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			shadowLight.begin(cameraManager.twRtsCamController.center, cameraManager.cam.direction);
			shadowBatch.begin(shadowLight.getCamera());

			modelBatch.begin(cameraManager.cam);
			//fxManager.beginRender();

			final float effectiveDelta = paused ? 0 : delta;
			for (final GameObject gameObject : gameObjects) {
				gameObject.render(effectiveDelta);
			}


			shadowBatch.end();
			shadowLight.end();

			modelBatch.end();
			decalBatch.flush();
			//fxManager.endRender();

			stage.draw();
		}
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		// TODO: when playing online, user can not actually pause the game, only bring up the pause menu...
		if (!paused) {
			if (loading)
				Gdx.input.setInputProcessor(internalLoadingInputAdapter);
			else
				Gdx.input.setInputProcessor(inputMultiplexer);
		}

		this.paused = paused;

	}

	public void resize(int width, int height) {

		stage.getViewport().update(width, height, true);

		if (hudGameObject != null)
			hudGameObject.resize(width, height);

		cameraManager.resize(width, height);

	}

	@Override
	public void dispose() {
		decalBatch.dispose();
		shadowLight.dispose();
		shadowBatch.dispose();
		modelBatch.dispose();
		//sounds.dispose();
		//fxManager.dispose();
		assetManager.dispose();
		stage.dispose();
		if (Gdx.input.getInputProcessor() == inputMultiplexer || Gdx.input.getInputProcessor() == internalLoadingInputAdapter)
			Gdx.input.setInputProcessor(null);
	}



	/**
	 * this input is accepted on the loading screen, the regular internal input adapter only works during gameplay
	 */
	private class InternalLoadingInputAdapter extends InputAdapter {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
				if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					app.exitApp();
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
				//app.setScreen(ScreenId.Pause);
				return true;
			}
			return false;
		}
	}
}
