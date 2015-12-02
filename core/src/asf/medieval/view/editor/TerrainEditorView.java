package asf.medieval.view.editor;

import asf.medieval.painter.Painter;
import asf.medieval.terrain.Terrain;
import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainEditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor, Painter.CoordProvider {
	public final MedievalWorld world;
	private boolean enabled;


	private InternalChangeListener internalChangeListener = new InternalChangeListener();
	private Table toolTable;
	private SelectBox<ModeSelectItem> modeSelectBox;
	private ModeSelectItem fileModeSelectItem, heightModeSelectItem, weightModeSelectItem;
	private Cell<?> modeContextCell; // the actor in this cell should be changed based on what mode is selected

	private final TerrainFileModeUi fileModeUi;
	protected final TerrainHeightMapUi heightMapUi;
	protected final TerrainWeightMapUi weightMapUi;

	private static class ModeSelectItem{
		String text;

		public ModeSelectItem(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public TerrainEditorView(MedievalWorld world) {
		this.world = world;

		fileModeUi = new TerrainFileModeUi(this);
		heightMapUi = new TerrainHeightMapUi(this);
		weightMapUi = new TerrainWeightMapUi(this);

		refreshHeightMapWeightMapPainters();
		initUi();

		//setHeigtPaintingEnabled(true);
		//setWeightPaintingEnabled(true);

	}

	//////////////////////////////////////////////////////
	/// Begin methods that set up and refresh the ui
	//////////////////////////////////////////////////////
	private void initUi() {
		Terrain terrain = world.terrainView.terrain;

		toolTable = new Table(world.app.skin);
		toolTable.align(Align.topLeft);

		// heightmap or weightmap selector
		{

			modeSelectBox = new SelectBox<ModeSelectItem>(world.app.skin);
			modeSelectBox.getSelection().setProgrammaticChangeEvents(false);
			modeSelectBox.addListener(internalChangeListener);
			fileModeSelectItem = new ModeSelectItem("File");
			heightModeSelectItem = new ModeSelectItem("Elevation");
			weightModeSelectItem = new ModeSelectItem("Texture");
			modeSelectBox.setItems(fileModeSelectItem,heightModeSelectItem,weightModeSelectItem);


			//toolTable.row();
			toolTable.add(modeSelectBox);
		}

		// mode context cell
		{
			modeContextCell = toolTable.add(new Label("no mode",world.app.skin)).fill().align(Align.topLeft);

		}

		fileModeUi.initUi();
		heightMapUi.initUi();
		weightMapUi.initUi();
	}



	protected void refreshUi() {

		fileModeUi.refreshFileUi();
		heightMapUi.refreshHeightMapUi();
		weightMapUi.refreshWeightMapUi();

		if(weightMapUi.enabled){
			setWeightPaintingEnabled(true);
		}else if(heightMapUi.enabled){
			setHeigtPaintingEnabled(true);
		}else{
			setFileModeEnabled(true);
		}

	}

	protected void refreshHeightMapWeightMapPainters() {
		heightMapUi.refreshHeightMapPainter();
		weightMapUi.refreshWeightMapPainter();
	}

	public void resize(int width, int height) {
		//topLeftLabelContainer.setBounds(0, 0, width, height);
	}

	@Override
	public void update(float delta) {
		if (!enabled) return;
		fileModeUi.update(delta);
		heightMapUi.update(delta);
		weightMapUi.update(delta);
	}

	@Override
	public void render(float delta) {
		if (!enabled) return;
		fileModeUi.render(delta);
		heightMapUi.render(delta);
		weightMapUi.render(delta);
	}

	@Override
	public void dispose() {
		setEnabled(false);
		fileModeUi.dispose();
		heightMapUi.dispose();
		weightMapUi.dispose();
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
			world.editorView.containerCell.setActor(toolTable);
			refreshUi();
		} else if (!enabled && this.enabled) {
			this.enabled = false;
			world.editorView.containerCell.setActor(new Container());
			//refreshHeightMapUi();
		}
	}

	public void setFileModeEnabled(boolean fileModeEnabled){
		fileModeUi.setEnabled(fileModeEnabled);
		if(fileModeEnabled){
			modeSelectBox.setSelected(fileModeSelectItem);
			modeContextCell.setActor(fileModeUi.fileModeTable);
			setHeigtPaintingEnabled(false);
			setWeightPaintingEnabled(false);
		}
	}

	public void setHeigtPaintingEnabled(boolean heightPaintingEnabled) {
		heightMapUi.setEnabled(heightPaintingEnabled);
		if (heightPaintingEnabled) {
			modeSelectBox.setSelected(heightModeSelectItem);
			modeContextCell.setActor(heightMapUi.heightTable);
			setFileModeEnabled(false);
			setWeightPaintingEnabled(false);
		}
	}

	public void setWeightPaintingEnabled(boolean weightPaintingEnabled) {
		weightMapUi.setEnabled(weightPaintingEnabled);
		if (weightPaintingEnabled) {
			modeSelectBox.setSelected(weightModeSelectItem);
			modeContextCell.setActor(weightMapUi.weightTable);
			setFileModeEnabled(false);
			setHeigtPaintingEnabled(false);
		}
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
		if(heightMapUi.keyDown(keycode)) return true;
		if(weightMapUi.keyDown(keycode)) return true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (!enabled) return false;
		if(heightMapUi.keyUp(keycode)) return true;
		if(weightMapUi.keyUp(keycode)) return true;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (!enabled) return false;
		if(heightMapUi.keyTyped(character)) return true;
		if(weightMapUi.keyTyped(character)) return true;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!enabled) return false;
		if(heightMapUi.touchDown(screenX, screenY, pointer, button)) return true;
		if(weightMapUi.touchDown(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!enabled) return false;
		if(heightMapUi.touchUp(screenX, screenY, pointer, button)) return true;
		if(weightMapUi.touchUp(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!enabled) return false;
		if(heightMapUi.touchDragged(screenX, screenY, pointer)) return true;
		if(weightMapUi.touchDragged(screenX, screenY, pointer)) return true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (!enabled) return false;
		if(heightMapUi.mouseMoved(screenX, screenY)) return true;
		if(weightMapUi.mouseMoved(screenX, screenY)) return true;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (!enabled) return false;
		if(heightMapUi.scrolled(amount)) return true;
		if(weightMapUi.scrolled(amount)) return true;
		return false;
	}



	private class InternalChangeListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			Actor actor = event.getListenerActor();

			if(actor == modeSelectBox){
				if(event instanceof ChangeListener.ChangeEvent){
					ModeSelectItem selectItem = modeSelectBox.getSelected();

					if(selectItem == fileModeSelectItem){
						setFileModeEnabled(true);
					}else if(selectItem == heightModeSelectItem){
						setHeigtPaintingEnabled(true);
					}else if(selectItem == weightModeSelectItem){
						setWeightPaintingEnabled(true);
					}
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
