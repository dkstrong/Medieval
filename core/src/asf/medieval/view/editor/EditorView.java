package asf.medieval.view.editor;

import asf.medieval.painter.Painter;
import asf.medieval.terrain.Terrain;
import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.FileManager;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Path;
import java.nio.file.WatchEvent;


/**
 * Created by daniel on 11/26/15.
 */
public class EditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor, Painter.CoordProvider {

	public final MedievalWorld world;

	private Container<Actor> baseTableContainer;


	public ModeSelectPane modeSelectPane;

	private FileWatcher fileWatcher;
	public final GameEditorMode gameEditorMode;
	public final ModeSelectPane terrainEditorMode;


	public final EditorMode fileMode;
	public final TerrainHeightMode heightMode;
	public final TerrainWeightMode weightMode;

	public EditorView(MedievalWorld world) {
		this.world = world;
		// Create SubEditors

		fileMode = new TerrainFileMode(this);
		heightMode = new TerrainHeightMode(this);
		weightMode = new TerrainWeightMode(this);


		gameEditorMode = new GameEditorMode(world);
		terrainEditorMode = new ModeSelectPane("Terrain",world, fileMode, heightMode, weightMode);
		modeSelectPane = new ModeSelectPane("Editor", world,gameEditorMode,terrainEditorMode);

		gameEditorMode.initUi();

		fileMode.initUi();
		heightMode.initUi();
		weightMode.initUi();

		terrainEditorMode.initUi();

		modeSelectPane.initUi();



		{
			// This is the base editor, set up the master container
			baseTableContainer = new Container<Actor>(modeSelectPane.getToolbarActor());
			baseTableContainer.setFillParent(true);
			baseTableContainer.align(Align.topLeft);
			baseTableContainer.fillX();
			world.stage.addActor(baseTableContainer);
		}


		// TODO: muuuuhhhh, might need to do relative and local watches for each directory
//		fileWatcher = new FileWatcher(this);
//		fileWatcher.addWatch(FileManager.relative("Terrain"));
//		fileWatcher.addWatch(Gdx.files.local("Shaders"));


		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				modeSelectPane.setEnabled(true);
				refreshUi();
			}
		});


	}

	public void refreshUi(){
		modeSelectPane.refreshUi();
	}

	@Override
	public void update(float delta) {
		modeSelectPane.update(delta);
	}

	@Override
	public void render(float delta) {
		modeSelectPane.render(delta);
	}

	@Override
	public void dispose() {
		fileWatcher.dispose();
		modeSelectPane.dispose();
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
	public boolean keyDown(int keycode) {
		return false;
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
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
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
