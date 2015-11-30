package asf.medieval.view.editor;

import asf.medieval.painter.PixmapPainter;
import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainLoader;
import asf.medieval.terrain.TerrainTextureAttribute;
import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.UtLog;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainEditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor, PixmapPainter.PixmapCoordProvider, FileChooser.Listener {
	public final MedievalWorld world;
	private boolean enabled;

	private InternalClickListener internalCl = new InternalClickListener();
	private Table toolTable;
	private Button fileMenuButton;
	private Container<Window> fileMenuWindowContainer;
	private Window fileMenuWindow;
	private Button fileMenuWindowCloseButton;
	private FileChooser fileChooser;
	private ButtonGroup<TextButton> heightOrWeightButtonGroup;
	private TextButton heightMapButton, weightMapButton;
	private Cell<?> table3Cell;
	private Table table3, heightTable, weightTable;

	private ButtonGroup<Button> texSelectionButtonGroup;
	private final Array<UiTexMapping> uiTexMappings = new Array<UiTexMapping>(true, 4, UiTexMapping.class);
	private Label texLocValueLabel, scaleValueLabel;
	private ButtonGroup<Button> toolSelectionButtonGroup;
	private ImageButton toolBucketFillButton, toolBrushButton, toolSprayButton, toolEraserButton;
	private Label toolRadiusLabel, toolOpacityValueLabel;


	// weightmap splat
	private boolean weightPaintingEnabled = false;
	private boolean weightPaintingPreview = true;
	private UiTexMapping selectedTexChannel = null;
	private Texture currentTexture;
	private String currentTextureLoc;
	public PixmapPainter editPixmapPainter;
	private String editTextureLoc;
	private FileHandle editFh;


	public TerrainEditorView(MedievalWorld world) {
		this.world = world;


		currentTexture = world.terrainView.terrain.getMaterialAttribute(TerrainTextureAttribute.WeightMap1);

		//editPixmapPainter = new PixmapPainter(1024, 1024, Pixmap.Format.RGBA8888); // currentTexture
		editPixmapPainter = new PixmapPainter(currentTexture); // currentTexture
		editPixmapPainter.coordProvider = this;

		editTextureLoc = "tmp/" + currentTextureLoc;
		editFh = Gdx.files.local(editTextureLoc);

		world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.WeightMap1, editPixmapPainter.texture, 1);
		Terrain terrain = world.terrainView.terrain;

		toolTable = new Table(world.app.skin);
		toolTable.align(Align.topLeft);


		// file settings / file operations
		{
			Table table1 = new Table(world.app.skin);
			table1.row();
			table1.add(UtEditor.createLabel(terrain.parameter.name + ".ter", world.app.skin));
			table1.add(fileMenuButton = UtEditor.createTextButton("..", world.app.skin, internalCl));
			toolTable.row();
			toolTable.add(table1).fill().align(Align.topLeft);


			fileMenuWindow = UtEditor.createModalWindow("Save/Open/Delete Terrain File", world.app.skin);
			fileMenuWindow.getTitleTable().add(fileMenuWindowCloseButton =UtEditor.createTextButton("[X]",world.app.skin, internalCl));

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
			heightOrWeightButtonGroup.setMinCheckCount(0);
			heightOrWeightButtonGroup.setUncheckLast(true);

			toolTable.row();
			toolTable.add(table2).fill().align(Align.topLeft);
		}

		// No tools selected
		{
			table3 = new Table(world.app.skin);
			table3.add(UtEditor.createLabel("label", world.app.skin));
			toolTable.row().align(Align.topLeft);
			table3Cell = toolTable.add(table3).fill().align(Align.topLeft);
		}

		// Heightmap tools
		{
			heightTable = new Table(world.app.skin);
		}

		// Weightmap Tools
		{
			weightTable = new Table(world.app.skin);
			// Texture Channel Selector
			{
				uiTexMappings.add(new UiTexMapping(TerrainTextureAttribute.Tex1, terrain.parameter.tex1, terrain.parameter.tex1Scale));
				uiTexMappings.add(new UiTexMapping(TerrainTextureAttribute.Tex2, terrain.parameter.tex2,terrain.parameter.tex2Scale));
				uiTexMappings.add(new UiTexMapping(TerrainTextureAttribute.Tex3, terrain.parameter.tex3,terrain.parameter.tex3Scale));
				uiTexMappings.add(new UiTexMapping(TerrainTextureAttribute.Tex4, terrain.parameter.tex4,terrain.parameter.tex4Scale));

				texSelectionButtonGroup = new ButtonGroup<Button>();
				texSelectionButtonGroup.setMaxCheckCount(1);
				texSelectionButtonGroup.setMinCheckCount(1);
				texSelectionButtonGroup.setUncheckLast(true);

				Table texSubtable = new Table(world.app.skin);
				texSubtable.row().pad(5);
				for (UiTexMapping uiTexMapping : uiTexMappings) {
					texSubtable.add(uiTexMapping.toolbarButton).fill();
					texSelectionButtonGroup.add(uiTexMapping.toolbarButton);
				}
				weightTable.row();
				weightTable.add(texSubtable);

			}
			// Current Texture Channel Details
			{

				Label texLocCaptionLabel = UtEditor.createLabel("Texture:", world.app.skin);
				texLocValueLabel = UtEditor.createLabel("", world.app.skin);
				Label scaleCaptionLabel = UtEditor.createLabel("Scale:", world.app.skin);
				scaleValueLabel = UtEditor.createLabel("", world.app.skin);

				Table texSettingsSubtable = new Table(world.app.skin);
				texSettingsSubtable.row();
				texSettingsSubtable.add(texLocCaptionLabel);
				texSettingsSubtable.add(texLocValueLabel);

				texSettingsSubtable.row();
				texSettingsSubtable.add(scaleCaptionLabel);
				texSettingsSubtable.add(scaleValueLabel);
				weightTable.row();
				weightTable.add(texSettingsSubtable).padBottom(10);
			}
			// PixmapPainter Tool selector
			{

				toolBucketFillButton = UtEditor.createImageButtonToggle("bucketfill",world.app.skin,internalCl);
				toolBrushButton = UtEditor.createImageButtonToggle("paintbrush",world.app.skin,internalCl);
				toolSprayButton = UtEditor.createImageButtonToggle("spraypaint",world.app.skin,null);
				toolEraserButton = UtEditor.createImageButtonToggle("eraser",world.app.skin,internalCl);

				toolBucketFillButton.setUserObject(PixmapPainter.Tool.Fill);
				toolBrushButton.setUserObject(PixmapPainter.Tool.Brush);
				toolSprayButton.setUserObject(null);
				toolEraserButton.setUserObject(PixmapPainter.Tool.Eraser);
				Table toolSubtable = new Table(world.app.skin);
				toolSubtable.row().pad(5);
				toolSubtable.add(toolBucketFillButton);
				toolSubtable.add(toolBrushButton);
				toolSubtable.add(toolSprayButton);
				toolSubtable.add(toolEraserButton);

				toolSelectionButtonGroup = new ButtonGroup<Button>();
				toolSelectionButtonGroup.setMaxCheckCount(1);
				toolSelectionButtonGroup.setMinCheckCount(1);
				toolSelectionButtonGroup.setUncheckLast(true);

				toolSelectionButtonGroup.add(toolBucketFillButton);
				toolSelectionButtonGroup.add(toolBrushButton);
				toolSelectionButtonGroup.add(toolSprayButton);
				toolSelectionButtonGroup.add(toolEraserButton);

				weightTable.row();
				weightTable.add(toolSubtable);
			}
			// PixmapPainter addiitonal settings
			{

				Label toolRadiusCaptionLabel = UtEditor.createLabel("Radius:", world.app.skin);
				toolRadiusLabel = UtEditor.createLabel("", world.app.skin);
				Label toolOpacityCaptionLabel = UtEditor.createLabel("Opacity:", world.app.skin);
				toolOpacityValueLabel = UtEditor.createLabel("", world.app.skin);

				Table pixmapSettingsSubtable = new Table(world.app.skin);
				pixmapSettingsSubtable.row();
				pixmapSettingsSubtable.add(toolRadiusCaptionLabel);
				pixmapSettingsSubtable.add(toolRadiusLabel);

				pixmapSettingsSubtable.row();
				pixmapSettingsSubtable.add(toolOpacityCaptionLabel);
				pixmapSettingsSubtable.add(toolOpacityValueLabel);

				weightTable.row();
				weightTable.add(pixmapSettingsSubtable).padBottom(10);
			}

		}


		setSelectedTexChannel(uiTexMappings.get(0));
		setSelectedPixmapTool(getSelectedPixmapTool());
		setSelectedPixmapRadius(getSelectedPixmapRadius());
		setSelectedPixmapOpacity(getSetSelectedPixmapOpacity());

	}



	private class UiTexMapping{
		TerrainTextureAttribute texAttribute;
		Texture tex;
		String texLocation;
		String texLocationFileName;
		float texScale;

		final ImageButton toolbarButton;

		final Color weightmapColor;

		private UiTexMapping(long materialAttributeId, String texLocation, float texScale){
			Terrain terrain = world.terrainView.terrain;
			texAttribute = (TerrainTextureAttribute)terrain.material.get(materialAttributeId);
			tex = texAttribute.textureDescription.texture;
			this.texLocation = texLocation;
			texLocationFileName = Paths.get(texLocation).getFileName().toString();
			this.texScale = texScale;

			toolbarButton = UtEditor.createImageButtonToggle(tex, world.app.skin, internalCl);
			toolbarButton.setUserObject(this);

			if(materialAttributeId == TerrainTextureAttribute.Tex1){
				this.weightmapColor = new Color(1,0,0,0);
			}else if(materialAttributeId == TerrainTextureAttribute.Tex2){
				this.weightmapColor = new Color(0,1,0,0);
			}else if(materialAttributeId == TerrainTextureAttribute.Tex3){
				this.weightmapColor = new Color(0,0,1,0);
			}else{
				this.weightmapColor = new Color(0,0,0,1);
			}

		}

	}


	public void resize(int width, int height) {
		//topLeftLabelContainer.setBounds(0, 0, width, height);
	}

	@Override
	public void update(float delta) {
		if(!enabled)
			return;
		if (weightPaintingEnabled && editPixmapPainter != null)
			editPixmapPainter.updateInput(delta);
	}

	@Override
	public void render(float delta) {
		if(!enabled)
			return;


		if(!world.editorView.isToolbarVisible()){

			String text = "";
			if (weightPaintingEnabled && editPixmapPainter != null) {
				text += "Tex: " + selectedTexChannel.texLocationFileName+"  ";
				text += "Tool: " + editPixmapPainter.getTool()+"   ";
				text += "Radius: " + editPixmapPainter.getBrush().getRadius()+"   ";
				text += "Opacity: " + editPixmapPainter.getBrushOpacity();
			}

			world.editorView.minTextLabel.setText(text);

		}


	}

	public void setEnabled(boolean enabled) {

		if (enabled && !this.enabled) {
			this.enabled = true;
			world.editorView.containerCell.setActor(toolTable);
			//toolTable.invalidateHierarchy();
			setWeightPaintingEnabled(weightPaintingEnabled);
		} else if (!enabled && this.enabled) {
			this.enabled = false;
			world.editorView.containerCell.setActor(new Container());
			//toolTable.remove();
			setWeightPaintingEnabled(weightPaintingEnabled);
		}
	}

	public void setWeightPaintingEnabled(boolean weightPaintingEnabled){
		this.weightPaintingEnabled = weightPaintingEnabled;
		editPixmapPainter.setPreviewPainting(this.enabled && this.weightPaintingEnabled && weightPaintingPreview);
		if(weightPaintingEnabled){
			table3Cell.setActor(weightTable);
		}
	}

	@Override
	public void onFileSave(FileHandle fh) {
		String name = fh.nameWithoutExtension();
		saveTerrain(name);
		fileMenuWindowContainer.remove();
	}

	@Override
	public void onFileOpen(FileHandle fh) {
		String name = fh.nameWithoutExtension();
		world.terrainView.terrain.loadTerrain(name);
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

	@Override
	public void dispose() {
		setEnabled(false);
		editPixmapPainter.dispose();
		editPixmapPainter = null;
	}

	private final Vector3 tempTranslation = new Vector3();

	@Override
	public void getPixmapCoord(int screenX, int screenY, int pixmapWidth, int pixmapHeight, Vector2 store) {
		world.hudView.hudCommandView.getWorldCoord(screenX, screenY, tempTranslation);
		Terrain terrain = world.terrainView.terrain;
		store.x = UtMath.scalarLimitsInterpolation(tempTranslation.x, terrain.corner00.x, terrain.corner11.x, 0, pixmapWidth - 1);
		store.y = UtMath.scalarLimitsInterpolation(tempTranslation.z, terrain.corner00.z, terrain.corner11.z, 0, pixmapHeight - 1);
	}

	public void setSelectedTexChannel(UiTexMapping selectedTexChannel) {
		this.selectedTexChannel = selectedTexChannel;
		editPixmapPainter.setBrushColor(selectedTexChannel.weightmapColor);
		selectedTexChannel.toolbarButton.setChecked(true);

		texLocValueLabel.setText(selectedTexChannel.texLocationFileName);
		scaleValueLabel.setText(selectedTexChannel.texScale+"x");
	}

	public PixmapPainter.Tool getSelectedPixmapTool(){
		return editPixmapPainter.getTool();
	}

	public void setSelectedPixmapTool(PixmapPainter.Tool tool){
		editPixmapPainter.setTool(tool);
		if(toolBucketFillButton.getUserObject() == tool){
			toolBucketFillButton.setChecked(true);
		}else if(toolBrushButton.getUserObject() == tool){
			toolBrushButton.setChecked(true);
		} else if(toolEraserButton.getUserObject() == tool){
			toolEraserButton.setChecked(true);
		}
	}

	public int getSelectedPixmapRadius(){
		return editPixmapPainter.getBrush().getRadius();
	}
	public void setSelectedPixmapRadius(int radius){
		editPixmapPainter.getBrush().setRadius(radius);
		toolRadiusLabel.setText(String.valueOf(radius));
	}

	public float getSetSelectedPixmapOpacity(){
		return editPixmapPainter.getBrushOpacity();
	}

	public void setSelectedPixmapOpacity(float opacity){
		editPixmapPainter.setBrushOpacity(opacity);

		toolOpacityValueLabel.setText(String.valueOf(UtMath.round(opacity, 2)));
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (!enabled)
			return false;
		switch (keycode) {
			case Input.Keys.NUM_1:
				setSelectedTexChannel(uiTexMappings.get(0));
				return true;
			case Input.Keys.NUM_2:
				setSelectedTexChannel(uiTexMappings.get(1));
				return true;
			case Input.Keys.NUM_3:
				setSelectedTexChannel(uiTexMappings.get(2));
				return true;
			case Input.Keys.NUM_4:
				setSelectedTexChannel(uiTexMappings.get(3));
				return true;
			case Input.Keys.LEFT_BRACKET:
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					setSelectedPixmapRadius(getSelectedPixmapRadius() - 1);
				} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					setSelectedPixmapOpacity(getSetSelectedPixmapOpacity() - 0.1f);
				}
				return true;
			case Input.Keys.RIGHT_BRACKET:
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					setSelectedPixmapRadius(getSelectedPixmapRadius() + 1);
				} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					setSelectedPixmapOpacity(getSetSelectedPixmapOpacity() + 0.1f);
				}
				return true;
			case Input.Keys.B:
				setSelectedPixmapTool(PixmapPainter.Tool.Brush);
				return true;
			case Input.Keys.E:
				setSelectedPixmapTool(PixmapPainter.Tool.Eraser);
				return true;
			case Input.Keys.F:
				setSelectedPixmapTool(PixmapPainter.Tool.Fill);
				return true;
		}
		if (weightPaintingEnabled && editPixmapPainter != null && editPixmapPainter.keyUp(keycode)) {
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
		if (!enabled)
			return false;
		if (weightPaintingEnabled && editPixmapPainter != null && editPixmapPainter.touchDown(screenX, screenY, pointer, button)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!enabled)
			return false;
		if (weightPaintingEnabled && editPixmapPainter != null && editPixmapPainter.touchUp(screenX, screenY, pointer, button)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!enabled)
			return false;
		if (weightPaintingEnabled && editPixmapPainter != null && editPixmapPainter.touchDragged(screenX, screenY, pointer)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (!enabled)
			return false;
		if (weightPaintingEnabled && editPixmapPainter != null && editPixmapPainter.mouseMoved(screenX, screenY)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (!enabled)
			return false;

		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			setSelectedPixmapRadius(getSelectedPixmapRadius() - amount);
			return true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			setSelectedPixmapOpacity(getSetSelectedPixmapOpacity() - amount * 0.05f);
			return true;
		}

		if (weightPaintingEnabled && editPixmapPainter != null && editPixmapPainter.scrolled(amount)) {
			return true;
		}
		return false;
	}

	private class InternalClickListener extends ClickListener {
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();

			if(actor == heightMapButton || actor == weightMapButton){
				TextButton checked = heightOrWeightButtonGroup.getChecked();

				if(checked == heightMapButton){
					table3Cell.setActor(heightTable);
					setWeightPaintingEnabled(false);
				}else if(checked == weightMapButton){
					setWeightPaintingEnabled(true);
				}else{
					table3Cell.setActor(table3);
					setWeightPaintingEnabled(false);
				}
			}else if(actor.getUserObject() instanceof UiTexMapping){
				Button checked = texSelectionButtonGroup.getChecked();
				UiTexMapping checkedUiTexMapping = (UiTexMapping)checked.getUserObject();
				setSelectedTexChannel(checkedUiTexMapping);
			}else if(actor.getUserObject() instanceof PixmapPainter.Tool){
				Button checked = toolSelectionButtonGroup.getChecked();
				PixmapPainter.Tool checkedTool = (PixmapPainter.Tool)checked.getUserObject();
				setSelectedPixmapTool(checkedTool);
			}else if(actor == fileMenuButton){
				fileChooser.changeDirectory("Terrain", new String[]{".ter"}, world.terrainView.terrain.parameter.name+".ter");
				world.stage.addActor(fileMenuWindowContainer);
			}else if(actor == fileMenuWindowCloseButton){
				fileMenuWindowContainer.remove();
			}


		}
	}



	private void saveTerrain(String name)
	{
		TerrainLoader.TerrainParameter parameter = world.terrainView.terrain.parameter;

		if(name != null)
			parameter.name = name;

		if(parameter.name == null || parameter.name.trim().isEmpty()){
			parameter.name="untitled-terrain";
			// TODO: make name unique to other terrains if it is not
		}

		parameter.weightMap1 ="Terrain/" + name+"_weightmap.png";

		FileHandle terrainFile =Gdx.files.local("Terrain/" + name + ".ter");

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
			if(parameter.heightmapName != null && !parameter.heightmapName.trim().isEmpty()){
				xmlWriter.element("heightData")
					.attribute("heightmapName", parameter.heightmapName)
					.pop();
			}else{
				xmlWriter.
					element("heightData")
					.attribute("seed", parameter.seed)
					.attribute("fieldWidth", parameter.fieldWidth)
					.attribute("fieldHeight", parameter.fieldHeight)
					.pop();
			}

			if(parameter.weightMap1 != null){
				xmlWriter.element("weightMap1")
					.attribute("tex", parameter.weightMap1)
					.pop();
			}

			if(parameter.tex1 != null){
				xmlWriter.element("tex1")
					.attribute("tex", parameter.tex1)
					.attribute("scale", parameter.tex1Scale)
					.pop();
			}

			if(parameter.tex2 != null){
				xmlWriter.element("tex2")
					.attribute("tex", parameter.tex2)
					.attribute("scale", parameter.tex2Scale)
					.pop();
			}

			if(parameter.tex3 != null){
				xmlWriter.element("tex3")
					.attribute("tex", parameter.tex3)
					.attribute("scale", parameter.tex3Scale)
					.pop();
			}

			if(parameter.tex4 != null){
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


		FileHandle weightmap1Fh = Gdx.files.local(parameter.weightMap1);

		//ensure that previews or whatnot havent screwed nothing up and that were saving the last history state..
		editPixmapPainter.history.recallHistory(editPixmapPainter.history.history.size-1,editPixmapPainter);
		PixmapIO.writePNG(weightmap1Fh, editPixmapPainter.pixmap);

		System.out.println("Saved file: "+terrainFile.name());

		// TODO: update UI

	}
}
