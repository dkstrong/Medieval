package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;


/**
 * Created by daniel on 11/26/15.
 */
public class EditorView implements View, Disposable, InputProcessor {

	public final MedievalWorld world;

	public final TerrainEditorView terrainEditorView;

	public EditorView(MedievalWorld world) {
		this.world = world;

		terrainEditorView = new TerrainEditorView(world);
		terrainEditorView.setEnabled(true);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int width, int height) {
			terrainEditorView.resize(width,height);

	}

	@Override
	public void update(float delta) {
		if(terrainEditorView.isEnabled())
			terrainEditorView.update(delta);
	}

	@Override
	public void render(float delta) {
		if(terrainEditorView.isEnabled())
			terrainEditorView.render(delta);
	}

	@Override
	public void dispose() {
		if(terrainEditorView.isEnabled())
			terrainEditorView.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode)
		{
			case Input.Keys.F5:
				// Game Mode
				terrainEditorView.setEnabled(false);
				return true;
			case Input.Keys.F6:
				// Terrain Edit Mode
				terrainEditorView.setEnabled(true);
				return true;
		}
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
}
