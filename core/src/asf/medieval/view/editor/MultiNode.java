package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 12/2/15.
 */
public abstract class MultiNode implements EditorNode {
	public final MedievalWorld world;
	public boolean enabled;
	public String modeName;

	protected Table toolTable;
	protected EditorNode[] modes;


	public MultiNode(String modeName, MedievalWorld world, EditorNode... modes) {
		this.modeName = modeName;
		this.world = world;
		this.modes = modes;
	}

	@Override
	public void initUi() {
		toolTable = new Table(world.app.skin);
		if(this instanceof View) // TODO: need proper way to determine if this is the root table...
			toolTable.setBackground("default-pane-trans"); // base editor, set up the background
		toolTable.align(Align.left);
		toolTable.row();
		for (EditorNode mode : modes) {
			mode.initUi();
		}
	}

	@Override
	public void refreshUi() {
		for (EditorNode mode : modes) {
			mode.refreshUi();
		}
	}

	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (EditorNode editorNode : modes) {
			editorNode.setEnabled(enabled);
		}
	}

	@Override
	public void update(float delta) {
		if(!enabled) return;
		for (EditorNode editorNode : modes) {
			editorNode.update(delta);
		}
	}

	@Override
	public void render(float delta) {
		if(!enabled) return;
		for (EditorNode editorNode : modes) {
			editorNode.render(delta);
		}
	}

	@Override
	public void dispose() {
		for (EditorNode editorNode : modes) {
			editorNode.dispose();
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		if(!enabled) return false;
		for (EditorNode mode : modes) {
			if(mode.keyDown(keycode))
				return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(!enabled) return false;
		for (EditorNode mode : modes) {
			if(mode.keyUp(keycode))
				return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(!enabled) return false;
		for (EditorNode mode : modes) {
			if(mode.keyTyped(character))
				return true;
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		for (EditorNode mode : modes) {
			if(mode.touchDown(screenX, screenY, pointer, button))
				return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!enabled) return false;
		for (EditorNode mode : modes) {
			if(mode.touchUp(screenX, screenY, pointer, button))
				return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!enabled) return false;
		for (EditorNode mode : modes) {
			if(mode.touchDragged(screenX, screenY, pointer))
				return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!enabled) return false;
		for (EditorNode mode : modes) {
			if(mode.mouseMoved(screenX, screenY))
				return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!enabled) return false;
		for (EditorNode mode : modes) {
			if(mode.scrolled(amount))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return modeName;
	}
}
