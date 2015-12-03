package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/26/15.
 */
public class GameEditorPane implements EditorNode {
	public final MedievalWorld world;
	private boolean enabled;

	private Table toolTable;


	public GameEditorPane(MedievalWorld world) {
		this.world = world;

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

	}


	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}


	@Override
	public void refreshUi() {
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


	////////////////////////////////////////////////////////////
	/// Begin methods that edit the terrain or edit files
	////////////////////////////////////////////////////////////


	@Override
	public String toString(){
		return "Game";
	}

}
