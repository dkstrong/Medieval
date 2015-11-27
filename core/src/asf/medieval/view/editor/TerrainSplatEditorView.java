package asf.medieval.view.editor;

import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainTextureAttribute;
import asf.medieval.utility.DrawablePixmap;
import asf.medieval.utility.UtMath;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/26/15.
 */
public class TerrainSplatEditorView implements View,Disposable,InputProcessor {
	public final MedievalWorld world;
	private boolean enabled;

	private ModelInstance modelInstance;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	private int brushPaint = 1;

	private Texture currentTexture;
	private String currentTextureLoc;
	public DrawablePixmap editDrawablePixmap;
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

			editDrawablePixmap = new DrawablePixmap(currentTexture); // currentTexture

			editTextureLoc = "tmp/"+currentTextureLoc;
			editFh = Gdx.files.local(editTextureLoc);

			world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.WeightMap1,editDrawablePixmap.texture,1);

			setBrushPaint(brushPaint);


		}else if(!enabled && this.enabled){
			this.enabled = false;

			currentTexture = null;
			currentTextureLoc = null;
			editDrawablePixmap.dispose();
			editDrawablePixmap= null;
			editTextureLoc = null;
			editFh = null;

		}

		leftDown = false;
		rightDown = false;
	}

	public boolean isEnabled(){
		return enabled;
	}


	public int getBrushPaint() {
		return brushPaint;
	}

	public void setBrushPaint(int brushPaint) {
		this.brushPaint = brushPaint;

		if(brushPaint == 1) editDrawablePixmap.setBrushColor(1, 0, 0, 0);
		else if(brushPaint == 2) editDrawablePixmap.setBrushColor(0, 1, 0, 0);
		else if(brushPaint == 3) editDrawablePixmap.setBrushColor(0, 0, 1, 0);
		else if(brushPaint == 4) editDrawablePixmap.setBrushColor(0, 0, 0, 1);
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
				setBrushPaint(1);
				return true;
			case Input.Keys.NUM_2:
				setBrushPaint(2);
				return true;
			case Input.Keys.NUM_3:
				setBrushPaint(3);
				return true;
			case Input.Keys.NUM_4:
				setBrushPaint(4);
				return true;
		}
		if(editDrawablePixmap!= null && editDrawablePixmap.keyUp(keycode)){
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	private boolean leftDown;
	private boolean rightDown;
	private int lastX = -1;
	private int lastY = -1;
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT)
			leftDown = true;
		if(button == Input.Buttons.RIGHT)
			rightDown = true;
		if(!enabled)
			return false;

		if(leftDown)
		{
			paint(screenX,screenY);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			leftDown = false;
			lastX = -1;
			lastY = -1;
		}else if(button == Input.Buttons.RIGHT){
			rightDown = false;
			lastX = -1;
			lastY = -1;
		}

		if(!enabled)
			return false;

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!enabled)
			return false;
		if(leftDown)
		{
			paint(screenX,screenY);
			return true;
		}
		return false;
	}

	private void paint(int screenX, int screenY){

		world.hudView.hudCommandView.getWorldCoord(screenX, screenY,translation);

		Terrain terrain = world.terrainView.terrain;

		int texX = Math.round(UtMath.scalarLimitsInterpolation(translation.x, terrain.corner00.x, terrain.corner11.x, 0, editDrawablePixmap.pixmap.getWidth() - 1));
		int texY = Math.round(UtMath.scalarLimitsInterpolation(translation.z, terrain.corner00.z, terrain.corner11.z, 0, editDrawablePixmap.pixmap.getHeight() - 1));

		editDrawablePixmap.draw(lastX, lastY, texX, texY);

		lastX = texX;
		lastY = texY;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!enabled)
			return false;
		world.hudView.hudCommandView.getWorldCoord(Gdx.input.getX(), Gdx.input.getY(),translation);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!enabled)
			return false;

		if(editDrawablePixmap!= null && editDrawablePixmap.scrolled(amount)){
			return true;
		}

		return false;
	}
}
