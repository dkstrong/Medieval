package asf.medieval.view.editor;

import asf.medieval.painter.Painter;
import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainLoader;
import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.FileManager;
import asf.medieval.utility.UtLog;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainEditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor, Painter.CoordProvider, FileChooser.Listener {
	public final MedievalWorld world;
	private boolean enabled;

	private InternalClickListener internalCl = new InternalClickListener();
	private Table toolTable;
	private Label terrainNameLabel;
	private Button fileMenuButton;
	private Container<Window> fileMenuWindowContainer;
	private Window fileMenuWindow;
	private Button fileMenuWindowCloseButton;
	private FileChooser fileChooser;
	private ButtonGroup<TextButton> heightOrWeightButtonGroup;
	private TextButton heightMapButton, weightMapButton;
	private Cell<?> table3Cell;
	private Table table3;

	private final TerrainHeightMapUi heightMapUi;
	private final TerrainWeightMapUi weightMapUi;

	public TerrainEditorView(MedievalWorld world) {
		this.world = world;

		heightMapUi = new TerrainHeightMapUi(this);
		weightMapUi = new TerrainWeightMapUi(this);

		refreshHeightMapWeightMapPainters();
		initUi();

		//setHeigtPaintingEnabled(true);
		setWeightPaintingEnabled(true);

	}

	//////////////////////////////////////////////////////
	/// Begin methods that set up and refresh the ui
	//////////////////////////////////////////////////////
	private void initUi() {
		Terrain terrain = world.terrainView.terrain;

		toolTable = new Table(world.app.skin);
		toolTable.align(Align.topLeft);


		// file settings / file operations
		{
			Table table1 = new Table(world.app.skin);
			table1.row();
			table1.add(terrainNameLabel = UtEditor.createLabel(terrain.parameter.name + ".ter", world.app.skin));
			table1.add(fileMenuButton = UtEditor.createTextButton("..", world.app.skin, internalCl));
			toolTable.row();
			toolTable.add(table1).fill().align(Align.topLeft);


			fileMenuWindow = UtEditor.createModalWindow("Save/Open/Delete Terrain File", world.app.skin);
			fileMenuWindow.getTitleTable().add(fileMenuWindowCloseButton = UtEditor.createTextButton("[X]", world.app.skin, internalCl));

			fileChooser = UtEditor.createFileChooser(this, world.app.skin);
			fileMenuWindow.row();
			fileMenuWindow.add(fileChooser).fill().expand();

			fileMenuWindowContainer = new Container<Window>(fileMenuWindow);
			fileMenuWindowContainer.setFillParent(true);
			fileMenuWindowContainer.center();
			fileMenuWindowContainer.minSize(400, 300);


		}
		// heightmap or weightmap selector
		{
			Table table2 = new Table(world.app.skin);
			table2.row();
			table2.add(heightMapButton = UtEditor.createTextButtonToggle("Elevation", world.app.skin, internalCl)).fill();
			table2.add(weightMapButton = UtEditor.createTextButtonToggle("Texture", world.app.skin, internalCl)).fill();

			heightOrWeightButtonGroup = new ButtonGroup<TextButton>(heightMapButton, weightMapButton);
			heightOrWeightButtonGroup.setMaxCheckCount(1);
			heightOrWeightButtonGroup.setMinCheckCount(1);
			heightOrWeightButtonGroup.setUncheckLast(true);

			toolTable.row();
			toolTable.add(table2).fill().align(Align.topLeft);
		}

		// No tools selected
		{
			table3 = new Table(world.app.skin);
			toolTable.row().align(Align.topLeft);
			table3Cell = toolTable.add(table3).fill().align(Align.topLeft);


			table3.row();
			table3.add(UtEditor.createLabel("label",world.app.skin));
		}


		heightMapUi.initUi();
		weightMapUi.initUi();



	}



	private void refreshUi() {
		Terrain terrain = world.terrainView.terrain;
		terrainNameLabel.setText(terrain.parameter.name + ".ter");

		heightMapUi.refreshHeightMapUi();
		weightMapUi.refreshWeightMapUi();

		if(weightMapUi.enabled){
			setWeightPaintingEnabled(true);
		}else{
			setHeigtPaintingEnabled(true);
		}
	}

	public void resize(int width, int height) {
		//topLeftLabelContainer.setBounds(0, 0, width, height);
	}

	@Override
	public void update(float delta) {
		if (!enabled) return;
		heightMapUi.update(delta);
		weightMapUi.update(delta);
	}

	@Override
	public void render(float delta) {
		if (!enabled) return;
		heightMapUi.render(delta);
		weightMapUi.render(delta);
	}

	@Override
	public void dispose() {
		setEnabled(false);
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

	public void setHeigtPaintingEnabled(boolean heightPaintingEnabled) {
		heightMapUi.setEnabled(heightPaintingEnabled);
		if (heightPaintingEnabled) {
			heightMapButton.setChecked(true);
			table3Cell.setActor(heightMapUi.heightTable);
			setWeightPaintingEnabled(false);
		}
	}

	public void setWeightPaintingEnabled(boolean weightPaintingEnabled) {
		weightMapUi.setEnabled(weightPaintingEnabled);
		if (weightPaintingEnabled) {
			weightMapButton.setChecked(true);
			table3Cell.setActor(weightMapUi.weightTable);
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

	private class InternalClickListener extends ClickListener implements TextField.TextFieldFilter, TextField.TextFieldListener{

		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();

			if (actor == heightMapButton || actor == weightMapButton) {
				TextButton checked = heightOrWeightButtonGroup.getChecked();

				if (checked == heightMapButton) {
					setHeigtPaintingEnabled(true);
				} else if (checked == weightMapButton) {
					setWeightPaintingEnabled(true);
				} else {
					table3Cell.setActor(table3);
					setHeigtPaintingEnabled(false);
					setWeightPaintingEnabled(false);
				}
			} else if (actor == fileMenuButton) {

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

	////////////////////////////////////////////////////////////
	/// Begin methods that edit the terrain or edit files
	////////////////////////////////////////////////////////////

	@Override
	public void onFileSave(FileHandle fh) {
		String name = fh.nameWithoutExtension();
		saveTerrain(name);
		refreshUi();
		fileMenuWindowContainer.remove();
	}

	@Override
	public void onFileOpen(FileHandle fh) {
		//String name = fh.nameWithoutExtension();
		world.terrainView.terrain.loadTerrain(fh);
		refreshHeightMapWeightMapPainters();
		refreshUi();
		fileMenuWindowContainer.remove();
	}

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

	protected void refreshHeightMapWeightMapPainters() {
		heightMapUi.refreshHeightMapPainter();
		weightMapUi.refreshWeightMapPainter();
	}


	private void saveTerrain(String name) {
		TerrainLoader.TerrainParameter parameter = world.terrainView.terrain.parameter;

		if (name != null)
			parameter.name = name;

		if (parameter.name == null || parameter.name.trim().isEmpty()) {
			parameter.name = "untitled-terrain";
			// TODO: make name unique to other terrains if it is not
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
		heightMapUi.savePainterToFile(heightmapFh);

		FileHandle weightmap1Fh = FileManager.relative(parameter.weightMap1);
		weightMapUi.savePainterToFile(weightmap1Fh);

		System.out.println("Saved file: " + terrainFile.file().getAbsolutePath());

	}


}
