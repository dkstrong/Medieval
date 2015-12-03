package asf.medieval.view.editor;

import asf.medieval.painter.Painter;
import asf.medieval.terrain.Terrain;
import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainEditorMode implements EditorMode, FileWatcher.FileChangeListener, Painter.CoordProvider {
	public final MedievalWorld world;
	private boolean enabled;


	private InternalChangeListener internalChangeListener = new InternalChangeListener();
	private Table toolTable;
	private SelectBox<EditorView.ModeSelectItem> modeSelectBox;
	private Cell<?> modeContextCell; // the actor in this cell should be changed based on what mode is selected

	public final EditorMode fileMode;
	public final TerrainHeightMode heightMode;
	public final TerrainWeightMode weightMode;

	public TerrainEditorMode(MedievalWorld world) {
		this.world = world;

		fileMode = new TerrainFileMode(this);
		heightMode = new TerrainHeightMode(this);
		weightMode = new TerrainWeightMode(this);
	}

	//////////////////////////////////////////////////////
	/// Begin methods that set up and refresh the ui
	//////////////////////////////////////////////////////
	@Override
	public void initUi() {
		fileMode.initUi();
		heightMode.initUi();
		weightMode.initUi();

		//refreshPainters();

		// Tool Table
		{
			modeSelectBox = new SelectBox<EditorView.ModeSelectItem>(world.app.skin);
			modeSelectBox.getSelection().setProgrammaticChangeEvents(false);
			modeSelectBox.addListener(internalChangeListener);
			EditorView.ModeSelectItem fileModeSelectItem = new EditorView.ModeSelectItem("File", fileMode);
			EditorView.ModeSelectItem heightModeSelectItem = new EditorView.ModeSelectItem("Elevation", heightMode);
			EditorView.ModeSelectItem weightModeSelectItem = new EditorView.ModeSelectItem("Texture", weightMode);
			modeSelectBox.setItems(fileModeSelectItem, heightModeSelectItem, weightModeSelectItem);

			toolTable = new Table(world.app.skin);
			toolTable.align(Align.topLeft);
			toolTable.row();
			toolTable.add(modeSelectBox);
			modeContextCell = toolTable.add(new Label("no mode", world.app.skin)).fill().align(Align.topLeft);
		}


	}


	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}


	@Override
	public void refreshUi() {
		//heightMode.refreshPainter();
		//weightMode.refreshPainter();
		setMode(getMode());

	}

	public void resize(int width, int height) {
		//topLeftLabelContainer.setBounds(0, 0, width, height);
	}

	@Override
	public void update(float delta) {
		if (!enabled) return;
		fileMode.update(delta);
		heightMode.update(delta);
		weightMode.update(delta);
	}

	@Override
	public void render(float delta) {
		if (!enabled) return;
		fileMode.render(delta);
		heightMode.render(delta);
		weightMode.render(delta);
	}

	@Override
	public void dispose() {
		setEnabled(false);
		fileMode.dispose();
		heightMode.dispose();
		weightMode.dispose();
	}
	////////////////////////////////////////
	/// Begin methods that mutate the UI (ie button actions)
	////////////////////////////////////////

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (enabled && !this.enabled) {
			this.enabled = true;
		} else if (!enabled && this.enabled) {
			this.enabled = false;

		}
	}

	public EditorMode getMode() {
		if(!enabled) return null;
		return modeSelectBox.getSelected().mode;
	}

	public void setMode(EditorMode editorMode) {
		Array<EditorView.ModeSelectItem> items = modeSelectBox.getItems();
		boolean valid =false;
		for (EditorView.ModeSelectItem item : items) {
			boolean enabled = editorMode == item.mode;
			item.mode.setEnabled(enabled);
			item.mode.refreshUi();
			if (enabled) {
				valid = true;
				modeSelectBox.setSelected(item);
				modeContextCell.setActor(item.mode.getToolbarActor());
			}

		}

		if(!valid)
			modeContextCell.setActor(null);

	}

	private final Vector3 tempTranslation = new Vector3();

	@Override
	public void getPixmapCoord(int screenX, int screenY, int pixmapWidth, int pixmapHeight, Vector2 store) {
		world.hudView.hudCommandView.getWorldCoord(screenX, screenY, tempTranslation);
		Terrain terrain = world.terrainView.terrain;
		store.x = UtMath.scalarLimitsInterpolation(tempTranslation.x, terrain.corner00.x, terrain.corner11.x, 0, pixmapWidth - 1);
		store.y = UtMath.scalarLimitsInterpolation(tempTranslation.z, terrain.corner00.z, terrain.corner11.z, 0, pixmapHeight - 1);
	}

	////////////////////////////////////////////////
	/// Begin methods that listen for user input
	////////////////////////////////////////////////

	@Override
	public boolean keyDown(int keycode) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (!enabled) return false;
		return false;
	}


	private class InternalChangeListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			Actor actor = event.getListenerActor();

			if (actor == modeSelectBox) {
				if (event instanceof ChangeListener.ChangeEvent) {
					EditorView.ModeSelectItem selectItem = modeSelectBox.getSelected();
					setMode(selectItem.mode);
				}
			}


			return false;
		}
	}

	////////////////////////////////////////////////////////////
	/// Begin methods that edit the terrain or edit files
	////////////////////////////////////////////////////////////


	@Override
	public void onFileChanged(WatchEvent<Path> event) {
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
	}


}
