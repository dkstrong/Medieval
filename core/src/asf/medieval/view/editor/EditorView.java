package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/26/15.
 */
public class EditorView implements View, Disposable {

	public final MedievalWorld world;

	public TerrainEditorView terrainEditorView;

	public EditorView(MedievalWorld world) {
		this.world = world;

		terrainEditorView = new TerrainEditorView(world);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int width, int height) {
		if(terrainEditorView!= null)
			terrainEditorView.resize(width,height);

	}

	@Override
	public void update(float delta) {
		if(terrainEditorView!= null)
			terrainEditorView.update(delta);
	}

	@Override
	public void render(float delta) {
		if(terrainEditorView!= null)
			terrainEditorView.render(delta);
	}

	@Override
	public void dispose() {
		if(terrainEditorView!= null)
			terrainEditorView.dispose();
	}
}
