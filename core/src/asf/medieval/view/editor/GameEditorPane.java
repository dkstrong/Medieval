package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/26/15.
 */
public class GameEditorPane implements EditorNode {
	public final MedievalWorld world;
	public final EditorView editorView;
	private boolean enabled;

	InternalCLickListener internalCl = new InternalCLickListener();
	private Table toolTable;

	private TextButton fpsToggleButton;



	public GameEditorPane(EditorView editorView) {
		this.editorView = editorView;
		this.world = editorView.world;

	}

	//////////////////////////////////////////////////////
	/// Begin methods that set up and refresh the ui
	//////////////////////////////////////////////////////
	@Override
	public void initUi() {



		toolTable = new Table(world.app.skin);
		toolTable.align(Align.left);

		toolTable.row();
		toolTable.add(new Label("Game Mode", world.app.skin));
		toolTable.add(fpsToggleButton = UtEditor.createTextButtonToggle("Show FPS", world.app.skin,internalCl));


	}


	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}


	@Override
	public void refreshUi() {
		fpsToggleButton.setChecked(editorView.isFpsVisible());

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
		setEnabled(false);
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
		} else if (!enabled && this.enabled) {
			this.enabled = false;
		}
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


	private class InternalCLickListener extends ClickListener{
		@Override
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();
			if(actor == fpsToggleButton){
				editorView.toggleFpsVisible();
			}
		}
	}

	@Override
	public String toString(){
		return "Game";
	}

}
