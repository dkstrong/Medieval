package asf.medieval.view.editor;

import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainTextureAttribute;
import asf.medieval.utility.PixmapPainter;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainSplatEditorView implements View,Disposable,InputProcessor,PixmapPainter.PixmapCoordProvider {
	public final MedievalWorld world;
	private boolean enabled;

	private ModelInstance modelInstance;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	private int selectedTexChannel = 1;

	private Texture currentTexture;
	private String currentTextureLoc;
	public PixmapPainter editPixmapPainter;
	private String editTextureLoc;
	private FileHandle editFh;



	public TerrainSplatEditorView(MedievalWorld world) {
		this.world = world;

		ModelBuilder modelBuilder = new ModelBuilder();
		Material mat = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
		int attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
		Model model = modelBuilder.createCylinder(1, .1f, 1, 16, mat, attributes);


		modelInstance = new ModelInstance(model);
	}

	@Override
	public void update(float delta)
	{
		if(editPixmapPainter!=null)
			editPixmapPainter.updateInput(delta);

	}

	@Override
	public void render(float delta)
	{
		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);

		//world.modelBatch.render(modelInstance, world.environment);
		world.modelBatch.render(modelInstance);
	}

	public void setEnabled(boolean enabled) {
		//http://badlogicgames.com/forum/viewtopic.php?f=11&t=5686
		if(enabled && !this.enabled){
			this.enabled = true;
			currentTexture = world.terrainView.terrain.getMaterialAttribute(TerrainTextureAttribute.WeightMap1);

			editPixmapPainter = new PixmapPainter(1024,1024, Pixmap.Format.RGBA8888); // currentTexture
			editPixmapPainter.coordProvider = this;

			editTextureLoc = "tmp/"+currentTextureLoc;
			editFh = Gdx.files.local(editTextureLoc);

			world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.WeightMap1, editPixmapPainter.texture,1);

			setSelectedTexChannel(selectedTexChannel);


		}else if(!enabled && this.enabled){
			this.enabled = false;

			currentTexture = null;
			currentTextureLoc = null;
			editPixmapPainter.dispose();
			editPixmapPainter = null;
			editTextureLoc = null;
			editFh = null;

		}
	}

	public boolean isEnabled(){
		return enabled;
	}


	public int getSelectedTexChannel() {
		return selectedTexChannel;
	}

	public void setSelectedTexChannel(int selectedTexChannel) {
		this.selectedTexChannel = selectedTexChannel;

		if(selectedTexChannel == 1) editPixmapPainter.setBrushColor(1, 0, 0, 0);
		else if(selectedTexChannel == 2) editPixmapPainter.setBrushColor(0, 1, 0, 0);
		else if(selectedTexChannel == 3) editPixmapPainter.setBrushColor(0, 0, 1, 0);
		else if(selectedTexChannel == 4) editPixmapPainter.setBrushColor(0, 0, 0, 1);
	}

	@Override
	public void dispose() {
		modelInstance.model.dispose();
		setEnabled(false);
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(!enabled)
			return false;
		switch(keycode){
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
		if(editPixmapPainter != null && editPixmapPainter.keyUp(keycode)){
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

		if(!enabled)
			return false;
		if(editPixmapPainter != null && editPixmapPainter.touchDown(screenX, screenY, pointer, button)){
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!enabled)
			return false;
		if(editPixmapPainter != null && editPixmapPainter.touchUp(screenX, screenY, pointer, button)){
			return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!enabled)
			return false;
		if(editPixmapPainter != null && editPixmapPainter.touchDragged(screenX, screenY, pointer)){
			return true;
		}
		return false;
	}

	@Override
	public void getPixmapCoord(int screenX, int screenY, int pixmapWidth, int pixmapHeight, Vector2 store) {
		world.hudView.hudCommandView.getWorldCoord(screenX, screenY,translation);
		Terrain terrain = world.terrainView.terrain;
		store.x =UtMath.scalarLimitsInterpolation(translation.x, terrain.corner00.x, terrain.corner11.x, 0, pixmapWidth - 1);
		store.y = UtMath.scalarLimitsInterpolation(translation.z, terrain.corner00.z, terrain.corner11.z, 0, pixmapHeight - 1);
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!enabled)
			return false;
		if(editPixmapPainter != null && editPixmapPainter.mouseMoved(screenX, screenY)){
			return true;
		}
		world.hudView.hudCommandView.getWorldCoord(Gdx.input.getX(), Gdx.input.getY(),translation);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!enabled)
			return false;

		if(editPixmapPainter != null && editPixmapPainter.scrolled(amount)){
			return true;
		}

		return false;
	}


}
