package asf.medieval.view.editor;

import asf.medieval.painter.PixmapPainter;
import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainTextureAttribute;
import asf.medieval.utility.FileWatcher;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainEditorView implements View, FileWatcher.FileChangeListener, Disposable, InputProcessor, PixmapPainter.PixmapCoordProvider {
	public final MedievalWorld world;
	private boolean enabled;

	private Container bottomRightLabelContainer;
	private Label bottomRightLabel;




	// weightmap splat
	private Tree.Node weightmatTreeNode;
	private int selectedTexChannel = 1;
	private Texture currentTexture;
	private String currentTextureLoc;
	public PixmapPainter editPixmapPainter;
	private String editTextureLoc;
	private FileHandle editFh;


	public TerrainEditorView(MedievalWorld world) {
		this.world = world;

		currentTexture = world.terrainView.terrain.getMaterialAttribute(TerrainTextureAttribute.WeightMap1);

		//editPixmapPainter = new PixmapPainter(1024, 1024, Pixmap.Format.RGBA8888); // currentTexture
		editPixmapPainter = new PixmapPainter(currentTexture); // currentTexture
		editPixmapPainter.coordProvider = this;

		editTextureLoc = "tmp/" + currentTextureLoc;
		editFh = Gdx.files.local(editTextureLoc);

		world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.WeightMap1, editPixmapPainter.texture, 1);
		setSelectedTexChannel(selectedTexChannel);

	}


	private void createUi()
	{
		if(bottomRightLabel == null){
			bottomRightLabel = new Label("Hello, there!", world.app.skin);
			bottomRightLabel.setAlignment(Align.bottomRight, Align.bottomRight);
			bottomRightLabelContainer = new Container<Label>(bottomRightLabel);
			bottomRightLabelContainer.align(Align.bottomRight).pad(10);



			weightmatTreeNode = world.editorView.addLabelNode(null, "Terrain Texture");
			Table texTable = world.editorView.addTableNode(weightmatTreeNode);
			texTable.row();
			texTable.add(new Label("WeightMap:", world.app.skin));
			texTable.add(new TextButton("wm.png", world.app.skin));
		}
		world.stage.addActor(bottomRightLabelContainer);
		world.editorView.baseTree.add(weightmatTreeNode);
	}

	private void uncreateUi(){
		bottomRightLabelContainer.remove();
		weightmatTreeNode.remove();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			createUi();
			editPixmapPainter.setPreviewPainting(true);
		} else {
			uncreateUi();
			editPixmapPainter.setPreviewPainting(false);
			//editPixmapPainter.
		}
	}

	public void resize(int width, int height) {
		if(!enabled){
			return;
		}
		bottomRightLabelContainer.setBounds(0, 0, width, height);
	}

	@Override
	public void update(float delta) {
		if (editPixmapPainter != null)
			editPixmapPainter.updateInput(delta);
	}

	@Override
	public void render(float delta) {

		final Terrain terrain = world.terrainView.terrain;
		String text = "";

		text += "Loaded Terrain: " + terrain.parameter.name;

		if (editPixmapPainter != null) {
			text += "\nSplat Painting";

			text += "\nTool: " + editPixmapPainter.getTool();
			text += "\nBrush: " + editPixmapPainter.getBrush();
			text += "\nTex Channel: " + selectedTexChannel;
			text += "\nOpacity: " + editPixmapPainter.getBrushOpacity();

		}

		bottomRightLabel.setText(text);
	}

	@Override
	public void onFileChanged(WatchEvent<Path> event) {
		System.out.println("file changed:" + event.context() + ", kind: " + event.kind());

		String fileChanged = String.valueOf(event.context());

		if (fileChanged.endsWith(".ter")) {
			System.out.println("file changed:" + fileChanged + ", kind: " + event.kind());
			final Terrain terrain = world.terrainView.terrain;
			terrain.loadTerrain();
		} else if (fileChanged.endsWith(".glsl")) {
			System.out.println("file changed:" + fileChanged + ", kind: " + event.kind());
			final Terrain terrain = world.terrainView.terrain;
			terrain.getTerrainChunk(0, 0).renderable.shader = null;
			world.modelShaderProvider.terrainShaderProvider.clearShaderCache();
		}
	}

	@Override
	public void dispose() {

		editPixmapPainter.dispose();
		editPixmapPainter = null;
	}







	private final Vector3 tempTranslation = new Vector3();

	@Override
	public void getPixmapCoord(int screenX, int screenY, int pixmapWidth, int pixmapHeight, Vector2 store) {
		world.hudView.hudCommandView.getWorldCoord(screenX, screenY, tempTranslation);
		Terrain terrain = world.terrainView.terrain;
		store.x = UtMath.scalarLimitsInterpolation(tempTranslation.x, terrain.corner00.x, terrain.corner11.x, 0, pixmapWidth - 1);
		store.y = UtMath.scalarLimitsInterpolation(tempTranslation.z, terrain.corner00.z, terrain.corner11.z, 0, pixmapHeight - 1);
	}

	public void setSelectedTexChannel(int selectedTexChannel) {
		this.selectedTexChannel = selectedTexChannel;

		if (selectedTexChannel == 1) editPixmapPainter.setBrushColor(1, 0, 0, 0);
		else if (selectedTexChannel == 2) editPixmapPainter.setBrushColor(0, 1, 0, 0);
		else if (selectedTexChannel == 3) editPixmapPainter.setBrushColor(0, 0, 1, 0);
		else if (selectedTexChannel == 4) editPixmapPainter.setBrushColor(0, 0, 0, 1);
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
		if (!enabled)
			return false;
		switch (keycode) {
			case Input.Keys.NUM_1:
				setSelectedTexChannel(1);
				return true;
			case Input.Keys.NUM_2:
				setSelectedTexChannel(2);
				return true;
			case Input.Keys.NUM_3:
				setSelectedTexChannel(3);
				return true;
			case Input.Keys.NUM_4:
				setSelectedTexChannel(4);
				return true;
		}
		if (editPixmapPainter != null && editPixmapPainter.keyUp(keycode)) {
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
		if (!enabled)
			return false;
		if (editPixmapPainter != null && editPixmapPainter.touchDown(screenX, screenY, pointer, button)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!enabled)
			return false;
		if (editPixmapPainter != null && editPixmapPainter.touchUp(screenX, screenY, pointer, button)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!enabled)
			return false;
		if (editPixmapPainter != null && editPixmapPainter.touchDragged(screenX, screenY, pointer)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (!enabled)
			return false;
		if (editPixmapPainter != null && editPixmapPainter.mouseMoved(screenX, screenY)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (!enabled)
			return false;

		if (editPixmapPainter != null && editPixmapPainter.scrolled(amount)) {
			return true;
		}
		return false;
	}
}
