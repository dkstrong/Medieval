package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 12/2/15.
 */
public class ModeSelectPane implements EditorMode{
	public final MedievalWorld world;
	public boolean enabled;
	public String modeName;

	private final InternalChangeListener internalCl = new InternalChangeListener();
	public Table toolTable;
	private SelectBox<EditorMode> modeSelectBox;
	protected Cell modeContextCell;
	private EditorMode[] modes;


	public ModeSelectPane(String modeName, MedievalWorld world, EditorMode... modes) {
		this.modeName = modeName;
		this.world = world;
		this.modes = modes;
	}

	@Override
	public void initUi() {
		modeSelectBox = new SelectBox<EditorMode>(world.app.skin);
		modeSelectBox.getSelection().setProgrammaticChangeEvents(false);
		modeSelectBox.addListener(internalCl);
		//ModeSelectPane.ModeSelectItem gameSelectItem = new ModeSelectPane.ModeSelectItem("Game", gameEditorMode);
		//ModeSelectPane.ModeSelectItem terrainSelectItem = new ModeSelectPane.ModeSelectItem("Terrain", terrainEditorMode);
		modeSelectBox.setItems(modes);


		toolTable = new Table(world.app.skin);
		if(modes.length<3) // TODO: need proper way to determine if this is the root table...
			toolTable.setBackground("default-pane-trans"); // base editor, set up the background
		toolTable.align(Align.topLeft);
		toolTable.row();//.padBottom(Value.percentWidth(0.05f));
		toolTable.add(modeSelectBox);
		modeContextCell = toolTable.add(new Label("no mode", world.app.skin)).fill().expand().align(Align.topLeft);
	}

	@Override
	public void refreshUi() {
		setMode(getMode());
	}

	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (EditorMode editorMode : modeSelectBox.getItems()) {
			editorMode.setEnabled(enabled);
		}
	}

	@Override
	public void update(float delta) {
		if(!enabled) return;
		for (EditorMode editorMode : modeSelectBox.getItems()) {
			editorMode.update(delta);
		}
	}

	@Override
	public void render(float delta) {
		if(!enabled) return;
		for (EditorMode editorMode : modeSelectBox.getItems()) {
			editorMode.render(delta);
		}
	}

	@Override
	public void dispose() {
		for (EditorMode editorMode : modeSelectBox.getItems()) {
			editorMode.dispose();
		}
	}

	public EditorMode getMode() {
		if(!enabled) return null;
		return modeSelectBox.getSelected();
	}

	public void setMode(EditorMode editorMode){
		boolean valid = false;
		for (EditorMode mode : modeSelectBox.getItems()) {
			boolean enabled = editorMode == mode;
			mode.setEnabled(enabled);
			mode.refreshUi();
			if(enabled){
				valid = true;
				modeSelectBox.setSelected(mode);
				modeContextCell.setActor(mode.getToolbarActor());
			}

		}

		if (!valid)
			modeContextCell.setActor(null);
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

	private class InternalChangeListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			Actor actor = event.getListenerActor();

			if(actor == modeSelectBox){
				if(event instanceof ChangeListener.ChangeEvent){
					setMode(modeSelectBox.getSelected());
				}
			}


			return false;
		}
	}

	@Override
	public String toString() {
		return modeName;
	}
}
