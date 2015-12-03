package asf.medieval.view.editor;

import asf.medieval.painter.Painter;
import asf.medieval.terrain.Terrain;
import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/30/15.
 */
public class TerrainHeightPane implements EditorNode, PainterPane.PainterProvider {
	public final MedievalWorld world;
	public final EditorView editorView;
	public boolean enabled;


	private InternalClickListener internalCl = new InternalClickListener();
	public Table toolTable;

	// heightmap ui
	private TextField hm_scaleTextField;
	private TextButton hm_scaleUpButton, hm_scaleDownButton;
	private TextField hm_magnitudeTextField;
	private TextButton hm_magnitudeUpButton, hm_magnitudeDownButton;


	public TerrainHeightPane(EditorView editorView) {
		this.world = editorView.world;
		this.editorView = editorView;
	}

	@Override
	public void initUi()
	{
		Terrain terrain = world.terrainView.terrain;
		toolTable = new Table(world.app.skin);
		toolTable.align(Align.left);

		toolTable.row();
		toolTable.add(UtEditor.createLabel("Scale", world.app.skin));
		toolTable.add(hm_scaleTextField = UtEditor.createTextField(terrain.parameter.scale + "", world.app.skin, internalCl, internalCl));
		toolTable.add(hm_scaleUpButton =UtEditor.createTextButton("/\\",world.app.skin, internalCl));
		toolTable.add(hm_scaleDownButton =UtEditor.createTextButton("\\/",world.app.skin, internalCl));

		//toolTable.row();
		toolTable.add(UtEditor.createLabel("Magnitude", world.app.skin));
		toolTable.add(hm_magnitudeTextField = UtEditor.createTextField(terrain.parameter.magnitude + "", world.app.skin, internalCl, internalCl));
		toolTable.add(hm_magnitudeUpButton = UtEditor.createTextButton("/\\", world.app.skin, internalCl));
		toolTable.add(hm_magnitudeDownButton =UtEditor.createTextButton("\\/",world.app.skin, internalCl));

	}

	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}

	private TerrainPainterDelegate terrainPainterDelegate;

	@Override
	public Painter providePainter(Painter currentPainter) {
		if (currentPainter != null) {
			if(world.terrainView.terrain.fieldData == terrainPainterDelegate.fieldData){

				return currentPainter; // // texture hasnt changed externally, exit out early.
			}
			currentPainter.dispose();
		}

		//System.out.println("refreshing the height painter");
		//wm_pixmapPainter = new PixmapPainter(1024, 1024, Pixmap.Format.RGBA8888);
		terrainPainterDelegate =new TerrainPainterDelegate(this);
		currentPainter = new Painter(terrainPainterDelegate);
		currentPainter.setBrushColor(new Color(1,1,1,1));
		currentPainter.setBrushOpacity(0.1f);
		currentPainter.coordProvider = editorView;
		return currentPainter;

	}

	@Override
	public void refreshUi() {
		setUiParamScale(getParamScale());
		setUiParamMagnitude(getParamMagnitude());


	}

	////////////
	/// View methods
	////////////

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
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

	}

	public void render(float delta){
		if(!enabled)
			return;

	}

	public void dispose()
	{
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

	////////////////////////////////////////////////
	/// Begin methods that listen for user input
	////////////////////////////////////////////////

	@Override
	public boolean keyDown(int keycode) {
		if(!enabled) return false;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(!enabled) return false;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(!enabled) return false;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!enabled) return false;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!enabled) return false;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!enabled) return false;
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

		terrain.rebuildTerrain(terrain.parameter);
		//editorView.refreshPainters();
		refreshUi(); // refresh just terrain height, not the entire terrain editor view

		//setUiParamScale(scale);
	}

	public float getParamMagnitude(){
		return world.terrainView.terrain.parameter.magnitude;
	}

	private void setParamMagnitude(float magnitude){
		// TODO: input validation
		Terrain terrain = world.terrainView.terrain;
		terrain.parameter.magnitude = magnitude;

		terrain.rebuildTerrain(terrain.parameter);
		//editorView.refreshPainters();
		refreshUi(); // refresh just terrain height, not the entire terrain editor view

		//setUiParamMagnitude(magnitude);
	}

	@Override
	public String toString(){
		return "Elevation";
	}

}
