package asf.medieval.view.editor;

import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainTextureAttribute;
import asf.medieval.utility.UtLog;
import asf.medieval.utility.UtMath;
import asf.medieval.utility.UtPixmap;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
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

	public float brushRadius = 10;
	public int brushPaint = 1;

	private Texture currentTexture;
	private String currentTextureLoc;
	private Texture editTexture;
	private String editTextureLoc;
	private Pixmap editPixmap;
	private FileHandle editFh;

	public TerrainSplatEditorView(MedievalWorld world) {
		this.world = world;

		ModelBuilder modelBuilder = new ModelBuilder();
		Material mat = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
		int attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
		Model model = modelBuilder.createCylinder(brushRadius, .1f, brushRadius, 16, mat, attributes);


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

			currentTexture.getTextureData().prepare();
			currentTextureLoc =world.terrainView.terrain.parameter.weightMap1;
			Pixmap currentPixmap = currentTexture.getTextureData().consumePixmap();
			if(currentPixmap.getFormat() != Pixmap.Format.RGBA8888){
				UtLog.warning("pixmap was not rgba8888, weightmaps should be rgba8888");
			}

			editPixmap = UtPixmap.copyPixmap(currentPixmap);
			currentPixmap.dispose();
			//
			editTexture = new Texture(new PixmapTextureData(editPixmap,editPixmap.getFormat(),false,false));
			editTextureLoc = "tmp/"+currentTextureLoc;
			editFh = Gdx.files.local(editTextureLoc);

			world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.WeightMap1,editTexture,1);


		}else if(!enabled && this.enabled){
			this.enabled = false;
			if(currentTexture.getTextureData().disposePixmap()){
				editPixmap.dispose();
			}
			currentTexture = null;
			currentTextureLoc = null;
			editTexture = null;
			editTextureLoc = null;
			editPixmap.dispose();
			editPixmap = null;
			editFh = null;

		}

		leftDown = false;
		rightDown = false;
	}

	public boolean isEnabled(){
		return enabled;
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
		switch(keycode){
			case Input.Keys.NUM_1:
				brushPaint =1;
				//world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.Tex1,"Textures/Terrain/dirt.png", 20f);
				break;
			case Input.Keys.NUM_2:
				brushPaint =2;
				//world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.Tex1,"Textures/Terrain/grass.png", 20f);
				break;
			case Input.Keys.NUM_3:
				brushPaint =3;
				//world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.Tex1,"Textures/Terrain/water.jpg", 20f);
				break;
			case Input.Keys.NUM_4:
				brushPaint =4;
				//world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.Tex1,"Textures/Terrain/water.jpg", 20f);
				break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	private boolean leftDown;
	private boolean rightDown;
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
		if(button == Input.Buttons.LEFT)
			leftDown = false;
		if(button == Input.Buttons.RIGHT)
			rightDown = false;
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
		//Vector2 vec2 = world.terrainView.terrain.getFieldCoordiante(translation, new Vector2());

		if(brushPaint == 1){
			editPixmap.setColor(1,0,0,0);
		}else if(brushPaint == 2){
			editPixmap.setColor(0,1,0,0);
		}else if(brushPaint == 3){
			editPixmap.setColor(0,0,1,0);
		}else if(brushPaint == 4){
			editPixmap.setColor(0,0,0,1);
		}

		int texX = (int)UtMath.scalarLimitsInterpolation(translation.x, terrain.corner00.x, terrain.corner11.x, 0, editPixmap.getWidth() - 1);
		int texY = (int)UtMath.scalarLimitsInterpolation(translation.z, terrain.corner00.z, terrain.corner11.z, 0, editPixmap.getHeight() - 1);

		//System.out.println("write ("+texX+","+texY+"): "+brushPaint+" to: "+editTextureLoc);
		int effectiveRadius =  (int)(brushRadius *0.5f);

		editPixmap.drawCircle( texX, texY, 0);
		//editPixmap.drawCircle( texX, texY, 1);
		//editPixmap.drawCircle( texX, texY, 2);


		editTexture.draw(editPixmap,0,0);

		//world.terrainView.terrain.setMaterialAttribute(TerrainTextureAttribute.WeightMap1,editTexture,1);
		//editTexture.draw(editPixmap,0,0);


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
		return false;
	}
}
