package asf.medieval.view.editor;

import asf.medieval.terrain.Terrain;
import asf.medieval.utility.FileWatcher;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainEditorView implements View, FileWatcher.FileChangeListener, Disposable {
	public final MedievalWorld world;

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



	}

	public void resize(int width, int height) {


		bottomRightLabelContainer.setBounds(0, 0, width, height);
	}

	@Override
	public void update(float delta) {

	}

	@Override
	public void render(float delta) {
		final Terrain terrain = world.terrainView.terrain;
		String text = "";

		text +="Loaded Terrain: "+terrain.parameter.name;

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
	}
}
