package asf.medieval.view.editor;

import asf.medieval.painter.Painter;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by daniel on 12/2/15.
 */
public class PainterPane implements EditorNode {
	public final MedievalWorld world;
	public final PainterProvider painterProvider;
	public boolean enabled;

	private InternalClickListener internalCl = new InternalClickListener();
	private Table toolSubtable;

	public ButtonGroup<Button> toolSelectionButtonGroup;
	public ImageButton bucketFillButton, brushButton, sprayButton, eraserButton;
	public Label radiusLabel, hardEdgeLabel, opacityValueLabel;
	public boolean paintingPreview = true;
	public Painter painter;


	public static interface PainterProvider{
		public Painter providePainter(Painter currentPainter);

	}

	public PainterPane(MedievalWorld world, PainterProvider painterProvider) {
		this.world = world;
		this.painterProvider= painterProvider;
	}

	@Override
	public void initUi() {
		// PixmapPainter Tool selector
		{

			bucketFillButton = UtEditor.createImageButtonToggle("bucketfill", world.app.skin, internalCl);
			brushButton = UtEditor.createImageButtonToggle("paintbrush", world.app.skin, internalCl);
			sprayButton = UtEditor.createImageButtonToggle("spraypaint", world.app.skin, null);
			eraserButton = UtEditor.createImageButtonToggle("eraser", world.app.skin, internalCl);

			bucketFillButton.setUserObject(Painter.Tool.Fill);
			brushButton.setUserObject(Painter.Tool.Brush);
			sprayButton.setUserObject(null);
			eraserButton.setUserObject(Painter.Tool.Eraser);
			toolSubtable = new Table(world.app.skin);
			toolSubtable.row().pad(5);
			toolSubtable.add(bucketFillButton);
			toolSubtable.add(brushButton);
			toolSubtable.add(sprayButton);
			toolSubtable.add(eraserButton);

			toolSelectionButtonGroup = new ButtonGroup<Button>();
			toolSelectionButtonGroup.setMaxCheckCount(1);
			toolSelectionButtonGroup.setMinCheckCount(1);
			toolSelectionButtonGroup.setUncheckLast(true);

			toolSelectionButtonGroup.add(bucketFillButton);
			toolSelectionButtonGroup.add(brushButton);
			toolSelectionButtonGroup.add(sprayButton);
			toolSelectionButtonGroup.add(eraserButton);

		}


		// PixmapPainter addiitonal settings
		{

			Label toolRadiusCaptionLabel = UtEditor.createLabel("Radius:", world.app.skin);
			radiusLabel = UtEditor.createLabel("", world.app.skin);
			Label hardEdgeCaptionLabel = UtEditor.createLabel("Hard Edge:", world.app.skin);
			hardEdgeLabel = UtEditor.createLabel("", world.app.skin);
			Label toolOpacityCaptionLabel = UtEditor.createLabel("Opacity:", world.app.skin);
			opacityValueLabel = UtEditor.createLabel("", world.app.skin);

			Table pixmapSettingsSubtable = new Table(world.app.skin);
			pixmapSettingsSubtable.row();
			pixmapSettingsSubtable.add(toolRadiusCaptionLabel);
			pixmapSettingsSubtable.add(radiusLabel);


			pixmapSettingsSubtable.row();
			pixmapSettingsSubtable.add(hardEdgeCaptionLabel);
			pixmapSettingsSubtable.add(hardEdgeLabel);

			pixmapSettingsSubtable.row();
			pixmapSettingsSubtable.add(toolOpacityCaptionLabel);
			pixmapSettingsSubtable.add(opacityValueLabel);

			//toolTable.row();
			toolSubtable.add(pixmapSettingsSubtable);
		}
	}

	@Override
	public void refreshUi() {
		painter = painterProvider.providePainter(painter);
		painter.setPreviewPainting(enabled && paintingPreview);

		//System.out.println("preview painting is now "+painter.isPreviewPainting()+" for: "+painterProvider.getClass());


		setUiPixmapTool(getUiPixmapTool());
		setUiPixmapRadius(getUiPixmapRadius());
		setUiPixmapHardEdge(getUiPixmapHardEdge());
		setUiPixmapOpacity(getUiPixmapOpacity());
	}

	@Override
	public Actor getToolbarActor() {
		return toolSubtable;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void update(float delta) {
		if(!enabled) return;
		if (painter != null)
			painter.updateInput(delta);
	}

	@Override
	public void render(float delta) {
		if(!enabled) return;
	}

	@Override
	public void dispose() {
		if (painter != null) {
			painter.dispose();
			painter = null;
		}
	}

	public Painter.Tool getUiPixmapTool() {
		return painter.getTool();
	}

	public void setUiPixmapTool(Painter.Tool tool) {
		painter.setTool(tool);
		if (bucketFillButton.getUserObject() == tool) {
			bucketFillButton.setChecked(true);
		} else if (brushButton.getUserObject() == tool) {
			brushButton.setChecked(true);
		} else if (eraserButton.getUserObject() == tool) {
			eraserButton.setChecked(true);
		}
	}

	public int getUiPixmapRadius() {
		return painter.getBrush().getRadius();
	}

	public void setUiPixmapRadius(int radius) {
		painter.getBrush().setRadius(radius);
		radiusLabel.setText(String.valueOf(painter.getBrush().getRadius()));
	}

	public float getUiPixmapHardEdge() {
		return painter.getBrush().getHardEdge();
	}

	public void setUiPixmapHardEdge(float hardEdge) {
		painter.getBrush().setHardEdge(hardEdge);
		hardEdgeLabel.setText(String.valueOf(UtMath.round(painter.getBrush().getHardEdge(), 2)));
	}

	public float getUiPixmapOpacity() {
		return painter.getBrushOpacity();
	}

	public void setUiPixmapOpacity(float opacity) {
		painter.setBrushOpacity(opacity);
		opacityValueLabel.setText(String.valueOf(UtMath.round(painter.getBrushOpacity(), 2)));
	}

	@Override
	public boolean keyDown(int keycode) {
		if(!enabled) return false;
		if (painter != null && painter.keyDown(keycode)) return true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(!enabled) return false;
		switch (keycode) {
			case Input.Keys.LEFT_BRACKET:
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					setUiPixmapRadius(getUiPixmapRadius() - 1);
				} else if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
					setUiPixmapOpacity(getUiPixmapOpacity() - 0.1f);
				}else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
					setUiPixmapHardEdge(getUiPixmapHardEdge() - 0.1f);
				}
				return true;
			case Input.Keys.RIGHT_BRACKET:
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					setUiPixmapRadius(getUiPixmapRadius() + 1);
				} else if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
					setUiPixmapOpacity(getUiPixmapOpacity() + 0.1f);
				}else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
					setUiPixmapHardEdge(getUiPixmapHardEdge() + 0.1f);
				}
				return true;
			case Input.Keys.B:
				setUiPixmapTool(Painter.Tool.Brush);
				return true;
			case Input.Keys.E:
				setUiPixmapTool(Painter.Tool.Eraser);
				return true;
			case Input.Keys.F:
				setUiPixmapTool(Painter.Tool.Fill);
				return true;
		}

		if (painter != null && painter.keyUp(keycode)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(!enabled) return false;
		if (painter != null && painter.keyTyped(character)) return true;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		if (painter != null && painter.touchDown(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		if (painter != null && painter.touchUp(screenX, screenY, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!enabled) return false;
		if (painter != null && painter.touchDragged(screenX, screenY, pointer)) return true;

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!enabled) return false;
		if (painter != null && painter.mouseMoved(screenX, screenY))  return true;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!enabled) return false;
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			setUiPixmapRadius(getUiPixmapRadius() - amount);
			return true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
			setUiPixmapOpacity(getUiPixmapOpacity() - amount * 0.05f);
			return true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			setUiPixmapHardEdge(getUiPixmapHardEdge() - amount * 0.05f);
			return true;
		}

		if (painter != null && painter.scrolled(amount)) {
			return true;
		}
		return false;
	}

	private class InternalClickListener extends ClickListener implements TextField.TextFieldFilter, TextField.TextFieldListener{
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();

			if (actor.getUserObject() instanceof Painter.Tool) {
				Button checked = toolSelectionButtonGroup.getChecked();
				Painter.Tool checkedTool = (Painter.Tool) checked.getUserObject();
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

	public void savePainterToFile(FileHandle fh){
		painter.output(fh);
	}
}
