package asf.medieval.view.editor;

import asf.medieval.painter.Painter;
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
import com.badlogic.gdx.utils.Align;

import java.nio.file.Path;
import java.nio.file.WatchEvent;


/**
 * Created by daniel on 11/26/15.
 */
public class EditorView extends SelectNode implements View, FileWatcher.FileChangeListener, Painter.CoordProvider {


	private Container<Actor> baseTableContainer;


	private FileWatcher fileWatcher;


	public EditorView(MedievalWorld world) {
		super("Editor",world);
		// Create SubEditors

		// Game
		GameEditorPane gameEditorSubNode = new GameEditorPane(world);
		HorizontalRowNode gameEditorMode = new HorizontalRowNode("Game",world, gameEditorSubNode);

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

		baseTableContainer = new Container<Actor>(getToolbarActor());
		baseTableContainer.setFillParent(true);
		baseTableContainer.align(Align.topLeft);
		baseTableContainer.fillX();
		world.stage.addActor(baseTableContainer);


		setEnabled(true);
		refreshUi();   // refreshUi() always needs to be called after setEnabled()


		// Editor view functionality (file watches etc)

		// TODO: muuuuhhhh, might need to do relative and local watches for each directory
//		fileWatcher = new FileWatcher(this);
//		fileWatcher.addWatch(FileManager.relative("Terrain"));
//		fileWatcher.addWatch(Gdx.files.local("Shaders"));





	}


	@Override
	public void dispose() {
		fileWatcher.dispose();
		super.dispose();
	}

	public void setToolbarVisible(boolean visible) {
		if (visible) {
			world.stage.addActor(baseTableContainer);
		} else {
			baseTableContainer.remove();
		}
	}

	public boolean isToolbarVisible() {
		return baseTableContainer.getParent() != null;
	}

	public void toggleToolbarVisible() {
		setToolbarVisible(!isToolbarVisible());
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Input.Keys.TAB:
				toggleToolbarVisible();
				return true;
		}
		return false;
	}

	@Override
	public void onFileChanged(WatchEvent<Path> event) {

		/*

		System.out.println("file changed:" + event.context() + ", kind: " + event.kind());

		String fileChanged = String.valueOf(event.context());

		if (fileChanged.endsWith(".ter")) {
			System.out.println("file changed:" + fileChanged + ", kind: " + event.kind());
			//final Terrain terrain = world.terrainView.terrain;
			//terrain.loadTerrain(terrain.parameter.name);
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
