package asf.medieval.view.editor;

import asf.medieval.painter.PixmapPainter;
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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/30/15.
 */
public class TerrainHeightMapUi implements View, Disposable, InputProcessor {
	public final MedievalWorld world;
	public final TerrainEditorView terrainEditorView;
	public boolean enabled;

	// weightmap splat ui
	private InternalClickListener internalCl = new InternalClickListener();
	public Table heightTable;
	// heightmap ui
	private TextField hm_scaleTextField;
	private TextButton hm_scaleUpButton, hm_scaleDownButton;
	private TextField hm_magnitudeTextField;
	private TextButton hm_magnitudeUpButton, hm_magnitudeDownButton;

	public ButtonGroup<Button> wm_toolSelectionButtonGroup;
	public ImageButton wm_bucketFillButton, wm_brushButton, wm_sprayButton, wm_eraserButton;
	public Label wm_radiusLabel, wm_opacityValueLabel;



	// weightmap splat 3d interface
	public boolean hm_paintingPreview = true;
	public PixmapPainter hm_pixmapPainter;

	public TerrainHeightMapUi(TerrainEditorView terrainEditorView) {
		this.world = terrainEditorView.world;
		this.terrainEditorView = terrainEditorView;
	}

	protected void initUi()
	{
		Terrain terrain = world.terrainView.terrain;
		// Heightmap tools
		{
			heightTable = new Table(world.app.skin);

			heightTable.row();
			heightTable.add(UtEditor.createLabel("Scale", world.app.skin));
			heightTable.add(hm_scaleTextField =UtEditor.createTextField(terrain.parameter.scale+"", world.app.skin,internalCl,internalCl));
			heightTable.add(hm_scaleUpButton =UtEditor.createTextButton("/\\",world.app.skin, internalCl));
			heightTable.add(hm_scaleDownButton =UtEditor.createTextButton("\\/",world.app.skin, internalCl));

			heightTable.row();
			heightTable.add(UtEditor.createLabel("Magnitude", world.app.skin));
			heightTable.add(hm_magnitudeTextField =UtEditor.createTextField(terrain.parameter.magnitude+"", world.app.skin, internalCl,internalCl));
			heightTable.add(hm_magnitudeUpButton =UtEditor.createTextButton("/\\",world.app.skin, internalCl));
			heightTable.add(hm_magnitudeDownButton =UtEditor.createTextButton("\\/",world.app.skin, internalCl));
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

			heightTable.row();
			heightTable.add(toolSubtable).colspan(4);
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

			heightTable.row();
			heightTable.add(pixmapSettingsSubtable).padBottom(10).colspan(4);
		}

		refreshHeightMapUi();
	}

	public void refreshHeightMapUi() {
		setUiParamScale(getParamScale());
		setUiParamMagnitude(getParamMagnitude());

		setUiPixmapTool(getUiPixmapTool());
		setUiPixmapRadius(getUiPixmapRadius());
		setUiPixmapOpacity(getUiPixmapOpacity());
	}
	////////////
	/// View methods
	////////////

	public void setEnabled(boolean enabled){
		this.enabled = enabled;

		if(hm_pixmapPainter!=null)
			hm_pixmapPainter.setPreviewPainting(terrainEditorView.isEnabled() && this.enabled && hm_paintingPreview);
	}

	private float buttonHoldTimer;
	private float buttonHoldDelay;
	private float buttonHoldInterval;

	public void update(float delta){
		if(!enabled)
			return;

		if(hm_scaleUpButton.isPressed()){
			buttonHoldTimer+=delta;
			if(buttonHoldTimer > buttonHoldDelay + buttonHoldInterval){
				setParamScale(getParamScale() + 1f);
				buttonHoldTimer = buttonHoldDelay;
			}
		}else if(hm_scaleDownButton.isPressed()){
			buttonHoldTimer+=delta;
			if(buttonHoldTimer > buttonHoldDelay + buttonHoldInterval){
				setParamScale(getParamScale() - 1f);
				buttonHoldTimer = buttonHoldDelay;
			}
		}else if(hm_magnitudeUpButton.isPressed()){
			buttonHoldTimer+=delta;
			if(buttonHoldTimer > buttonHoldDelay + buttonHoldInterval){
				setParamMagnitude(getParamMagnitude() + 1f);
				buttonHoldTimer = buttonHoldDelay;
			}
		}else if(hm_magnitudeDownButton.isPressed()){
			buttonHoldTimer+=delta;
			if(buttonHoldTimer > buttonHoldDelay + buttonHoldInterval){
				setParamMagnitude(getParamMagnitude() - 1f);
				buttonHoldTimer = buttonHoldDelay;
			}
		}

		if (hm_pixmapPainter != null)
			hm_pixmapPainter.updateInput(delta);
	}

	public void render(float delta){
		if(!enabled)
			return;

		if (!world.editorView.isToolbarVisible()) {
			String text = "Height map edit mode";

			world.editorView.minTextLabel.setText(text);
		}

	}

	public void dispose()
	{
		if (hm_pixmapPainter != null) {
			hm_pixmapPainter.dispose();
			hm_pixmapPainter = null;
		}
	}


	////////////////////////////////////////
	/// Begin methods that mutate the UI (ie button actions)
	////////////////////////////////////////

	public void setUiParamScale(float scale){
		hm_scaleTextField.setText(String.valueOf(scale));
	}



	public void setUiParamMagnitude(float magnitude){
		hm_magnitudeTextField.setText(String.valueOf(magnitude));
	}

	public PixmapPainter.Tool getUiPixmapTool() {
		return hm_pixmapPainter.getTool();
	}

	public void setUiPixmapTool(PixmapPainter.Tool tool) {
		hm_pixmapPainter.setTool(tool);
		if (wm_bucketFillButton.getUserObject() == tool) {
			wm_bucketFillButton.setChecked(true);
		} else if (wm_brushButton.getUserObject() == tool) {
			wm_brushButton.setChecked(true);
		} else if (wm_eraserButton.getUserObject() == tool) {
			wm_eraserButton.setChecked(true);
		}
	}

	public int getUiPixmapRadius() {
		return hm_pixmapPainter.getBrush().getRadius();
	}

	public void setUiPixmapRadius(int radius) {
		hm_pixmapPainter.getBrush().setRadius(radius);
		wm_radiusLabel.setText(String.valueOf(radius));
	}

	public float getUiPixmapOpacity() {
		return hm_pixmapPainter.getBrushOpacity();
	}

	public void setUiPixmapOpacity(float opacity) {
		hm_pixmapPainter.setBrushOpacity(opacity);

		wm_opacityValueLabel.setText(String.valueOf(UtMath.round(opacity, 2)));
	}


	////////////////////////////////////////////////
	/// Begin methods that listen for user input
	////////////////////////////////////////////////

	@Override
	public boolean keyDown(int keycode) {
		if(!enabled) return false;
		if (hm_pixmapPainter != null && hm_pixmapPainter.keyDown(keycode)) return true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(!enabled) return false;
		switch(keycode){
			case Input.Keys.TAB:
				terrainEditorView.setWeightPaintingEnabled(true);
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
		if (hm_pixmapPainter != null && hm_pixmapPainter.keyUp(keycode)) return true;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(!enabled) return false;
		if (hm_pixmapPainter != null && hm_pixmapPainter.keyTyped(character)) return true;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		if (hm_pixmapPainter != null && hm_pixmapPainter.touchDown(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		if (hm_pixmapPainter != null && hm_pixmapPainter.touchUp(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!enabled) return false;
		if (hm_pixmapPainter != null && hm_pixmapPainter.touchDragged(screenX, screenY, pointer)) return true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!enabled) return false;
		if (hm_pixmapPainter != null && hm_pixmapPainter.mouseMoved(screenX, screenY)) return true;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!enabled) return false;
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			setUiPixmapRadius(getUiPixmapRadius() - amount);
			return true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			setUiPixmapOpacity(getUiPixmapOpacity() - amount * 0.05f);
			return true;
		}
		if (hm_pixmapPainter != null && hm_pixmapPainter.scrolled(amount)) return true;
		return false;
	}

	private class InternalClickListener extends ClickListener implements TextField.TextFieldFilter, TextField.TextFieldListener{
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();

			if(actor == hm_scaleUpButton){
				if(buttonHoldTimer < buttonHoldDelay){
					setParamScale(getParamScale() + 1f);
				}
				buttonHoldTimer=0;
			}else if(actor == hm_scaleDownButton){
				if(buttonHoldTimer < buttonHoldDelay){
					setParamScale(getParamScale() - 1f);
				}
				buttonHoldTimer = 0;
			}else if(actor == hm_magnitudeUpButton){
				if(buttonHoldTimer < buttonHoldDelay) {
					setParamMagnitude(getParamMagnitude() + 1f);
				}
				buttonHoldTimer = 0;
			}else if(actor == hm_magnitudeDownButton){
				if(buttonHoldTimer < buttonHoldDelay) {
					setParamMagnitude(getParamMagnitude() - 1f);
				}
				buttonHoldTimer = 0;
			} else if (actor.getUserObject() instanceof PixmapPainter.Tool) {
				Button checked = wm_toolSelectionButtonGroup.getChecked();
				PixmapPainter.Tool checkedTool = (PixmapPainter.Tool) checked.getUserObject();
				setUiPixmapTool(checkedTool);
			}


		}

		@Override
		public boolean acceptChar(TextField textField, char c) {
			if(textField == hm_scaleTextField)
				return Character.isDigit(c) || c=='.' || c =='-' || c=='\r' || c=='\n';
			else if(textField == hm_magnitudeTextField)
				return Character.isDigit(c) || c=='.' || c =='-' || c=='\r' || c=='\n';
			return true;
		}

		@Override
		public void keyTyped(TextField textField, char c) {
			if(textField == hm_scaleTextField) {
				if(c=='\n' || c=='\r'){
					try{
						setParamScale(Float.parseFloat(hm_scaleTextField.getText()));
					}catch(NumberFormatException nfe){
						setParamScale(100f);
					}
					world.stage.setKeyboardFocus(null);
				}
			}else if(textField == hm_magnitudeTextField) {
				if(c=='\n' || c=='\r'){
					try{
						setParamMagnitude(Float.parseFloat(hm_magnitudeTextField.getText()));
					}catch(NumberFormatException nfe){
						setParamMagnitude(30f);
					}
					world.stage.setKeyboardFocus(null);
				}
			}
		}
	}

	public float getParamScale(){
		return world.terrainView.terrain.parameter.scale;
	}

	private void setParamScale(float scale){
		if(scale < 1) scale= 1;
		Terrain terrain = world.terrainView.terrain;
		terrain.parameter.scale = scale;

		terrain.createTerrain(terrain.parameter,hm_pixmapPainter.pixmap);
		terrainEditorView.refreshHeightMapWeightMapPainters();
		refreshHeightMapUi();

		//setUiParamScale(scale);
	}

	public float getParamMagnitude(){
		return world.terrainView.terrain.parameter.magnitude;
	}

	private void setParamMagnitude(float magnitude){
		// TODO: input validation
		Terrain terrain = world.terrainView.terrain;
		terrain.parameter.magnitude = magnitude;

		terrain.createTerrain(terrain.parameter,hm_pixmapPainter.pixmap);
		terrainEditorView.refreshHeightMapWeightMapPainters();
		refreshHeightMapUi();

		//setUiParamMagnitude(magnitude);
	}

	private TerrainPainterModel terrainPainterModel;

	public void refreshHeightMapPainter(){

		// Be sure to call initUi() / refreshHeightMapUi() after refreshing the painters..

		if (hm_pixmapPainter != null) {
			if(world.terrainView.terrain.fieldData == terrainPainterModel.fieldData){
				// texture hasnt changed, exit out early.
				return;
			}
			hm_pixmapPainter.dispose();
			hm_pixmapPainter = null;
		}

		//wm_pixmapPainter = new PixmapPainter(1024, 1024, Pixmap.Format.RGBA8888);
		hm_pixmapPainter = new PixmapPainter(terrainPainterModel=new TerrainPainterModel(this));
		hm_pixmapPainter.setBrushColor(new Color(1,1,1,1));
		hm_pixmapPainter.setBrushOpacity(0.1f);
		hm_pixmapPainter.coordProvider = terrainEditorView;

		hm_pixmapPainter.setPreviewPainting(terrainEditorView.isEnabled() && this.enabled && hm_paintingPreview);


	}

	public void savePainterToFile(FileHandle fh){
		//ensure that previews or whatnot havent screwed nothing up and that were saving the last history state..
		hm_pixmapPainter.history.recallHistory(hm_pixmapPainter.history.history.size - 1, hm_pixmapPainter);
		PixmapIO.writePNG(fh, hm_pixmapPainter.pixmap);
	}

}