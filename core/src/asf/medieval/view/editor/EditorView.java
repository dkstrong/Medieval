package asf.medieval.view.editor;

import asf.medieval.utility.FileWatcher;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import javax.xml.soap.Text;
import java.nio.file.Path;
import java.nio.file.WatchEvent;


/**
 * Created by daniel on 11/26/15.
 */
public class EditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor {

	public final MedievalWorld world;
	private final InternalClickListener internalCl = new InternalClickListener();

	private Table baseTable;
	protected Tree baseTree;
	private ButtonGroup<TextButton> buttonGroup;
	private TextButton gameButton,terrainButton, terrainTextureButton;

	private FileWatcher fileWatcher;
	public final TerrainEditorView terrainEditorView;



	public EditorView(MedievalWorld world) {
		this.world = world;


		baseTable = new Table(world.app.skin);
		baseTable.setBackground("default-pane");
		world.stage.addActor(baseTable);

		baseTree = new Tree(world.app.skin);
		baseTable.row();
		baseTable.add(baseTree).fill().expand().align(Align.topLeft);


		Tree.Node fileNode = addLabelNode(baseTree, "File");

		addTextButtonNode(fileNode, "New");
		addTextButtonNode(fileNode, "Load");
		addTextButtonNode(fileNode, "Save");

		Tree.Node modeNode = addLabelNode(baseTree, "Mode");

		gameButton = addTextButtonNode(modeNode, "Game");
		terrainButton = addTextButtonNode(modeNode, "Terrain");
		terrainTextureButton= addTextButtonNode(modeNode, "Terrain Texture");

		buttonGroup = new ButtonGroup<TextButton>(gameButton, terrainButton, terrainTextureButton);
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(1);


		terrainEditorView = new TerrainEditorView(world);
		//terrainEditorView.setEnabled(true);


		fileWatcher = new FileWatcher(this);
		fileWatcher.addWatch(Gdx.files.local("Terrain"));
		fileWatcher.addWatch(Gdx.files.local("Shaders"));


		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				setModeTerrainTexture();
			}
		});

	}

	protected Tree.Node addLabelNode(Object parent, String labelText) {
		Label label = new Label(labelText, world.app.skin);
		Tree.Node childNode = new Tree.Node(label);
		childNode.setSelectable(false);
		childNode.setExpanded(true);
		if (parent instanceof Tree) {
			((Tree) parent).add(childNode);
		} else if (parent instanceof Tree.Node) {
			((Tree.Node) parent).add(childNode);
		}
		return childNode;
	}

	protected TextButton addTextButtonNode(Tree.Node parent, String buttonText) {
		TextButton textButton = new TextButton(buttonText, world.app.skin,"toggle");
		textButton.addListener(internalCl);
		Tree.Node childNode = new Tree.Node(textButton);
		childNode.setSelectable(false);
		parent.add(childNode);
		return textButton;
	}

	protected Table addTableNode(Tree.Node parent) {

		Table table = new Table(world.app.skin);

		Tree.Node childNode = new Tree.Node(table);
		childNode.setSelectable(false);
		parent.add(childNode);
		return table;
	}

	public void resize(int width, int height) {

		float tableWidth = 175;
		baseTable.setBounds(0, 0, tableWidth, height);
		terrainEditorView.resize(width, height);

	}

	@Override
	public void update(float delta) {
		if (terrainEditorView.isEnabled())
			terrainEditorView.update(delta);
	}

	@Override
	public void render(float delta) {
		if (terrainEditorView.isEnabled())
			terrainEditorView.render(delta);
	}

	@Override
	public void dispose() {
		fileWatcher.dispose();
		terrainEditorView.dispose();
	}

	public void setModeGame(){
		gameButton.setChecked(true);
		terrainEditorView.setEnabled(false);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void setModeTerrainTexture(){
		terrainTextureButton.setChecked(true);
		terrainEditorView.setEnabled(true);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Input.Keys.F5:
				// Game Mode
				terrainEditorView.setEnabled(false);
				return true;
			case Input.Keys.F6:
				// Terrain Edit Mode
				terrainEditorView.setEnabled(true);
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
			if(checked == gameButton){
				setModeGame();
			}else if(checked == terrainButton){

			}else if(checked == terrainTextureButton){
				setModeTerrainTexture();
			}
		}
	}

	private class InternalChangeListener extends ChangeListener{

		@Override
		public void changed(ChangeEvent event, Actor actor) {

		}
	}



}
