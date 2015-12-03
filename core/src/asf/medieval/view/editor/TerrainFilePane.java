package asf.medieval.view.editor;

import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainLoader;
import asf.medieval.utility.FileManager;
import asf.medieval.utility.UtLog;
import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by daniel on 12/2/15.
 */
public class TerrainFilePane implements EditorNode, FileChooser.Listener {
	public final MedievalWorld world;
	public final EditorView editorView;
	public PainterPane heightPainerPane;
	public PainterPane weightPainterPane;
	public boolean enabled;

	private InternalClickListener internalCl = new InternalClickListener();
	public Table toolTable;
	private Label terrainNameLabel;
	private Button newTerrainButton, loadTerrainButton, saveTerrainButton, deleteTerrainButton;
	private Container<Window> fileMenuWindowContainer;
	private Window fileMenuWindow;
	private Button fileMenuWindowCloseButton;
	private FileChooser fileChooser;

	private boolean newBeforeSave = false;

	public TerrainFilePane(EditorView editorView, PainterPane heightPainerPane, PainterPane weightPainterPane) {
		this.world = editorView.world;
		this.editorView = editorView;
		this.heightPainerPane = heightPainerPane;
		this.weightPainterPane = weightPainterPane;
	}

	public void initUi()
	{
		Terrain terrain = world.terrainView.terrain;

		fileMenuWindow = UtEditor.createModalWindow("Save/Open/Delete Terrain File", world.app.skin);
		fileMenuWindow.getTitleTable().add(fileMenuWindowCloseButton = UtEditor.createTextButton("[X]", world.app.skin, internalCl));

		fileChooser = UtEditor.createFileChooser(this, world.app.skin);
		fileMenuWindow.row();
		fileMenuWindow.add(fileChooser).fill().expand();

		fileMenuWindowContainer = new Container<Window>(fileMenuWindow);
		fileMenuWindowContainer.setFillParent(true);
		fileMenuWindowContainer.center();
		fileMenuWindowContainer.minSize(400, 300);

		toolTable = new Table(world.app.skin);
		toolTable.align(Align.left);
		toolTable.row();
		toolTable.add(newTerrainButton = UtEditor.createTextButton("New..", world.app.skin, internalCl));
		toolTable.add(loadTerrainButton = UtEditor.createTextButton("Load..", world.app.skin, internalCl));
		toolTable.add(saveTerrainButton = UtEditor.createTextButton("Save..", world.app.skin, internalCl));
		toolTable.add(deleteTerrainButton = UtEditor.createTextButton("Delete..", world.app.skin, internalCl));

		toolTable.add(terrainNameLabel = UtEditor.createLabel(terrain.parameter.name + ".ter", world.app.skin));
	}

	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}

	@Override
	public void refreshUi()
	{
		Terrain terrain = world.terrainView.terrain;
		terrainNameLabel.setText(terrain.parameter.name + ".ter");
	}

	@Override
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	@Override
	public void update(float delta) {
		if (!enabled) return;
	}

	@Override
	public void render(float delta) {
		if (!enabled) return;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void onFileSave(FileHandle fh) {

		String name = fh.nameWithoutExtension();
		if(newBeforeSave){
			world.terrainView.terrain.initNewTerrain(name);
			// refresh before calling saveTerrain(), because we need to update the painters, or they wont have the most up to date info to save
			editorView.refreshUi();
		}
		saveTerrain(name);
		editorView.refreshUi();
		fileMenuWindowContainer.remove();
	}

	@Override
	public void onFileOpen(FileHandle fh) {
		world.terrainView.terrain.init(fh);
		editorView.refreshUi();
		fileMenuWindowContainer.remove();
	}

	@Override
	public void onFileDelete(FileHandle fh) {
			System.out.println("delete file: "+fh.file().getAbsolutePath());
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
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

	private class InternalClickListener extends ClickListener implements TextField.TextFieldFilter, TextField.TextFieldListener{

		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();

			if (actor == newTerrainButton) {
				newBeforeSave = true;
				fileMenuWindow.getTitleLabel().setText("New Terrain");
				fileChooser.setButtons(true, false, false);
				fileChooser.changeDirectory("Terrain", new String[]{".ter"}, world.terrainView.terrain.parameter.name + ".ter");
				world.stage.addActor(fileMenuWindowContainer);
			} else if (actor == loadTerrainButton) {
				fileMenuWindow.getTitleLabel().setText("Load Terrain");
				fileChooser.setButtons(false, true, false);
				fileChooser.changeDirectory("Terrain", new String[]{".ter"}, world.terrainView.terrain.parameter.name + ".ter");
				world.stage.addActor(fileMenuWindowContainer);
			} else if (actor == saveTerrainButton) {
				newBeforeSave = false;
				fileMenuWindow.getTitleLabel().setText("Save Terrain");
				fileChooser.setButtons(true, false, false);
				fileChooser.changeDirectory("Terrain", new String[]{".ter"}, world.terrainView.terrain.parameter.name + ".ter");
				world.stage.addActor(fileMenuWindowContainer);
			} else if(actor == deleteTerrainButton) {
				fileMenuWindow.getTitleLabel().setText("Delete Terrain");
				fileChooser.setButtons(false, false, true);
				fileChooser.changeDirectory("Terrain", new String[]{".ter"}, world.terrainView.terrain.parameter.name + ".ter");
				world.stage.addActor(fileMenuWindowContainer);
			} else if (actor == fileMenuWindowCloseButton) {
				fileMenuWindowContainer.remove();
			}
		}

		@Override
		public boolean acceptChar(TextField textField, char c) {
			return true;
		}

		@Override
		public void keyTyped(TextField textField, char c) {

		}
	}

	private void deleteTerrain(FileHandle fh){

	}

	private void saveTerrain(String name) {
		TerrainLoader.TerrainParameter parameter = world.terrainView.terrain.parameter;

		if (name != null)
			parameter.name = name;

		if (parameter.name == null || parameter.name.trim().isEmpty()) {
			parameter.name = "untitled-terrain";
		}

		parameter.heightmapName = "Terrain/" + name + "_heightmap.cim";
		parameter.weightMap1 = "Terrain/" + name + "_weightmap.png";

		FileHandle terrainFile = FileManager.relative("Terrain/" + name + ".ter");

		StringWriter stringWriter = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(stringWriter);
		try {
			xmlWriter
				.element("Terrain")
				.attribute("name", name)
				.attribute("scale", parameter.scale)
				.attribute("magnitude", parameter.magnitude)
				.attribute("chunkWidth", parameter.chunkWidth)
				.attribute("chunkHeight", parameter.chunkHeight);
			if (parameter.heightmapName != null && !parameter.heightmapName.trim().isEmpty()) {
				xmlWriter.element("heightData")
					.attribute("heightmapName", parameter.heightmapName)
					.pop();
			} else {
				xmlWriter.
					element("heightData")
					.attribute("seed", parameter.seed)
					.attribute("fieldWidth", parameter.fieldWidth)
					.attribute("fieldHeight", parameter.fieldHeight)
					.pop();
			}

			if (parameter.weightMap1 != null) {
				xmlWriter.element("weightMap1")
					.attribute("tex", parameter.weightMap1)
					.pop();
			}

			if (parameter.tex1 != null) {
				xmlWriter.element("tex1")
					.attribute("tex", parameter.tex1)
					.attribute("scale", parameter.tex1Scale)
					.pop();
			}

			if (parameter.tex2 != null) {
				xmlWriter.element("tex2")
					.attribute("tex", parameter.tex2)
					.attribute("scale", parameter.tex2Scale)
					.pop();
			}

			if (parameter.tex3 != null) {
				xmlWriter.element("tex3")
					.attribute("tex", parameter.tex3)
					.attribute("scale", parameter.tex3Scale)
					.pop();
			}

			if (parameter.tex4 != null) {
				xmlWriter.element("tex4")
					.attribute("tex", parameter.tex4)
					.attribute("scale", parameter.tex4Scale)
					.pop();
			}

			xmlWriter.pop();
			terrainFile.writeString(stringWriter.toString(), false);
		} catch (IOException e1) {

			UtLog.error("failed to write terrain file", e1);
		}

		FileHandle heightmapFh = FileManager.relative(parameter.heightmapName);
		heightPainerPane.savePainterToFile(heightmapFh);

		FileHandle weightmap1Fh = FileManager.relative(parameter.weightMap1);
		weightPainterPane.savePainterToFile(weightmap1Fh);

		System.out.println("Saved file: " + terrainFile.file().getAbsolutePath());

	}

	@Override
	public String toString(){
		return "File";
	}

}
