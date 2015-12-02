package asf.medieval.view.editor;

import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.FileManager;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Path;
import java.nio.file.WatchEvent;


/**
 * Created by daniel on 11/26/15.
 */
public class EditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor {

	public final MedievalWorld world;
	private final InternalChangeListener internalCl = new InternalChangeListener();


	private Container<Table> baseTableContainer;
	protected Cell modeContextCell;
	private SelectBox<ModeSelectItem> modeSelectBox;

	private FileWatcher fileWatcher;
	public final GameEditorMode gameEditorMode;
	public final TerrainEditorMode terrainEditorMode;

	protected static class ModeSelectItem {
		String text;
		EditorMode mode;

		public ModeSelectItem(String text, EditorMode mode) {
			this.text = text;
			this.mode = mode;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public EditorView(MedievalWorld world) {
		this.world = world;
		// Create SubEditors
		gameEditorMode = new GameEditorMode(world);
		terrainEditorMode = new TerrainEditorMode(world);
		gameEditorMode.initUi();
		terrainEditorMode.initUi();

		// Tool Table
		{
			modeSelectBox = new SelectBox<ModeSelectItem>(world.app.skin);
			modeSelectBox.getSelection().setProgrammaticChangeEvents(false);
			modeSelectBox.addListener(internalCl);
			ModeSelectItem gameSelectItem = new ModeSelectItem("Game", gameEditorMode);
			ModeSelectItem terrainSelectItem = new ModeSelectItem("Terrain", terrainEditorMode);
			modeSelectBox.setItems(gameSelectItem,terrainSelectItem);


			Table toolTable = new Table(world.app.skin);
			toolTable.setBackground("default-pane-trans"); // base editor, set up the background
			toolTable.align(Align.topLeft);
			toolTable.row();//.padBottom(Value.percentWidth(0.05f));
			toolTable.add(modeSelectBox);
			modeContextCell = toolTable.add(new Label("no mode", world.app.skin)).fill().expand().align(Align.topLeft);


			// This is the base editor, set up the master container
			baseTableContainer = new Container<Table>(toolTable);
			baseTableContainer.setFillParent(true);
			baseTableContainer.align(Align.topLeft);
			baseTableContainer.fillX();
			world.stage.addActor(baseTableContainer);
		}




		// TODO: muuuuhhhh, might need to do relative and local watches for each directory
		fileWatcher = new FileWatcher(this);
		fileWatcher.addWatch(FileManager.relative("Terrain"));
		fileWatcher.addWatch(Gdx.files.local("Shaders"));


		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				setMode(terrainEditorMode);
			}
		});


	}

	private void refreshUi(){
		// not really ever used because this is the highest most "Editor"
		// I'm thinking about refactoring things so that an editor contains a list of subeditors and whatnot
		// and standardize the whole thing..
		EditorMode currentMode = getMode();
		setMode(currentMode);
		if(currentMode!=null){
			currentMode.refreshUi();
		}
	}

	@Override
	public void update(float delta) {
		gameEditorMode.update(delta);
		terrainEditorMode.update(delta);
	}

	@Override
	public void render(float delta) {
		gameEditorMode.render(delta);
		terrainEditorMode.render(delta);
	}

	@Override
	public void dispose() {
		fileWatcher.dispose();
		gameEditorMode.dispose();
		terrainEditorMode.dispose();
	}

	public EditorMode getMode() {
		return modeSelectBox.getSelected().mode;
	}

	public void setMode(EditorMode editorMode){
		Array<ModeSelectItem> items = modeSelectBox.getItems();
		boolean valid = false;
		for (ModeSelectItem item : items) {
			boolean enabled = editorMode == item.mode;
			item.mode.setEnabled(enabled);
			item.mode.refreshUi();
			if (enabled) {
				valid = true;
				modeSelectBox.setSelected(item);
				modeContextCell.setActor(item.mode.getToolbarActor());
			}

		}
		if (!valid)
			modeContextCell.setActor(null);

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

	}

	private class InternalChangeListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			Actor actor = event.getListenerActor();

			if(actor == modeSelectBox){
				if(event instanceof ChangeListener.ChangeEvent){
					ModeSelectItem selectItem = modeSelectBox.getSelected();
					setMode(selectItem.mode);
				}
			}


			return false;
		}
	}

}
