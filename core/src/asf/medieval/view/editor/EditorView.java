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
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Path;
import java.nio.file.WatchEvent;


/**
 * Created by daniel on 11/26/15.
 */
public class EditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor {

	public final MedievalWorld world;
	private final InternalChangeListener internalCl = new InternalChangeListener();

	private Container<Table> minTableContainer;
	private Table minTable;
	protected Label minTextLabel;

	private Container<Table> baseTableContainer;
	private Table baseTable;
	protected Cell modeContextCell;
	private SelectBox<ModeSelectItem> modeSelectBox;
	private ModeSelectItem gameSelectItem, terrainSelectItem;
	private Container<?> gameToolbar;

	private FileWatcher fileWatcher;
	public final EditorMode terrainEditorView;

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

	public EditorView(MedievalWorld world) {
		this.world = world;

		minTable = new Table(world.app.skin);
		minTable.setBackground("default-pane-trans");
		minTable.row();
		minTextLabel = new Label("", world.app.skin);
		minTextLabel.setAlignment(Align.topLeft, Align.topLeft);
		minTable.add(minTextLabel);

		minTableContainer = new Container<Table>(minTable);
		minTableContainer.setFillParent(true);
		minTableContainer.align(Align.topLeft);


		baseTable = new Table(world.app.skin);
		baseTable.setBackground("default-pane-trans");
		//baseTable.align(Align.topLeft);
		baseTableContainer = new Container<Table>(baseTable);
		baseTableContainer.setFillParent(true);
		baseTableContainer.align(Align.topLeft);
		baseTableContainer.fillX();
		//baseTableContainer.minHeight(250);
		world.stage.addActor(baseTableContainer);

		//baseTable.row();
		//baseTable.add(createLabel("Mode", world.app.skin)).fill().align(Align.topLeft).colspan(2);
		baseTable.row();//.padBottom(Value.percentWidth(0.05f));
		baseTable.add(modeSelectBox = new SelectBox<ModeSelectItem>(world.app.skin));
		modeSelectBox.getSelection().setProgrammaticChangeEvents(false);
		modeSelectBox.addListener(internalCl);
		gameSelectItem = new ModeSelectItem("Game");
		terrainSelectItem = new ModeSelectItem("Terrain");
		modeSelectBox.setItems(gameSelectItem,terrainSelectItem);

		gameToolbar = new Container<Label>(new Label("label", world.app.skin));
		modeContextCell = baseTable.add(gameToolbar).fill().expand().align(Align.topLeft);


		terrainEditorView = new TerrainEditorView(world);
		terrainEditorView.initUi();


		// TODO: muuuuhhhh, might need to do relative and local watches for each directory
		fileWatcher = new FileWatcher(this);
		fileWatcher.addWatch(FileManager.relative("Terrain"));
		fileWatcher.addWatch(Gdx.files.local("Shaders"));


		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				setModeTerrain();
			}
		});


	}

	public void resize(int width, int height) {

	}

	@Override
	public void update(float delta) {
		terrainEditorView.update(delta);
	}

	@Override
	public void render(float delta) {
		if (!isToolbarVisible()) {
			minTextLabel.setText("");
		}
		terrainEditorView.render(delta);
	}

	@Override
	public void dispose() {
		fileWatcher.dispose();
		terrainEditorView.dispose();
	}

	public void setModeGame() {
		modeSelectBox.setSelected(gameSelectItem);
		modeContextCell.setActor(gameToolbar);

		terrainEditorView.setEnabled(false);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void setModeTerrain() {
		modeSelectBox.setSelected(terrainSelectItem);
		modeContextCell.setActor(terrainEditorView.getToolbarActor());

		terrainEditorView.setEnabled(true);
		terrainEditorView.refreshUi();

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void setToolbarVisible(boolean visible) {
		if (visible) {
			world.stage.addActor(baseTableContainer);
			minTableContainer.remove();
		} else {
			baseTableContainer.remove();
			world.stage.addActor(minTableContainer);
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
				if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
					// TODO: switch between modes
					return true;
				}
				return false;
			case Input.Keys.T:
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

					if(selectItem == gameSelectItem){
						System.out.println("set to game mode");
						setModeGame();
					}else if(selectItem == terrainSelectItem){
						System.out.println("set to terrain mode");
						setModeTerrain();
					}
				}
			}


			return false;
		}
	}

}
