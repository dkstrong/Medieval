package asf.medieval.view.editor;

import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.UtFileHandle;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Path;
import java.nio.file.WatchEvent;


/**
 * Created by daniel on 11/26/15.
 */
public class EditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor {

	public final MedievalWorld world;
	private final InternalClickListener internalCl = new InternalClickListener();

	private Container<Table> minTableContainer;
	private Table minTable;
	protected Label minTextLabel;

	private Container<Table> baseTableContainer;
	private Table baseTable;
	protected Cell containerCell;
	private ButtonGroup<TextButton> buttonGroup;
	private TextButton gameButton, terrainButton;

	private Container<?> gameToolbar;

	private FileWatcher fileWatcher;
	public final TerrainEditorView terrainEditorView;


	public EditorView(MedievalWorld world) {
		this.world = world;

		minTable = new Table(world.app.skin);
		minTable.setBackground("default-pane-trans");
		minTable.row();
		minTextLabel = new Label("", world.app.skin);
		minTextLabel.setAlignment(Align.bottomLeft, Align.bottomLeft);
		minTable.add(minTextLabel);

		minTableContainer = new Container<Table>(minTable);
		minTableContainer.setFillParent(true);
		minTableContainer.align(Align.bottomLeft);


		baseTable = new Table(world.app.skin);
		baseTable.setBackground("default-pane-trans");
		//baseTable.align(Align.topLeft);
		baseTableContainer = new Container<Table>(baseTable);
		baseTableContainer.setFillParent(true);
		baseTableContainer.align(Align.left);
		baseTableContainer.fillY();
		baseTableContainer.minWidth(250);
		world.stage.addActor(baseTableContainer);

		//baseTable.row();
		//baseTable.add(createLabel("Mode", world.app.skin)).fill().align(Align.topLeft).colspan(2);
		baseTable.row().padBottom(Value.percentWidth(0.1f));
		baseTable.add(gameButton = UtEditor.createTextButtonToggle("Game", world.app.skin, internalCl)).fill();
		baseTable.add(terrainButton = UtEditor.createTextButtonToggle("Terrain", world.app.skin, internalCl)).fill();
		baseTable.row();
		gameToolbar = new Container<Label>(new Label("label", world.app.skin));
		containerCell = baseTable.add(gameToolbar).fill().expand().align(Align.topLeft).colspan(2);


		buttonGroup = new ButtonGroup<TextButton>(gameButton, terrainButton);
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(1);


		terrainEditorView = new TerrainEditorView(world);
		//terrainEditorView.setEnabled(true);


		// TODO: muuuuhhhh, might need to do relative and local watches for each directory
		fileWatcher = new FileWatcher(this);
		fileWatcher.addWatch(UtFileHandle.relative("Terrain"));
		fileWatcher.addWatch(Gdx.files.local("Shaders"));


		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


//		Gdx.app.postRunnable(new Runnable() {
//			@Override
//			public void run() {
//				setModeTerrain();
//			}
//		});


	}

	public void resize(int width, int height) {
		terrainEditorView.resize(width, height);
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
		gameButton.setChecked(true);
		terrainEditorView.setEnabled(false);

		containerCell.setActor(gameToolbar);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void setModeTerrain() {
		terrainButton.setChecked(true);
		terrainEditorView.setEnabled(true);
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
					if(terrainEditorView.isEnabled()){
						setModeGame();
					}else{
						setModeTerrain();
					}
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

	private class InternalClickListener extends ClickListener {
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();

			TextButton checked = buttonGroup.getChecked();
			if (checked == gameButton) {
				setModeGame();
			} else if (checked == terrainButton) {
				setModeTerrain();
			}
		}
	}

	private class InternalChangeListener extends ChangeListener {

		@Override
		public void changed(ChangeEvent event, Actor actor) {

		}
	}


}
