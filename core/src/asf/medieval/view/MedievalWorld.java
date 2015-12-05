package asf.medieval.view;

import asf.medieval.CursorId;
import asf.medieval.MedievalApp;
import asf.medieval.model.ModelId;
import asf.medieval.model.steer.InfantryController;
import asf.medieval.model.Scenario;
import asf.medieval.model.ScenarioRand;
import asf.medieval.model.steer.StructureController;
import asf.medieval.model.Token;
import asf.medieval.net.NetworkedGameClient;
import asf.medieval.net.GameClient;
import asf.medieval.net.GameServer;
import asf.medieval.net.GameServerConfig;
import asf.medieval.net.OfflineGameClient;
import asf.medieval.model.Player;
import asf.medieval.net.User;
import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainLoader;
import asf.medieval.utility.FileManager;
import asf.medieval.view.editor.EditorInputEater;
import asf.medieval.view.editor.EditorView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Cursor;
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
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Random;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class MedievalWorld implements Disposable, Scenario.Listener, RtsCamController.ElevationProvider {


	public static class Settings {
		public GameServerConfig gameServerConfig;
		public boolean server;
		public String hostName;
		public boolean client;
		public boolean editor;
		public Random random;
	}

	public final MedievalApp app;
	public final Settings settings;
	public final CameraManager cameraManager;
	public final Environment environment;
	public final Stage stage;
	public final DecalBatch decalBatch;
	public final DirectionalShadowLight shadowLight;
	public final ModelBatch shadowBatch;
	public final MedievalShaderProvider modelShaderProvider;
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

	public final ResourceViewInfo[] resourceViewInfo;
	public final IntMap<ModelViewInfo> modelViewInfo = new IntMap<ModelViewInfo>(8);
	public final Scenario scenario;
	public final Array<View> gameObjects;
	public HudView hudView;
	public EditorView editorView;
	public TerrainView terrainView;

	public MedievalWorld(MedievalApp app, Settings settings)  {
		this.app = app;
		this.settings = settings;
		cameraManager = new CameraManager(this);
		environment = new Environment();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.54f, 0.54f, 0.54f, 1f));
		environment.set(new ColorAttribute(ColorAttribute.Fog, 0.13f, 0.13f, 0.13f, 1f));
		environment.add(new DirectionalLight().set(0.65f, 0.65f, 0.65f, -1f, -0.8f, 0.3f));

		environment.add((shadowLight = new DirectionalShadowLight(2048, 2048, 200,200, 1f, 500f))
			.set(0.65f, 0.65f, 0.65f, -1f, -0.8f, 0.3f));
		environment.shadowMap = shadowLight;
		//shadowLight.getDepthMap().magFilter = Texture.TextureFilter.Linear;
		//shadowLight.getDepthMap().minFilter = Texture.TextureFilter.Linear;

		stage = new Stage(new ScreenViewport());
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		decalBatch = new DecalBatch(32, new CameraGroupStrategy(cameraManager.cam));

		// https://github.com/libgdx/libgdx/wiki/ModelBatch
		modelShaderProvider = new MedievalShaderProvider();
		modelBatch = new ModelBatch(null,modelShaderProvider, new DefaultRenderableSorter());

		assetManager = new AssetManager();

		assetManager.setLoader(Terrain.class, new TerrainLoader(new FileManager()));
		inputMultiplexer = new InputMultiplexer(internalLoadingInputAdapter, stage);

		Gdx.input.setInputProcessor(internalLoadingInputAdapter);

		resourceViewInfo = ResourceViewInfo.standardConfiguration();
		ModelViewInfo.standardConfiguration(modelViewInfo);

		assetManager.load("Packs/Game.atlas", TextureAtlas.class);

		for (ModelViewInfo modelViewInfo : this.modelViewInfo.values()) {
			for (String s : modelViewInfo.assetLocation) {
				assetManager.load(s, Model.class);
			}
		}

		//AssetDescriptor<Terrain> terrainAssetDescriptor = new AssetDescriptor<Terrain>("Terrain/new-terrain.ter", Terrain.class,TerrainLoader.getNewTerrainParamter("new-terrain"));
		//assetManager.load(terrainAssetDescriptor);
		assetManager.load("Terrain/muh.ter",Terrain.class);
		//assetManager.load("Models/skydome.g3db", Model.class);


		gameObjects = new Array<View>(false, 128, View.class);


		scenario =new Scenario(new ScenarioRand(settings.random) );

		if(settings.server){
			gameServer = new GameServer();
			gameServer.bindServer(settings.gameServerConfig);
		}

		if(settings.server ||  settings.client){
			User player = new User();
			player.name = System.getProperty("user.name");
			String hostname = settings.server ? "localhost" : settings.hostName;

			NetworkedGameClient netGameClient = new NetworkedGameClient(player,scenario);
			netGameClient.connectToServer(hostname, settings.gameServerConfig.tcpPort, settings.gameServerConfig.udpPort);
			gameClient = netGameClient;
		}else{
			User user = new User();
			user.id = 1;
			user.team = 1;
			user.name = System.getProperty("user.name");

			OfflineGameClient offGameClient = new OfflineGameClient(user, scenario);
			offGameClient.connectToServer();
			gameClient = offGameClient;
		}

	}

	private boolean hasDoneLoading =false;

	private void startSimulation()
	{
		if(!hasDoneLoading){
			hasDoneLoading = true;

			hasDoneLoading = true;
			pack =  assetManager.get("Packs/Game.atlas", TextureAtlas.class);

			app.setCusor(CursorId.DEFAULT);



			//sounds.init();
			//fxManager.init();
			//dungeonApp.music.setPlaylist(SongId.MainTheme, SongId.Arabesque, SongId.RitualNorm);
			//dungeonApp.music.playSong(SongId.RitualNorm);

			addGameObject(hudView = new HudView(this));

			//addGameObject(characterGameObject = new CharacterGameObject(this, mission.characterToken));
			//cameraManager.setChaseTarget(characterGameObject);
			//addGameObject(new ShaderTestView(this));


			addGameObject(terrainView =new TerrainView(this));
			//addGameObject(new TerrainDebugView(this));

			scenario.terrain = terrainView.terrain;

			scenario.setListener(this);


			if(settings.editor){
				addGameObject(editorView = new EditorView(this));
			}


			if(editorView!=null){
				inputMultiplexer.addProcessor(editorView);
			}

			inputMultiplexer.addProcessor(cameraManager.rtsCamController);

			if(editorView!=null){
				// this input processor ensures the game doesnt receive input
				// unless the editor toolbar is hidden.
				inputMultiplexer.addProcessor(new EditorInputEater(editorView));
			}
			inputMultiplexer.addProcessor(hudView);
			inputMultiplexer.addProcessor(hudView.hudBuildView);
			inputMultiplexer.addProcessor(hudView.hudCommandView);
			inputMultiplexer.addProcessor(hudView.hudSelectionView);
		}


		// TODO; this might be a race condition, I might need to check to ensure that the client has received all "AddUser" messages
		// TODO: before sending a ready action, or bad timing could lead to the scenario simulation starting- but the scenario
		// TODO: may not have been populated with all users yet.
		gameClient.sendReadyAction();
		if(gameClient.isAllPlayersReady()){
			loading = false;
			app.onSimulationStarted(); // inform the dungeon app to close the loading screen
			setPaused(false); // call this to apply the gameplay input processors
			Gdx.gl.glClearColor(100 / 255f, 149 / 255f, 237 / 255f, 1f);
			onAllPlayersReady();
		}
	}

	private void onAllPlayersReady(){
		for(int i= 0; i<0; i++){
			scenario.setRandomNonOverlappingPosition(scenario.newSoldier(1, new Vector2(0,0), ModelId.Skeleton.ordinal()),30,50,-50,50);
		}

		for(int i= 0; i<0; i++){
			scenario.setRandomNonOverlappingPosition(scenario.newSoldier(2, new Vector2(0,0), ModelId.Skeleton.ordinal()),-50,-30,-50,50 );
		}
		//scenario.newStructure(2, new Vector2(-20,-20));
		//scenario.newSoldier(1,new Vector2(-74.47005f, 169.50835f), true);

	}

	public <T extends View> T addGameObject(T gameObject) {
		gameObjects.add(gameObject);
		return gameObject;
	}

	public void removeGameObject(View view) {
		gameObjects.removeValue(view, true);
	}

	@Override
	public void onNewPlayer(Player player) {

	}

	@Override
	public void onUpdatePlayer(Player player) {

	}

	@Override
	public void onRemovePlayer(Player player) {

	}

	@Override
	public void onNewToken(Token token) {

		if(token.agent instanceof InfantryController){
			addGameObject(new InfantryView(this,token));
		}else if(token.agent instanceof StructureController){
			addGameObject(new StructureView(this,token));
			hudView.hudBuildView.refreshUi();

		}

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
				for (final View view : gameObjects) {
					view.update(delta);
				}
			}
			cameraManager.update(delta);
			stage.act(delta);

			// render
			Gdx.gl.glClearColor(100 / 255f, 149 / 255f, 237 / 255f, 1f); // shadow light or shadow batch seems to reset the clear color- so do it every time...
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			shadowLight.begin(cameraManager.rtsCamController.center, cameraManager.cam.direction);
			//shadowLight.begin(cameraManager.cam);
			shadowBatch.begin(shadowLight.getCamera());

			modelBatch.begin(cameraManager.cam);
			//fxManager.beginRender();

			final float effectiveDelta = paused ? 0 : delta;
			for (final View view : gameObjects) {
				view.render(effectiveDelta);
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

		if (hudView != null)
			hudView.resize(width, height);

//		if(editorView!= null)
//			editorView.resize(width,height);

		cameraManager.resize(width, height);

	}

	@Override
	public void dispose() {
		if(editorView!=null)
			editorView.dispose();

		if(gameServer!= null)
			gameServer.dispose();

		if(gameClient instanceof NetworkedGameClient)
			((NetworkedGameClient) gameClient).dispose();

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

	@Override
	public float getElevationAt(float x, float z)
	{
		return scenario.terrain.getElevation(x,z);

	}
}
