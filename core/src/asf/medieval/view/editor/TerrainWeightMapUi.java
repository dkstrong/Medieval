package asf.medieval.view.editor;

import asf.medieval.painter.PixmapPainter;
import asf.medieval.painter.PixmapPainterDelegate;
import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainTextureAttribute;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Paths;

/**
 * Created by daniel on 11/30/15.
 */
public class TerrainWeightMapUi implements View, Disposable, InputProcessor {
	public final MedievalWorld world;
	public final TerrainEditorView terrainEditorView;
	public boolean enabled;

	// weightmap splat ui
	private InternalClickListener internalCl = new InternalClickListener();
	public Table weightTable;
	public ButtonGroup<Button> wm_texSelectionButtonGroup;
	public final Array<UiTexMapping> wm_uiTexMappings = new Array<UiTexMapping>(true, 4, UiTexMapping.class);
	public Label wm_texLocValueLabel, wm_scaleValueLabel;
	public ButtonGroup<Button> wm_toolSelectionButtonGroup;
	public ImageButton wm_bucketFillButton, wm_brushButton, wm_sprayButton, wm_eraserButton;
	public Label wm_radiusLabel, wm_opacityValueLabel;


	// weightmap splat 3d interface
	public boolean wm_paintingPreview = true;
	public UiTexMapping wm_selectedTexChannel = null;
	public PixmapPainter wm_pixmapPainter;

	public TerrainWeightMapUi(TerrainEditorView terrainEditorView) {
		this.world = terrainEditorView.world;
		this.terrainEditorView = terrainEditorView;
	}

	protected void initUi()
	{
		// Weightmap Tools
		{
			weightTable = new Table(world.app.skin);
			// Texture Channel Selector
			{

				wm_uiTexMappings.add(createUiTexMapping(TerrainTextureAttribute.Tex1));
				wm_uiTexMappings.add(createUiTexMapping(TerrainTextureAttribute.Tex2));
				wm_uiTexMappings.add(createUiTexMapping(TerrainTextureAttribute.Tex3));
				wm_uiTexMappings.add(createUiTexMapping(TerrainTextureAttribute.Tex4));

				wm_texSelectionButtonGroup = new ButtonGroup<Button>();
				wm_texSelectionButtonGroup.setMaxCheckCount(1);
				wm_texSelectionButtonGroup.setMinCheckCount(1);
				wm_texSelectionButtonGroup.setUncheckLast(true);

				Table texSubtable = new Table(world.app.skin);
				texSubtable.row().pad(5);
				for (UiTexMapping uiTexMapping : wm_uiTexMappings) {
					texSubtable.add(uiTexMapping.toolbarButton).fill();
					wm_texSelectionButtonGroup.add(uiTexMapping.toolbarButton);
				}
				weightTable.row();
				weightTable.add(texSubtable);

			}
			// Current Texture Channel Details
			{

				Label texLocCaptionLabel = UtEditor.createLabel("Texture:", world.app.skin);
				wm_texLocValueLabel = UtEditor.createLabel("", world.app.skin);
				Label scaleCaptionLabel = UtEditor.createLabel("Scale:", world.app.skin);
				wm_scaleValueLabel = UtEditor.createLabel("", world.app.skin);

				Table texSettingsSubtable = new Table(world.app.skin);
				texSettingsSubtable.row();
				texSettingsSubtable.add(texLocCaptionLabel);
				texSettingsSubtable.add(wm_texLocValueLabel);

				texSettingsSubtable.row();
				texSettingsSubtable.add(scaleCaptionLabel);
				texSettingsSubtable.add(wm_scaleValueLabel);
				weightTable.row();
				weightTable.add(texSettingsSubtable).padBottom(10);
			}
			// PixmapPainter Tool selector
			{

				wm_bucketFillButton = UtEditor.createImageButtonToggle("bucketfill", world.app.skin, internalCl);
				wm_brushButton = UtEditor.createImageButtonToggle("paintbrush", world.app.skin, internalCl);
				wm_sprayButton = UtEditor.createImageButtonToggle("spraypaint", world.app.skin, null);
				wm_eraserButton = UtEditor.createImageButtonToggle("eraser", world.app.skin, internalCl);

				wm_bucketFillButton.setUserObject(PixmapPainter.Tool.Fill);
				wm_brushButton.setUserObject(PixmapPainter.Tool.Brush);
				wm_sprayButton.setUserObject(null);
				wm_eraserButton.setUserObject(PixmapPainter.Tool.Eraser);
				Table toolSubtable = new Table(world.app.skin);
				toolSubtable.row().pad(5);
				toolSubtable.add(wm_bucketFillButton);
				toolSubtable.add(wm_brushButton);
				toolSubtable.add(wm_sprayButton);
				toolSubtable.add(wm_eraserButton);

				wm_toolSelectionButtonGroup = new ButtonGroup<Button>();
				wm_toolSelectionButtonGroup.setMaxCheckCount(1);
				wm_toolSelectionButtonGroup.setMinCheckCount(1);
				wm_toolSelectionButtonGroup.setUncheckLast(true);

				wm_toolSelectionButtonGroup.add(wm_bucketFillButton);
				wm_toolSelectionButtonGroup.add(wm_brushButton);
				wm_toolSelectionButtonGroup.add(wm_sprayButton);
				wm_toolSelectionButtonGroup.add(wm_eraserButton);

				weightTable.row();
				weightTable.add(toolSubtable);
			}
			// PixmapPainter addiitonal settings
			{

				Label toolRadiusCaptionLabel = UtEditor.createLabel("Radius:", world.app.skin);
				wm_radiusLabel = UtEditor.createLabel("", world.app.skin);
				Label toolOpacityCaptionLabel = UtEditor.createLabel("Opacity:", world.app.skin);
				wm_opacityValueLabel = UtEditor.createLabel("", world.app.skin);

				Table pixmapSettingsSubtable = new Table(world.app.skin);
				pixmapSettingsSubtable.row();
				pixmapSettingsSubtable.add(toolRadiusCaptionLabel);
				pixmapSettingsSubtable.add(wm_radiusLabel);

				pixmapSettingsSubtable.row();
				pixmapSettingsSubtable.add(toolOpacityCaptionLabel);
				pixmapSettingsSubtable.add(wm_opacityValueLabel);

				weightTable.row();
				weightTable.add(pixmapSettingsSubtable).padBottom(10);
			}

		}

		setUiPixmapTexChannel(wm_uiTexMappings.get(0));
		setUiPixmapTool(getUiPixmapTool());
		setUiPixmapRadius(getUiPixmapRadius());
		setUiPixmapOpacity(getUiPixmapOpacity());
	}

	private static class UiTexMapping {
		TerrainTextureAttribute texAttribute;
		Texture tex;
		String texLocation;
		String texLocationFileName;
		float texScale;
		ImageButton toolbarButton;
		Color weightmapColor;
	}

	private UiTexMapping createUiTexMapping(long materialAttributeId) {
		UiTexMapping uiTexMapping = new UiTexMapping();
		refreshUiTexMapping(uiTexMapping, materialAttributeId);
		return uiTexMapping;
	}

	private void refreshUiTexMapping(UiTexMapping uiTexMapping, long materialAttributeId) {
		Terrain terrain = world.terrainView.terrain;
		uiTexMapping.texAttribute = (TerrainTextureAttribute) terrain.material.get(materialAttributeId);
		uiTexMapping.tex = uiTexMapping.texAttribute.textureDescription.texture;

		if (materialAttributeId == TerrainTextureAttribute.Tex1) {
			uiTexMapping.texLocation = terrain.parameter.tex1;
			uiTexMapping.texScale = terrain.parameter.tex1Scale;
			uiTexMapping.weightmapColor = new Color(1, 0, 0, 0);
		} else if (materialAttributeId == TerrainTextureAttribute.Tex2) {
			uiTexMapping.texLocation = terrain.parameter.tex2;
			uiTexMapping.texScale = terrain.parameter.tex2Scale;
			uiTexMapping.weightmapColor = new Color(0, 1, 0, 0);
		} else if (materialAttributeId == TerrainTextureAttribute.Tex3) {
			uiTexMapping.texLocation = terrain.parameter.tex3;
			uiTexMapping.texScale = terrain.parameter.tex3Scale;
			uiTexMapping.weightmapColor = new Color(0, 0, 1, 0);
		} else if (materialAttributeId == TerrainTextureAttribute.Tex4) {
			uiTexMapping.texLocation = terrain.parameter.tex4;
			uiTexMapping.texScale = terrain.parameter.tex4Scale;
			uiTexMapping.weightmapColor = new Color(0, 0, 0, 1);
		}

		uiTexMapping.texLocationFileName = Paths.get(uiTexMapping.texLocation).getFileName().toString();

		if (uiTexMapping.toolbarButton == null) {
			uiTexMapping.toolbarButton = UtEditor.createImageButtonToggle(uiTexMapping.tex, world.app.skin, internalCl);
			uiTexMapping.toolbarButton.setUserObject(uiTexMapping);
		} else {
			TextureRegionDrawable trd = (TextureRegionDrawable) uiTexMapping.toolbarButton.getStyle().imageUp;
			trd.getRegion().setTexture(uiTexMapping.tex);
		}
	}


	public void refreshWeightMapUi() {
		// TODO: handle if the number of textures changes...
		refreshUiTexMapping(wm_uiTexMappings.get(0), TerrainTextureAttribute.Tex1);
		refreshUiTexMapping(wm_uiTexMappings.get(1), TerrainTextureAttribute.Tex2);
		refreshUiTexMapping(wm_uiTexMappings.get(2), TerrainTextureAttribute.Tex3);
		refreshUiTexMapping(wm_uiTexMappings.get(3), TerrainTextureAttribute.Tex4);

		setUiPixmapTexChannel(wm_selectedTexChannel);
		setUiPixmapTool(getUiPixmapTool());
		setUiPixmapRadius(getUiPixmapRadius());
		setUiPixmapOpacity(getUiPixmapOpacity());
	}
	////////////
	/// View methods
	////////////

	public void setEnabled(boolean enabled){
		this.enabled = enabled;

		wm_pixmapPainter.setPreviewPainting(terrainEditorView.isEnabled() && this.enabled && wm_paintingPreview);
	}

	public void update(float delta){
		if(!enabled)
			return;

		if (wm_pixmapPainter != null)
			wm_pixmapPainter.updateInput(delta);
	}

	public void render(float delta){
		if(!enabled)
			return;

		if (!world.editorView.isToolbarVisible()) {
			String text = "";
			if(wm_pixmapPainter != null)
			{
				text += "Tex: " + getUiPixmapTexChannel().texLocationFileName + "  ";
				text += "Tool: " + getUiPixmapTool() + "   ";
				text += "Radius: " + getUiPixmapRadius() + "   ";
				text += "Opacity: " + getUiPixmapOpacity();
			}
			world.editorView.minTextLabel.setText(text);
		}

	}

	public void dispose()
	{
		if (wm_pixmapPainter != null) {
			wm_pixmapPainter.dispose();
			wm_pixmapPainter = null;
		}
	}


	////////////////////////////////////////
	/// Begin methods that mutate the UI (ie button actions)
	////////////////////////////////////////

	public UiTexMapping getUiPixmapTexChannel(){
		return wm_selectedTexChannel;
	}

	public void setUiPixmapTexChannel(UiTexMapping selectedTexChannel) {
		this.wm_selectedTexChannel = selectedTexChannel;
		if (wm_pixmapPainter != null)
			wm_pixmapPainter.setBrushColor(selectedTexChannel.weightmapColor);
		selectedTexChannel.toolbarButton.setChecked(true);

		wm_texLocValueLabel.setText(selectedTexChannel.texLocationFileName);
		wm_scaleValueLabel.setText(selectedTexChannel.texScale + "x");
	}

	public PixmapPainter.Tool getUiPixmapTool() {
		return wm_pixmapPainter.getTool();
	}

	public void setUiPixmapTool(PixmapPainter.Tool tool) {
		wm_pixmapPainter.setTool(tool);
		if (wm_bucketFillButton.getUserObject() == tool) {
			wm_bucketFillButton.setChecked(true);
		} else if (wm_brushButton.getUserObject() == tool) {
			wm_brushButton.setChecked(true);
		} else if (wm_eraserButton.getUserObject() == tool) {
			wm_eraserButton.setChecked(true);
		}
	}

	public int getUiPixmapRadius() {
		return wm_pixmapPainter.getBrush().getRadius();
	}

	public void setUiPixmapRadius(int radius) {
		wm_pixmapPainter.getBrush().setRadius(radius);
		wm_radiusLabel.setText(String.valueOf(radius));
	}

	public float getUiPixmapOpacity() {
		return wm_pixmapPainter.getBrushOpacity();
	}

	public void setUiPixmapOpacity(float opacity) {
		wm_pixmapPainter.setBrushOpacity(opacity);

		wm_opacityValueLabel.setText(String.valueOf(UtMath.round(opacity, 2)));
	}

	////////////////////////////////////////////////
	/// Begin methods that listen for user input
	////////////////////////////////////////////////

	@Override
	public boolean keyDown(int keycode) {
		if(!enabled)
			return false;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(!enabled)
			return false;
		switch (keycode) {
			case Input.Keys.TAB:
				terrainEditorView.setHeigtPaintingEnabled(true);
				return true;
			case Input.Keys.NUM_1:
				setUiPixmapTexChannel(wm_uiTexMappings.get(0));
				return true;
			case Input.Keys.NUM_2:
				setUiPixmapTexChannel(wm_uiTexMappings.get(1));
				return true;
			case Input.Keys.NUM_3:
				setUiPixmapTexChannel(wm_uiTexMappings.get(2));
				return true;
			case Input.Keys.NUM_4:
				setUiPixmapTexChannel(wm_uiTexMappings.get(3));
				return true;
			case Input.Keys.LEFT_BRACKET:
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					setUiPixmapRadius(getUiPixmapRadius() - 1);
				} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					setUiPixmapOpacity(getUiPixmapOpacity() - 0.1f);
				}
				return true;
			case Input.Keys.RIGHT_BRACKET:
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					setUiPixmapRadius(getUiPixmapRadius() + 1);
				} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					setUiPixmapOpacity(getUiPixmapOpacity() + 0.1f);
				}
				return true;
			case Input.Keys.B:
				setUiPixmapTool(PixmapPainter.Tool.Brush);
				return true;
			case Input.Keys.E:
				setUiPixmapTool(PixmapPainter.Tool.Eraser);
				return true;
			case Input.Keys.F:
				setUiPixmapTool(PixmapPainter.Tool.Fill);
				return true;
		}
		if (wm_pixmapPainter != null && wm_pixmapPainter.keyUp(keycode)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(!enabled) return false;
		if (wm_pixmapPainter != null && wm_pixmapPainter.keyTyped(character)) return true;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		if (wm_pixmapPainter != null && wm_pixmapPainter.touchDown(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!enabled)
			return false;
		if (wm_pixmapPainter != null && wm_pixmapPainter.touchUp(screenX, screenY, pointer, button)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!enabled)
			return false;
		if (wm_pixmapPainter != null && wm_pixmapPainter.touchDragged(screenX, screenY, pointer)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!enabled)
			return false;
		if (wm_pixmapPainter != null && wm_pixmapPainter.mouseMoved(screenX, screenY)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!enabled)
			return false;

		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			setUiPixmapRadius(getUiPixmapRadius() - amount);
			return true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			setUiPixmapOpacity(getUiPixmapOpacity() - amount * 0.05f);
			return true;
		}

		if (wm_pixmapPainter != null && wm_pixmapPainter.scrolled(amount)) {
			return true;
		}

		return false;
	}

	private class InternalClickListener extends ClickListener implements TextField.TextFieldFilter, TextField.TextFieldListener{
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();

			if (actor.getUserObject() instanceof UiTexMapping) {
				Button checked = wm_texSelectionButtonGroup.getChecked();
				UiTexMapping checkedUiTexMapping = (UiTexMapping) checked.getUserObject();
				setUiPixmapTexChannel(checkedUiTexMapping);
			} else if (actor.getUserObject() instanceof PixmapPainter.Tool) {
				Button checked = wm_toolSelectionButtonGroup.getChecked();
				PixmapPainter.Tool checkedTool = (PixmapPainter.Tool) checked.getUserObject();
				setUiPixmapTool(checkedTool);
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

	private PixmapPainterDelegate painterModel;

	public void refreshWeightMapPainter(){
		// Be sure to call initUi() / refreshHeightMapUi() after refreshing the painters..
		Texture currentTexture = world.terrainView.terrain.getMaterialAttribute(TerrainTextureAttribute.WeightMap1);

		if (wm_pixmapPainter != null) {
			if(currentTexture == painterModel.texture){
				// texture hasnt changed, exit out early.
				return;
			}
			wm_pixmapPainter.dispose();
			wm_pixmapPainter = null;
		}

		//wm_pixmapPainter = new PixmapPainter(1024, 1024, Pixmap.Format.RGBA8888);
		painterModel = new PixmapPainterDelegate(currentTexture);
		wm_pixmapPainter = new PixmapPainter(painterModel);
		wm_pixmapPainter.coordProvider = terrainEditorView;
		world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.WeightMap1, painterModel.texture, 1);

		wm_pixmapPainter.setPreviewPainting(terrainEditorView.isEnabled() && this.enabled && wm_paintingPreview);
	}

	public void savePainterToFile(FileHandle fh){
		wm_pixmapPainter.output(fh);
	}

}
