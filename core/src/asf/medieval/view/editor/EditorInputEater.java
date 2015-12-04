package asf.medieval.view.editor;

import com.badlogic.gdx.InputProcessor;

/**
 * Created by daniel on 12/4/15.
 */
public class EditorInputEater implements InputProcessor {
	public final EditorView EditorView;

	public EditorInputEater(asf.medieval.view.editor.EditorView editorView) {
		EditorView = editorView;
	}

	@Override
	public boolean keyDown(int keycode) {
		return EditorView.isToolbarVisible();
	}

	@Override
	public boolean keyUp(int keycode) {
		return EditorView.isToolbarVisible();
	}

	@Override
	public boolean keyTyped(char character) {
		return EditorView.isToolbarVisible();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return EditorView.isToolbarVisible();
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return EditorView.isToolbarVisible();
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return EditorView.isToolbarVisible();
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return EditorView.isToolbarVisible();
	}

	@Override
	public boolean scrolled(int amount) {
		return EditorView.isToolbarVisible();
	}
}
