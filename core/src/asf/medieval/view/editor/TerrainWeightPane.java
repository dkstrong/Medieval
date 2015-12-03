package asf.medieval.view.editor;

import asf.medieval.painter.Painter;
import asf.medieval.painter.PixmapPainterDelegate;
import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainTextureAttribute;
import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.nio.file.Paths;

/**
 * Created by daniel on 11/30/15.
 */
public class TerrainWeightPane implements EditorNode, PainterPane.PainterProvider {
	public final MedievalWorld world;
	public final EditorView editorView;
	public boolean enabled;

	private InternalClickListener internalCl = new InternalClickListener();
	public Table toolTable;


	// weightmap ui
	public ButtonGroup<Button> wm_texSelectionButtonGroup;
	public final Array<UiTexMapping> wm_uiTexMappings = new Array<UiTexMapping>(true, 4, UiTexMapping.class);
	public Label wm_texLocValueLabel, wm_scaleValueLabel;
	public UiTexMapping wm_selectedTexChannel = null;

	public TerrainWeightPane(EditorView editorView) {
		this.world = editorView.world;
		this.editorView = editorView;
	}

	@Override
	public void initUi() {
		// Weightmap Tools
		{
			toolTable = new Table(world.app.skin);
			toolTable.align(Align.left);
			toolTable.row();

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
				//toolTable.row();
				toolTable.add(texSubtable);

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
				//toolTable.row();
				toolTable.add(texSettingsSubtable);
			}


		}

	}

	@Override
	public Actor getToolbarActor() {
		return toolTable;
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

	private PixmapPainterDelegate painterModel;


	public Painter providePainter(Painter currentPainter) {
		Texture currentTexture = world.terrainView.terrain.getMaterialAttribute(TerrainTextureAttribute.WeightMap1);

		if (currentPainter != null) {
			if (currentTexture == painterModel.texture) {
				// texture hasnt changed, exit out early.
				return currentPainter;
			}
			currentPainter.dispose();
		}

		//wm_pixmapPainter = new PixmapPainter(1024, 1024, Pixmap.Format.RGBA8888);
		painterModel = new PixmapPainterDelegate(currentTexture);
		currentPainter = new Painter(painterModel);
		currentPainter.coordProvider = editorView;
		world.terrainView.terrain.setOverrideMaterialAttribute(TerrainTextureAttribute.WeightMap1, painterModel.texture, 1);

		return currentPainter;
	}


	public void refreshUi() {

		// TODO: handle if the number of textures changes...
		refreshUiTexMapping(wm_uiTexMappings.get(0), TerrainTextureAttribute.Tex1);
		refreshUiTexMapping(wm_uiTexMappings.get(1), TerrainTextureAttribute.Tex2);
		refreshUiTexMapping(wm_uiTexMappings.get(2), TerrainTextureAttribute.Tex3);
		refreshUiTexMapping(wm_uiTexMappings.get(3), TerrainTextureAttribute.Tex4);

		if (wm_selectedTexChannel == null) {
			wm_selectedTexChannel = wm_uiTexMappings.get(0);
		}
		setUiPixmapTexChannel(wm_selectedTexChannel);


	}


	////////////
	/// View methods
	////////////

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void update(float delta) {
		if (!enabled) return;
	}

	public void render(float delta) {
		if (!enabled) return;
	}

	public void dispose() {

	}


	////////////////////////////////////////
	/// Begin methods that mutate the UI (ie button actions)
	////////////////////////////////////////

	public UiTexMapping getUiPixmapTexChannel() {
		return wm_selectedTexChannel;
	}

	public void setUiPixmapTexChannel(UiTexMapping selectedTexChannel) {
		this.wm_selectedTexChannel = selectedTexChannel;
		if (painterModel.painter != null)
			painterModel.painter.setBrushColor(selectedTexChannel.weightmapColor);
		selectedTexChannel.toolbarButton.setChecked(true);

		wm_texLocValueLabel.setText(selectedTexChannel.texLocationFileName);
		wm_scaleValueLabel.setText(selectedTexChannel.texScale + "x");
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
		switch (keycode) {
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
		}
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

	private class InternalClickListener extends ClickListener implements TextField.TextFieldFilter, TextField.TextFieldListener {
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();

			if (actor.getUserObject() instanceof UiTexMapping) {
				Button checked = wm_texSelectionButtonGroup.getChecked();
				UiTexMapping checkedUiTexMapping = (UiTexMapping) checked.getUserObject();
				setUiPixmapTexChannel(checkedUiTexMapping);
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

	@Override
	public String toString(){
		return "Texture";
	}

}
