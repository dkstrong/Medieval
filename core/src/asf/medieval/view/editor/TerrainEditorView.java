package asf.medieval.view.editor;

import asf.medieval.terrain.Terrain;
import asf.medieval.utility.FileWatcher;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainEditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor {
	public final MedievalWorld world;
	private boolean enabled;

	public final TerrainSplatEditorView terrainSplatEditorView;

	private Container bottomRightLabelContainer;
	private Label bottomRightLabel;

	private FileWatcher fileWatcher;

	public TerrainEditorView(MedievalWorld world) {
		this.world = world;

		bottomRightLabel = new Label("Hello, there!", world.app.skin);
		bottomRightLabel.setAlignment(Align.bottomRight, Align.bottomRight);
		bottomRightLabelContainer = new Container<Label>(bottomRightLabel);
		bottomRightLabelContainer.align(Align.bottomRight).pad(10);
		world.stage.addActor(bottomRightLabelContainer);

		fileWatcher = new FileWatcher(this);
		fileWatcher.addWatch(Gdx.files.local("Terrain"));
		fileWatcher.addWatch(Gdx.files.local("Shaders"));

		terrainSplatEditorView = new TerrainSplatEditorView(world);



	}

	public void resize(int width, int height) {


		bottomRightLabelContainer.setBounds(0, 0, width, height);
	}

	@Override
	public void update(float delta) {
		if(terrainSplatEditorView.isEnabled())
			terrainSplatEditorView.update(delta);
	}

	@Override
	public void render(float delta) {
		if(terrainSplatEditorView.isEnabled())
			terrainSplatEditorView.render(delta);

		final Terrain terrain = world.terrainView.terrain;
		String text = "";

		text +="Loaded Terrain: "+terrain.parameter.name;

		if(terrainSplatEditorView.isEnabled()){
			text+="\nSplat Painting";
			if(terrainSplatEditorView.editPixmapPainter != null){
				text+="\nTool: "+terrainSplatEditorView.editPixmapPainter.getTool();
				text+="\nBrush: "+terrainSplatEditorView.editPixmapPainter.getBrush();
				text+="\nTex Channel: "+terrainSplatEditorView.getSelectedTexChannel();
				text+="\nOpacity: "+terrainSplatEditorView.editPixmapPainter.getBrushOpacity();
			}
		}

		bottomRightLabel.setText(text);
	}

	@Override
	public void onFileChanged(WatchEvent<Path> event) {
		System.out.println("file changed:"+event.context()+", kind: "+event.kind());

		String fileChanged = String.valueOf(event.context());

		if (fileChanged.endsWith(".ter")) {
			System.out.println("file changed:"+fileChanged+", kind: "+event.kind());
			final Terrain terrain = world.terrainView.terrain;
			terrain.loadTerrain();
		}else if(fileChanged.endsWith(".glsl")){
			System.out.println("file changed:"+fileChanged+", kind: "+event.kind());
			final Terrain terrain = world.terrainView.terrain;
			terrain.getTerrainChunk(0,0).renderable.shader = null;
			world.modelShaderProvider.terrainShaderProvider.clearShaderCache();
		}
	}

	@Override
	public void dispose() {
		fileWatcher.dispose();
		terrainSplatEditorView.dispose();
	}




	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if(!enabled){
			terrainSplatEditorView.setEnabled(false);
		}else{
			terrainSplatEditorView.setEnabled(true);
		}
	}


	public boolean isEnabled() {
		return enabled;
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
}
