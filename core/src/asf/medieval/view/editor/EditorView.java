package asf.medieval.view.editor;

import asf.medieval.model.Token;
import asf.medieval.painter.Painter;
import asf.medieval.strictmath.VecHelper;
import asf.medieval.terrain.Terrain;
import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.nio.file.Path;
import java.nio.file.WatchEvent;


/**
 *Created by daniel on 11/26/15.
 */
public class EditorView extends SelectNode implements View, FileWatcher.FileChangeListener, Painter.CoordProvider {


	private Container<Actor> baseTableContainer;
	private Container<Label> baseFpsLabelContainer;

	private FileWatcher fileWatcher;


	public EditorView(MedievalWorld world) {
		super("Editor",world);
		// Create SubEditors

		// Game
		GameEditorPane gameEditorSubNode = new GameEditorPane(this);
		BuilderEditorPane builderEditorPane = new BuilderEditorPane(this);
		HorizontalRowNode gameEditorMode = new HorizontalRowNode("Game",world, gameEditorSubNode,builderEditorPane);

		// Terrain
		//	Height
		TerrainHeightPane terrainHeightPane = new TerrainHeightPane(this);
		PainterPane terrainHeightPainterPane = new PainterPane(world, terrainHeightPane);
		HorizontalRowNode heightHorizontalNode = new HorizontalRowNode("Elevation",world, terrainHeightPainterPane, terrainHeightPane);

		//	Weight
		TerrainWeightPane terrainWeightPane = new TerrainWeightPane(this);
		PainterPane terrainWeightPainerPane = new PainterPane(world, terrainWeightPane);
		HorizontalRowNode weightHorizontalNode = new HorizontalRowNode("Texture",world, terrainWeightPainerPane, terrainWeightPane);

		//	File
		TerrainFilePane fileMode = new TerrainFilePane(this, terrainHeightPainterPane, terrainWeightPainerPane);

		SelectNode terrainEditorMode = new SelectNode("Terrain",world, fileMode, heightHorizontalNode, weightHorizontalNode);


		// Editor View
		this.modes = new EditorNode[]{gameEditorMode,terrainEditorMode};
		initUi();

		//setEnabled(true);
		//setMode(terrainEditorMode);
		//terrainEditorMode.setMode(weightHorizontalNode);
		//refreshUi();   // refreshUi() always needs to be called after setEnabled()


		// Editor view functionality (file watches etc)

		// TODO: muuuuhhhh, might need to do relative and local watches for each directory
//		fileWatcher = new FileWatcher(this);
//		fileWatcher.addWatch(FileManager.relative("Terrain"));
//		fileWatcher.addWatch(Gdx.files.local("Shaders"));


	}

	@Override
	public void initUi() {
		super.initUi();

		Label fpsLabel = new Label("",world.app.skin);
		fpsLabel.setAlignment(Align.topRight, Align.topRight);
		baseFpsLabelContainer = new Container<Label>(fpsLabel);
		baseFpsLabelContainer.setFillParent(true);
		baseFpsLabelContainer.align(Align.topRight).pad(10);
		//world.stage.addActor(baseFpsLabelContainer);

		baseTableContainer = new Container<Actor>(getToolbarActor());
		baseTableContainer.setFillParent(true);
		baseTableContainer.align(Align.topLeft);
		baseTableContainer.fillX();
		//world.stage.addActor(baseTableContainer);
	}

	@Override
	public void refreshUi() {
		super.refreshUi();
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		if(baseFpsLabelContainer.getParent()!=null){
			updateFpsLabel();

		}
	}

	private void updateFpsLabel(){
		// http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=16813
		/*
		Runtime rt = Runtime.getRuntime();
		long max = rt.maxMemory();
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		long used = total - free;
		int availableProcessors = rt.availableProcessors();
		*/

		String soldierStatusString = "";
		Token soldier = world.scenario.getSoldier(2);
		if(soldier != null){
			soldierStatusString+="Pos: "+soldier.location;
			soldierStatusString+="\nVel: "+soldier.agent.velocity;
			soldierStatusString+="\nElevation: "+soldier.elevation;
			soldierStatusString+="\nDirection: "+soldier.direction;

			Vector3 normal = world.terrainView.terrain.getWeightedNormalAt(VecHelper.toVector3(soldier.location, new Vector3()), new Vector3());
			soldierStatusString+="\nNormal: "+UtMath.round(normal,2);
		}
		baseFpsLabelContainer.getActor().setText(

			"FPS: " + Gdx.graphics.getFramesPerSecond() +
				"\nMem: " + (Gdx.app.getJavaHeap() / 1024 / 1024) + " MB" +
				"\nTokens: " + world.scenario.tokens.size +
				"\n" + soldierStatusString


		);
	}

	@Override
	public void dispose() {
		fileWatcher.dispose();
		super.dispose();
	}

	public boolean isToolbarVisible() {
		return baseTableContainer.getParent() != null;
	}

	public void setToolbarVisible(boolean visible) {
		if (visible) {
			world.stage.addActor(baseTableContainer);
			setEnabled(true);
			refreshUi();   // refreshUi() always needs to be called after setEnabled()
		} else {
			baseTableContainer.remove();
			setEnabled(false);
			refreshUi();   // refreshUi() always needs to be called after setEnabled()
		}
	}

	public void toggleToolbarVisible() {
		setToolbarVisible(!isToolbarVisible());
	}

	public boolean isFpsVisible(){
		return baseFpsLabelContainer.getParent() != null;
	}

	public void setFpsVisible(boolean visible) {
		if (visible) {
			world.stage.addActor(baseFpsLabelContainer);
		} else {
			baseFpsLabelContainer.remove();
		}
	}

	public void toggleFpsVisible() {
		setFpsVisible(!isFpsVisible());
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.GRAVE){
			toggleToolbarVisible();
			return true;
		}

		if(!enabled)
			return false;

		switch (keycode) {
			case Input.Keys.C:
				world.cameraManager.rtsCamController.printCamValues();
				return true;
			case Input.Keys.TAB:
				EditorNode mode = getMode();
				for (int i = 0; i < modes.length; i++) {
					if(modes[i] == mode){
						if(++i >= modes.length) i=0;
						setMode(modes[i]);
						return true;
					}
				}
				return true;
		}
		return super.keyUp(keycode);
	}

	@Override
	public void onFileChanged(WatchEvent<Path> event) {

		/*

		System.out.println("file changed:" + event.context() + ", kind: " + event.kind());

		String fileChanged = String.valueOf(event.context());

		if (fileChanged.endsWith(".ter")) {
			System.out.println("file changed:" + fileChanged + ", kind: " + event.kind());
			//final Terrain terrain = world.terrainView.terrain;
			//terrain.init(terrain.parameter.name);
		} else if (fileChanged.endsWith(".glsl")) {
			System.out.println("file changed:" + fileChanged + ", kind: " + event.kind());
			final Terrain terrain = world.terrainView.terrain;
			terrain.getTerrainChunk(0, 0).renderable.shader = null;
			world.modelShaderProvider.terrainShaderProvider.clearShaderCache();
		}

		 */
	}

	private final Vector3 tempTranslation = new Vector3();

	@Override
	public void getPixmapCoord(int screenX, int screenY, int pixmapWidth, int pixmapHeight, Vector2 store) {
		world.hudView.hudCommandView.getWorldCoord(screenX, screenY, tempTranslation);
		Terrain terrain = world.terrainView.terrain;
		store.x = UtMath.scalarLimitsInterpolation(tempTranslation.x, terrain.corner00.x, terrain.corner11.x, 0, pixmapWidth - 1);
		store.y = UtMath.scalarLimitsInterpolation(tempTranslation.z, terrain.corner00.z, terrain.corner11.z, 0, pixmapHeight - 1);
	}


}
